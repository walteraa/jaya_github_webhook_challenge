package webhook.query.repository

import mu.KotlinLogging
import org.jetbrains.exposed.dao.IntIdTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
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
 *  Class used as repository of Event objects. Here are all methods which the system uses
 *  to access Event objects.
 */
class IssueRepository {
    private val log = KotlinLogging.logger(javaClass.name)


    /**
     *  Search a Issue by issue Number
     *  @issueNumber: Search key to find issue by its number. Returns null if not exists
     *
     */
    fun findByIssueNumber(issueNumber: Int): Issue? {
        return transaction {
            return@transaction Issues.select { Issues.issueNumber eq issueNumber }.limit(1).map {
                    Issue(
                        issueId = it[Issues.issueID],
                        issueNumber = it[Issues.issueNumber],
                        repositoryId = it[Issues.repositoryId],
                        repositoryName = it[Issues.repositoryName],
                        issueSender = it[Issues.issueSender],
                        events = null
                    )
                }.firstOrNull()
        }

    }

    /**
     * Method used to fetch and return all Issues from DB. It works in a pagination mode.
     * @limit: The maximum list size which will be returned
     * @offset: From where the index starts, this param together
     */
    fun list(limit: Int, offset: Int): List<Issue>{
        return transaction {
            return@transaction Issues.selectAll().limit(limit, offset).map {
                    Issue(
                        issueId = it[Issues.issueID],
                        issueNumber = it[Issues.issueNumber],
                        repositoryId = it[Issues.repositoryId],
                        repositoryName = it[Issues.repositoryName],
                        issueSender = it[Issues.issueSender],
                        events = null
                    )
            }
        }
    }
}