package fr.isen.calvet.mqtt_project

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.hivemq.client.mqtt.MqttClient
import com.hivemq.client.mqtt.mqtt3.message.connect.connack.Mqtt3ConnAck
import com.hivemq.client.mqtt.mqtt3.message.publish.Mqtt3Publish
import com.hivemq.client.mqtt.mqtt3.message.subscribe.suback.Mqtt3SubAck
import fr.isen.calvet.mqtt_project.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding

    /*
    lateinit var mqttAndroidClient: MqttAndroidClient
    private lateinit var context: Context
    val serverUri = "broker.mqttdashboard.com"
    val clientId: String = MqttClient.generateClientId()

     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var client = MqttClient.builder()
            .useMqttVersion3()
            .identifier("my-mqtt-client-id")
            .serverHost("localhost")
            .serverPort(1883)
            .useSslWithDefaultConfig()
            .buildAsync()

        client.connectWith()
            .simpleAuth()
            .username("my-user")
            .password("my-password".toByteArray())
            .applySimpleAuth()
            .send()
            .whenComplete { connAck: Mqtt3ConnAck?, throwable: Throwable? ->
                if (throwable != null) {
                    // handle failure
                } else {
                    // setup subscribes or start publishing
                }
            }
        client.subscribeWith()
            .topicFilter("the/topic")
            .callback { publish: Mqtt3Publish? -> }
            .send()
            .whenComplete { subAck: Mqtt3SubAck?, throwable: Throwable? ->
                if (throwable != null) {
                    // Handle failure to subscribe
                } else {
                    // Handle successful subscription, e.g. logging or incrementing a metric
                }
            }
        client.publishWith()
            .topic("the/topic")
            .payload("hello world".toByteArray())
            .send()
            .whenComplete { publish: Mqtt3Publish?, throwable: Throwable? ->
                if (throwable != null) {
                    // handle failure to publish
                } else {
                    // handle successful publish, e.g. logging or incrementing a metric
                }
            }
        //init(serverUri,clientId)
    }










    /*
    fun setCallback(callback: MqttCallbackExtended?) {
        mqttAndroidClient.setCallback(callback)
    }

    init/*(serverUri : String, clientId: String)*/ {
        mqttAndroidClient = MqttAndroidClient(this, serverUri, clientId)
        mqttAndroidClient.setCallback(object : MqttCallbackExtended {
            override fun connectionLost(cause: Throwable?) {
                Log.d("init", "MQTT connection lost")
            }

            override fun messageArrived(topic: String?, message: MqttMessage?) {
                Log.w("init", message.toString())
            }

            override fun deliveryComplete(token: IMqttDeliveryToken?) {
                Log.d("init", "message delivery complete")
            }

            override fun connectComplete(reconnect: Boolean, serverURI: String?) {
                serverURI?.let {
                    Log.w("init", it)
                }
            }
        })
        connect()
    }

    private fun connect() {
        val mqttConnectOptions = MqttConnectOptions()
        mqttConnectOptions.isAutomaticReconnect = false
        mqttConnectOptions.isCleanSession = true
        //mqttConnectOptions.userName = ADAFRUIT_CLIENT_USER_NAME
        //mqttConnectOptions.password = ADAFRUIT_CLIENT_PASSWORD.toCharArray()
        mqttConnectOptions.connectionTimeout = 3
        mqttConnectOptions.keepAliveInterval = 60

        try {

            mqttAndroidClient.connect(
                mqttConnectOptions, null, object : IMqttActionListener {
                    override fun onSuccess(asyncActionToken: IMqttToken?) {
                        Log.d("connect", "onSuccess: Successfully connected to the broker")
                        val disconnectBufferOptions = DisconnectedBufferOptions()
                        disconnectBufferOptions.isBufferEnabled = true
                        disconnectBufferOptions.bufferSize = 100
                        disconnectBufferOptions.isPersistBuffer = false
                        disconnectBufferOptions.isDeleteOldestMessages = false
                        mqttAndroidClient.setBufferOpts(disconnectBufferOptions)
                    }

                    override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                        Log.w("connect", "Failed to connect to: server; ${Log.getStackTraceString(exception)}"
                        )
                    }
                }
            )
        } catch (ex: MqttException) {
            ex.printStackTrace()
        }
    }
    private fun subscribe(subscriptionTopic: String, qos: Int = 1) {
        try {
            mqttAndroidClient.subscribe(subscriptionTopic, qos, null, object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken?) {
                    Log.w("subscribe", "Subscribed to topic, $subscriptionTopic")
                }
                override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                    Log.w("subscribe", "Subscription to topic $subscriptionTopic failed!")
                }
            })
        } catch (ex: MqttException) {
            System.err.println("Exception whilst subscribing to topic '$subscriptionTopic'")
            94
            ex.printStackTrace()
        }
    }
    private fun publish(topic: String, msg: String, qos: Int = 1) {
        try {
            val message = MqttMessage()
            message.payload = msg.toByteArray()
            mqttAndroidClient.publish(topic, message.payload, qos, false)
            Log.d("publish", "Message published to topic `$topic`: $msg")
        } catch (e: MqttException) {
            Log.d("publish", "Error publishing to $topic: " + e.message)
            e.printStackTrace()
        }
    }*/











        /*
        val mqttAndroidClient: MqttAndroidClient
        val serverUri = "tcp://eu.thethings.network:1883"
        val clientId = "Ewine"
        mqttAndroidClient = MqttAndroidClient(this, serverUri, clientId)

        //val username = "Ewine"
        //val password = "ttn-account-v2.vC-aqMRnLLzGkNjODWgy81kLqzxBPAT8_mE-L7U2C_w" // Access Keys
        val subscriptionTopic = "isen12/led"

        val mqttConnectOptions = MqttConnectOptions()
        mqttConnectOptions.isAutomaticReconnect = true
        mqttConnectOptions.isCleanSession = false
        //mqttConnectOptions.userName = username
        //mqttConnectOptions.password = password.toCharArray()

        mqttAndroidClient.connect(mqttConnectOptions, null, object : IMqttActionListener {
            override fun onSuccess(asyncActionToken: IMqttToken) {
                val disconnectedBufferOptions = DisconnectedBufferOptions()
                disconnectedBufferOptions.isBufferEnabled = true
                disconnectedBufferOptions.bufferSize = 100
                disconnectedBufferOptions.isPersistBuffer = false
                disconnectedBufferOptions.isDeleteOldestMessages = false
                mqttAndroidClient.setBufferOpts(disconnectedBufferOptions)

                mqttAndroidClient.subscribe(subscriptionTopic, 0, null, object : IMqttActionListener {
                    override fun onSuccess(asyncActionToken: IMqttToken) {
                        // On successful subscription
                    }

                    override fun onFailure(asyncActionToken: IMqttToken, exception: Throwable) {
                        // On failed subscription
                    }
                })
            }

            override fun onFailure(asyncActionToken: IMqttToken, exception: Throwable) {
                // On failed connection
            }
        })
    }*/
}