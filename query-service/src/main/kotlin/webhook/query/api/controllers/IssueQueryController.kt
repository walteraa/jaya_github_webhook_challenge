package webhook.query.api.controllers

import io.javalin.Javalin
import io.javalin.apibuilder.ApiBuilder.get
import mu.KotlinLogging
import webhook.query.utils.http.HttpStatusCode
import webhook.query.utils.Environment

class IssueQueryController{
    private val logger = KotlinLogging.logger(IssueQueryController::class.java.name)

    fun init(): Javalin {
        val app = Javalin.create().apply {

            port(Environment.getServerPort())

            exception(io.javalin.NotFoundResponse::class.java) { e, ctx ->
                ctx.status(HttpStatusCode.NOT_FOUND)
                logger.info(e.message)
            }

            exception(Exception::class.java) { e, ctx ->
                logger.error(e.message)
                ctx.status(HttpStatusCode.INTERNAL_SERVER_ERROR)
            }
        }.start()

        app.routes{
            get("/"){ ctx ->
                ctx.status(HttpStatusCode.OK)
            }



            get("/:id" ){ ctx ->
                ctx.status(HttpStatusCode.OK)

            }

            get(":id/events"){ ctx ->
                ctx.status(HttpStatusCode.OK)

            }

        }

        return app
    }

}
