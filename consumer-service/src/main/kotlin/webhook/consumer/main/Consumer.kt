package webhook.consumer.main

import com.rabbitmq.client.*
import mu.KotlinLogging
import webhook.consumer.utils.Environment
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import webhook.consumer.repository.EventRepository
import webhook.consumer.utils.helpers.extractPayloadData


class Consumer {

        private val factory = ConnectionFactory()
        private val logger = KotlinLogging.logger(Consumer::class.java.name)
        private val eventRepository = EventRepository()

        fun startConsuming(queueName: String): Channel {


            logger.info("Connecting to Rabbitmq({host: ${Environment.getBrokerHost()}}) ...")
            factory.host = Environment.getBrokerHost()
            val connection = factory.newConnection()
            val channel = connection.createChannel()
            logger.info("Connection stablished!")

            channel.queueDeclare(queueName, false, false, true, null)
            logger.info(" [*] Waiting for messages")

            val consumer = object : DefaultConsumer(channel) {
                override fun handleDelivery(
                    consumerTag: String,
                    envelope: Envelope,
                    properties: AMQP.BasicProperties,
                    body: ByteArray
                ) {
                    val message = String(body, charset("UTF-8"))
                    logger.info(" Received data:'$message'")

                    GlobalScope.launch {
                        try {
                            val toSave = extractPayloadData(message)
                            eventRepository.create(toSave)
                        }catch(e: Exception){
                            logger.info("Invalid Payload")
                            logger.error(e.message)
                        }
                    }
                }
            }
            channel.basicConsume(queueName, true, consumer)
            return channel
        }

}

