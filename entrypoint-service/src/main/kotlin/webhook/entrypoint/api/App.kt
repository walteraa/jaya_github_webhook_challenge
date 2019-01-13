package webhook.entrypoint.api

import io.javalin.apibuilder.ApiBuilder.*
import io.javalin.Javalin
import mu.KotlinLogging
import webhook.entrypoint.utils.Consts
import webhook.entrypoint.utils.Environment
import webhook.entrypoint.utils.http.HttpStatusCode
import webhook.entrypoint.utils.security.sha1

/**
 * Server main method
 */
fun main(args: Array<String>) {
    val logger = KotlinLogging.logger("api.App")

    logger.info("Starting server on port ${Environment.getServerPort()}")
    val app = Javalin.create().apply {
        exception(io.javalin.NotFoundResponse::class.java) { e, ctx ->
            ctx.status(HttpStatusCode.NOT_FOUND)
        }
        exception(Exception::class.java) { e, ctx ->
            e.printStackTrace()
            ctx.status(HttpStatusCode.INTERNAL_SERVER_ERROR)
        }
    }.start(Environment.getServerPort())

    app.routes{

        /**
         * Endpoint which will receives events from Github
         */
        post("/") { ctx ->
            // Security feature provided by github hooks
            if(Environment.getGithubSecret() != null){
                val githubSignature = ctx.header(Consts.GITHUB_SIGNTURE)
                if (!sha1(Environment.getGithubSecret()).equals(githubSignature)){
                    ctx.status(HttpStatusCode.UNAUTHORIZED)
                }
            }

            val githubEventName = ctx.header(Consts.GITHUB_EVENT)

            if(githubEventName != null && githubEventName == "issues"){
                val payload = ctx.body()
                try {
                    // TODO: Implement the the event processing
                    logger.info("Data received: " + payload)
                    ctx.status(HttpStatusCode.ACCEPTED)
                }catch(e: Exception){
                    ctx.status(HttpStatusCode.INTERNAL_SERVER_ERROR)
                }
            }else{
                ctx.status(HttpStatusCode.BAD_REQUET)
            }

        }

    }
}