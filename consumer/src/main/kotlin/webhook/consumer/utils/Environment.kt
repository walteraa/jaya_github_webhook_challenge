package webhook.consumer.utils

import com.natpryce.konfig.*
import com.natpryce.konfig.ConfigurationProperties.Companion.systemProperties

/**
 * Object which will be used to load Environment variables
 */
object Environment {
    /**
     * Environment keys
     */
    private val BROKER_HOST = Key("BROKER_HOST", stringType)
    private val QUEUE_NAME = Key("QUEUE_NAME", stringType)

    /**
     * It chains the variables "search" preferences
     */
    private val config = systemProperties() overriding
            EnvironmentVariables() overriding
            ConfigurationProperties.fromResource("application.conf")


    fun getBrokerHost(): String = config[BROKER_HOST]
    fun getQueue(): String = config[QUEUE_NAME]


}