package com.example.lootbox

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference


private const val REQUEST_CODE = 42
class CollectionsView : AppCompatActivity() {
    private var titlesList = mutableListOf<String>()
    private var descList = mutableListOf< String>()
    private var imagesList = mutableListOf<Int>()
    private var goalList = mutableListOf<String>()

    var viewImage: ImageView? = null
    var catL = arrayListOf<String>()

    private val pickImage = 100
    private var imageUri: Uri? = null
    private var data = " "
    var numCategories : Int = 0
    var currUrl = ""
    var storageRef: StorageReference = FirebaseStorage.getInstance().getReference()
    var fileName : String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_collections_view)

        val intent: Intent = intent
        data = intent.getStringExtra("user").toString()

        val db = FirebaseFirestore.getInstance()
        val docRef: DocumentReference = db.collection(data).document("categories")
        docRef.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val document: DocumentSnapshot? = task.getResult()
                if (document != null) {
                    numCategories = document.getLong("numCats")!!.toInt()
                    if(numCategories != 0){
                        getFromDB()
                    }
                } else {
                    Log.d("LOGGER", "No such document")
                }
            } else {
                Log.d("LOGGER", "get failed with ", task.exception)
            }
        }

        var recView :RecyclerView = findViewById(R.id.rcvCategoryList)
        recView.layoutManager = LinearLayoutManager(this)
        recView.adapter = Collection_RecAdapter(titlesList,descList,imagesList,goalList, data)


        var btnSettings : ImageButton = findViewById(R.id.btnSettings)
        btnSettings.setOnClickListener(object: View.OnClickListener{
            override fun onClick(v: View?) {
                val intent = Intent(this@CollectionsView,Settings::class.java).apply{}
                startActivity(intent)
            }
        })

        var btnGoals : ImageButton = findViewById(R.id.btnGoals)
        btnGoals.setOnClickListener(object: View.OnClickListener{
            override fun onClick(v: View?) {
                val intent = Intent(this@CollectionsView,Goals::class.java).apply{}
                intent.putExtra("user", data)
                startActivity(intent)
            }
        })

        var btnProfile : ImageButton = findViewById(R.id.btnProfile)
        btnProfile.setOnClickListener(object: View.OnClickListener{
            override fun onClick(v: View?) {
                val intent = Intent(this@CollectionsView,Profile::class.java).apply{}
                intent.putExtra("user", data)
                startActivity(intent)
            }
        })

        var showPopUp : ImageButton = findViewById<ImageButton>(R.id.btnAdd)

        showPopUp.setOnClickListener(object : View.OnClickListener{
            override fun onClick(v: View?) {
                val diagView = LayoutInflater.from(this@CollectionsView).inflate(R.layout.new_cat_popup,null)
                val alertBuild = AlertDialog.Builder(this@CollectionsView).setView(diagView).setTitle("Create category")
                val alertDiag = alertBuild.show()

                var captureImg: ImageView = diagView.findViewById<ImageView>(R.id.imgThumb)
                captureImg.setOnClickListener {
                    viewImage = diagView.findViewById<ImageView>(R.id.imgThumb)

                    val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
                    startActivityForResult(gallery, pickImage)
                }

                var create : ImageButton = diagView.findViewById<ImageButton>(R.id.btnCreate)
                create.setOnClickListener(object : View.OnClickListener {
                    @RequiresApi(Build.VERSION_CODES.P)
                    override fun onClick(v: View?) {
                        val catName = diagView.findViewById<EditText>(R.id.edtCatName).text.toString()
                        val catDesc = diagView.findViewById<EditText>(R.id.edtCatDesc).text.toString()

                        fileName = numCategories.toString() + catName
                        uploadImg(fileName!!,imageUri!!)
                       // var downloadUri : Uri = imageUri!!
                        addToList(catName,catDesc,R.drawable.launcher_icon,"0")
                        /*val ref = storageRef.child(fileName!!)
                        ref.downloadUrl.addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                             var  downloadUri = task.result!!
                                addToList(catName,catDesc,currUrl,"0")
                            } else {
                                // Handle failures
                                // ...
                            }
                        }*/

                        recView.layoutManager = LinearLayoutManager(this@CollectionsView)
                        recView.adapter = Collection_RecAdapter(titlesList,descList,imagesList,goalList, data)
                        alertDiag.dismiss()
                    }
                })

                var cancel : ImageButton = diagView.findViewById<ImageButton>(R.id.btnCancel)
                cancel.setOnClickListener(object : View.OnClickListener {
                    override fun onClick(v: View?) {
                        alertDiag.dismiss()
                    }
                })
            }})
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == pickImage) {
            imageUri = data?.data
            viewImage?.setImageURI(imageUri)
        }
    }

    fun uploadImg(name: String, contentUri:Uri){

        var image : StorageReference  = storageRef.child("images/"+ name)
        var uploadTask = image.putFile(contentUri)
        uploadTask.addOnFailureListener {
            // Handle unsuccessful uploads
        }.addOnSuccessListener { taskSnapshot ->
            image.downloadUrl.addOnCompleteListener () {taskSnapshot ->
                currUrl = taskSnapshot.result.toString()
            }
        }
    }

    private fun getFromDB(){
        Toast.makeText(this@CollectionsView, "in ", Toast.LENGTH_LONG).show()
        var catsFound : List<String> = emptyList()
        val db = FirebaseFirestore.getInstance()

        val docRef = db.collection(data).document("categories")
        docRef.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val document: DocumentSnapshot? = task.getResult()
                if (document != null) {
                    numCategories = document.getLong("numCats")!!.toInt()
                    catsFound = document.get("existing") as List<String>
                    var i : Int = 0
                    do {
                        val docRef = db.collection(data).document("categories").collection(catsFound.get(i)).document("info")
                        docRef.get().addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val document: DocumentSnapshot? = task.getResult()
                                if (document != null) {
                                    val title = document.getString("tilte") as String
                                    var desc = document.getString("desc")as String

                                    //var image = document.getString("image") as String
                                    var image = document.getLong("image")!!.toInt()
                                    var goal = document.getString("goal")as String

                                    //val storageImgRef : StorageReference = storageRef.child("images/"+image)
                                    //val check = storageImgRef.downloadUrl.result!!.toString()
                                    imagesList.add(image)

                                    /*ref.downloadUrl.addOnCompleteListener { task ->
                                        if (task.isSuccessful) {
                                            var downloadUri = task.result!!
                                            imagesList.add(downloadUri)
                                        } else {
                                            Toast.makeText(this@CollectionsView, "nope ", Toast.LENGTH_LONG).show()
                                        }
                                    }*/

                                    titlesList.add(title)
                                    descList.add(desc)
                                    goalList.add(goal)

                                    var recView :RecyclerView = findViewById(R.id.rcvCategoryList)
                                    recView.layoutManager = LinearLayoutManager(this)
                                    recView.adapter = Collection_RecAdapter(titlesList,descList,imagesList,goalList, data)

                                    catL.add(title)
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
                    }while(i < numCategories)
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


    private fun addToList(title: String, desc: String , image: Int, goal : String){
        titlesList.add(title)
        descList.add(desc)
        imagesList.add(image)
        goalList.add(goal)
        numCategories++

        val db = FirebaseFirestore.getInstance()
        val user = hashMapOf(
            "tilte" to title,
            "desc" to desc,
            "image" to image,
            "goal" to goal,
            "itemsGathered" to 0
        )
        db.collection(data)
            .document("categories").collection(title).document("info").set(user)

        val newCat = hashMapOf(
            "numCats" to numCategories,
        )
        db.collection(data)
            .document("categories").set(newCat)

        catL.add(title)

        val docRef = db.collection(data).document("categories")
        docRef.update("existing", catL)

        val newItm = hashMapOf(
            "numItems" to 0,
            "existing" to ""
        )
        db.collection(data)
            .document("categories").collection(title).document("items").set(newItm)
    }


}