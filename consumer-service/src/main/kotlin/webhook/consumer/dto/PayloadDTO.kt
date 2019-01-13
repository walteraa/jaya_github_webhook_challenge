package webhook.consumer.dto


data class PayloadDTO(
    val action: String, val issueNumber: Int, val repositoryId: Int, val repositoryName: String,
    val issueSender: String, val fullPayload: String, val eventSender: String
)