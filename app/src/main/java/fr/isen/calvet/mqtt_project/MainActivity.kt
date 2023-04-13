package fr.isen.calvet.mqtt_project

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils.substring
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.hivemq.client.mqtt.MqttClient
import com.hivemq.client.mqtt.MqttGlobalPublishFilter
import com.hivemq.client.mqtt.mqtt3.Mqtt3AsyncClient
import com.hivemq.client.mqtt.mqtt3.message.connect.connack.Mqtt3ConnAck
import com.hivemq.client.mqtt.mqtt3.message.publish.Mqtt3Publish
import com.hivemq.client.mqtt.mqtt3.message.subscribe.suback.Mqtt3SubAck
import com.jjoe64.graphview.Viewport
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.DataPointInterface
import com.jjoe64.graphview.series.LineGraphSeries
import fr.isen.calvet.mqtt_project.databinding.ActivityMainBinding
import io.netty.util.AsciiString.contains
import java.util.*
import java.util.concurrent.CompletableFuture
import kotlin.text.Charsets.UTF_8


class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding
    private lateinit var client : Mqtt3AsyncClient
    private lateinit var tempList : Array<dataTemp>
    private var cptButton1 : Int = 0
    private var cptButton2 : Int = 0
    private var temperature : String = ""
    private var cpt : Double = 0.0

    @RequiresApi(Build.VERSION_CODES.Q)
    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var ledBlueOn = false
        var ledGreenOn = false
        var ledRedOn = false
        var tempOn = false

        tempList = arrayOf(dataTemp(), dataTemp(), dataTemp())

        val topicLed = "isen09/led"
        val topicTemp = "isen09/temp"
        val topicButton = "isen09/button"
        val topicGetTemp = "isen09/getTemp"

        val getTemp = "{\"request\": 1}"

        val led1on = "{\"id\": 1,\"state\": 1}"
        val led1off = "{\"id\": 1,\"state\": 0}"
        val led2on = "{\"id\": 2,\"state\": 1 }"
        val led2off = "{\"id\": 2,\"state\": 0 }"
        val led3on = "{\"id\": 3,\"state\": 1 }"
        val led3off = "{\"id\": 3,\"state\": 0 }"

        val handler = Handler()
        val r = object : Runnable {
            override fun run() {
                publish(topicGetTemp, getTemp);
                handler.postDelayed(this, 2000);
            }
        }

        hide()

        clientBuild()
        connectClient().thenAccept {
            if(it) {
                show()

                subscribe(topicButton)
                subscribe(topicTemp)

                getMessage()

                Log.d("IS CLICKABLE ? 1", binding.led1.isClickable.toString())
                Log.d("IS CLICKABLE ? 2", binding.led2.isClickable.toString())
                Log.d("IS CLICKABLE ? 3", binding.led3.isClickable.toString())

                binding.led1.setOnClickListener {
                    Log.d("ButtonLED1", "CLICKED")
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
                    Log.d("ButtonLED2", "CLICKED")
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
                    Log.d("ButtonLED3", "CLICKED")
                    if(!ledRedOn) {
                        publish(topicLed, led3on)
                        binding.led3.setColorFilter(Color.RED)
                    } else {
                        publish(topicLed, led3off)
                        binding.led3.clearColorFilter()
                    }
                    ledRedOn = !ledRedOn
                }
                Log.d("IS CLICKABLE ?", binding.led2.isClickable.toString())

            }
        }
        binding.buttonGetTemp.setOnClickListener {
            if(!tempOn){
                binding.buttonGetTemp.text = "Getting temperature ..."
                handler.post(r)

            } else {
                binding.buttonGetTemp.text = "Get temp"
                binding.temp.text = "..."
                handler.removeCallbacks(r)

            }
            tempOn = !tempOn
        }
    }

    //Show connect tools in the UI
    fun show() {
        runOnUiThread {
            binding.group.visibility = View.VISIBLE
            binding.status.visibility = View.GONE
            binding.led1.isClickable = true
            binding.led2.isClickable = true
            binding.led3.isClickable = true
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
        val clientBuilder = MqttClient.builder()
            .identifier(UUID.randomUUID().toString())
            .serverHost("broker.mqttdashboard.com")

        client = clientBuilder.useMqttVersion3().buildAsync()
    }
    private fun connectClient(): CompletableFuture<Boolean> {
        var success = CompletableFuture<Boolean>()
        client.connectWith()
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
            .send()
            .whenComplete { subAck: Mqtt3SubAck?, throwable: Throwable? ->
                if (throwable != null) {
                    Log.d("subscribe", "failure")
                } else {
                    Log.d("subscribe", "success")
                }
            }
    }
    private fun publish(topic : String , message : String) {
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

    private fun getMessage() {
        client.toAsync().publishes(MqttGlobalPublishFilter.ALL) { publish: Mqtt3Publish ->
            val message = publish.payloadAsBytes.toString(UTF_8)
            if(publish.topic.toString().contains("button")) {
                    Log.d("Received message: {} -> {}, ", "${publish.topic}, $message")
                    if(contains(message,"id\":1")){
                        cptButton1 ++
                        runOnUiThread {
                            binding.button1.text = cptButton1.toString()
                            binding.numeroButton.text = "1"
                        }
                        //Log.d("subscribe", "button1")
                    }
                    if(contains(message,"id\":2")){
                        cptButton2 ++
                        runOnUiThread {
                            binding.button2.text = cptButton2.toString()
                            binding.numeroButton.text = "2"
                        }
                        //Log.d("subscribe", "button2")
                    }
            } else if(publish.topic.toString().contains("temp")) {
                Log.d("temp Received message: {} -> {}, ", "${publish.topic}, $message")
                if(contains(message,"value")){
                    temperature = substring(message, message.indexOf(':') + 1, message.indexOf('}'))
                    runOnUiThread {
                        binding.temp.text = temperature
                        val newTemp = dataTemp()
                        newTemp.addTemp(temperature.toDouble(),cpt)
                        Log.d("temp Received message: {} -> {}, cpt ", "$cpt")
                        tempList += newTemp
                        graphDisplay()
                        cpt++
                    }
                    //Log.d("subscribe", "temp")
                }
            }
        }
    }

    private fun graphDisplay(){
        val series = LineGraphSeries<DataPointInterface>()

        for (i in tempList.indices) {
            val tempObj = tempList[i]
            val dataPoint = DataPoint(tempObj.cpt, tempObj.temperature)
            series.appendData(dataPoint, true, tempList.size)
        }


        binding.graph.addSeries(series)
        val viewport = binding.graph.viewport

        //set the YAxis between 5 bellow the minimum value and 5 above the maximum value of the graph
        viewport.isYAxisBoundsManual = true
        viewport.setMinY(20.0)
        viewport.setMaxY(series.highestValueY + 5)

        // Set the visible x-axis range of the graph
        val minX = 0.1
        val maxX = series.highestValueX
        val rangeWidth = maxX - minX
        viewport.setMinX(minX)
        viewport.setMaxX(maxX + rangeWidth / 5) // Add 1/5 of the range to the max value
        viewport.isXAxisBoundsManual = true
    }

    class dataTemp{
        var temperature : Double = 0.0
        var cpt : Double = 0.0

        fun addTemp(Temp: Double, Cpt : Double) {
            temperature = Temp
            cpt = Cpt
        }
    }
}