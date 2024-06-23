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
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessaging
import java.security.MessageDigest

class Register : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var sharedPref: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance("https://jopanik-399b9-default-rtdb.asia-southeast1.firebasedatabase.app/")
        sharedPref = getSharedPreferences("myPrefs", Context.MODE_PRIVATE)

        val registerButton = findViewById<Button>(R.id.register)
        registerButton.setOnClickListener {
            val fullNameEditText = findViewById<EditText>(R.id.nama)
            val emailEditText = findViewById<EditText>(R.id.email)
            val passwordEditText = findViewById<EditText>(R.id.pass)
            val confirmPasswordEditText = findViewById<EditText>(R.id.repass)

            val fullName = fullNameEditText.text.toString()
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()
            val confirmPassword = confirmPasswordEditText.text.toString()

            if (fullName.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            } else if (password != confirmPassword) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
            } else {
                val passwordHash = hashString(password, "SHA-256") // Mengenkripsi password dengan SHA-256
                auth.createUserWithEmailAndPassword(email, passwordHash)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            val user = auth.currentUser
                            val uid = user?.uid
                            val userData = HashMap<String, String>()
                            userData["fullName"] = fullName
                            userData["email"] = email
                            userData["password"] = passwordHash // Menyimpan password yang telah dienkripsi
                            userData["role"] = "user"
                            database.getReference("users").child(uid!!).setValue(userData)
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show()

                                        // Menyimpan informasi registrasi pengguna dan informasi login pengguna menggunakan Shared Preferences
                                        with (sharedPref.edit()) {
                                            putString("email", email)
                                            putString("password", passwordHash)
                                            apply()
                                        }

                                        val intent = Intent(this, Homepage::class.java)
                                        startActivity(intent)
                                        finish()
                                    } else {
                                        Toast.makeText(this, "Registration failed", Toast.LENGTH_SHORT).show()
                                    }
                                }
                        } else {
                            Toast.makeText(this, "Registration failed", Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        }

        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result
                saveTokenToDatabase(token)
            }
        }

        val klikTextView = findViewById<TextView>(R.id.klik)
        klikTextView.setOnClickListener {
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
        }

        // Mengambil informasi login pengguna dari Shared Preferences saat membuka aplikasi
        val email = sharedPref.getString("email", "")
        val password = sharedPref.getString("password", "")
        if (email!!.isNotEmpty() && password!!.isNotEmpty()) {
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, Homepage::class.java)
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

    private fun saveTokenToDatabase(token: String?) {
        // Pastikan token tidak null
        if (token != null) {
            val user = auth.currentUser
            val uid = user?.uid
            if (uid != null) {
                database.getReference("users").child(uid).child("token").setValue(token)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            // Token berhasil disimpan
                            Toast.makeText(this, "FCM Token saved successfully", Toast.LENGTH_SHORT).show()
                        } else {
                            // Gagal menyimpan token
                            Toast.makeText(this, "Failed to save FCM Token", Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        }
    }
}