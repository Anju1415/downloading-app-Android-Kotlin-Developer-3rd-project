package com.udacity

import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.database.Cursor
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*


class MainActivity : AppCompatActivity() {

    var fileName = ""
    private var downloadID: Long = 0

    var flag = true
    var downloading = true
    var check_status = ""

    private lateinit var notificationManager: NotificationManager
    private lateinit var pendingIntent: PendingIntent
    private lateinit var action: NotificationCompat.Action


    private lateinit var URL : String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        createChannel(
            getString(R.string.notification_channel_id),
            getString(R.string.notification_channel_name)
        )

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        custom_button.setOnClickListener {
              when {
                  glide.isChecked -> {URL = "https://github.com/bumptech/glide"
                      fileName= getString(R.string.glide)
                      download()
                      }
                  load.isChecked -> { URL =
                      "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter"
                      fileName= getString(R.string.load)
                      download()
                  }
                  retrofit.isChecked ->{URL = "https://github.com/square/retrofit"
                      fileName= getString(R.string.retrofit)
                       download()
                  }
                  else -> {
                      Toast.makeText(this, "Please select any option", Toast.LENGTH_LONG).show().toString()

                  }
              }
        }
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {


            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)


            val notificationManager = ContextCompat.getSystemService(
                context!!,
                NotificationManager::class.java
            ) as NotificationManager
            notificationManager.sendNotification(
                fileName,
                check_status,
                context
            )
        }
    }

    private fun download() {
        val request =
            DownloadManager.Request(Uri.parse(URL))
                .setTitle(getString(R.string.app_name))
                .setDescription(getString(R.string.app_description))
                .setRequiresCharging(false)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)



        val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        downloadID = downloadManager.enqueue(request)// enqueue puts the download request in the queue.

        val query: DownloadManager.Query?
        query = DownloadManager.Query()
        var c: Cursor?
        query.setFilterByStatus(DownloadManager.STATUS_FAILED or DownloadManager.STATUS_PAUSED or DownloadManager.STATUS_SUCCESSFUL or DownloadManager.STATUS_RUNNING or DownloadManager.STATUS_PENDING)
        while (downloading) {
            c = downloadManager.query(query);
            if(c.moveToFirst()) {
                Log.i ("FLAG","Downloading");
                val status = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS));

                if (status==DownloadManager.STATUS_SUCCESSFUL) {
                    Log.i ("FLAG","done")
                    check_status = "Success"
                    downloading = false
                    flag=true;
                    break;
                }
                if (status==DownloadManager.STATUS_FAILED) {
                    Log.i ("FLAG","Fail");
                    check_status="Fail"
                    downloading = false;
                    flag=false;
                    break;
                }
            }
        }

    }

/*
    companion object {
        */
/*private const val URL =
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"
        *//*
private const val CHANNEL_ID = "channelId"
    }

*/

    private fun createChannel(channelId: String, channelName: String) {
        // TODO: Step 1.6 START create a channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_HIGH
            )
                    .apply {
                        setShowBadge(false)
                    }

            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationChannel.description = "load the file"

            val notificationManager = this.getSystemService(
                NotificationManager::class.java
            )
            notificationManager.createNotificationChannel(notificationChannel)


        }

    }

}
