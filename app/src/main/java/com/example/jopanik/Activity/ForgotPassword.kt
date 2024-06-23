package com.example.jopanik.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.jopanik.R
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import java.security.MessageDigest

class ForgotPassword : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        auth = FirebaseAuth.getInstance()

        val changePasswordButton = findViewById<Button>(R.id.konfirmasi)
        changePasswordButton.setOnClickListener {
            val currentPasswordEditText = findViewById<EditText>(R.id.txt_current_pass)
            val newPasswordEditText = findViewById<EditText>(R.id.txt_new_pass)
            val confirmPasswordEditText = findViewById<EditText>(R.id.txt_new_confirm_pass)

            val currentPassword = currentPasswordEditText.text.toString()
            val newPassword = newPasswordEditText.text.toString()
            val confirmPassword = confirmPasswordEditText.text.toString()

            if (currentPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            } else if (newPassword != confirmPassword) {
                Toast.makeText(this, "New passwords do not match", Toast.LENGTH_SHORT).show()
            } else {
                val passwordHash = hashString(currentPassword, "SHA-256") // Mengenkripsi password saat ini dengan SHA-256
                val newPasswordHash = hashString(newPassword, "SHA-256") // Mengenkripsi password baru dengan SHA-256
                val user = auth.currentUser
                val credential = EmailAuthProvider.getCredential(user?.email!!, passwordHash)
                user.reauthenticate(credential)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            user.updatePassword(newPasswordHash)
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        Toast.makeText(this, "Password updated successfully", Toast.LENGTH_SHORT).show()
                                        finish()
                                    } else {
                                        Toast.makeText(this, "Password update failed", Toast.LENGTH_SHORT).show()
                                    }
                                }
                        } else {
                            Toast.makeText(this, "Authentication failed", Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        }
    }

    private fun hashString(input: String, algorithm: String): String {
        return MessageDigest.getInstance(algorithm)
            .digest(input.toByteArray())
            .fold("", { str, it -> str + "%02x".format(it) })
    }
}