package com.example.groupproject

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.groupproject.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        // Check if user is signed in
        val currentUser = auth.currentUser
        if (currentUser == null) {
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
            finish()
            return
        }

        // Initial Fragment
        replaceFragment(ProfileFragment())

        binding.homeButton.setOnClickListener {
            replaceFragment(ProfileFragment())
        }

        binding.calorieCounterButton.setOnClickListener {
            replaceFragment(CalorieCounterFragment())
        }

        binding.exerciseButton.setOnClickListener {
            replaceFragment(ExerciseTrackerFragment())
        }

        binding.stepCounterButton.setOnClickListener {
            replaceFragment(StepCounterFragment())
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_sign_out -> {
                auth.signOut()
                val intent = Intent(this, SignInActivity::class.java)
                startActivity(intent)
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragmentContainer, fragment)
        fragmentTransaction.commit()
    }
}
