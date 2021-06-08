package com.example.lootbox

import android.app.Activity
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
import org.w3c.dom.Text

private const val REQUEST_CODE = 42
class ItemListActivity : AppCompatActivity() {

    private var titlesList = mutableListOf<String>()
    private var descriptionList = mutableListOf<String>()
    private var imageList = mutableListOf<Int>()
    var viewImage :  ImageButton? = null
    var numItems : Int = 0
    private var goalAmount :Int = 0
    private var categoryPass : String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item_list)
        addToList("COD MW2","Modern Warefare of COD franchise",R.drawable.launcher_icon)
        addToList("Last of US 2","Second installment of the LOU franchise",R.drawable.launcher_icon)

        val intent = intent
        goalAmount = intent.getIntExtra("Goal",0)
        categoryPass = intent.getStringExtra("Category")

        var rcvItemList : RecyclerView = findViewById(R.id.rcvItemList)
        rcvItemList.layoutManager = LinearLayoutManager(this)
        rcvItemList.adapter = ItemsRecyclerAdapter(titlesList,descriptionList,imageList)
        numItems = rcvItemList?.adapter!!.itemCount

        var goalIndic :TextView = findViewById<TextView>(R.id.txtGoal)
        goalIndic.text = "You have $numItems out of $goalAmount items collected"
        var catDisp : TextView =  findViewById(R.id.txtCatName)
        catDisp.text = categoryPass

        val btnAdd : ImageButton = findViewById(R.id.btnAdd)
        btnAdd.setOnClickListener(object : View.OnClickListener{
            override fun onClick(v:View){
                val diagPopUp = LayoutInflater.from(this@ItemListActivity).inflate(R.layout.itempopup,null)
                val alertBuilder = AlertDialog.Builder(this@ItemListActivity).setView(diagPopUp).setTitle("Add Game")
                val alertDialog = alertBuilder.show()

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

                var add : ImageButton = diagPopUp.findViewById(R.id.btnAddGameToList)

                add.setOnClickListener(object : View.OnClickListener {
                    override fun onClick(v : View){
                        val gameName = diagPopUp.findViewById<EditText>(R.id.txtEnterGameName).text.toString()
                        val gameDescription = diagPopUp.findViewById<EditText>(R.id.txtEnterGameDescription).text.toString()
                        addToList(gameName,gameDescription,R.drawable.launcher_icon)

                        numItems = rcvItemList?.adapter!!.itemCount
                        goalIndic.text = "You have $numItems out of $goalAmount items collected"
                        alertDialog.dismiss()
                    }
                })

            }
        })


    }
    public fun addToList(title: String, description: String, image:Int){
        titlesList.add(title)
        descriptionList.add(description)
        imageList.add(image)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val takenImage = data?.extras?.get("data") as Bitmap
            viewImage?.setImageBitmap(takenImage)
        } else {
            super.onActivityResult(requestCode, requestCode, data)
        }
    }

}

