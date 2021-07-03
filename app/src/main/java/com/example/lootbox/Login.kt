package com.example.lootbox

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

class Login : AppCompatActivity() {
    private  var mAuth: FirebaseAuth? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        mAuth = FirebaseAuth.getInstance();
        val db = FirebaseFirestore.getInstance()
        var washingtonRef = db.collection("cities");
    }
    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = mAuth!!.currentUser
        if(currentUser != null){
            Log.d(TAG, currentUser.displayName.toString())
        }

        val signUp = findViewById<Button>(R.id.btnSignUp)
        val username = findViewById<EditText>(R.id.email)
        val password = findViewById<EditText>(R.id.password)
        val signIn = findViewById<ImageButton>(R.id.btnSignIn)

        signUp.setOnClickListener {
           // createAccount(username.text.toString(), password.text.toString())
            val intent = Intent(this@Login,NewUserAccount::class.java).apply{}
            startActivity(intent)
        }

        signIn.setOnClickListener {
            login(username.text.toString(), password.text.toString())
            val db = FirebaseFirestore.getInstance()
            val user: MutableMap<String, Any> = HashMap()
            db.collection("users")
                .add(user)
                .addOnSuccessListener {
                    Toast.makeText(this@Login, "record added successfully ", Toast.LENGTH_SHORT ).show()
                }
                .addOnFailureListener{
                    Toast.makeText(this@Login, "record Failed to add ", Toast.LENGTH_SHORT ).show()
                }
        }
    }

    private fun reload() {
        TODO("Not yet implemented")
    }

    companion object {
        private const val TAG = "EmailPassword"
    }

    private fun updateUI(currentUser: FirebaseUser?) {
        if (currentUser != null) {
            Log.d(TAG, currentUser.displayName.toString())
        }
    }

    private fun createAccount(email: String, password: String) {
        mAuth!!.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this@Login) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "createUserWithEmail:success")
                    val user = mAuth!!.currentUser
                    updateUI(user)
                    val intent = Intent(this@Login,CollectionsView::class.java).apply{}
                    startActivity(intent)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                    updateUI(null)
                }
            }
    }

    private fun login(email: String, password: String)
    {
        mAuth!!.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithEmail:success")
                    val user = mAuth!!.currentUser
                    updateUI(user)
                    val intent = Intent(this@Login,CollectionsView::class.java).apply{}
                    startActivity(intent)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                    Toast.makeText(baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                    updateUI(null)
                }
            }
    }

}
