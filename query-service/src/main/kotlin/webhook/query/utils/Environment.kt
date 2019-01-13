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

    fun getServerPort(): Int = config[SERVER_PORT]
    fun getSchema(): String = config[DB_SCHEMA]
    fun getDBUser(): String = config[DB_USER]
    fun getDBUserPassword(): String = config[DB_PASSWORD]
    private fun getDBHost(): String = config[DB_HOST]
    private fun getDBName(): String = config[DB_NAME]
    fun getDBUrl(): String = "jdbc:postgresql://${getDBHost()}:5432/${getDBName()}?currentSchema=${getSchema()}"
}