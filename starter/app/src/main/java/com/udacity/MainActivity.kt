package com.udacity

import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*


class MainActivity : AppCompatActivity() {

    private var downloadID: Long = 0

    private lateinit var notificationManager: NotificationManager
    private lateinit var pendingIntent: PendingIntent
    private lateinit var action: NotificationCompat.Action

    private var selectedOption : String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        custom_button.setOnClickListener {
            download()
        }

        radioButton_1.setOnClickListener {
            selectedOption = "op1"
        }
        radioButton_2.setOnClickListener {
            selectedOption = "op2"
        }
        radioButton_3.setOnClickListener {
            selectedOption = "op3"
        }

        createChannel()
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            sendNotification()
        }
    }

    private fun download() {
        if (selectedOption == "") {
            Toast.makeText(applicationContext, "Please select the file to download", Toast.LENGTH_LONG).show()
            return
        }
        custom_button.onStartDownload()

        val downloadOption = downloadOptions[selectedOption]

        val request =
            DownloadManager.Request(Uri.parse(downloadOption?.url))
                .setTitle(downloadOption?.title)
                .setDescription(downloadOption?.description)
                .setRequiresCharging(false)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)

        val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        downloadID =
            downloadManager.enqueue(request)// enqueue puts the download request in the queue.

    }

    fun sendNotification() {
        val downloadOption = downloadOptions[selectedOption]

        val notificationManager = ContextCompat.getSystemService(
                applicationContext,
                NotificationManager::class.java
        ) as NotificationManager

        notificationManager.cancelNotifications()
        notificationManager.sendNotification(
                downloadOption?.title?:"Download",
                applicationContext
        )
    }

    private fun createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                    getString(R.string.channel_id),
                    getString(R.string.channel_name),
                    NotificationManager.IMPORTANCE_HIGH
            )
                    .apply {
                        setShowBadge(false)
                    }

            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationChannel.description = getString(R.string.channel_description)

            val notificationManager = getSystemService(
                    NotificationManager::class.java
            )
            notificationManager.createNotificationChannel(notificationChannel)

        }
    }

    companion object {
        private val downloadOptions = mapOf(
                Pair("op1", DownloadOption("https://github.com/bumptech/glide/archive/refs/heads/master.zip", "Glide", "Glide is a fast and efficient open source media management.")),
                Pair("op2", DownloadOption("https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/refs/heads/master.zip", "LoadApp", "In this project students will create an app to download a file from Internet by clicking on a custom-built button.")),
                Pair("op3", DownloadOption("https://github.com/square/retrofit/archive/refs/heads/master.zip", "Retrofit", "A type-safe HTTP client for Android and Java."))
        )

        class DownloadOption(
            var url : String,
            var title : String,
            var description : String
        )
    }
}
