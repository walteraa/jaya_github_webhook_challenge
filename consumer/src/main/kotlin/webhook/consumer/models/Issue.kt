package webhook.consumer.models

data class Issue(
    val issueId: Int?, val issueNumber: Int, val repositoryId: Int, val repositoryName: String,
    val issueSender: String, val events: List<Event>?
)