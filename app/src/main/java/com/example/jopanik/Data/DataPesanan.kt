package com.example.jopanik.Data

import com.google.firebase.database.ServerValue

data class DataPesanan(
    val nama_sampah: String,
    val jumlah_sampah: Float,
    val rks: String,
    val nama_user: String,
    val no_telp: String,
    val timestamp: Any = ServerValue.TIMESTAMP,
    val ambil: Boolean = false,
    var key: String? = null
) {
    constructor() : this("", 0.0f, "", "", "", "", false)
}
