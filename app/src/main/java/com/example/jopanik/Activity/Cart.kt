package com.example.jopanik.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.jopanik.Adapter.AdapterCart
import com.example.jopanik.Data.DataCart
import com.example.jopanik.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class Cart : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var cartAdapter: AdapterCart
    private lateinit var cartList: MutableList<DataCart>
    private lateinit var emptyView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        recyclerView = findViewById(R.id.recycler_view)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(this)

        emptyView = findViewById(R.id.emptyview)

        cartList = mutableListOf()

        val database = FirebaseDatabase.getInstance("https://jopanik-399b9-default-rtdb.asia-southeast1.firebasedatabase.app/")
        val tableCartRef = database.getReference("cart")
        val user = FirebaseAuth.getInstance().currentUser

        tableCartRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                cartList.clear()

                for (cartSnapshot in dataSnapshot.children) {
                    val cart = cartSnapshot.getValue(DataCart::class.java)

                    if (cart != null && cart.id_user == user?.uid) {
                        cartList.add(cart)
                    }
                }

                if (cartList.isEmpty()) {
                    emptyView.visibility = View.VISIBLE
                    recyclerView.visibility = View.GONE
                } else {
                    emptyView.visibility = View.GONE
                    recyclerView.visibility = View.VISIBLE
                    cartAdapter = AdapterCart(this@Cart, cartList)
                    recyclerView.adapter = cartAdapter
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Menampilkan pesan error jika terjadi kesalahan
                Log.w("TAG", "Failed to read value.", error.toException())
            }
        })

        val btnReset = findViewById<Button>(R.id.btn_reset)
        btnReset.setOnClickListener {
            // Hapus semua data cart dari Firebase Realtime Database
            tableCartRef.removeValue()
        }

        val btnAngkut = findViewById<Button>(R.id.btn_angkut)
        btnAngkut.setOnClickListener {
            // Pindah ke DataActivity
            val intent = Intent(this@Cart, UploadSampah::class.java)
            intent.putExtra("FROM_KUMPUL_BUTTON", true) // true jika data berasal dari tabel "cart"
            startActivity(intent)
        }
        val back: ImageButton = findViewById(R.id.back)
        back.setOnClickListener {
            onBackPressed()
        }
    }
}