package com.example.projemanage.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.telecom.Call
import android.util.Log
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.*
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.projemanage.R
import com.example.projemanage.activities.firebase.FirestoreClass
import com.example.projemanage.activities.models.User
import com.example.projemanage.activities.utils.Constants
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.IOException

class MyProfileActivity : BaseActivity() {

    private var mSelectedImageFileUri: Uri? = null
    private lateinit var mUserDetails: User
    private var mProfileImageURL: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_profile)

        setupActionBar()

        FirestoreClass().loadUserData(this)

        findViewById<ImageView>(R.id.iv_profile_user_image).setOnClickListener{
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                Constants.showImageChooser(this)
            }else{
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), Constants.READ_STORAGE_PERMISSION_CODE)
            }
        }
        findViewById<Button>(R.id.btn_update).setOnClickListener{
            if(mSelectedImageFileUri != null){
                uploadUserImage()
            }else{
                showProgressDialog(resources.getString(R.string.please_wait))
                updateUserProfileData()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == Constants.READ_STORAGE_PERMISSION_CODE){
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Constants.showImageChooser(this)
            }else{
                Toast.makeText(this, "Ooppss, you just denied the permission for storage", Toast.LENGTH_LONG).show()
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK && requestCode == Constants.PICK_IMAGE_REQUEST_CODE && data!!.data != null){
            mSelectedImageFileUri = data.data
        }
        try {
            Glide.with(this@MyProfileActivity).load(mSelectedImageFileUri).centerCrop()
                .placeholder(R.drawable.ic_user_place_holder).into(findViewById(R.id.iv_profile_user_image))
        }catch (e: IOException){
            e.printStackTrace()
        }
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
    //error fix
    
    fun setUserDataInUI(user: User) {
        mUserDetails = user

        Glide.with(this@MyProfileActivity).load(user.image).centerCrop()
            .placeholder(R.drawable.ic_user_place_holder).into(findViewById(R.id.iv_profile_user_image))
        findViewById<AppCompatEditText>(R.id.et_name).setText(user.name)
        findViewById<AppCompatEditText>(R.id.et_email).setText(user.email)
        if(user.mobile != null){
        findViewById<AppCompatEditText>(R.id.et_mobile).setText(user.mobile.toString())
        }

    }
    fun updateUserProfileData(){
        val userHashMap = HashMap<String, Any>()

        if(mProfileImageURL.isNotEmpty() && mProfileImageURL != mUserDetails.image){
            userHashMap[Constants.IMAGE] = mProfileImageURL
        }
        if(findViewById<TextView>(R.id.et_name).text.toString() != mUserDetails.name){
            userHashMap[Constants.NAME] = findViewById<TextView>(R.id.et_name).text.toString()
        }
        if(findViewById<TextView>(R.id.et_mobile).text.toString() != mUserDetails.mobile.toString()){
            userHashMap[Constants.MOBILE] = findViewById<TextView>(R.id.et_mobile).text.toString()
        }
        FirestoreClass().updateUserProfileData(this, userHashMap)
    }

    private fun uploadUserImage(){
        showProgressDialog(resources.getString(R.string.please_wait))
        if(mSelectedImageFileUri != null){
            val sRef: StorageReference = FirebaseStorage.getInstance().reference.child("USER_IMAGE" + System.currentTimeMillis() + "." + Constants.getFileExtension(this, mSelectedImageFileUri))
            sRef.putFile(mSelectedImageFileUri!!).addOnSuccessListener {
                taskSnapshot ->
                Log.i("Firebase Image URL", taskSnapshot.metadata!!.reference!!.downloadUrl.toString())
                taskSnapshot.metadata!!.reference!!.downloadUrl.addOnSuccessListener {
                    uri ->
                    Log.i("Downloadable Image URL", uri.toString())
                    mProfileImageURL = uri.toString()

                    updateUserProfileData()
                }
            }.addOnFailureListener{
                exception ->
                Toast.makeText(this@MyProfileActivity, exception.message, Toast.LENGTH_LONG).show()
                hideProgressDialog()
            }

        }
    }



    fun profileUpdateSuccess(){
        hideProgressDialog()
        setResult(Activity.RESULT_OK)
        finish()
    }
}