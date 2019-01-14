package webhook.consumer.main

import mu.KotlinLogging
import webhook.consumer.utils.Environment
import webhook.consumer.utils.helpers.initDB
import webhook.consumer.utils.helpers.migrateDB


class Main {

    companion object {
        private val logger = KotlinLogging.logger(Main::class.java.name)
        @JvmStatic
        fun main(args: Array<String>) {
            logger.info("Starting up the database connection... ")
            initDB()
            logger.info("Starting migration... ")
            migrateDB()
            logger.info("Starting the consumer...")
            val consumer = Consumer()
            consumer.startConsuming(Environment.getQueue())

        }
    }
}