package com.example.jopanik.Data

import com.google.firebase.database.ServerValue

data class DataOrders(
    val rks: String,
    val nama_user: String,
    val no_telp: String,
    val nama_sampah: String,
    val jumlah_sampah: Float,
    val timestamp: Any = ServerValue.TIMESTAMP,
    val uid: String,
    val ambil: Boolean = false
) {
    constructor() : this("", "", "", "", 0.0f, "", "", false)
}