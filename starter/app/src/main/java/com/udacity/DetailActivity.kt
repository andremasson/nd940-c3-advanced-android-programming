package com.udacity

import android.app.NotificationManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.content_detail.*

class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)

        cancelNotificationsAndShowDetails()
    }

    private fun cancelNotificationsAndShowDetails() {
        val notificationManager = ContextCompat.getSystemService(
                applicationContext,
        NotificationManager::class.java
        ) as NotificationManager

        notificationManager.cancelNotifications()

        val filenameText =  applicationContext.getString(R.string.filename_label) + " " + intent.extras?.getString("filename")
        val statusText = applicationContext.getString(R.string.status_label) + " " + intent.extras?.getString("status")
        download_filename.text = filenameText
        download_status.text = statusText
    }
}
