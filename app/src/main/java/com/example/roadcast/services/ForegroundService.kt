package com.example.roadcast.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.BatteryManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.MutableLiveData
import com.example.roadcast.R

class ForegroundService : LifecycleService(), SensorEventListener {

    companion object {
        const val NOTIFICATION_CHANNEL_ID = "my_channel"
        const val NOTIFICATION_CHANNEL_NAME = "Monitor"
        const val NOTIFICATION_ID = 1
    }

    private lateinit var sensorManager: SensorManager
    private var mTemperature: Sensor? = null

    private val temp = MutableLiveData<String?>()
    private val battery = MutableLiveData<Float?>()

    private lateinit var batteryReceiver: BroadcastReceiver


    override fun onCreate() {
        super.onCreate()

        this.batteryReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val level: Int = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
                val scale: Int = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
                battery.value = level * 100 / scale.toFloat()
            }
        }

        IntentFilter(Intent.ACTION_BATTERY_CHANGED).let { filter ->
            this.registerReceiver(batteryReceiver, filter)
        }

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        mTemperature = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE)

        mTemperature?.also { it ->
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }

        battery.observe(this) {
            updateNotification(it, temp.value)
        }
        temp.observe(this) {
            updateNotification(battery.value, it)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        showNotification()
        return super.onStartCommand(intent, flags, startId)
    }

    private fun showNotification() {
        startForeground(NOTIFICATION_ID, getMyActivityNotification(0.0f, ""))
    }

    private fun getMyActivityNotification(batteryPct: Float?, temp: String?): Notification {
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(notificationManager)
        }

        val notificationBuilder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setAutoCancel(false)
            .setOngoing(true)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("Running App")
            .setContentText("Battery Percentage: $batteryPct    Device Temperature: $temp")

        return notificationBuilder.build()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(notificationManager: NotificationManager) {
        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            NOTIFICATION_CHANNEL_NAME,
            NotificationManager.IMPORTANCE_LOW
        )

        notificationManager.createNotificationChannel(channel)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        val temperature = event?.values?.get(0)
        temp.value = temperature.toString()
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

    }

    override fun onDestroy() {
        super.onDestroy()
        sensorManager.unregisterListener(this)
    }

    private fun updateNotification(batteryPct: Float?, temp: String?) {
        val notification: Notification = getMyActivityNotification(batteryPct, temp)

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, notification)
    }
}