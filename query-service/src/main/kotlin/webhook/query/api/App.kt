package webhook.query.api

import mu.KotlinLogging
import webhook.query.api.controllers.IssueQueryController
import webhook.query.utils.helpers.initDB

/**
 * Main class used to startup the Issue controller and init the database.
 */
class Main {

    companion object {
        private  val logger = KotlinLogging.logger(Main::class.java.name)
        @JvmStatic
        fun main(args: Array<String>) {
            logger.info("Initializing database...")
            initDB()

            logger.info("Initializing web app...")
            val  app = IssueQueryController()
            app.init()
            logger.info("Web app initialized!!")

        }
    }
}