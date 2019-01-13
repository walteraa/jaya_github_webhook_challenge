package helpers

import dto.PayloadDTO
import org.flywaydb.core.Flyway
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.exceptions.ExposedSQLException
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import webhook.consumer.models.Event
import webhook.consumer.models.Issue
import webhook.query.repository.Events
import webhook.query.repository.Issues
import webhook.query.utils.Environment
import helpers.TestHelpers

fun tearDownTestDB(){
    val flyway = Flyway()
    val config = Environment.getDBUrl()
    flyway.setDataSource(config, Environment.getDBUser(), Environment.getDBUserPassword())
    flyway.clean()
}



class DBHelpers {
    private val helper = TestHelpers()
    fun buildEventsToSameIssue(n: Int): Int{

        val dto = helper.extractPayloadData(helper.validPayload())
        for(i in 1..n){
            this.create(dto)
        }
        return dto.issueNumber
    }

    fun buildEventsToDifferentIssues(n: Int){

        for(i in 1..n){
            val dto = helper.extractPayloadData(helper.validPayload())
            this.create(dto)
        }
    }

    fun buildAndReturnEvents(n: Int): List<PayloadDTO>{
        var result = mutableListOf<PayloadDTO>()

        for(i in 1..n){
            val dto = helper.extractPayloadData(helper.validPayload())
            result.add(dto)
            this.create(dto)
        }

        return result
    }

    @Throws(ExposedSQLException::class)
    private fun create(payload: PayloadDTO) {
        val issue = findByIssueNumberAndRepositoryId(payload.issueNumber, payload.repositoryId)

        if (issue == null) {

            val issueObject = Issue(
                null, payload.issueNumber, payload.repositoryId, payload.repositoryName,
                payload.issueSender, null
            )

            val issueId = issue_create(issueObject) ?: -1

            transaction {
                Events.insert { row ->
                    row[parentId] = issueId
                    row[parentEntity] = Issues.tableName
                    row[Events.payload] = payload.fullPayload
                    row[action] = payload.action
                    row[sender] = payload.eventSender
                }
            }

        } else {
            // Workaround to avoid errors when using eq, without that we have a mismatch Type: Int eq Int?
            val localIssueID = issue.issueId ?: -1
            transaction {
                Events.insert { row ->
                    row[parentId] = localIssueID
                    row[parentEntity] = Issues.tableName
                    row[Events.payload] = payload.fullPayload
                    row[action] = payload.action
                    row[sender] = payload.eventSender
                }
            }
        }
    }

    private fun issue_create(issue: Issue): Int? {
        var parentId: EntityID<Int>? = null

        transaction {

            val eventParentId = Issues.insertAndGetId { row ->
                row[issueNumber] = issue.issueNumber
                row[repositoryId] = issue.repositoryId
                row[repositoryName] = issue.repositoryName
                row[issueSender] = issue.issueSender
            }

            commit()

            parentId = eventParentId
        }

        return parentId?.value
    }

    private fun findByIssueNumberAndRepositoryId(issueNumber: Int, repositoryId: Int): Issue? {
        return transaction {
            val issue = Issues.select { Issues.issueNumber eq issueNumber }
                .andWhere { Issues.repositoryId eq repositoryId }.limit(1).map {
                    Issue(
                        issueId = it[Issues.issueID],
                        issueNumber = it[Issues.issueNumber],
                        repositoryId = it[Issues.repositoryId],
                        repositoryName = it[Issues.repositoryName],
                        issueSender = it[Issues.issueSender],
                        events = emptyList()
                    )
                }.firstOrNull()

            if (issue != null) {
                // Workaround to avoid errors when using eq, without that we have a mismatch Type: Int eq Int?
                val localIssueID = issue.issueId ?: -1
                val events = Events.select {
                    (Events.parentEntity eq Issues.tableName) and (Events.parentId eq localIssueID)
                }.map {
                    Event(
                        eventId = it[Events.eventId],
                        parentEntity = it[Events.parentEntity],
                        parentId = it[Events.parentId],
                        payload = it[Events.payload],
                        createdAt = it[Events.createdAt],
                        action = it[Events.action],
                        sender = it[Events.sender]
                    )
                }
                return@transaction issue.copy(events = events)
            }

            return@transaction issue
        }
    }
}

