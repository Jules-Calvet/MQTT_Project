package fr.isen.calvet.mqtt_project

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.hivemq.client.mqtt.MqttClient
import com.hivemq.client.mqtt.MqttClientBuilder
import com.hivemq.client.mqtt.mqtt3.Mqtt3AsyncClient
import com.hivemq.client.mqtt.mqtt3.Mqtt3Client
import com.hivemq.client.mqtt.mqtt3.message.connect.connack.Mqtt3ConnAck
import com.hivemq.client.mqtt.mqtt3.message.publish.Mqtt3Publish
import com.hivemq.client.mqtt.mqtt3.message.subscribe.suback.Mqtt3SubAck
import fr.isen.calvet.mqtt_project.databinding.ActivityMainBinding
import java.util.*


class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding
    private lateinit var client : Mqtt3AsyncClient
    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val topicLed = "isen12/led"
        val topicTemp = "isen12/temp"
        val topicButton = "isen12/button"
        val topicGetTemp = "isen12/getTemp"

        val led1on = "{\\n\" + \" \\\"id\\\": 1,\\n\" + \" \\\"state\\\": 0\\n\" + \"}"
        val led1off = "{\\n\" + \" \\\"id\\\": 1,\\n\" + \" \\\"state\\\": 0\\n\" + \"}"

        clientBuild()
        connectHost()
        connectClient()

        subscribe(topicButton)
        subscribe(topicTemp)

        publish(topicLed,led1on)
    }
    private fun clientBuild(){
        /*var clientBuild = Mqtt3Client.builder()
            .identifier(UUID.randomUUID().toString())
            .serverHost("broker.hivemq.com")
            .build()*/
        val clientBuilder = MqttClient.builder()
            .identifier(UUID.randomUUID().toString())
            .serverHost("broker.hivemq.com")

        client = clientBuilder.useMqttVersion3().buildAsync()
    }
    private fun connectHost(){
        client = MqttClient.builder()
            .useMqttVersion3()
            .identifier("my-mqtt-client-id")
            .serverHost("broker.mqttdashboard.com")
            .serverPort(1883)
            .useSslWithDefaultConfig()
            .buildAsync()
    }
    private fun connectClient(){
        client.connectWith()
            .simpleAuth()
            .username("androidApp")
            .password("1234".toByteArray())
            .applySimpleAuth()
            .send()
            .whenComplete { connAck: Mqtt3ConnAck?, throwable: Throwable? ->
                if (throwable != null) {
                    // handle failure
                } else {
                    // setup subscribes or start publishing
                }
            }
    }
    private fun subscribe(topic : String){
        client.subscribeWith()
            .topicFilter(topic)
            .callback { publish: Mqtt3Publish? -> }
            .send()
            .whenComplete { subAck: Mqtt3SubAck?, throwable: Throwable? ->
                if (throwable != null) {
                    // Handle failure to subscribe
                } else {
                    // Handle successful subscription, e.g. logging or incrementing a metric
                }
            }
    }
    private fun publish(topic : String , message : String){
        client.publishWith()
            .topic(topic)
            .payload(message.toByteArray())
            .send()
            .whenComplete { publish: Mqtt3Publish?, throwable: Throwable? ->
                if (throwable != null) {
                    // handle failure to publish
                } else {
                    // handle successful publish, e.g. logging or incrementing a metric
                }
            }
    }
}