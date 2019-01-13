package webhook.entrypoint.utils

/**
 * General constants
 */
object Consts{
    const val GITHUB_SIGNTURE = "X-Hub-Signature"
    const val GITHUB_EVENT = "X-GitHub-Event"
    const val PING = "ping"
    const val CONTENT_HEADER_KEY = "Content-Type"
    const val GITHUB_SECRET = "GITHUB_SECRET"
}