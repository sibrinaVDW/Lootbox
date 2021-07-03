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
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class NewUserAccount : AppCompatActivity() {
    private var mAuth: FirebaseAuth? = null
    var cal = Calendar.getInstance()
    val myFormat = "dd/MM/yyyy"
    val sdf = SimpleDateFormat(myFormat, Locale.UK)
    var dateDisp : TextView? = null
    val db= FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_user_account)
        mAuth = FirebaseAuth.getInstance();

        val username : String = findViewById<EditText>(R.id.edtName).text.toString()
        val password : String = findViewById<EditText>(R.id.edtPassword).text.toString()
        val email : String = findViewById<EditText>(R.id.edtEmail).text.toString()


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

        /*val user=hashMapOff(
            "first" to username,
            "email" to email
            "password" to "password"
            "born" to dateofBirth
        )
//Adding a new document with generated ID
        db.collection("users").add(user).addOnSuccessListener { documentReference ->
            Log.w(TAG,"DocumentSnapshot added with ID:${documentReference.id}")
        }
            .addOnFailureListener{e ->
                Log.w(TAG,"Error adding document",e)
            }*/


        val createAcc: ImageButton = findViewById<ImageButton>(R.id.btnAccountCreate)
        createAcc.setOnClickListener {
            //createAccount(username, email, password, cal.getTime())
            val intent = Intent(this@NewUserAccount,CollectionsView::class.java).apply{}
            startActivity(intent)
        }
    }

    private fun createAccount(username: String, email: String, password: String, dob: Date) {
        //Need to add to database

        mAuth!!.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this@NewUserAccount) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "createUserWithEmail:success")
                    val user = mAuth!!.currentUser
                    //updateUI(user)
                    Toast.makeText(baseContext, "User created successfully",
                        Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@NewUserAccount,CollectionsView::class.java).apply{}
                    startActivity(intent)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                    //updateUI(null)
                }
            }
    }
}