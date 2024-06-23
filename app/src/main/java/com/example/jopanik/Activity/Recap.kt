package com.example.jopanik.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.jopanik.Adapter.AdapterRecap
import com.example.jopanik.Data.DataOrders
import com.example.jopanik.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class Recap : AppCompatActivity() {
    private lateinit var rekapAdapter: AdapterRecap
    private lateinit var dataOrdersList: MutableList<DataOrders>
    private lateinit var ordersRef: DatabaseReference
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recap)

        dataOrdersList = mutableListOf()
        ordersRef = FirebaseDatabase.getInstance("https://jopanik-399b9-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("orders") // inisialisasi ordersRef

        recyclerView = findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)

        rekapAdapter = AdapterRecap(this, dataOrdersList)
        recyclerView.adapter = rekapAdapter

        ordersRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                dataOrdersList.clear()
                for (ordersSnapshot in snapshot.children) {
                    val dataOrders = ordersSnapshot.getValue(DataOrders::class.java)
                    if (dataOrders != null) {
                        dataOrdersList.add(dataOrders)
                    }
                }
                dataOrdersList.sortByDescending { it.timestamp as Long}
                rekapAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("RekapActivity", "Error: ${error.message}")
            }
        })

        val back: ImageButton = findViewById(R.id.back)
        back.setOnClickListener {
            onBackPressed()
        }
    }
}