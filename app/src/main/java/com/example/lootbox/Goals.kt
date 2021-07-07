package com.example.lootbox

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore

class Goals : AppCompatActivity() {
    private var titlesList = mutableListOf<String>()
    private var descriptionList = mutableListOf<String>()
    private var imageList = mutableListOf<Int>()
    private var statusList = mutableListOf<String>()
    private var data = " "
    var dbGoalCount : Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_goals)

        val intent = intent
        data = intent.getStringExtra("user").toString()

        var rcvItemList : RecyclerView = findViewById(R.id.rcvGoalsList)
        rcvItemList.layoutManager = LinearLayoutManager(this)
        rcvItemList.adapter = Goal_RecAdapter(titlesList,descriptionList,imageList,statusList)

        getFromDB()

    }

    private fun getFromDB(){
        Toast.makeText(this@Goals, "in ", Toast.LENGTH_LONG).show()
        var goalsFound : List<String> = emptyList()
        val db = FirebaseFirestore.getInstance()

        val docRef = db.collection(data).document("goals")
        docRef.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val document: DocumentSnapshot? = task.getResult()
                if (document != null) {
                    dbGoalCount = document.getLong("numGoals")!!.toInt()
                    goalsFound = document.get("existing") as List<String>
                    var i : Int = 0
                    do {
                        val docRef = db.collection(data).document("goals").collection(goalsFound.get(i)).document("goalInfo")
                        docRef.get().addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val document: DocumentSnapshot? = task.getResult()
                                if (document != null) {
                                    val title = document.getString("tilte") as String
                                    var desc = document.getString("category")as String
                                    var status = document.getString("status")as String

                                    titlesList.add(title)
                                    descriptionList.add(desc)
                                    imageList.add(R.drawable.launcher_icon)
                                    statusList.add(status)
                                    var recView :RecyclerView = findViewById(R.id.rcvGoalsList)
                                    recView.layoutManager = LinearLayoutManager(this)
                                    recView.adapter = Goal_RecAdapter(titlesList,descriptionList,imageList,statusList)
                                }
                                else
                                {
                                    Log.d("LOGGER", "No such document")
                                }
                            }
                            else
                            {
                                Log.d("LOGGER", "get failed with ", task.exception)

                            }
                        }
                        i++
                    }while(i < dbGoalCount)
                }
                else
                {
                    Log.d("LOGGER", "No such document")
                }
            }
            else
            {
                Log.d("LOGGER", "get failed with ", task.exception)
            }
        }
    }
}