package com.example.lootbox

import android.app.DatePickerDialog
import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
//import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

const val DEFVALS = "com.example.googleloginlogout.MESSAGE";

class NewUserAccount : AppCompatActivity() {
    private var mAuth: FirebaseAuth? = null
    var cal = Calendar.getInstance()
    val myFormat = "dd/MM/yyyy"
    val sdf = SimpleDateFormat(myFormat, Locale.UK)
    var dateDisp : TextView? = null
    private var data = " "

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_user_account)

        mAuth = FirebaseAuth.getInstance();
        val db = FirebaseFirestore.getInstance()
       // data = intent.getStringExtra("user").toString()



        dateDisp = findViewById(R.id.edtDOB)
        val dateSetListener = object : DatePickerDialog.OnDateSetListener {
            override fun onDateSet(view: DatePicker, year: Int, monthOfYear: Int,
                                   dayOfMonth: Int) {
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                dateDisp?.text = sdf.format(cal.getTime())
            }
        }

        val dateOfBirth = findViewById<TextView>(R.id.edtDOB)
        dateOfBirth.setOnClickListener(object : View.OnClickListener  {
            override fun onClick(view: View) {
                DatePickerDialog(this@NewUserAccount,
                    dateSetListener,
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)).show()
            }
        })
    }


    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val password = findViewById<EditText>(R.id.edtPassword)
        val email = findViewById<EditText>(R.id.edtEmail)
        val createAcc: ImageButton = findViewById<ImageButton>(R.id.btnAccountCreate)
        val name  = findViewById<EditText>(R.id.edtName)

        createAcc.setOnClickListener {
            createAccount(email.text.toString(), password.text.toString(), name.text.toString()) //name.text.toString()
        }
    }

   //private fun updateUI(currentUser: FirebaseUser?) {


    private fun createAccount(email: String, password: String, name: String) {
        mAuth!!.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this@NewUserAccount) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "createUserWithEmail:success")
                    val user = mAuth!!.currentUser
                 //   val uid = FirebaseAuth.getInstance().uid ?: ""
                 //   val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")

                    val db = FirebaseFirestore.getInstance()
                    val userS = hashMapOf(
                        "email" to user?.email,
                        "UID" to user?.uid,
                        "date" to sdf.format(cal.getTime()),
                        "name" to name,
                    )
                    db.collection((user?.uid)as String)
                        .document("details").set(userS)

                    val catView = hashMapOf(
                        "existing" to "",
                        "numCats" to 0
                    )
                    db.collection((user!!.uid)).document("categories").set(catView)

                    val intent = Intent(this@NewUserAccount,CollectionsView::class.java).apply{}
                    intent.putExtra("user", user?.uid.toString())
                    startActivity(intent)
                   //updateUI(user)
                } else {

                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                   // updateUI(null)
                }
            }
    }
    //updateUI(user)
}