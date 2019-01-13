package webhook.consumer.models

import org.joda.time.DateTime

data class Event(
    val eventId: Int, val parentEntity: String, val parentId: Int, val payload: String,
    val createdAt: DateTime, val action: String, val sender: String
)