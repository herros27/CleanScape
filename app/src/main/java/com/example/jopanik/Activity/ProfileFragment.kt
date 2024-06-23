package com.example.jopanik.Activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import com.example.jopanik.Activity.LoginOrRegister
import com.example.jopanik.R
import com.example.jopanik.databinding.FragmentProfileBinding

class ProfileFragment(val nama: String?) : Fragment() {

    lateinit var binding: FragmentProfileBinding
    lateinit var auth: FirebaseAuth

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        auth = FirebaseAuth.getInstance()
        val user = auth.currentUser
        binding.namaTextView.text = nama
        binding.emailTextView.text = user?.email
        binding.logout.setOnClickListener {
            logout(requireContext())
        }
        binding.keranjang.setOnClickListener {
            val intent = Intent(requireContext(), Cart::class.java)
            startActivity(intent)
        }
        binding.histori.setOnClickListener {
            val intent = Intent(requireContext(), History::class.java)
            startActivity(intent)
        }
        binding.gantipass.setOnClickListener {
            val intent = Intent(requireContext(), ForgotPassword::class.java)
            startActivity(intent)
        }
        return binding.root
    }

    fun logout(context: Context) {
        FirebaseAuth.getInstance().signOut()

        // Menghapus informasi login pengguna dari Shared Preferences
        val sharedPreferences = context.getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        with (sharedPreferences.edit()) {
            remove("email")
            remove("password")
            apply()
        }

        // Mengarahkan pengguna ke MainActivity
        val intent = Intent(context, LoginOrRegister::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        context.startActivity(intent)
    }

    companion object {
        fun newInstance(nama: String?, email: String?) = ProfileFragment(nama)
    }
}