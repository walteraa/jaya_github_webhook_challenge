package helpers

import com.github.javafaker.Faker
import com.rabbitmq.client.Channel
import com.rabbitmq.client.ConnectionFactory
import mu.KotlinLogging
import webhook.entrypoint.utils.Environment

class FactoryHelpers{
    private val logger = KotlinLogging.logger(FactoryHelpers::class.java.name)
    val faker = Faker.instance()
    private val factory = ConnectionFactory()

    fun getMaskText(): String{


        val file = javaClass.classLoader.getResource("mask.json")

        return file.readText()
    }

    fun validPayload(): String {
        var maskString = getMaskText()

        maskString = maskString.replace("\$number_key", "number")
        maskString = maskString.replace("\$issue_number", faker.number().randomNumber().toString())
        maskString = maskString.replace("\$action", "edited")
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

    fun getQueueChecker(queueName: String): Channel {
        logger.info("Connecting to Rabbitmq({host: ${Environment.getBrokerHost()}...")
        factory.host = Environment.getBrokerHost()
        val connection = factory.newConnection()
        val channel = connection.createChannel()
        logger.info("Connection stablished!")

        channel.queueDeclare(queueName, false, false, true, null)

        return channel
    }
}