package webhook.consumer.models

/**
 *  The issue model
 */
data class Issue(
    val issueId: Int?, val issueNumber: Int, val repositoryId: Int, val repositoryName: String,
    val issueSender: String, val events: List<Event>?
)
