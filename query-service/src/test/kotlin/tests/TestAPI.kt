package tests

import helpers.DBHelpers
import helpers.tearDownTestDB
import io.javalin.Javalin
import junit.framework.TestCase
import webhook.query.utils.http.HttpStatusCode
import webhook.query.api.controllers.IssueQueryController
import webhook.query.utils.Environment
import webhook.query.utils.helpers.initDB
import webhook.query.utils.helpers.migrateDB
import khttp.get as httpGet

class TestAPI: TestCase(){
    private val dbHelpers = DBHelpers()
    private lateinit var app: Javalin
    private val url = "http://localhost:${Environment.getServerPort()}/"
    override fun setUp(){
        initDB()
        migrateDB()
        app = IssueQueryController().init()
    }

    override fun tearDown() {
        app.stop()
        tearDownTestDB()
    }

    fun testEventList(){
        val eventCount = (1..10).random()
        val id = dbHelpers.buildEventsToSameIssue(eventCount)
        var response = httpGet(url)

        var responseData = response.jsonArray

        assertEquals(1, responseData.length())

        response = httpGet(url + id.toString() + "/events")
        responseData = response.jsonArray
        assertEquals(eventCount, responseData.length())

    }

    fun testIssueList(){
        val eventCount = (1..10).random()
        dbHelpers.buildEventsToDifferentIssues(eventCount)
        var response = httpGet(url)

        var responseData = response.jsonArray

        assertEquals(eventCount, responseData.length())
    }

    fun testSomePropertiesKeys(){
        val instance = dbHelpers.buildAndReturnEvents(1).first()
        var response = httpGet(url + instance.issueNumber.toString())

        var responseData = response.jsonObject


        // Validate keys
        assertTrue(responseData.has("issue_number"))
        assertTrue(responseData.has("repository_id"))
        assertTrue(responseData.has("repository_name"))
        assertTrue(responseData.has("issue_sender"))

        // Validate Values
        assertEquals(instance.issueNumber, responseData.get("issue_number"))
        assertEquals(instance.repositoryId,responseData.get("repository_id"))
        assertEquals(instance.repositoryName, responseData.get("repository_name"))
        assertEquals(instance.issueSender, responseData.get("issue_sender"))

    }

    fun testStatusCode(){
        var response = httpGet(url +  "invalid")
        assertEquals(HttpStatusCode.NOT_FOUND, response.statusCode)

        val id = dbHelpers.buildEventsToSameIssue(1)
        response = httpGet(url + id.toString())
        assertEquals(HttpStatusCode.OK, response.statusCode)

        response = httpGet(url + id.toString() + "/events")
        assertEquals(HttpStatusCode.OK, response.statusCode)

        response = httpGet(url + id.toString() + "/events/invalid")
        assertEquals(HttpStatusCode.NOT_FOUND, response.statusCode)

    }





}