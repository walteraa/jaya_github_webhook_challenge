package webhook.entrypoint.communication

import com.rabbitmq.client.ConnectionFactory
import mu.KotlinLogging
import webhook.entrypoint.utils.Environment

/**
 * Object which wraps communication between services, it uses RabbitMQ as message broker
 */
object Producer{
    private val logger = KotlinLogging.logger {  }
    private val factory = ConnectionFactory()

    /**
     * Method to publish a message in the broker event queue
     */
    fun publish(queueName: String, data: String){

        logger.info("Connecting to Rabbitmq({host: ${Environment.getBrokerHost()}...")
        factory.host = Environment.getBrokerHost()
        val connection = factory.newConnection()
        val channel = connection.createChannel()
        logger.info("Connection stablished!")

        channel.queueDeclare(queueName, false, false, true, null)
        logger.info("Publishing $data on queue $queueName")
        val message = data.toByteArray(charset("UTF-8"))
        channel.basicPublish("",queueName, null, message)
        logger.info("$data published")
        channel.close()
        connection.close()
    }
}