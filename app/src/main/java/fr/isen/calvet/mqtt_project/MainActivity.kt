package fr.isen.calvet.mqtt_project

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
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
import java.util.concurrent.CompletableFuture


class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding
    private lateinit var client : Mqtt3AsyncClient
    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val topicLed = "isen09/led"
        val topicTemp = "isen09/temp"
        val topicButton = "isen09/button"
        val topicGetTemp = "isen09/getTemp"

        var ledBlueOn = false
        var ledGreenOn = false
        var ledRedOn = false

        val led1on = "{\"id\": 1,\"state\": 1}"
        val led1off = "{\"id\": 1,\"state\": 0}"
        val led2on = "{\"id\": 2,\"state\": 1 }"
        val led2off = "{\"id\": 2,\"state\": 0 }"
        val led3on = "{\"id\": 3,\"state\": 1 }"
        val led3off = "{\"id\": 3,\"state\": 0 }"

        hide()

        clientBuild()
        //connectHost()
        connectClient().thenAccept {
            if(it) {
                show()
                subscribe(topicButton)
                subscribe(topicTemp)

                binding.led1.setOnClickListener {
                    if(!ledBlueOn) {
                        publish(topicLed, led1on)
                        binding.led1.setColorFilter(Color.BLUE)
                    } else {
                        publish(topicLed, led1off)
                        binding.led1.clearColorFilter()
                    }
                    ledBlueOn = !ledBlueOn
                }
                binding.led2.setOnClickListener {
                    if(!ledGreenOn) {
                        publish(topicLed, led2on)
                        binding.led2.setColorFilter(Color.GREEN)
                    } else {
                        publish(topicLed, led2off)
                        binding.led2.clearColorFilter()
                    }
                    ledGreenOn = !ledGreenOn
                }
                binding.led3.setOnClickListener {
                    if(!ledRedOn) {
                        publish(topicLed, led3on)
                        binding.led3.setColorFilter(Color.RED)
                    } else {
                        publish(topicLed, led3off)
                        binding.led3.clearColorFilter()
                    }
                    ledRedOn = !ledRedOn
                }

            }
        }

    }

    //Show connect tools in the UI
    fun show() {
        runOnUiThread {
            binding.group.visibility = View.VISIBLE
            binding.status.visibility = View.GONE
        }
    }

    //Hide connect tools in the UI
    fun hide() {
        runOnUiThread {
            binding.group.visibility = View.GONE
            binding.status.visibility = View.VISIBLE
        }
    }
    private fun clientBuild(){
        /*var clientBuild = Mqtt3Client.builder()
            .identifier(UUID.randomUUID().toString())
            .serverHost("broker.hivemq.com")
            .build()*/
        val clientBuilder = MqttClient.builder()
            .identifier(UUID.randomUUID().toString())
            .serverHost("broker.mqttdashboard.com")

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
    private fun connectClient(): CompletableFuture<Boolean> {
        var success = CompletableFuture<Boolean>()
        client.connectWith()
            /*.simpleAuth()
            .username("androidApp")
            .password("1234".toByteArray())
            .applySimpleAuth()*/
            .send()
            .whenComplete { connAck: Mqtt3ConnAck?, throwable: Throwable? ->
                if (throwable != null) {
                    Log.d("connect", "failure")
                    success.complete(false)
                } else {
                    Log.d("connect", "success")
                    success.complete(true)
                }
            }
        Log.d("success value", "$success")
        return success
    }
    private fun subscribe(topic : String){
        client.subscribeWith()
            .topicFilter(topic)
            .callback { publish: Mqtt3Publish? -> }
            .send()
            .whenComplete { subAck: Mqtt3SubAck?, throwable: Throwable? ->
                if (throwable != null) {
                    Log.d("subscribe", "failure")
                } else {
                    Log.d("subscribe", "success")
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
                    Log.d("publish", "failure")
                } else {
                    Log.d("publish", "success")
                }
            }
    }
}