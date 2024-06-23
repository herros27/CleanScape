package com.example.jopanik.Activity

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.cardview.widget.CardView
import com.example.jopanik.R
import com.example.jopanik.databinding.FragmentHomepageBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class HomepageFragment(val nama: String?) : Fragment() {

    private lateinit var binding: FragmentHomepageBinding
    private lateinit var view3: View
    private lateinit var card_rekap: CardView
    private lateinit var notifButton: ImageButton
    private lateinit var auth: FirebaseAuth

    constructor() : this(null)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentHomepageBinding.inflate(inflater, container, false)
        auth = FirebaseAuth.getInstance()
        binding.namaTextView.text = nama
        binding.cardAnorganik.setOnClickListener {
            openAnorganikActivity()
        }

        view3 = binding.root.findViewById<View>(R.id.view3)
        card_rekap = binding.root.findViewById<CardView>(R.id.card_rekap)
        notifButton = binding.root.findViewById<ImageButton>(R.id.btn_notif)
//        motionLayout = binding.root.findViewById(R.id.motion_layout)

        val user = FirebaseAuth.getInstance().currentUser
        val uid = user?.uid
        val userRef = FirebaseDatabase.getInstance("https://jopanik-399b9-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("users").child(uid!!)
        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val userData = snapshot.value as? HashMap<*, *>
                val role = userData?.get("role") as? String
                if (role == "admin") {
                    view3.visibility = View.VISIBLE // Menampilkan View jika role pengguna adalah "admin"
                    notifButton.visibility = View.VISIBLE // Menampilkan ImageButton jika role pengguna adalah "admin"
                    notifButton.setOnClickListener {
                        val intent = Intent(activity, Notification::class.java)
                        startActivity(intent)
                    }
                    card_rekap.visibility = CardView.VISIBLE
                    card_rekap.setOnClickListener {
                        val intent = Intent(activity, Recap::class.java)
                        startActivity(intent)
                    }
                } else {
                    view3.visibility = View.GONE // Menyembunyikan View jika role pengguna adalah "user"
                    notifButton.visibility = View.GONE // Menyembunyikan ImageButton jika role pengguna adalah "user"
                    card_rekap.visibility = CardView.GONE
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })


        return binding.root
    }

    private fun openAnorganikActivity() {
        val intent = Intent(activity, Anorganik::class.java)
        startActivity(intent)
    }

    companion object {
        fun newInstance(nama: String?) = HomepageFragment(nama)
    }
}