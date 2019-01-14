package tests

import com.github.javafaker.Faker
import helpers.FactoryHelpers
import io.javalin.Javalin
import junit.framework.TestCase
import mu.KotlinLogging
import webhook.entrypoint.api.controllers.EntrypointController
import webhook.entrypoint.utils.Consts
import webhook.entrypoint.utils.Environment
import webhook.entrypoint.utils.http.HttpStatusCode
import webhook.entrypoint.utils.security.calculateSignature
import khttp.post as httpPost

class TestAPI: TestCase(){
    private val factory = FactoryHelpers()
    private lateinit var app: Javalin
    private val url = "http://localhost:${Environment.getServerPort()}/"
    private val logger  = KotlinLogging.logger(TestAPI::class.java.name)

    override fun setUp(){
        app = EntrypointController().init()
    }

    override fun tearDown() {
        app.stop()
    }

    fun testPing(){
        val payload = factory.validPayload()
        val headers = mapOf(Consts.GITHUB_EVENT to "ping", Consts.CONTENT_HEADER_KEY to "application/json")
        val response = httpPost(url=url, headers=headers, data=payload)
        assertEquals(HttpStatusCode.OK, response.statusCode)
    }

    fun testValidHeaderAddingMessageInQueue(){
        val eventType = "test"
        val payload = factory.validPayload()
        val queueChannel = factory.getQueueChecker(eventType)

        val msgCount = (1..10).random()
        for (i in 1..msgCount) {
            val headers = mapOf(Consts.GITHUB_EVENT to eventType, Consts.CONTENT_HEADER_KEY to "application/json")
            val response = httpPost(url = url, headers = headers, data = payload)

            assertEquals(HttpStatusCode.ACCEPTED, response.statusCode)
        }

        assertEquals(msgCount, queueChannel.messageCount(eventType).toInt())
        queueChannel.queuePurge(eventType)

    }

    fun testGithubSecuritySecretValidRequest(){
        val secret = Faker().name().username()
        val eventType = "test"
        System.setProperty(Consts.GITHUB_SECRET, secret)

        assertEquals(secret, Environment.getGithubSecret())

        val payload = factory.validPayload()
        val signature = "sha1=${calculateSignature(payload, secret)}"
        val headers = mapOf(Consts.GITHUB_EVENT to eventType, Consts.CONTENT_HEADER_KEY to "application/json",
                            Consts.GITHUB_SIGNTURE to signature)
        val queueChannel = factory.getQueueChecker(eventType)
        val response = httpPost(url = url, headers = headers, data = payload)

        assertEquals(HttpStatusCode.ACCEPTED, response.statusCode)
        System.setProperty(Consts.GITHUB_SECRET, "")

        assertEquals("", Environment.getGithubSecret())
        queueChannel.queuePurge(eventType)
    }

    fun testGithubSecuritySecretInvalidRequest(){
        val secretFromGithub = Faker().internet().password(6, 10)
        val secretConfigured = Faker().internet().password(6, 10)
        val eventType = "test"
        val payload = factory.validPayload()



        // Testing secret not configured in Github, it should not authorize
        System.setProperty(Consts.GITHUB_SECRET, secretConfigured)
        assertEquals(secretConfigured, Environment.getGithubSecret())

        var headers = mapOf(Consts.GITHUB_EVENT to eventType, Consts.CONTENT_HEADER_KEY to "application/json")
        var response = httpPost(url = url, headers = headers, data = payload)
        assertEquals(HttpStatusCode.UNAUTHORIZED, response.statusCode)

        val signature = "sha1=${calculateSignature(payload, secretFromGithub)}"
        // Testing wrong secrets configurations
        headers = mapOf(Consts.GITHUB_EVENT to eventType, Consts.CONTENT_HEADER_KEY to "application/json",
            Consts.GITHUB_SIGNTURE to signature)
        val queueChannel = factory.getQueueChecker(eventType)
        response = httpPost(url = url, headers = headers, data = payload)

        assertEquals(HttpStatusCode.UNAUTHORIZED, response.statusCode)
        System.setProperty(Consts.GITHUB_SECRET, "")

        assertEquals("", Environment.getGithubSecret())
        queueChannel.queuePurge(eventType)
    }
}