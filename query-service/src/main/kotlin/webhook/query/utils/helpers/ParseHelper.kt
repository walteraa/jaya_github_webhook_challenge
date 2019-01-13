package webhook.query.utils.helpers

import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import io.javalin.Context
import webhook.consumer.models.Issue

fun deserialize(payload: String): JsonObject {
    val parser = Gson()
    val element = parser.fromJson<JsonElement>(payload, JsonElement::class.java)
    return element.asJsonObject
}

fun jsonifyIssue(issue: Issue): Any{
    return object{
        val issue_number = issue.issueNumber
        val repository_id = issue.repositoryId
        val repository_name = issue.repositoryName
        val issue_sender = issue.issueSender
    }
}

fun getPaginationFromContext(ctx: Context ): Map<String,Int>{
    var page = 1
    var count = 15
    ctx.queryParam("page", "1")?.let{
        try{
            if(it.toInt() > 0 ){
                page = it.toInt()
            }
        }catch(e: Exception){}
    }
    ctx.queryParam("count", "15")?.let {
        try{
            if(it.toInt() > 0 ){
                count = it.toInt()
            }
        }catch(e: Exception){}
    }

    return hashMapOf("page" to page, "count" to count)
}
