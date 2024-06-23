package com.example.jopanik.Activity

import android.Manifest
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.jopanik.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessaging

private lateinit var auth: FirebaseAuth
private lateinit var sharedPref: SharedPreferences

class SplashScreen : AppCompatActivity() {

    private val SPLASH_TIME_OUT: Long = 1500 // milidetik

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        // Memeriksa apakah izin notifikasi sudah diberikan atau belum
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED
        ) {
            // Jika belum diberikan, meminta izin notifikasi
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                4869
            )
        } else {
            // Jika sudah diberikan, lanjutkan dengan proses lainnya
            continueWithOtherProcesses()
        }

    }

    private fun continueWithOtherProcesses() {
        auth = FirebaseAuth.getInstance()
        sharedPref = getSharedPreferences("myPrefs", MODE_PRIVATE)
        val progressBar: ProgressBar = findViewById(R.id.progressBar)

        // atur visibility ProgressBar menjadi VISIBLE
        progressBar.visibility = View.VISIBLE

        Handler().postDelayed({
            // lakukan pengecekan login dan arahkan ke activity yang sesuai
            val email = sharedPref.getString("email", "")
            val password = sharedPref.getString("password", "")
            if (email!!.isNotEmpty() && password!!.isNotEmpty()) {
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this@SplashScreen, "Login successful", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this@SplashScreen, Homepage::class.java)
                            startActivity(intent)
                            finish()

                            // Dapatkan UID dari user yang sedang login
                            val uid = auth.currentUser?.uid

                            // Dapatkan token FCM dan simpan ke database
                            FirebaseMessaging.getInstance().token.addOnCompleteListener { tokenTask ->
                                if (tokenTask.isSuccessful) {
                                    val token = tokenTask.result
                                    saveTokenToDatabase(uid, token)
                                }
                            }
                        }
                    }
            } else {
                val intent = Intent(this@SplashScreen, LoginOrRegister::class.java)
                startActivity(intent)
                finish()
            }
        }, SPLASH_TIME_OUT)
    }

    // Override fungsi onRequestPermissionsResult() untuk menangani hasil permintaan izin
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 4869) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Jika izin diberikan oleh pengguna, lanjutkan dengan proses lainnya
                continueWithOtherProcesses()
            } else {
                // Jika izin ditolak, Anda bisa memberikan informasi kepada pengguna atau
                // mengambil tindakan lain sesuai kebutuhan aplikasi Anda
            }
        }
    }

    // Menyimpan token FCM ke Firebase Database
    private fun saveTokenToDatabase(uid: String?, token: String?) {
        if (uid != null && token != null) {
            val userRef = FirebaseDatabase.getInstance().getReference("users").child(uid)
            userRef.child("fcmToken").setValue(token)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Token FCM berhasil disimpan
                        Toast.makeText(this, "FCM Token saved successfully", Toast.LENGTH_SHORT).show()
                    } else {
                        // Gagal menyimpan token FCM
                        Toast.makeText(this, "Failed to save FCM Token", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }
}