package webhook.entrypoint.api.controllers

import io.javalin.Javalin
import io.javalin.apibuilder.ApiBuilder
import mu.KotlinLogging
import webhook.entrypoint.communication.Producer
import webhook.entrypoint.utils.Consts
import webhook.entrypoint.utils.Environment
import webhook.entrypoint.utils.http.HttpStatusCode
import webhook.entrypoint.utils.security.sha1

class EntrypointController(){
    private val logger  = KotlinLogging.logger(EntrypointController::class.java.name)

    fun init(): Javalin {
        logger.info("Starting server on port ${Environment.getServerPort()}")
        val app = Javalin.create().apply {

            port(Environment.getServerPort())
            exception(io.javalin.NotFoundResponse::class.java) { e, ctx ->
                ctx.status(HttpStatusCode.NOT_FOUND)
            }
            exception(Exception::class.java) { e, ctx ->
                e.printStackTrace()
                ctx.status(HttpStatusCode.INTERNAL_SERVER_ERROR)
            }
        }.start()

        app.routes{

            /**
             * Endpoint which will receives events from Github
             */
            ApiBuilder.post("/") { ctx ->

                logger.info("Environment.getGithubSecret() -> ${Environment.getGithubSecret()}")
                // Security feature provided by github hooks
                if (!Environment.getGithubSecret().isNullOrBlank()) {
                    val githubSignature = ctx.header(Consts.GITHUB_SIGNTURE)
                    logger.info("githubSignature --> $githubSignature")
                    logger.info("Checking credentials...")
                    if (!sha1(Environment.getGithubSecret()).equals(githubSignature)) {
                        ctx.status(HttpStatusCode.UNAUTHORIZED)
                        return@post
                    }
                    logger.info("Credentials approved!")
                }

                val githubEventName = ctx.header(Consts.GITHUB_EVENT)

                if (githubEventName != null && githubEventName in Environment.getBrokerQueueWhiteList()) {
                    if (githubEventName.equals(Consts.PING)) {
                        ctx.status(HttpStatusCode.OK)
                        return@post
                    }

                    val payload = ctx.body()
                    try {
                        Producer.publish(githubEventName, payload)
                        ctx.status(HttpStatusCode.ACCEPTED)
                    } catch (e: Exception) {
                        ctx.status(HttpStatusCode.INTERNAL_SERVER_ERROR)
                    }
                } else {
                    ctx.status(HttpStatusCode.BAD_REQUET)
                }

            }

        }

        return app
    }

}