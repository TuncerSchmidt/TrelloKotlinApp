package com.example.projemanage.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.Toolbar
import com.bumptech.glide.Glide
import com.example.projemanage.R
import com.example.projemanage.activities.firebase.FirestoreClass
import com.example.projemanage.activities.models.User
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class MyProfileActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_profile)

        setupActionBar()

        FirestoreClass().loadUserData(this)
    }

    private fun setupActionBar(){
        setSupportActionBar(findViewById(R.id.toolbar_my_profile_activity))
        val actionBar = supportActionBar
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24dp)
            actionBar.title = resources.getString(R.string.my_profile_title)
        }
        findViewById<Toolbar>(R.id.toolbar_my_profile_activity).setNavigationOnClickListener { onBackPressed() }
    }

    fun setUserDataInUI(user: User) {
        Glide.with(this@MyProfileActivity).load(user.image).centerCrop()
            .placeholder(R.drawable.ic_user_place_holder).into(findViewById(R.id.iv_user_image))
        findViewById<AppCompatEditText>(R.id.et_name).setText(user.name)
        findViewById<AppCompatEditText>(R.id.et_email).setText(user.email)
        if(user.mobile != null){
        findViewById<AppCompatEditText>(R.id.et_mobile).setText(user.mobile.toString())
        }

    }
}