package com.example.jopanik.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import androidx.cardview.widget.CardView
import com.example.jopanik.Activity.Sampah.BotolPlastik
import com.example.jopanik.Activity.Sampah.Kardus
import com.example.jopanik.Activity.Sampah.Kertas
import com.example.jopanik.Activity.Sampah.Kresek
import com.example.jopanik.Activity.Sampah.Logam
import com.example.jopanik.R

class Anorganik : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_anorganik)

        val view_back: View = findViewById(R.id.view_back)
        view_back.setOnClickListener {
            // Menambahkan kode untuk pindah ke halaman HomeActivity
            startActivity(Intent(this, Homepage::class.java))
        }

        val back: ImageButton = findViewById(R.id.back_home)
        back.setOnClickListener {
            // Menambahkan kode untuk pindah ke halaman HomeActivity
            startActivity(Intent(this, Homepage::class.java))
        }

        // Fungsi pindah halaman botol plastik
        val card_plastik: CardView = findViewById(R.id.botol_plastik)
        card_plastik.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                // Pindah ke halaman baru
                val intent = Intent(applicationContext, BotolPlastik::class.java)
                startActivity(intent)
            }
        })

        // Fungsi pindah halaman kardus
        val card_kardus: CardView = findViewById(R.id.kardus)
        card_kardus.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                // Pindah ke halaman baru
                val intent = Intent(applicationContext, Kardus::class.java)
                startActivity(intent)
            }
        })

        // Fungsi pindah halaman Botol Plastik
        val card_kertas: CardView = findViewById(R.id.kertas)
        card_kertas.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                // Pindah ke halaman baru
                val intent = Intent(applicationContext, Kertas::class.java)
                startActivity(intent)
            }
        })

        // Fungsi pindah halaman Logam
        val card_logam: CardView = findViewById(R.id.logam)
        card_logam.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                // Pindah ke halaman baru
                val intent = Intent(applicationContext, Logam::class.java)
                startActivity(intent)
            }
        })

        // Fungsi pindah halaman kresek
        val card_kresek: CardView = findViewById(R.id.kresek)
        card_kresek.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                // Pindah ke halaman baru
                val intent = Intent(applicationContext, Kresek::class.java)
                startActivity(intent)
            }
        })
    }
}