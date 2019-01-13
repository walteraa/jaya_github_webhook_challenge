package webhook.consumer.repository

import mu.KotlinLogging
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntIdTable
import org.jetbrains.exposed.exceptions.ExposedSQLException
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import webhook.consumer.models.Event
import webhook.consumer.models.Issue

/**
*  Object Used to map Issues objects in the m_issues table
*/
object Issues : IntIdTable("m_issues") {

    val issueID: Column<Int> = integer("id").uniqueIndex()
    val issueNumber: Column<Int> = integer("issue_number")
    val repositoryId: Column<Int> = integer("repository_id")
    val repositoryName: Column<String> = varchar("repository_name", 150)
    val issueSender: Column<String> = varchar("issue_sender", 100)

    // It means should have only one pair of (issueNumber, repositoryId)
    init {
        index(true, issueNumber, repositoryId)
        uniqueIndex(issueNumber, repositoryId)
    }

}

/**
 * This classe is used as a repositoruy to perform Issues saving in the database
 */
class IssueRepository {
    private val log = KotlinLogging.logger(javaClass.name)


    /**
     * This method creates a issue based on the Issue object
     */
    @Throws(ExposedSQLException::class)
    fun create(issue: Issue): Int? {
        var parentId: EntityID<Int>? = null

        log.info("Inserting issue: $issue in the database")
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

    /**
     * This methods is used to query the database looking for an Issue. It is normally used to figure it out if
     * the Issue is already saved in the database
     */
    fun findByIssueNumberAndRepositoryId(issueNumber: Int, repositoryId: Int): Issue? {
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

    /**
     *  This method is used to list all Issues in the database.
     *  WARNING: It should be used only for Test purpose
     */
    fun list(): List<Issue>{
        return transaction {
            return@transaction  Issues.selectAll().map{
                Issue(
                    issueId = it[Issues.issueID],
                    issueNumber = it[Issues.issueNumber],
                    repositoryId = it[Issues.repositoryId],
                    repositoryName = it[Issues.repositoryName],
                    issueSender = it[Issues.issueSender],
                    events = emptyList()
                )
            }
        }
    }
}