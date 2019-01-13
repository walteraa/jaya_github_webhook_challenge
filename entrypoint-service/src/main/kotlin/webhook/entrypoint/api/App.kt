package webhook.entrypoint.api

import io.javalin.apibuilder.ApiBuilder.*
import io.javalin.Javalin
import mu.KotlinLogging
import webhook.entrypoint.api.controllers.EntrypointController
import webhook.entrypoint.communication.Producer
import webhook.entrypoint.utils.Consts
import webhook.entrypoint.utils.Environment
import webhook.entrypoint.utils.http.HttpStatusCode
import webhook.entrypoint.utils.security.sha1

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