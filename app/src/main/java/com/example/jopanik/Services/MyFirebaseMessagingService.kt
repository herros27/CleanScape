package com.example.jopanik.Services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.example.jopanik.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        saveTokenToDatabase(token)
    }

    private fun saveTokenToDatabase(token: String) {
        val database = FirebaseDatabase.getInstance("https://jopanik-399b9-default-rtdb.asia-southeast1.firebasedatabase.app/")
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        val userRef = database.getReference("users").child(uid!!)
        userRef.child("token").setValue(token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        if (remoteMessage.notification != null) {
            showNotification(
                remoteMessage.notification!!.title, remoteMessage.notification!!.body
            )
        }
    }

    private fun showNotification(title: String?, body: String?) {
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "admin"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(notificationManager, channelId, "admin")
        }
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setContentTitle(title)
            .setContentText(body)
            .setSmallIcon(R.drawable.ic_notification)
            .setAutoCancel(true)
        notificationManager.notify(1, notificationBuilder.build())
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private fun createNotificationChannel(
        notificationManager: NotificationManager,
        channelId: String,
        channelName: String
    ) {
        val channel =
            NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH)
        channel.description = "Admin Notification"
        notificationManager.createNotificationChannel(channel)
    }
}