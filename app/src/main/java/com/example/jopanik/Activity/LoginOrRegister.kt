package com.example.jopanik.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import com.example.jopanik.R

class LoginOrRegister : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_or_register)

        // Fungsi pindah halaman registrasi
        val button_reg: Button = findViewById(R.id.btn_reg)
        button_reg.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                // Pindah ke halaman baru
                val intent = Intent(applicationContext, Register::class.java)
                startActivity(intent)
            }
        })

        // Fungsi pindah halaman login
        val button_log: Button = findViewById(R.id.btn_log)
        button_log.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                // Pindah ke halaman baru
                val intent = Intent(applicationContext, Login::class.java)
                startActivity(intent)
            }
        })
    }
}