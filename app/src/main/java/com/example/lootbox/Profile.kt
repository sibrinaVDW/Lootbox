package com.example.lootbox

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
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
        var date : String? = ""
        var name : String? = ""

        val intent: Intent = intent
        data = intent.getStringExtra("user").toString()

        val db = FirebaseFirestore.getInstance()
        val docRef: DocumentReference = db.collection(data).document("details")
        docRef.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val document: DocumentSnapshot? = task.getResult()
                if (document != null) {
                    email = document.getString("email")
                    val emailDisp : TextView = findViewById(R.id.txtProfileEmail)
                    emailDisp.text = email
                    Toast.makeText(this@Profile, email, Toast.LENGTH_LONG).show()

                    date = document.getString("date")
                    val dateDisp : TextView = findViewById(R.id.txtProfileBirthdate)
                    dateDisp.text = date
                    Toast.makeText(this@Profile, date, Toast.LENGTH_LONG).show()

                    name = document.getString("name")
                    val usernameDisp : TextView = findViewById(R.id.txtProfileName)
                    usernameDisp.text = name
                    Toast.makeText(this@Profile, name, Toast.LENGTH_LONG).show()

                } else {
                    Log.d("LOGGER", "No such document")
                }
            } else {
                Log.d("LOGGER", "get failed with ", task.exception)
            }
        }
    }
}