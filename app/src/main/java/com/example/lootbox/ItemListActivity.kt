package com.example.lootbox

import android.animation.ObjectAnimator
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


private const val REQUEST_CODE = 42
class ItemListActivity : AppCompatActivity() {

    private var titlesList = mutableListOf<String>()
    private var descriptionList = mutableListOf<String>()
    private var imageList = mutableListOf<Int>()
    private var dateList = mutableListOf<String>()
    var dateAccquireDisp : TextView? = null
    var dateReleasedDisp : TextView? = null

    var viewImage :  ImageButton? = null
    var numItems : Int = 0
    var itemsGathered : Int = 0
    var dbItemCount : Int = 0
    var dbGoalCount : Int = 0
    private var goalAmount :Int = 0
    private var categoryPass : String? = ""
    private var catSize :Int = 50
    private var donutOpen : Boolean = false;
    var accquiredCal = Calendar.getInstance()
    var releasedCal = Calendar.getInstance()
    val myFormat = "dd/MM/yyyy"
    val sdf = SimpleDateFormat(myFormat, Locale.UK)
    var itemL = arrayListOf<String>()
    var goalL = arrayListOf<String>()

    var donutPanel : ImageView? = null
    var donutBack : ProgressBar? = null
    var donutProg : ProgressBar? = null
    var progText : TextView? = null

    private var data = " "


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item_list)

        val intent = intent
        data = intent.getStringExtra("user").toString()
        categoryPass = intent.getStringExtra("Category")

        donutPanel = findViewById(R.id.imgDonutBack)
        donutBack  = findViewById(R.id.background_donut)
        donutProg  = findViewById(R.id.donut_progressbar)
        progText  = findViewById(R.id.txtCatSize)
        donutPanel!!.visibility = View.GONE
        donutBack!!.visibility = View.GONE
        donutProg!!.visibility = View.GONE
        progText!!.visibility = View.GONE

        donutOpen = false;

        val db = FirebaseFirestore.getInstance()
        
        var rcvItemList : RecyclerView = findViewById(R.id.rcvItemList)
        rcvItemList.layoutManager = LinearLayoutManager(this)
        rcvItemList.adapter = ItemsRecyclerAdapter(titlesList,descriptionList,imageList,dateList,categoryPass!!,data)


        var goalIndic :TextView = findViewById<TextView>(R.id.txtGoal)
        goalIndic.text = "You have $numItems out of $goalAmount items collected"
        var catDisp : TextView =  findViewById(R.id.txtCatName)
        catDisp.text = categoryPass
        //progress bar
        val pb = findViewById<ProgressBar>(R.id.pb)
        val docRef1 = db.collection(data).document("categories").collection(categoryPass!!).document("info")
        docRef1.get().addOnCompleteListener { task ->
            if(task.isSuccessful){
                val document : DocumentSnapshot? = task.getResult()
                if(document != null){
                    goalAmount = document.getString("goal")!!.toInt()
                    itemsGathered = document.getLong("itemsGathered")!!.toInt()
                    goalIndic.text = "You have $itemsGathered out of $goalAmount items collected"
                    pb.max = goalAmount

                    if(itemsGathered == goalAmount)
                    {
                        //goal achieved, add to DB, do popup, do prompt 4 new goal
                        val newGoalPopUp = LayoutInflater.from(this@ItemListActivity)
                            .inflate(R.layout.next_goal_popup, null)
                        val alertBuilder = AlertDialog.Builder(this@ItemListActivity).setView(newGoalPopUp)
                            .setTitle("Add New Goal")
                        val alertDialog = alertBuilder.show()
                        //adding new goal to list
                        val btnNewGoal : ImageButton = newGoalPopUp.findViewById(R.id.btnNewGoalCreate)
                        btnNewGoal.setOnClickListener(object : View.OnClickListener{
                            override fun onClick(v:View) {

                                var goalTitle : String = "Collect " + goalAmount.toString() + " New Items"
                                var edtNewGoal :String =  newGoalPopUp.findViewById<TextView>(R.id.edtNewGoal).text.toString()
                                goalAmount = (edtNewGoal).toInt()

                                val db = FirebaseFirestore.getInstance()
                                val user = hashMapOf(
                                    "tilte" to goalTitle,
                                    "category" to categoryPass,
                                    "status" to "Complete",
                                )
                                db.collection(data)
                                    .document("goals").collection(goalTitle).document("goalInfo").set(user)

                                val docRef = db.collection(data).document("categories").collection(categoryPass!!).document("info")
                                docRef.update("goal", goalAmount.toString())
                                //restart goal
                                itemsGathered = 0
                                docRef.update("itemsGathered", itemsGathered)
                                var newGoalIndic :TextView = findViewById<TextView>(R.id.txtGoal)
                                newGoalIndic.text = "You have $itemsGathered out of $goalAmount items collected"

                                var goalsFound : List<String> = emptyList()
                                val docRef2 = db.collection(data).document("goals")
                                docRef2.get().addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        val document: DocumentSnapshot? = task.getResult()
                                        if(document != null){
                                            dbGoalCount = document.getLong("numGoals")!!.toInt()
                                            dbGoalCount++
                                            //goalsFound = document.get("existing") as List<String>
                                            goalL = document.get("existing") as ArrayList<String>
                                            goalL.add(goalTitle)

                                            //goalsFound.toMutableList().add(goalTitle)
                                            db.collection(data).document("goals").update("numGoals",dbGoalCount)
                                            db.collection(data).document("goals").update("existing",goalL)
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
                                alertDialog.dismiss()
                            }})

                    }

                } else {
                    Log.d("Logger", "No such document")
                }
            } else {
                Log.d("Logger", "get failed with", task.exception)
            }
        }

        val currentProgress = itemsGathered;
        ObjectAnimator.ofInt(pb,"Progress",currentProgress)
            .setDuration(2000)
            .start()

        val docRef: DocumentReference = db.collection(data).document("categories").collection(categoryPass!!).document("items")
        docRef.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val document: DocumentSnapshot? = task.getResult()
                if (document != null) {
                    dbItemCount = document.getLong("numItems")!!.toInt()
                    Toast.makeText(this@ItemListActivity, "doc found ", Toast.LENGTH_LONG).show()
                    if(dbItemCount != 0){
                        getFromDB()
                    }
                } else {
                    Log.d("LOGGER", "No such document")
                }
            } else {
                Log.d("LOGGER", "get failed with ", task.exception)
            }
        }

        numItems = rcvItemList?.adapter!!.itemCount

        var btnSettings : ImageButton = findViewById(R.id.btnSettings)
        btnSettings.setOnClickListener(object: View.OnClickListener{
            override fun onClick(v: View?) {
                val intent = Intent(this@ItemListActivity,Settings::class.java).apply{}
                startActivity(intent)
            }
        })

        var btnProfile : ImageButton = findViewById(R.id.btnProfile)
        btnProfile.setOnClickListener(object: View.OnClickListener{
            override fun onClick(v: View?) {
                val intent = Intent(this@ItemListActivity,Profile::class.java).apply{}
                intent.putExtra("user", data)
                startActivity(intent)
            }
        })

        var btnGoals : ImageButton = findViewById(R.id.btnGoals)
        btnGoals.setOnClickListener(object: View.OnClickListener{
            override fun onClick(v: View?) {
                val intent = Intent(this@ItemListActivity,Goals::class.java).apply{}
                intent.putExtra("user", data)
                startActivity(intent)
            }
        })

        var btnHome : ImageButton = findViewById(R.id.btnHome)
        btnHome.setOnClickListener(object: View.OnClickListener{
            override fun onClick(v: View?) {
                val intent = Intent(this@ItemListActivity,CollectionsView::class.java).apply{}
                intent.putExtra("user", data)
                startActivity(intent)
            }
        })

        val btnDonut : ImageButton = findViewById(R.id.btnDonut)
        btnDonut.setOnClickListener(object : View.OnClickListener{
            override fun onClick(v:View){
                if(donutOpen == false){
                    DisplayChart()
                    donutPanel!!.visibility = View.VISIBLE
                    donutProg!!.visibility = View.VISIBLE
                    donutBack!!.visibility = View.VISIBLE
                    progText!!.visibility = View.VISIBLE
                    donutOpen = true
                }
                else{
                    donutPanel!!.visibility = View.GONE
                    donutBack!!.visibility = View.GONE
                    donutProg!!.visibility = View.GONE
                    progText!!.visibility = View.GONE
                    donutOpen = false;
                }
            }
        })

        val btnAdd : ImageButton = findViewById(R.id.btnAdd)
        btnAdd.setOnClickListener(object : View.OnClickListener{
            override fun onClick(v:View){
                val diagPopUp = LayoutInflater.from(this@ItemListActivity).inflate(R.layout.itempopup,null)
                val alertBuilder = AlertDialog.Builder(this@ItemListActivity).setView(diagPopUp).setTitle("Add Game")
                val alertDialog = alertBuilder.show()
                dateAccquireDisp = diagPopUp.findViewById(R.id.txtEnterCollectedDate)
                dateReleasedDisp = diagPopUp.findViewById(R.id.txtenterReleaseDate)

                val takeAPicture = diagPopUp.findViewById<ImageButton>(R.id.imgGameImage)

                takeAPicture.setOnClickListener {
                    val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    viewImage = diagPopUp.findViewById<ImageButton>(R.id.imgGameImage)
                    if (takePictureIntent.resolveActivity(this@ItemListActivity.packageManager) != null) {
                        startActivityForResult(takePictureIntent, REQUEST_CODE)
                    } else {
                        Toast.makeText(this@ItemListActivity, "Unable to open camera", Toast.LENGTH_LONG).show()
                    }
                }

                //region Date for when accquired
                val dateASetListener = object : DatePickerDialog.OnDateSetListener {
                    override fun onDateSet(view: DatePicker, year: Int, monthOfYear: Int,
                                           dayOfMonth: Int) {
                        accquiredCal.set(Calendar.YEAR, year)
                        accquiredCal.set(Calendar.MONTH, monthOfYear)
                        accquiredCal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                        dateAccquireDisp?.text = sdf.format(accquiredCal.getTime())
                    }
                }

                val chooseDate = diagPopUp.findViewById<TextView>(R.id.txtEnterCollectedDate)
                chooseDate.setOnClickListener(object : View.OnClickListener  {
                    override fun onClick(view: View) {
                        DatePickerDialog(this@ItemListActivity,
                            dateASetListener,
                            accquiredCal.get(Calendar.YEAR),
                            accquiredCal.get(Calendar.MONTH),
                            accquiredCal.get(Calendar.DAY_OF_MONTH)).show()
                    }
                })
                //endregion

                //region Date for when game was released
                val dateRSetListener = object : DatePickerDialog.OnDateSetListener {
                    override fun onDateSet(view: DatePicker, year: Int, monthOfYear: Int,
                                           dayOfMonth: Int) {
                        releasedCal.set(Calendar.YEAR, year)
                        releasedCal.set(Calendar.MONTH, monthOfYear)
                        releasedCal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                        dateReleasedDisp?.text = sdf.format(releasedCal.getTime())
                    }
                }

                val chooseRealDate = diagPopUp.findViewById<TextView>(R.id.txtenterReleaseDate)
                chooseRealDate.setOnClickListener(object : View.OnClickListener  {
                    override fun onClick(view: View) {
                        DatePickerDialog(this@ItemListActivity,
                            dateRSetListener,
                            releasedCal.get(Calendar.YEAR),
                            releasedCal.get(Calendar.MONTH),
                            releasedCal.get(Calendar.DAY_OF_MONTH)).show()
                    }
                })
                //endregion


                var add : ImageButton = diagPopUp.findViewById(R.id.btnAddGameToList)

                add.setOnClickListener(object : View.OnClickListener {
                    override fun onClick(v : View){
                        val gameName = diagPopUp.findViewById<EditText>(R.id.txtEnterGameName).text.toString()
                        val gameDescription = diagPopUp.findViewById<EditText>(R.id.txtEnterGameDescription).text.toString()
                        val publisher = diagPopUp.findViewById<EditText>(R.id.txtEnterPublishersName).text.toString()
                        addToList(gameName,gameDescription,R.drawable.launcher_icon,sdf.format(accquiredCal.getTime()),publisher,sdf.format(releasedCal.getTime()))
                        rcvItemList.layoutManager = LinearLayoutManager(this@ItemListActivity)
                        rcvItemList.adapter = ItemsRecyclerAdapter(titlesList,descriptionList,imageList,dateList,categoryPass!!,data)
                        numItems = rcvItemList?.adapter!!.itemCount

                        val docRef1 = db.collection(data).document("categories").collection(categoryPass!!).document("info")
                        docRef1.get().addOnCompleteListener { task ->
                            if(task.isSuccessful){
                                val document : DocumentSnapshot? = task.getResult()
                                if(document != null){
                                    itemsGathered = document.getLong("itemsGathered")!!.toInt()
                                    itemsGathered++
                                    val currentProgress = itemsGathered;
                                    ObjectAnimator.ofInt(pb,"Progress",currentProgress)
                                        .setDuration(2000)
                                        .start()
                                    goalIndic.text = "You have $itemsGathered out of $goalAmount items collected"

                                    val docRef = db.collection(data).document("categories").collection(categoryPass!!).document("info")
                                    docRef.update("itemsGathered", itemsGathered)
                                } else {
                                    Log.d("Logger", "No such document")
                                }
                            } else {
                                Log.d("Logger", "get failed with", task.exception)
                            }
                        }
                        alertDialog.dismiss()
                    }
                })

                var cancel : ImageButton = diagPopUp.findViewById<ImageButton>(R.id.btnCancelAdd)
                cancel.setOnClickListener(object : View.OnClickListener {
                    override fun onClick(v: View?) {
                        alertDialog.dismiss()
                    }
                })

            }
        })
    }

    private fun checkGoal(){

    }

    private fun getFromDB(){
        Toast.makeText(this@ItemListActivity, "in ", Toast.LENGTH_LONG).show()
        var itemsFound : List<String> = emptyList()
        val db = FirebaseFirestore.getInstance()

        val docRef = db.collection(data).document("categories").collection((categoryPass!!)).document("items")
        docRef.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val document: DocumentSnapshot? = task.getResult()
                if (document != null) {
                    dbItemCount = document.getLong("numItems")!!.toInt()
                    itemsFound = document.get("existing") as List<String>
                    var i : Int = 0
                    do {
                        val docRef = db.collection(data).document("categories").collection(categoryPass!!).document("items").collection(itemsFound.get(i)).document("itemInfo")
                        docRef.get().addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val document: DocumentSnapshot? = task.getResult()
                                if (document != null) {
                                    val title = document.getString("tilte") as String
                                    var desc = document.getString("description")as String
                                    var image = document.getLong("image")!!.toInt()
                                    var date = document.getString("date")as String

                                    titlesList.add(title)
                                    descriptionList.add(desc)
                                    imageList.add(image)
                                    dateList.add(date)
                                    var recView :RecyclerView = findViewById(R.id.rcvItemList)
                                    recView.layoutManager = LinearLayoutManager(this)
                                    recView.adapter = ItemsRecyclerAdapter(titlesList,descriptionList,imageList,dateList,categoryPass!!,data)

                                    itemL.add(title)
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
                    }while(i < dbItemCount)
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

    fun addToList(title: String, description: String, image:Int, date:String, publisher:String,dateReal:String){
        titlesList.add(title)
        descriptionList.add(description)
        imageList.add(image)
        dateList.add(date)
        dbItemCount++

        val db = FirebaseFirestore.getInstance()
        val user = hashMapOf(
            "tilte" to title,
            "description" to description,
            "image" to image,
            "date" to date,
            "release date" to dateReal,
            "publisher" to publisher
            )
        db.collection(data)
            .document("categories").collection(categoryPass!!).document("items").collection(title).document("itemInfo").set(user)

        val newItm = hashMapOf(
            "numItems" to dbItemCount,
        )
        db.collection(data)
            .document("categories").collection(categoryPass!!).document("items").set(newItm)

        itemL.add(title)

        val docRef = db.collection(data).document("categories").collection(categoryPass!!).document("items")
        docRef.update("existing", itemL)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val takenImage = data?.extras?.get("data") as Bitmap
            viewImage?.setImageBitmap(takenImage)
        } else {
            super.onActivityResult(requestCode, requestCode, data)
        }
    }

    fun DisplayChart() {
        // Update the text in a center of the chart:
        val db = FirebaseFirestore.getInstance()

        val docRef = db.collection(data).document("categories").collection((categoryPass!!)).document("items")
        docRef.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val document: DocumentSnapshot? = task.getResult()
                if(document != null){
                    dbItemCount = document.getLong("numItems")!!.toInt()
                    progText!!.setText(java.lang.String.valueOf(dbItemCount).toString() + " / " + catSize)

                    // Calculate the slice size and update the pie chart:
                    val d = dbItemCount.toDouble() / catSize.toDouble()
                    val progress = (d * 100).toInt()
                    Toast.makeText(this@ItemListActivity, d.toString() + "  "+progress.toString(), Toast.LENGTH_LONG).show()
                    progText!!.setText(java.lang.String.valueOf(dbItemCount).toString() + " / " + catSize + "\n\t" + java.lang.String.valueOf(Math.round((d * 100)* 100) / 100.0).toString() + "% collected.")
                    donutBack!!.max = catSize
                    donutBack!!.progress = catSize
                    donutProg!!.progress = progress
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

