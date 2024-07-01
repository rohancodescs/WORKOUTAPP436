package com.example.groupproject

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.groupproject.databinding.FragmentCalorieCounterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class CalorieCounterFragment : Fragment() {

    private var _binding: FragmentCalorieCounterBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCalorieCounterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference

        binding.addMeal.setOnClickListener {
            addMeal()
        }

        binding.clearMeals.setOnClickListener {
            clearMeals()
        }

        binding.healthRatingBar.setOnRatingBarChangeListener { ratingBar, rating, fromUser ->
            //handle rating change
        }

        loadMeals()
    }

    private fun addMeal() {
        val meal = binding.mealET.text.toString()
        val calories = binding.calorieET.text.toString()
        val healthRating = binding.healthRatingBar.rating

        if (meal.isNotEmpty() && calories.isNotEmpty()) {
            val userId = auth.currentUser?.uid ?: return
            val mealData = Meal(meal, calories.toInt(), healthRating)

            database.child("users").child(userId).child("meals").push().setValue(mealData)

            loadMeals()
        }
    }

    private fun loadMeals() {
        val userId = auth.currentUser?.uid ?: return

        database.child("users").child(userId).child("meals").get().addOnSuccessListener { dataSnapshot ->
            val meals = mutableListOf<String>()
            var totalCalories = 0
            dataSnapshot.children.forEach { mealSnapshot ->
                val meal = mealSnapshot.getValue(Meal::class.java)
                meal?.let {
                    meals.add("${it.name}: ${it.calories} calories, Rating: ${it.healthRating}")
                    totalCalories += it.calories
                }
            }
            binding.mealList.text = meals.joinToString("\n")
            binding.totalCalories.text = "Total Calories: $totalCalories"
        }
    }

    private fun clearMeals() {
        val userId = auth.currentUser?.uid ?: return

        database.child("users").child(userId).child("meals").removeValue()
        binding.mealList.text = ""
        binding.totalCalories.text = "Total Calories: 0"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

data class Meal(val name: String = "", val calories: Int = 0, val healthRating: Float = 0f)
