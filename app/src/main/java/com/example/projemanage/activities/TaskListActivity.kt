package com.example.projemanage.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import com.example.projemanage.R
import com.example.projemanage.activities.firebase.FirestoreClass
import com.example.projemanage.activities.models.Board
import com.example.projemanage.activities.utils.Constants

class TaskListActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_list)

        var boardDocumentId = ""
        if(intent.hasExtra(Constants.DOCUMENT_ID)){
            boardDocumentId = intent.getStringExtra(Constants.DOCUMENT_ID).toString()
        }

        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getBoardDetails(this, boardDocumentId)
    }
    private fun setupActionBar(title:String){
        setSupportActionBar(findViewById(R.id.toolbar_task_list_activity))
        val actionBar = supportActionBar
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24dp)
            actionBar.title = title
        }
        findViewById<Toolbar>(R.id.toolbar_my_profile_activity).setNavigationOnClickListener { onBackPressed() }
    }

    fun boardDetails(board:Board){
        hideProgressDialog()
        setupActionBar(board.name)
    }
}