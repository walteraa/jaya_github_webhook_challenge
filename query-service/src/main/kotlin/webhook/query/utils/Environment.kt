package webhook.query.utils

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



    /**
     * It chains the variables "search" preferences
     */
    private val config = systemProperties() overriding
                EnvironmentVariables() overriding
                ConfigurationProperties.fromResource("applicationTest.conf")

    fun getServerPort(): Int = config[SERVER_PORT]

}