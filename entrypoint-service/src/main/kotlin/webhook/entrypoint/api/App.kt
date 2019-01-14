package webhook.entrypoint.api


import mu.KotlinLogging
import webhook.entrypoint.api.controllers.EntrypointController

/**
 * Server main class used to startup the Controller
 */

class Main {

    companion object {
        private  val logger = KotlinLogging.logger(Main::class.java.name)
        @JvmStatic
        fun main(args: Array<String>) {

            logger.info("Initializing web app...")
            val  app = EntrypointController()
            app.init()
            logger.info("Web app initialized!!")

        }
    }
}