package com.example.jopanik.Activity.Sampah

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import com.example.jopanik.Activity.Anorganik
import com.example.jopanik.Activity.Cart
import com.example.jopanik.Activity.UploadSampah
import com.example.jopanik.Data.DataAngkut
import com.example.jopanik.Data.DataCart
import com.example.jopanik.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class Logam : AppCompatActivity() {

    private var jumlahSampah = 0.0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_logam)

        val tambahButton = findViewById<Button>(R.id.button_tambah)
        val kurangButton = findViewById<Button>(R.id.button_kurang)
        val jumlahEditText = findViewById<EditText>(R.id.jumlah_edit)
        val angkutButton = findViewById<Button>(R.id.btn_angkut)
        val btnKumpul = findViewById<Button>(R.id.btn_kumpul)

        jumlahEditText.setText(jumlahSampah.toString())

        val database = FirebaseDatabase.getInstance("https://ppko-gcc-uad-default-rtdb.asia-southeast1.firebasedatabase.app/")
        val tableSampahRef = database.getReference("sampah")
        val tableCartRef = database.getReference("cart")
        val tableAngkutRef = database.getReference("angkut")
        val user = FirebaseAuth.getInstance().currentUser

        val sampah1 = HashMap<String, Any>()
        sampah1["id_sampah"] = 4
        sampah1["nama_sampah"] = "Logam"
        tableSampahRef.child("1").setValue(sampah1)

        val txtTitle = findViewById<TextView>(R.id.txt_title)

        tableSampahRef.child("1").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Mendapatkan data dari Firebase Realtime Database
                val namaSampah = dataSnapshot.child("nama_sampah").value.toString()

                // Menampilkan nama sampah di dalam TextView
                txtTitle.text = namaSampah
            }

            override fun onCancelled(error: DatabaseError) {
                // Menampilkan pesan error jika terjadi kesalahan
                Log.w("TAG", "Failed to read value.", error.toException())
            }
        })

        tambahButton.setOnClickListener {
            jumlahSampah++
            jumlahEditText.setText(jumlahSampah.toString())
        }

        kurangButton.setOnClickListener {
            if (jumlahSampah > 0) {
                jumlahSampah--
                jumlahEditText.setText(jumlahSampah.toString())
            }
        }

        angkutButton.setOnClickListener {
            val idUser = user?.uid // Mendapatkan uid dari user saat ini
            val namaSampah = txtTitle.text.toString() // Mendapatkan nama sampah dari TextView
            var jumlahSampah = jumlahEditText.text.toString().toFloat() // Mendapatkan jumlah sampah dari EditText

            // Membaca data cart dari Firebase Realtime Database
            tableAngkutRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    var found = false

                    for (angkutSnapshot in dataSnapshot.children) {
                        val angkut = angkutSnapshot.getValue(DataCart::class.java)

                        if (angkut != null && angkut.id_user == idUser && angkut.nama_sampah == namaSampah) {
                            found = true
                            break
                        }
                    }

                    if (!found) {
                        // Jika data "nama_sampah" yang sama tidak ditemukan, tambahkan data angkut baru
                        val idAngkut = tableAngkutRef.push().key // Generate id_angkut

                        if (idAngkut != null) {
                            val angkut = DataAngkut(idAngkut, idUser, namaSampah, jumlahSampah)
                            tableAngkutRef.child(idAngkut).setValue(angkut)
                        } else {
                            // Menangani kasus jika idAngkut null
                            Log.w("TAG", "Failed to generate id_angkut.")
                        }
                    }

                    // Pindah ke DataActivity
                    val intent = Intent(this@Logam, UploadSampah::class.java)
                    startActivity(intent)
                }

                override fun onCancelled(error: DatabaseError) {
                    // Menampilkan pesan error jika terjadi kesalahan
                    Log.w("TAG", "Failed to read value.", error.toException())
                }
            })
        }

        btnKumpul.setOnClickListener {
            val idUser = user?.uid // Mendapatkan uid dari user saat ini
            val namaSampah = txtTitle.text.toString() // Mendapatkan nama sampah dari TextView
            var jumlahSampah = jumlahEditText.text.toString().toFloat() // Mendapatkan jumlah sampah dari EditText

            // Membaca data cart dari Firebase Realtime Database
            tableCartRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    var found = false

                    for (cartSnapshot in dataSnapshot.children) {
                        val cart = cartSnapshot.getValue(DataCart::class.java)

                        if (cart != null && cart.id_user == idUser && cart.nama_sampah == namaSampah) {
                            // Jika data "nama_sampah" yang sama ditemukan, update data cart tersebut
                            val cartUpdate = HashMap<String, Any>()
                            val newJumlahSampah = jumlahEditText.text.toString().toFloat() // Mengambil nilai dari EditText
                            cartUpdate["jumlah_sampah"] = newJumlahSampah
                            tableCartRef.child(cartSnapshot.key!!).updateChildren(cartUpdate)
                            found = true
                            break
                        }
                    }

                    if (!found) {
                        // Jika data "nama_sampah" yang sama tidak ditemukan, tambahkan data cart baru
                        val idCart = tableCartRef.push().key // Generate id_cart

                        if (idCart != null) {
                            val cart = DataCart(idCart, idUser, namaSampah, jumlahSampah)
                            tableCartRef.child(idCart).setValue(cart)
                        } else {
                            // Menangani kasus jika idCart null
                            Log.w("TAG", "Failed to generate id_cart.")
                        }
                    }

                    // Menampilkan pesan alert
                    val builder = AlertDialog.Builder(this@Logam)
                    builder.setTitle("Sampah berhasil ditambahkan ke dalam cart")
                    builder.setPositiveButton("Lihat Keranjang", DialogInterface.OnClickListener { dialog, which ->
                        // Pindah ke BagActivity
                        val intentBag = Intent(this@Logam, Cart::class.java)
                        startActivity(intentBag)
                    })
                    builder.setNegativeButton("Tambah Sampah Lain", DialogInterface.OnClickListener { dialog, which ->
                        // Kosongkan jumlah sampah dan EditText, dan pindah ke AnorganikActivity
                        jumlahSampah = 0.0f
                        jumlahEditText.setText(jumlahSampah.toString())
                        val intentAnorganik = Intent(this@Logam, Anorganik::class.java)
                        startActivity(intentAnorganik)
                    })
                    val alertDialog = builder.create()
                    alertDialog.show()
                }

                override fun onCancelled(error: DatabaseError) {
                    // Menampilkan pesan error jika terjadi kesalahan
                    Log.w("TAG", "Failed to read value.", error.toException())
                }
            })
        }

        val back: ImageButton = findViewById(R.id.back)
        back.setOnClickListener {
            // Menambahkan kode untuk pindah ke halaman HomeActivity
            startActivity(Intent(this, Anorganik::class.java))
        }
    }
}