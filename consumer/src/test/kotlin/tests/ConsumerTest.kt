package tests

import com.rabbitmq.client.Channel
import helpers.tearDownTestDB
import junit.framework.TestCase
import webhook.consumer.main.Consumer
import webhook.consumer.repository.IssueRepository
import webhook.consumer.utils.Environment
import webhook.consumer.utils.helpers.extractPayloadData
import helpers.FactoryHelpers
import webhook.consumer.utils.helpers.initDB
import webhook.consumer.utils.helpers.migrateDB

class ConsumerTest: TestCase(){

 //   companion object {
        private lateinit var channel : Channel
        private val issueRepo = IssueRepository()
        private val helpers = FactoryHelpers()

        override fun setUp(){
            initDB()
            migrateDB()
            val con = Consumer()
            channel = con.startConsuming(Environment.getQueue())
        }


        override fun tearDown(){
            channel.close()
            tearDownTestDB()
        }

        fun testReceivingValidPayload(){
            val payload = helpers.validPayload()

            helpers.publish(Environment.getQueue(), payload)

            // Sleeping added to await the insert process, which is async
            Thread.sleep(5000)

            val issueDtoSent = extractPayloadData(payload)
            val issue = issueRepo.findByIssueNumberAndRepositoryId(issueDtoSent.issueNumber, issueDtoSent.repositoryId)

            assertNotNull("Issue is null, it probably was not saved", issue)

            assertEquals("Saved with different issueNumber identifier",
                issueDtoSent.issueNumber, issue?.issueNumber)

            assertEquals("",issueDtoSent.issueSender, issue?.issueSender )

            assertEquals("",issueDtoSent.repositoryName, issue?.repositoryName )

            assertEquals("",issueDtoSent.repositoryId, issue?.repositoryId )

            assertEquals("Correct event List", 1, issue?.events?.size)
        }


        fun testPayloadMissingIssueNumber(){
            val payload = helpers.payloadMissingIssueNumber()

            helpers.publish(Environment.getQueue(), payload)

            Thread.sleep(5000)
            val issues = issueRepo.list()

            assertEquals("It should not save the issue", 0, issues.size)

        }


}