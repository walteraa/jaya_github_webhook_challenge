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
    private val DB_HOST = Key("DB_HOST", stringType)
    private val DB_USER = Key("DB_USER", stringType)
    private val DB_PASSWORD = Key("DB_PASSWORD", stringType)
    private val DB_SCHEMA = Key("DB_SCHEMA", stringType)
    private val DB_NAME = Key("DB_NAME", stringType)

    /**
     * It chains the variables "search" preferences
     */
    private val config = systemProperties() overriding
            EnvironmentVariables() overriding
            ConfigurationProperties.fromResource("applicationTest.conf")


    fun getBrokerHost(): String = config[BROKER_HOST]
    fun getQueue(): String = config[QUEUE_NAME]
    fun getSchema(): String = config[DB_SCHEMA]
    fun getDBUser(): String = config[DB_USER]
    fun getDBUserPassword(): String = config[DB_PASSWORD]
    fun getDBHost(): String = config[DB_HOST]
    fun getDBName(): String = config[DB_NAME]
    fun getDBUrl(): String = "jdbc:postgresql://${getDBHost()}:5432/${getDBName()}?currentSchema=${getSchema()}"


}