package com.example.jopanik.Activity

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.jopanik.Adapter.AdapterCart
import com.example.jopanik.Data.DataAngkut
import com.example.jopanik.Data.DataCart
import com.example.jopanik.Data.DataNotif
import com.example.jopanik.Data.DataOrders
import com.example.jopanik.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import com.google.firebase.database.ValueEventListener
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.RemoteMessage
import com.google.firebase.FirebaseApp

class UploadSampah : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    private lateinit var database: FirebaseDatabase
    private lateinit var firebaseMessaging: FirebaseMessaging
    private lateinit var recyclerView: RecyclerView
    private lateinit var tableAngkutRef: DatabaseReference
    private lateinit var user: FirebaseUser
    private lateinit var cartAdapter: AdapterCart
    private lateinit var cartList: MutableList<DataCart>
    private lateinit var notifList: MutableList<DataNotif>
    private lateinit var emptyView: TextView
    private lateinit var context: Context // Deklarasi variabel context

    companion object {
        private const val INTERNET_PERMISSION_REQUEST_CODE = 101
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload_sampah)

        // Initialize Firebase Cloud Messaging
        firebaseMessaging = FirebaseMessaging.getInstance()
        firebaseMessaging.isAutoInitEnabled = true

        // Pengecekan izin INTERNET
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET)
            == PackageManager.PERMISSION_GRANTED) {
            // Lanjutkan dengan operasi yang memerlukan izin INTERNET
            initializeFirebase()
        } else {
            // Jika izin belum diberikan, meminta izin kepada pengguna
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.INTERNET),
                INTERNET_PERMISSION_REQUEST_CODE
            )
        }
    }

    private fun initializeFirebase() {
        // Inisialisasi Firebase
        database = FirebaseDatabase.getInstance("https://jopanik-399b9-default-rtdb.asia-southeast1.firebasedatabase.app/")
        recyclerView = findViewById(R.id.recycler_view)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(this)
        notifList = mutableListOf()
        emptyView = findViewById(R.id.emptyview)
        cartList = mutableListOf()
        context = this // Inisialisasi variabel context

        val tableCartRef = database.getReference("cart")
        tableAngkutRef = database.getReference("angkut")
        user = FirebaseAuth.getInstance().currentUser!!

        val fromKumpulButton = intent.getBooleanExtra("FROM_KUMPUL_BUTTON", false)

        if (fromKumpulButton) {
            // Jika tombol "kumpulButton" diklik, gunakan tabel "cart" untuk menampilkan data
            tableCartRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    cartList.clear()

                    for (cartSnapshot in dataSnapshot.children) {
                        val cart = cartSnapshot.getValue(DataCart::class.java)

                        if (cart != null && cart.id_user == user.uid) {
                            cartList.add(cart)
                        }
                    }

                    if (cartList.isEmpty()) {
                        emptyView.visibility = View.VISIBLE
                        recyclerView.visibility = View.GONE
                    } else {
                        emptyView.visibility = View.GONE
                        recyclerView.visibility = View.VISIBLE
                        cartAdapter = AdapterCart(this@UploadSampah, cartList)
                        recyclerView.adapter = cartAdapter
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Menampilkan pesan error jika terjadi kesalahan
                    Log.w("TAG", "Failed to read value.", error.toException())
                }
            })
        } else {
            // Jika tombol "angkutButton" diklik, gunakan tabel "angkut" untuk menampilkan data
            tableAngkutRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    cartList.clear()

                    for (angkutSnapshot in dataSnapshot.children) {
                        val angkut = angkutSnapshot.getValue(DataAngkut::class.java)

                        if (angkut != null && angkut.id_user == user.uid) {
                            cartList.add(DataCart(angkut.id_angkut, angkut.id_user, angkut.nama_sampah, angkut.jumlah_sampah))
                        }
                    }

                    if (cartList.isEmpty()) {
                        emptyView.visibility = View.VISIBLE
                        recyclerView.visibility = View.GONE
                    } else {
                        emptyView.visibility = View.GONE
                        recyclerView.visibility = View.VISIBLE
                        cartAdapter = AdapterCart(this@UploadSampah, cartList)
                        recyclerView.adapter = cartAdapter
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Menampilkan pesan error jika terjadi kesalahan
                    Log.w("TAG", "Failed to read value.", error.toException())
                }
            })
        }

        val spinner: Spinner = findViewById(R.id.cmb_rks)
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter.createFromResource(
            this,
            R.array.rks_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            spinner.adapter = adapter
        }

        val namaUserEditText: EditText = findViewById(R.id.namaText)
        namaUserEditText.isEnabled = false

        val userRef = database.getReference("users").child(user.uid.toString())

        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val namaUser = dataSnapshot.child("fullName").getValue(String::class.java)
                namaUserEditText.setText(namaUser)
            }

            override fun onCancelled(error: DatabaseError) {
                // Menampilkan pesan error jika terjadi kesalahan
                Log.w("TAG", "Failed to read value.", error.toException())
            }
        })

        val noTelpEditText: EditText = findViewById(R.id.telpText)

        val uploadButton: Button = findViewById(R.id.upload)
        uploadButton.setOnClickListener {
            val spinner: Spinner = findViewById(R.id.cmb_rks)
            val rks = spinner.selectedItem.toString()

            val noTelp = noTelpEditText.text.toString()

            val orders = mutableListOf<DataOrders>()
            for (cart in cartList) {
                val order = DataOrders(
                    rks,
                    namaUserEditText.text.toString(),
                    noTelp,
                    cart.nama_sampah ?: "",
                    cart.jumlah_sampah ?: 0.0f,
                    ServerValue.TIMESTAMP, // Tambahkan properti timestamp
                    user.uid, // Tambahkan UID dari user yang sedang login
                    false
                )
                orders.add(order)
            }

            // Upload data orders ke dalam tabel "orders"
            val tableOrdersRef = database.getReference("orders")
            for (order in orders) {
                val key = tableOrdersRef.push().key
                if (key != null) {
                    tableOrdersRef.child(key).setValue(order)
                }
            }

            // Buat satu data orders dari seluruh data sampah pada cartList dan simpan ke dalam tabel "pesanan"
            val order = createOrderFromCartList(cartList, rks, namaUserEditText.text.toString(), noTelp,
                user.uid
            )
            val tablePesananRef = database.getReference("pesanan")
            val key = tablePesananRef.push().key
            if (key != null) {
                tablePesananRef.child(key).setValue(order)
            }

            // Tampilkan dialog upload berhasil
            showUploadSuccessDialog(key)
        }

    }

    private fun showUploadSuccessDialog(key: String?) {
        val noTelpEditText: EditText = findViewById(R.id.telpText)
        val alertDialog = AlertDialog.Builder(this)
        alertDialog.setTitle("Upload Berhasil")
        alertDialog.setMessage("Sampah berhasil diupload.")
        alertDialog.setPositiveButton("OK") { dialog, _ ->
            // Menambahkan kode untuk langsung pindah ke halaman HomeActivity
            startActivity(Intent(this, Homepage::class.java))
            dialog.dismiss()
            // Hapus data dari tabel "cart" yang memiliki id_user sama dengan id user saat ini
            val tableCartRef = database.getReference("cart")
            tableCartRef.orderByChild("id_user").equalTo(user.uid).addListenerForSingleValueEvent(object :
                ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (cartSnapshot in dataSnapshot.children) {
                        cartSnapshot.ref.removeValue()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Menampilkan pesan error jika terjadi kesalahan
                    Log.w("TAG", "Failed to read value.", error.toException())
                }
            })
            val tableAngkutRef = database.getReference("angkut")
            tableAngkutRef.orderByChild("id_user").equalTo(user.uid).addListenerForSingleValueEvent(object :
                ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (cartSnapshot in dataSnapshot.children) {
                        cartSnapshot.ref.removeValue()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Menampilkan pesan error jika terjadi kesalahan
                    Log.w("TAG", "Failed to read value.", error.toException())
                }
            })
            cartList.clear()
            noTelpEditText.setText("")

            val namaUserEditText: EditText = findViewById(R.id.namaText)

            // Send push notification to admin
            val topic = "admin"
            val title = "New Order"
            val body = "A new order has been placed by ${namaUserEditText.text}" // Assuming namaUserEditText is the name of the user who placed the order

            // Build the message payload
            val message = RemoteMessage.Builder(topic)
                .setMessageId(java.lang.String.valueOf(System.currentTimeMillis()))
                .setData(mapOf("title" to title, "body" to body, "orderId" to key)) // Assuming key is the ID of the newly placed order
                .build()

            FirebaseMessaging.getInstance().send(message)
        }
        alertDialog.show()
    }

    private fun createOrderFromCartList(cartList: List<DataCart>, rks: String, namaUser: String, noTelp: String, uid: String): DataOrders {
        var totalJumlahSampah = 0.0f
        for (cart in cartList) {
            totalJumlahSampah += cart.jumlah_sampah ?: 0.0f
        }

        return DataOrders(
            rks,
            namaUser,
            noTelp,
            cartList.map { it.nama_sampah ?: "" }.joinToString(", "),
            totalJumlahSampah,
            ServerValue.TIMESTAMP, // Tambahkan properti timestamp
            uid // Tambahkan UID dari user yang sedang login
        )
    }

    override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
        val spinner: Spinner = findViewById(R.id.cmb_rks)
        spinner.onItemSelectedListener = this
    }

    override fun onNothingSelected(parent: AdapterView<*>) {
        // Another interface callback
    }

    override fun onDestroy() {
        super.onDestroy()

        // Hapus data dari tabel "angkut" yang memiliki id_user sama dengan id user saat ini
        val tableAngkutRef = database.getReference("angkut")
        val user = FirebaseAuth.getInstance().currentUser

        tableAngkutRef.orderByChild("id_user").equalTo(user?.uid).addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (angkutSnapshot in dataSnapshot.children) {
                    angkutSnapshot.ref.removeValue()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Menampilkan pesan error jika terjadi kesalahan
                Log.w("TAG", "Failed to read value.", error.toException())
            }
        })
    }
}