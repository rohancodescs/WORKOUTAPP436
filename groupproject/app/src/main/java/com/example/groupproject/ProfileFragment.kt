package com.example.groupproject

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import com.example.groupproject.databinding.FragmentProfileBinding
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.android.material.snackbar.Snackbar

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference

        binding.saveProfileButton.setOnClickListener {
            saveProfileData()
            Snackbar.make(view, "Profile saved!", Snackbar.LENGTH_SHORT).show()
        }

        binding.fitnessLevelSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                // Handle SeekBar change
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        loadProfileData()

        // Initialize MobileAds
        MobileAds.initialize(requireContext()) {}

        // Load Ad
        val adRequest = AdRequest.Builder().build()
        binding.adView.loadAd(adRequest)
    }

    private fun saveProfileData() {
        val feet = binding.feetEditText.text.toString()
        val inches = binding.inchesEditText.text.toString()
        val pounds = binding.poundsEditText.text.toString()
        val years = binding.yearsEditText.text.toString()
        val fitnessLevel = binding.fitnessLevelSeekBar.progress

        if (feet.isNotEmpty() && inches.isNotEmpty() && pounds.isNotEmpty() && years.isNotEmpty()) {
            val userId = auth.currentUser?.uid ?: return
            val profile = Profile(feet, inches, pounds, years, fitnessLevel)

            database.child("users").child(userId).child("profile").setValue(profile)
        }
    }

    private fun loadProfileData() {
        val userId = auth.currentUser?.uid ?: return

        database.child("users").child(userId).child("profile").get().addOnSuccessListener { dataSnapshot ->
            val profile = dataSnapshot.getValue(Profile::class.java)
            profile?.let {
                binding.feetEditText.setText(it.feet)
                binding.inchesEditText.setText(it.inches)
                binding.poundsEditText.setText(it.pounds)
                binding.yearsEditText.setText(it.years)
                binding.fitnessLevelSeekBar.progress = it.fitnessLevel
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

data class Profile(val feet: String = "", val inches: String = "", val pounds: String = "", val years: String = "", val fitnessLevel: Int = 0)
