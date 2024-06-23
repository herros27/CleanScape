package com.example.jopanik.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.jopanik.Activity.ProfileFragment
import com.example.jopanik.databinding.ActivityHomepageBinding
import com.example.jopanik.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class Homepage : AppCompatActivity() {

    lateinit var binding: ActivityHomepageBinding
    lateinit var database: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomepageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val email = intent.getStringExtra("EXTRA_EMAIL")
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        database = FirebaseDatabase.getInstance("https://jopanik-399b9-default-rtdb.asia-southeast1.firebasedatabase.app/")
        val userRef = database.getReference("users").child(uid!!)
        userRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val fullName = snapshot.child("fullName").getValue(String::class.java)
                val homepageFragment = HomepageFragment.newInstance(fullName)
                val profileFragment = ProfileFragment.newInstance(fullName, email)

                setCurrentFragment(homepageFragment)

                binding.navigationBarView.setOnItemSelectedListener { item ->
                    when (item.itemId) {
                        R.id.nav_home -> setCurrentFragment(homepageFragment)
                        R.id.nav_person -> setCurrentFragment(profileFragment)
                    }
                    true
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Menangani kesalahan pembacaan data
            }
        })
    }

    fun setCurrentFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.flFragment, fragment)
            commit()
        }
    }
    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity() // Menutup semua activity dan keluar dari aplikasi
    }
}