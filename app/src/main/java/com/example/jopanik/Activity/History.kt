package com.example.jopanik.Activity

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.jopanik.Adapter.AdapterHistory
import com.example.jopanik.Data.DataOrders
import com.example.jopanik.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class History : AppCompatActivity() {
    private lateinit var historiAdapter: AdapterHistory
    private lateinit var ordersList: MutableList<DataOrders>
    private lateinit var ordersRef: DatabaseReference
    private lateinit var recyclerView: RecyclerView

    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        val database = FirebaseDatabase.getInstance("https://jopanik-399b9-default-rtdb.asia-southeast1.firebasedatabase.app/")
        val ordersRef = database.getReference("orders")
        // Mengambil UID pengguna saat ini
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            val userId = user.uid

            ordersList = mutableListOf()
            historiAdapter = AdapterHistory(this, ordersList)

            recyclerView = findViewById(R.id.recycler_view)
            recyclerView.layoutManager = LinearLayoutManager(this)
            recyclerView.adapter = historiAdapter

            // Menambahkan query untuk menampilkan histori sampah hanya dari user dengan UID yang sesuai
            ordersRef.orderByChild("uid").equalTo(userId).addValueEventListener(object :
                ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    ordersList.clear()
                    for (ordersSnapshot in snapshot.children) {
                        val orders = ordersSnapshot.getValue(DataOrders::class.java)
                        if (orders != null) {
                            ordersList.add(orders)
                        }
                    }
                    ordersList.sortByDescending { it.timestamp as Long}
                    historiAdapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("HistoriActivity", "Error: ${error.message}")
                }
            })
        }

        val back: ImageButton = findViewById(R.id.back)
        back.setOnClickListener {
            onBackPressed()
        }
    }
}