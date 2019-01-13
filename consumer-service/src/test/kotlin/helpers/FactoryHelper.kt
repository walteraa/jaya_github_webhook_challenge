package helpers

import com.github.javafaker.Faker
import com.rabbitmq.client.ConnectionFactory
import mu.KotlinLogging
import tests.ConsumerTest
import webhook.consumer.utils.Environment
import java.io.File


class FactoryHelpers {
    /**
     * Helper to generate a payload with minimum data
     */
    private val logger = KotlinLogging.logger {  }
    private val factory = ConnectionFactory()

    val faker = Faker.instance()
    fun getMaskText(): String{


        val file = javaClass.classLoader.getResource("mask.json")

        return file.readText()
    }

    fun validPayload(): String {
        var maskString = getMaskText()

        maskString = maskString.replace("\$number_key", "number")
        maskString = maskString.replace("\$issue_number", faker.number().randomNumber().toString())
        maskString = maskString.replace("\$action", "read")
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

    fun payloadMissingIssueNumber(): String{
        var maskString = getMaskText()

        maskString = maskString.replace("\$number_key", "missing")
        maskString = maskString.replace("\$issue_number", faker.number().randomNumber().toString())
        maskString = maskString.replace("\$action", "read")
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

    fun publish(queueName: String, data: String){

        logger.info("Connecting to Rabbitmq({host: ${Environment.getBrokerHost()}...")
        factory.host = Environment.getBrokerHost()
        val connection = factory.newConnection()
        val channel = connection.createChannel()
        logger.info("Connection stablished!")

        channel.queueDeclare(queueName, false, false, true, null)
        logger.info("Publishing $data on queue $queueName")
        val message = data.toByteArray(charset("UTF-8"))
        channel.basicPublish("",queueName, null, message)
        logger.info("$data published")
        channel.close()
        connection.close()
    }
}
