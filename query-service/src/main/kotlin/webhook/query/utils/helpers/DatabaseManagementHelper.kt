package webhook.query.utils.helpers

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import mu.KotlinLogging
import org.flywaydb.core.Flyway
import org.jetbrains.exposed.sql.Database
import webhook.query.utils.Environment

/**
 *  Here will be added all helper functions
 */


/**
 * Helper function used to startup the database access
 */
fun initDB() {
    val logger = KotlinLogging.logger("initDB")
    val config = HikariConfig()
    config.username = Environment.getDBUser()
    config.password = Environment.getDBUserPassword()
    config.jdbcUrl = Environment.getDBUrl()
    logger.info("Using config jdbcUrl:${config.jdbcUrl}")
    val ds = HikariDataSource(config)
    Database.connect(ds)
    logger.info("Database initialized!")
}

/**
 * Used to migrate database: It should be run only in a test environment!!
 */
fun migrateDB() {
    val flyway = Flyway()
    val config = Environment.getDBUrl()
    val schema = Environment.getSchema()
    flyway.setDataSource(config, Environment.getDBUser(), Environment.getDBUserPassword())
    flyway.setSchemas(schema)
    flyway.setLocations("migrations")
    flyway.migrate()
}