package com.example.jopanik.Activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.jopanik.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.messaging.FirebaseMessaging
import java.security.MessageDigest

class Login : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var sharedPref: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance("https://jopanik-399b9-default-rtdb.asia-southeast1.firebasedatabase.app/")
        sharedPref = getSharedPreferences("myPrefs", Context.MODE_PRIVATE)

        val loginButton = findViewById<Button>(R.id.login)
        loginButton.setOnClickListener {
            val emailEditText = findViewById<EditText>(R.id.user)
            val passwordEditText = findViewById<EditText>(R.id.pass)

            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            } else {
                val passwordHash = hashString(password, "SHA-256") // Mengenkripsi password dengan SHA-256
                auth.signInWithEmailAndPassword(email, passwordHash)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            val user = auth.currentUser
                            val uid = user?.uid
                            val userRef = database.getReference("users").child(uid!!)

//                            // Dapatkan token FCM dan simpan ke database
//                            FirebaseMessaging.getInstance().token.addOnCompleteListener { tokenTask ->
//                                if (tokenTask.isSuccessful) {
//                                    val token = tokenTask.result
//                                    saveTokenToDatabase(uid, token)
//                                }
//
//                                // Logika selanjutnya setelah token didapatkan
//                            }
                            userRef.addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    val userData = snapshot.value as? HashMap<*, *>
                                    val role = userData?.get("role") as? String
                                    if (role == "user") {
                                        Toast.makeText(this@Login, "Login successful", Toast.LENGTH_SHORT).show()
                                        saveLoginInfo(email, passwordHash) // Menyimpan informasi login pengguna menggunakan Shared Preferences
                                        val intent = Intent(this@Login, Homepage::class.java)
                                        startActivity(intent)
                                        finish()
                                    } else if (role == "admin") {
                                        Toast.makeText(this@Login, "Login successful", Toast.LENGTH_SHORT).show()
                                        saveLoginInfo(email, passwordHash) // Menyimpan informasi login pengguna menggunakan Shared Preferences
                                        val intent = Intent(this@Login, Homepage::class.java)
                                        startActivity(intent)
                                        finish()
                                    } else {
                                        Toast.makeText(this@Login, "Login failed: Invalid role", Toast.LENGTH_SHORT).show()
                                    }
                                }

                                override fun onCancelled(error: DatabaseError) {
                                    Toast.makeText(this@Login, "Login failed: Database error", Toast.LENGTH_SHORT).show()
                                }
                            })
                        } else {
                            Toast.makeText(this, "Login failed", Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        }

        val klikTextView = findViewById<TextView>(R.id.klik)
        klikTextView.setOnClickListener {
            val intent = Intent(this, Register::class.java)
            startActivity(intent)
        }

        // Mengambil informasi login pengguna dari Shared Preferences saat membuka aplikasi
        val email = sharedPref.getString("email", "")
        val password = sharedPref.getString("password", "")
        if (email!!.isNotEmpty() && password!!.isNotEmpty()) {
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this@Login, "Login successful", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this@Login, Homepage::class.java)
                        startActivity(intent)
                        finish()
                    }
                }
        }
    }

    private fun hashString(input: String, algorithm: String): String {
        return MessageDigest.getInstance(algorithm)
            .digest(input.toByteArray())
            .fold("", { str, it -> str + "%02x".format(it) })
    }

    // Menyimpan informasi login pengguna menggunakan Shared Preferences
    private fun saveLoginInfo(email: String, password: String) {
        with (sharedPref.edit()) {
            putString("email", email)
            putString("password", password)
            apply()
        }
    }

//    // Menyimpan token FCM ke Firebase Database
//    private fun saveTokenToDatabase(uid: String?, token: String?) {
//        if (uid != null && token != null) {
//            val userRef = database.getReference("users").child(uid)
//            userRef.child("token").setValue(token)
//                .addOnCompleteListener { task ->
//                    if (task.isSuccessful) {
//                        // Token FCM berhasil disimpan
//                        Toast.makeText(this, "FCM Token saved successfully", Toast.LENGTH_SHORT).show()
//                    } else {
//                        // Gagal menyimpan token FCM
//                        Toast.makeText(this, "Failed to save FCM Token", Toast.LENGTH_SHORT).show()
//                    }
//                }
//        }
//    }
}