package webhook.query.api.controllers

import io.javalin.Javalin
import io.javalin.NotFoundResponse
import io.javalin.apibuilder.ApiBuilder.get
import mu.KotlinLogging
import webhook.query.utils.http.HttpStatusCode
import webhook.query.repository.EventRepository
import webhook.query.repository.IssueRepository
import webhook.query.utils.Environment
import webhook.query.utils.helpers.deserialize
import webhook.query.utils.helpers.getPaginationFromContext
import webhook.query.utils.helpers.jsonifyIssue

class IssueQueryController{
    private val issueRepository = IssueRepository()
    private val eventRepo = EventRepository()
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
                val pagination = getPaginationFromContext(ctx)
                val page = pagination.getOrDefault("page", 1)
                val count = pagination.getOrDefault("count", 15)
                val offset = ( page - 1) * count


                val issues = issueRepository.list(count, offset).map{
                    jsonifyIssue(it)
                }

                ctx.json(issues)
            }



            get("/:id" ){ ctx ->
                var issueNumber = -1
                try {
                    issueNumber = ctx.pathParam(":id").toInt()
                }catch(e: Exception){
                    throw NotFoundResponse("Not found")
                }
                val issue  = issueRepository.findByIssueNumber(issueNumber)

                if(issue != null) {
                    ctx.json(jsonifyIssue(issue))
                }else{
                    ctx.status(HttpStatusCode.NOT_FOUND)
                }

            }

            get(":id/events"){ ctx ->
                val issueNumber = ctx.pathParam(":id").toInt()

                val pagination = getPaginationFromContext(ctx)

                val issue = issueRepository.findByIssueNumber(issueNumber)

                if(issue == null){
                    ctx.status(HttpStatusCode.NOT_FOUND)
                    logger.info("Issue not found")
                }else {
                    val page = pagination.getOrDefault("page", 1)
                    val count = pagination.getOrDefault("count", 15)
                    val offset = ( page - 1) * count

                    logger.info("Querying events for ${issue}")

                    var events = eventRepo.get(issue, count, offset)
                    logger.info("Events found: ${events.size}")


                    val eventsResponse = events.map {deserialize(it.payload)}

                    ctx.header("Content-Type", "application/json; charset=utf-8")
                    ctx.result(eventsResponse.toString())
                }



            }
        }

        return app
    }

}
