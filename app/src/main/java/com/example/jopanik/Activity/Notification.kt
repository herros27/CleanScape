package com.example.jopanik.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.jopanik.Adapter.AdapterNotification
import com.example.jopanik.Data.DataPesanan
import com.example.jopanik.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class Notification : AppCompatActivity() {
    private lateinit var notifAdapter: AdapterNotification
    private lateinit var dataPesananList: MutableList<DataPesanan>
    private lateinit var pesananRef: DatabaseReference
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification)

        dataPesananList = mutableListOf()
        pesananRef = FirebaseDatabase.getInstance("https://jopanik-399b9-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("pesanan") // inisialisasi pesananRef
        notifAdapter = AdapterNotification(dataPesananList, pesananRef)

        recyclerView = findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = notifAdapter

        pesananRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                dataPesananList.clear()
                for (pesananSnapshot in snapshot.children) {
                    val dataPesanan = pesananSnapshot.getValue(DataPesanan::class.java)
                    dataPesanan?.key = pesananSnapshot.key
                    if (dataPesanan != null) {
                        dataPesananList.add(dataPesanan)
                    }
                }
                dataPesananList.sortByDescending { it.timestamp as Long }
                notifAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("NotifActivity", "Error: ${error.message}")
            }
        })
        val back: ImageButton = findViewById(R.id.back)
        back.setOnClickListener {
            onBackPressed()
        }
    }
}