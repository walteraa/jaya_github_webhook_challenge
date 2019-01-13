package helpers

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.javafaker.Faker
import dto.PayloadDTO
import mu.KotlinLogging


class TestHelpers {
    /**
     * Helper to generate a payload with minimum data
     */
    private val logger = KotlinLogging.logger {  }

    val faker = Faker.instance()
    fun getMaskText(): String{


        val file = javaClass.classLoader.getResource("mask.json")

        return file.readText()
    }

    fun validPayload(): String {
        var maskString = getMaskText()

        maskString = maskString.replace("\$number_key", "number")
        maskString = maskString.replace("\$issue_number", faker.number().randomNumber().toString())
        maskString = maskString.replace("\$action", "edit")
        maskString = maskString.replace("\$url", faker.internet().url())
        maskString = maskString.replace("\$repo_id", faker.number().randomNumber().toString())
        val repoName = "${faker.name().username()}/${faker.name().username()}"
        maskString = maskString.replace("\$repo_name", repoName)
        maskString = maskString.replace("\$owner_login", faker.name().username())
        maskString = maskString.replace("\$owner_id", faker.number().randomNumber().toString())
        maskString = maskString.replace("\$sender_login", faker.name().username())
        maskString = maskString.replace("\$issue_user_login", faker.name().username())
        return maskString.replace("\$sender_id", faker.number().randomNumber().toString())

    }

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

}
