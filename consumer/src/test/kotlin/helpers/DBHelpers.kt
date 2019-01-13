package helpers

import org.flywaydb.core.Flyway
import webhook.consumer.utils.Environment

fun tearDownTestDB(){
    val flyway = Flyway()
    val config = Environment.getDBUrl()
    flyway.setDataSource(config, Environment.getDBUser(), Environment.getDBUserPassword())
    flyway.clean()
}