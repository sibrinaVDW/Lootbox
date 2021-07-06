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
        }
    }

    private fun reload() {
        TODO("Not yet implemented")
    }

    companion object {
        private const val TAG = "EmailPassword"
    }

    private fun updateUI(currentUser: FirebaseUser?) {
        val db = FirebaseFirestore.getInstance()
        val user = hashMapOf(
            "email" to currentUser?.email,
            "UID" to currentUser?.uid
        )
        db.collection(currentUser!!.uid)
            .document("details").set(user)
            .addOnSuccessListener { documentReference ->
                Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference}")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
            }

        /*val catView = hashMapOf(
            "existing" to "",
            "numCats" to 0
        )
        db.collection((currentUser!!.uid)).document("categories").set(catView)*/
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
                    intent.putExtra("user", user?.uid.toString())
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
