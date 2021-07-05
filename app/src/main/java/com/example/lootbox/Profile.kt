package com.example.lootbox

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore


class Profile : AppCompatActivity() {
    private var data = " "

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        var email : String? = ""

        val intent: Intent = intent
        data = intent.getStringExtra("user").toString()

        val db = FirebaseFirestore.getInstance()
        val docRef: DocumentReference = db.collection("users").document("details")
        docRef.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val document: DocumentSnapshot? = task.getResult()
                if (document != null) {
                    email = document.getString("email")
                } else {
                    Log.d("LOGGER", "No such document")
                }
            } else {
                Log.d("LOGGER", "get failed with ", task.exception)
            }
        }

        val emailDisp : TextView = findViewById(R.id.txtProfileEmail)
        emailDisp.text = email
    }
}