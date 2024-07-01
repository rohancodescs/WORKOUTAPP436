package com.example.groupproject

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.groupproject.databinding.FragmentExerciseTrackerBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class ExerciseTrackerFragment : Fragment() {

    private var _binding: FragmentExerciseTrackerBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentExerciseTrackerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference

        binding.addExerciseButton.setOnClickListener {
            saveExercise()
        }

        binding.clearExercisesButton.setOnClickListener {
            clearExercises()
        }

        loadExercises()
    }

    private fun saveExercise() {
        val exercise = binding.exerciseInput.text.toString()
        val duration = binding.durationInput.text.toString().toIntOrNull()

        if (exercise.isNotEmpty() && duration != null) {
            val userId = auth.currentUser?.uid ?: return
            val exerciseEntry = ExerciseEntry(exercise, duration)

            database.child("users").child(userId).child("exercises").push().setValue(exerciseEntry)
        }
    }

    private fun loadExercises() {
        val userId = auth.currentUser?.uid ?: return

        database.child("users").child(userId).child("exercises").get().addOnSuccessListener { dataSnapshot ->
            val exercises = mutableListOf<String>()
            dataSnapshot.children.forEach { snapshot ->
                val exercise = snapshot.getValue(ExerciseEntry::class.java)
                exercise?.let {
                    exercises.add("${it.name}: ${it.duration} minutes")
                }
            }
            binding.exerciseList.text = exercises.joinToString("\n")
        }
    }

    private fun clearExercises() {
        val userId = auth.currentUser?.uid ?: return
        database.child("users").child(userId).child("exercises").removeValue()
        binding.exerciseList.text = ""
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

data class ExerciseEntry(val name: String = "", val duration: Int = 0)
