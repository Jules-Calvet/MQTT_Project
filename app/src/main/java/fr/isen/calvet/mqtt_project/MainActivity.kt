package fr.isen.calvet.mqtt_project

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import fr.isen.calvet.mqtt_project.databinding.ActivityMainBinding
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.DisconnectedBufferOptions
import org.eclipse.paho.client.mqttv3.IMqttActionListener
import org.eclipse.paho.client.mqttv3.IMqttToken
import org.eclipse.paho.client.mqttv3.MqttConnectOptions


class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
    }
}