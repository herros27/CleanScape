package com.example.jopanik.Data

import com.google.firebase.database.ServerValue

data class DataNotif(
    val namaSampah: String?,
    val jumlahSampah: Float?,
    val rks: String?,
    val timestamp: Any = ServerValue.TIMESTAMP
) {
    constructor() : this("", 0.0f, "", "")
}
