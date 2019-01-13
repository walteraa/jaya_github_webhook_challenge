package webhook.consumer.main

import mu.KotlinLogging
import webhook.consumer.utils.Environment


class Main {

    object companion {
        private val logger = KotlinLogging.logger(Main::class.java.name)
        @JvmStatic
        fun main(args: Array<String>) {
            logger.info("Starting the consumer...")
            val consumer = Consumer()
            consumer.startConsuming(Environment.getQueue())

        }
    }
}