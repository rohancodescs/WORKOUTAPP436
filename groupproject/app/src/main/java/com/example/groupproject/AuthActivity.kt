package com.example.groupproject

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class AuthActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        auth = FirebaseAuth.getInstance()

        val emailEditText: EditText = findViewById(R.id.emailEditText)
        val passwordEditText: EditText = findViewById(R.id.passwordEditText)
        val signUpButton: Button = findViewById(R.id.signUpButton)
        val signInButton: Button = findViewById(R.id.signInButton)

        signUpButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()
            signUp(email, password)
        }

        signInButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()
            signIn(email, password)
        }
    }

    private fun signUp(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d("AuthActivity", "createUserWithEmail:success")
                    Toast.makeText(baseContext, "Sign Up Successful.", Toast.LENGTH_SHORT).show()
                } else {
                    Log.w("AuthActivity", "createUserWithEmail:failure", task.exception)
                    Toast.makeText(baseContext, "Sign Up Failed.", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun signIn(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d("AuthActivity", "signInWithEmail:success")
                    Toast.makeText(baseContext, "Sign In Successful.", Toast.LENGTH_SHORT).show()
                } else {
                    Log.w("AuthActivity", "signInWithEmail:failure", task.exception)
                    Toast.makeText(baseContext, "Sign In Failed.", Toast.LENGTH_SHORT).show()
                }
            }
    }
}
