package com.example.projemanage.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.example.projemanage.R
import com.example.projemanage.activities.firebase.FirestoreClass
import com.example.projemanage.activities.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import org.w3c.dom.Text

class SignUpActivity : BaseActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        setupActionBar()
    }

    fun userRegisteredSuccess(){
        Toast.makeText(this, "You have successfully registered", Toast.LENGTH_LONG).show()
        hideProgressDialog()
        FirebaseAuth.getInstance().signOut()
        finish()
    }

    private fun setupActionBar(){
        setSupportActionBar(findViewById(R.id.toolbar_sign_up_activity))
        val actionBar = supportActionBar
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24dp)
        }

        findViewById<Toolbar>(R.id.toolbar_sign_up_activity).setNavigationOnClickListener {
            onBackPressed()
        }

        findViewById<Button>(R.id.btn_sign_up).setOnClickListener{
            registerUser()
        }
    }

    private fun registerUser(){
        val name: String = findViewById<TextView>(R.id.et_name).text.toString().trim{ it<=' '}
        val email: String = findViewById<TextView>(R.id.et_email).text.toString().trim{ it<=' '}
        val password: String = findViewById<TextView>(R.id.et_password).text.toString().trim{ it<=' '}

        if(validateForm(name, email, password)){
            showProgressDialog(resources.getString(R.string.please_wait))
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password).addOnCompleteListener{
                task ->

                if(task.isSuccessful){
                    val firebaseUser: FirebaseUser = task.result!!.user!!
                    val registeredEmail = firebaseUser.email!!
                    val user = User(firebaseUser.uid, name,registeredEmail)
                    FirestoreClass().registerUser(this, user)
                }else{
                    Toast.makeText(this, "Registration failed", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    private fun validateForm(name:String, email:String, password: String): Boolean{
        return when{
            TextUtils.isEmpty(name)->{
                showErrorSnackBar("Please enter a name")
                return false
            }
            TextUtils.isEmpty(email)->{
                showErrorSnackBar("Please enter an email")
                return false
            }
            TextUtils.isEmpty(password)->{
                showErrorSnackBar("Please enter a password")
                return false
            }else->{
                return true
            }
        }
    }
}