package webhook.entrypoint.utils

import com.natpryce.konfig.*
import com.natpryce.konfig.ConfigurationProperties.Companion.systemProperties

/**
 * Object which will be used to load Environment variables
 */
object Environment{
    /**
     * Environment keys
     */
    private val SERVER_PORT = Key("SERVER_PORT", intType)
    private val GITHUB_SECRET = Key("GITHUB_SECRET", stringType)


    /**
     * It chains the variables "search" preferences
     */
    private val config = systemProperties() overriding
                EnvironmentVariables() overriding
                ConfigurationProperties.fromResource("application.conf")

    fun getServerPort(): Int = config[SERVER_PORT]
    fun getGithubSecret(): String? = config.getOrNull(GITHUB_SECRET)
}