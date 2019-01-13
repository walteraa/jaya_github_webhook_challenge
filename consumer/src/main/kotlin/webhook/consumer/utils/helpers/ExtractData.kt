package webhook.consumer.utils.helpers

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import mu.KotlinLogging
import webhook.consumer.dto.PayloadDTO

private val logger = KotlinLogging.logger("ExtractData")
fun extractPayloadData(json: String): PayloadDTO {
    val mapper = ObjectMapper()

    var data: JsonNode

    data = mapper.readTree(json)
    val action = data.get("action").asText()
    val issueNumber = data.path("issue").get("number").asInt()
    val repositoryId = data.path("repository").get("id").asInt()
    val repositoryName = data.path("repository").get("full_name").asText()
    val issueSender = data.path("issue").path("user").get("login").asText()
    val eventSender = data.path("sender").get("login").asText()

    return PayloadDTO(action, issueNumber, repositoryId, repositoryName, issueSender, json, eventSender)
}