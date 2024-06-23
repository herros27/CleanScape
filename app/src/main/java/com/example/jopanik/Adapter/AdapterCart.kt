package com.example.jopanik.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.jopanik.Data.DataCart
import com.example.jopanik.R

class AdapterCart(private val context: Context, private val cartList: List<DataCart>) :
    RecyclerView.Adapter<AdapterCart.CartViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_cart, parent, false)
        return CartViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val cart = cartList[position]

        holder.namaSampahTextView.text = cart.nama_sampah
        holder.jumlahSampahTextView.text = cart.jumlah_sampah.toString()
    }

    override fun getItemCount(): Int {
        return cartList.size
    }

    inner class CartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val namaSampahTextView: TextView = itemView.findViewById(R.id.nama_sampah_text_view)
        val jumlahSampahTextView: TextView = itemView.findViewById(R.id.jumlah_sampah_text_view)
    }
}