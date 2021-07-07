package com.example.lootbox

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
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
        var numCats : String? = ""
        var numGoals : String? = ""

        val intent: Intent = intent
        data = intent.getStringExtra("user").toString()


        var btnSettings : ImageButton = findViewById(R.id.btnGoalSetting)
        btnSettings.setOnClickListener(object: View.OnClickListener{
            override fun onClick(v: View?) {
                val intent = Intent(this@Profile,Settings::class.java).apply{}
                startActivity(intent)
            }
        })

        var btnGoals : ImageButton = findViewById(R.id.btnGoalsGoals)
        btnGoals.setOnClickListener(object: View.OnClickListener{
            override fun onClick(v: View?) {
                val intent = Intent(this@Profile,Goals::class.java).apply{}
                intent.putExtra("user", data)
                startActivity(intent)
            }
        })

        var btnHome : ImageButton = findViewById(R.id.btnHome)
        btnHome.setOnClickListener(object: View.OnClickListener{
            override fun onClick(v: View?) {
                val intent = Intent(this@Profile,Profile::class.java).apply{}
                intent.putExtra("user", data)
                startActivity(intent)
            }
        })


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


        val docRef2: DocumentReference = db.collection(data).document("categories")
        docRef2.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val document: DocumentSnapshot? = task.getResult()
                if (document != null) {
                    numCats = (document.getLong("numCats")!!.toInt()).toString()
                    val catDisp : TextView = findViewById(R.id.txtCategoriesStats)
                    catDisp.text = numCats

                } else {
                    Log.d("LOGGER", "No such document")
                }
            } else {
                Log.d("LOGGER", "get failed with ", task.exception)
            }
        }

        val docRef3: DocumentReference = db.collection(data).document("goals")
        docRef3.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val document: DocumentSnapshot? = task.getResult()
                if (document != null) {
                    numGoals = (document.getLong("numGoals")!!.toInt()).toString()
                    val goalDisp : TextView = findViewById(R.id.txtGoalStats)
                    goalDisp.text = numGoals

                } else {
                    Log.d("LOGGER", "No such document")
                }
            } else {
                Log.d("LOGGER", "get failed with ", task.exception)
            }
        }
    }
}