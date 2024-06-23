package com.example.jopanik.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.jopanik.Data.DataPesanan
import com.example.jopanik.R
import com.google.firebase.database.DatabaseReference
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AdapterNotification(private val dataPesananList: MutableList<DataPesanan>, private val pesananRef: DatabaseReference) :
    RecyclerView.Adapter<AdapterNotification.PesananViewHolder>() {

    inner class PesananViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val namaSampahTextView: TextView = itemView.findViewById(R.id.nama_sampah_text_view)
        val jumlahSampahTextView: TextView = itemView.findViewById(R.id.jumlah_sampah_text_view)
        val rksTextView: TextView = itemView.findViewById(R.id.rks_text_view)
        val timestampTextView: TextView = itemView.findViewById(R.id.timestamp_text_view)
        val timestampTextView2: TextView = itemView.findViewById(R.id.timestamp_text_view2)
        val sudahBelumCardView: CardView = itemView.findViewById(R.id.sudah_belum)
        var ambil: Boolean = false
        var key: String? = null

        init {
            sudahBelumCardView.setOnClickListener {
                ambil = !ambil
                pesananRef.child(key!!).child("ambil").setValue(ambil)
                if (ambil) {
                    sudahBelumCardView.setCardBackgroundColor(ContextCompat.getColor(itemView.context, R.color.card_selected))
                } else {
                    sudahBelumCardView.setCardBackgroundColor(ContextCompat.getColor(itemView.context, R.color.card_unselected))
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PesananViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_notification, parent, false)
        return PesananViewHolder(view)
    }

    override fun onBindViewHolder(holder: PesananViewHolder, position: Int) {
        val pesanan = dataPesananList[position]
        holder.namaSampahTextView.text = pesanan.nama_sampah
        holder.jumlahSampahTextView.text = "Jumlah Sampah: ${pesanan.jumlah_sampah} KG"
        holder.rksTextView.text = "${pesanan.rks}"
        holder.timestampTextView.text = "${convertTimestampToDateString(pesanan.timestamp as Long)}"
        holder.timestampTextView2.text = "${convertTimestampToClockString(pesanan.timestamp)}"
        holder.ambil = pesanan.ambil
        holder.key = dataPesananList[position].key

        if (holder.ambil) {
            holder.sudahBelumCardView.setCardBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.card_selected))
        } else {
            holder.sudahBelumCardView.setCardBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.card_unselected))
        }
    }

    override fun getItemCount(): Int {
        return dataPesananList.size
    }

    fun addPesanan(dataPesanan: DataPesanan) {
        dataPesananList.add(0, dataPesanan)
        notifyItemInserted(0)
    }

    fun removePesanan(position: Int) {
        dataPesananList.removeAt(position)
        notifyItemRemoved(position)
    }

    fun updatePesanan(position: Int, dataPesanan: DataPesanan) {
        dataPesananList[position] = dataPesanan
        notifyItemChanged(position)
    }

    fun sortPesananByTimestamp() {
        dataPesananList.sortByDescending { it.timestamp as Long }
        notifyDataSetChanged()
    }

    private fun convertTimestampToDateString(timestamp: Long): String {
        val sdf = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        val date = Date(timestamp)
        return sdf.format(date)
    }

    private fun convertTimestampToClockString(timestamp: Long): String {
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        val date = Date(timestamp)
        return sdf.format(date)
    }
}