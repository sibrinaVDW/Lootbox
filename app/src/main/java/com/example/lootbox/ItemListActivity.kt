package com.example.lootbox

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*


private const val REQUEST_CODE = 42
class ItemListActivity : AppCompatActivity() {

    private var titlesList = mutableListOf<String>()
    private var descriptionList = mutableListOf<String>()
    private var imageList = mutableListOf<Int>()
    private var dateList = mutableListOf<String>()
    var dateDisp : TextView? = null

    var viewImage :  ImageButton? = null
    var numItems : Int = 0
    private var goalAmount :Int = 0
    private var categoryPass : String? = ""
    private var catSize :Int = 1000
    private var donutOpen : Boolean = false;
    var cal = Calendar.getInstance()
    val myFormat = "dd/MM/yyyy"
    val sdf = SimpleDateFormat(myFormat, Locale.UK)

    var donutPanel : ImageView? = null
    var donutBack : ProgressBar? = null
    var donutProg : ProgressBar? = null
    var progText : TextView? = null

    private var data = " "

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item_list)
        addToList("COD MW2","Modern Warefare of COD franchise",R.drawable.launcher_icon,"24/08/2017")
        addToList("Last of US 2","Second installment of the LOU franchise",R.drawable.launcher_icon,"24/08/2017")


        var data = intent.getStringExtra("user")

        donutPanel = findViewById(R.id.imgDonutBack)
        donutBack  = findViewById(R.id.background_donut)
        donutProg  = findViewById(R.id.donut_progressbar)
        progText  = findViewById(R.id.txtCatSize)

        /*donutPanel!!.visibility = View.GONE
        donutBack!!.visibility = View.GONE
        donutProg!!.visibility = View.GONE
        progText!!.visibility = View.GONE*/
        DisplayChart()
        donutOpen = true;

        val intent = intent
        data = intent.getStringExtra("user").toString()
        goalAmount = intent.getIntExtra("Goal",0)
        categoryPass = intent.getStringExtra("Category")

        var rcvItemList : RecyclerView = findViewById(R.id.rcvItemList)
        rcvItemList.layoutManager = LinearLayoutManager(this)
        rcvItemList.adapter = ItemsRecyclerAdapter(titlesList,descriptionList,imageList,dateList)
        numItems = rcvItemList?.adapter!!.itemCount

        var goalIndic :TextView = findViewById<TextView>(R.id.txtGoal)
        goalIndic.text = "You have $numItems out of $goalAmount items collected"
        var catDisp : TextView =  findViewById(R.id.txtCatName)
        catDisp.text = categoryPass

        var btnSettings : ImageButton = findViewById(R.id.btnSettings)
        btnSettings.setOnClickListener(object: View.OnClickListener{
            override fun onClick(v: View?) {
                val intent = Intent(this@ItemListActivity,Settings::class.java).apply{}
                startActivity(intent)
            }
        })

        val btnDonut : ImageButton = findViewById(R.id.btnDonut)
        btnDonut.setOnClickListener(object : View.OnClickListener{
            override fun onClick(v:View){
                if(donutOpen == false){
                    DisplayChart()
                    donutPanel!!.visibility = View.VISIBLE
                    //donutBack!!.visibility = View.VISIBLE
                    //donutProg!!.visibility = View.VISIBLE
                    runOnUiThread {

                        donutProg!!.visibility = View.VISIBLE
                        donutBack!!.visibility = View.VISIBLE
                    }
                    //donutBack!!.setVisibility(View.VISIBLE);
                    progText!!.visibility = View.VISIBLE
                    //DisplayChart()
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
                dateDisp = diagPopUp.findViewById(R.id.txtEnterCollectedDate)

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

                val dateSetListener = object : DatePickerDialog.OnDateSetListener {
                    override fun onDateSet(view: DatePicker, year: Int, monthOfYear: Int,
                                           dayOfMonth: Int) {
                        cal.set(Calendar.YEAR, year)
                        cal.set(Calendar.MONTH, monthOfYear)
                        cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                        dateDisp?.text = sdf.format(cal.getTime())
                    }
                }

                val chooseDate = diagPopUp.findViewById<TextView>(R.id.txtEnterCollectedDate)
                chooseDate.setOnClickListener(object : View.OnClickListener  {
                    override fun onClick(view: View) {
                        DatePickerDialog(this@ItemListActivity,
                            dateSetListener,
                            cal.get(Calendar.YEAR),
                            cal.get(Calendar.MONTH),
                            cal.get(Calendar.DAY_OF_MONTH)).show()
                    }
                })

                var add : ImageButton = diagPopUp.findViewById(R.id.btnAddGameToList)

                add.setOnClickListener(object : View.OnClickListener {
                    override fun onClick(v : View){
                        val gameName = diagPopUp.findViewById<EditText>(R.id.txtEnterGameName).text.toString()
                        val gameDescription = diagPopUp.findViewById<EditText>(R.id.txtEnterGameDescription).text.toString()
                        addToList(gameName,gameDescription,R.drawable.launcher_icon,sdf.format(cal.getTime()))
                        rcvItemList.layoutManager = LinearLayoutManager(this@ItemListActivity)
                        rcvItemList.adapter = ItemsRecyclerAdapter(titlesList,descriptionList,imageList,dateList)
                        numItems = rcvItemList?.adapter!!.itemCount
                        goalIndic.text = "You have $numItems out of $goalAmount items collected"
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

    fun addToList(title: String, description: String, image:Int, date:String){
        titlesList.add(title)
        descriptionList.add(description)
        imageList.add(image)
        dateList.add(date)


        val db = FirebaseFirestore.getInstance()
        val user = hashMapOf(
            "tilte" to title,
            "description" to description,
            "image" to image,
            "date" to date,

            )
        db.collection(data)
            .document("items").collection(categoryPass!!).add(user)
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
        //val numberOfItems = findViewById<TextView>(R.id.txtCatSize)
        progText!!.setText(java.lang.String.valueOf(numItems).toString() + " / " + catSize)
        //numberOfItems.setText(Integer.toString(numItems)+ " / " + Integer.toString(catSize))
        //numberOfItems.text = numItems.toString() + "/" + catSize.toString()


        // Calculate the slice size and update the pie chart:
        val d = numItems.toDouble() / catSize
        val progress = (d * 1000).toInt()
        donutProg!!.progress = progress
    }


}

