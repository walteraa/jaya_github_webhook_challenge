package webhook.consumer.repository


import mu.KotlinLogging
import org.jetbrains.exposed.dao.IntIdTable
import org.jetbrains.exposed.exceptions.ExposedSQLException
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import webhook.consumer.dto.PayloadDTO
import webhook.consumer.models.Issue


/**
 *  Object class Used to map Event objects in the m_events database table
 */
object Events : IntIdTable("m_events") {
    val eventId: Column<Int> = integer("id").uniqueIndex()
    val parentId: Column<Int> = integer("parent_id")
    val parentEntity: Column<String> = varchar("parent_entity", 50)
    val payload: Column<String> = text("payload")
    val createdAt: Column<DateTime> = datetime("created_at").default(DateTime(DateTimeZone.UTC))
    val action: Column<String> = varchar("event_action", 25)
    val sender: Column<String> = varchar("sender", 100)
}

/**
 * This class is used as a repository to perform Event saving in the database
 *
 */
class EventRepository {
    private val log = KotlinLogging.logger(javaClass.name)


    /**
     * This method save an event in the database
     * @payload; The DTO which represents the Instance to be saved
     */
    @Throws(ExposedSQLException::class)
    fun create(payload: PayloadDTO) {
        val issueRepository = IssueRepository()
        val issue = issueRepository.findByIssueNumberAndRepositoryId(payload.issueNumber, payload.repositoryId)

        if (issue == null) {

            val issueObject = Issue(
                null, payload.issueNumber, payload.repositoryId, payload.repositoryName,
                payload.issueSender, null
            )

            log.info("Creating Issue")
            val issueId = issueRepository.create(issueObject) ?: -1

            log.info("Inserting first event of the new Issue on database")
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
            log.info("Inserting Event on database")
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
}