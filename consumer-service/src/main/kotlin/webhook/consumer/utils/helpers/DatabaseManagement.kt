package webhook.consumer.utils.helpers

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import mu.KotlinLogging
import org.flywaydb.core.Flyway
import org.jetbrains.exposed.sql.Database
import webhook.consumer.utils.Environment


fun migrateDB() {
    val flyway = Flyway()
    val config = Environment.getDBUrl()
    val schema = Environment.getSchema()
    flyway.setDataSource(config, Environment.getDBUser(), Environment.getDBUserPassword())
    flyway.setSchemas(schema)
    flyway.setLocations("migrations")
    flyway.migrate()
}

fun initDB() {
    val logger = KotlinLogging.logger("initDB")
    val config = HikariConfig()
    config.username = Environment.getDBUser()
    config.password = Environment.getDBUserPassword()
    config.jdbcUrl = Environment.getDBUrl()
    logger.info("Using config jdbcUrl:${config.jdbcUrl}")
    val ds = HikariDataSource(config)
    Database.connect(ds)
}