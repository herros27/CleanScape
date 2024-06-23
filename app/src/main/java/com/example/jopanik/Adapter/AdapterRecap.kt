package com.example.jopanik.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.jopanik.Activity.Recap
import com.example.jopanik.Data.DataOrders
import com.example.jopanik.R
import java.text.SimpleDateFormat
import java.util.Date

class AdapterRecap(private val context: Recap, private val dataOrdersList: MutableList<DataOrders>) :
    RecyclerView.Adapter<AdapterRecap.ViewHolder>() {

    init {
        dataOrdersList.sortByDescending { it.timestamp as Long }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_recap, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val orders = dataOrdersList[position]

        holder.namaUserText.text = orders.nama_user
        holder.rksText.text = orders.rks
        holder.namaSampahText.text = orders.nama_sampah
        holder.jumlahSampahText.text = orders.jumlah_sampah.toString()
        holder.noTelpText.text = orders.no_telp

        // Mengubah nilai timestamp menjadi format waktu yang biasa
        val dateFormat = SimpleDateFormat("EEE dd MMMM yyyy HH:mm")
        val date = Date(orders.timestamp as Long)
        val formattedDate = dateFormat.format(date)
        holder.timestampText.text = formattedDate
    }

    override fun getItemCount(): Int {
        return dataOrdersList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val namaUserText: TextView = itemView.findViewById(R.id.nama_user_text)
        val rksText: TextView = itemView.findViewById(R.id.rks_text)
        val namaSampahText: TextView = itemView.findViewById(R.id.nama_sampah_text)
        val jumlahSampahText: TextView = itemView.findViewById(R.id.jumlah_sampah_text)
        val noTelpText: TextView = itemView.findViewById(R.id.no_telp_text)
        val timestampText: TextView = itemView.findViewById(R.id.timestamp_text)
    }
}