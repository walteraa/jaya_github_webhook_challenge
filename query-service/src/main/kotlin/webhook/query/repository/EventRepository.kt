package webhook.query.repository


import org.jetbrains.exposed.dao.IntIdTable
import org.jetbrains.exposed.exceptions.ExposedSQLException
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import webhook.consumer.models.Issue
import webhook.consumer.models.Event

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
 *  Class used as repository of Event objects. Here are all methods which the system uses
 *  to access Event objects.
 */
class EventRepository {

    /**
     * Method used to search and return all events of a Issue. It works in a pagination mode
     * @issue: Issue object to search the related events
     * @limit: The maximum list size which will be returned
     * @offset: From where the index starts, this param together
     */
    @Throws(ExposedSQLException::class)
    fun  get(issue: Issue, limit: Int, offset: Int): List<Event>{
        return transaction {
            //Defining defaults
            val issueId = issue?.issueId ?: -1

            return@transaction Events.select { Events.parentId eq issueId }.limit(limit, offset = offset).map{
                Event(
                    eventId = it[Events.eventId],
                    parentId = it[Events.parentId],
                    parentEntity = it[Events.parentEntity],
                    createdAt = it[Events.createdAt],
                    payload = it[Events.payload],
                    action = it[Events.action],
                    sender = it[Events.sender]
                )
            }


        }
    }
}