package com.example.projemanage.activities.firebase

import android.app.Activity
import android.util.Log
import android.widget.Toast
import com.example.projemanage.activities.*
import com.example.projemanage.activities.models.Board
import com.example.projemanage.activities.models.User
import com.example.projemanage.activities.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class FirestoreClass {
    private val mFireStore= FirebaseFirestore.getInstance()

    fun registerUser(activity: SignUpActivity, userInfo: User){
        mFireStore.collection(Constants.USERS)
            .document(getCurrentUserId()).set(userInfo, SetOptions.merge()).addOnSuccessListener {
                activity.userRegisteredSuccess()
            }.addOnFailureListener{
                e->
                Log.e("SignInUser", "Error occurred")
            }
    }

    fun getCurrentUserId():String{

        var currentUser = FirebaseAuth.getInstance().currentUser
        var currentUserID = ""
        if(currentUser != null){
            currentUserID = currentUser.uid

        }
        return currentUserID
    }
    fun getBoardDetails(activity: TaskListActivity, documentId: String){
        mFireStore.collection(Constants.BOARDS).document(documentId).get().addOnSuccessListener {
                document -> Log.i(activity.javaClass.simpleName, document.toString())

            activity.boardDetails(document.toObject(Board::class.java)!!)
        }.addOnFailureListener{
                e->
            activity.hideProgressDialog()
            Log.e(activity.javaClass.simpleName, "Error while creating board", e)
        }
    }

    fun createBoard(activity: CreateBoardActivity, board: Board){
        mFireStore.collection(Constants.BOARDS).document().set(board, SetOptions.merge()).addOnSuccessListener {
            Toast.makeText(activity, "Board created successfully", Toast.LENGTH_LONG).show()
            activity.boardCreateSuccessfully()
        }.addOnFailureListener{
            exception ->
            activity.hideProgressDialog()
            Log.e(
                activity.javaClass.simpleName,
                "Error while creating a board",
                exception
            )
        }
    }

    fun getBoardsList(activity: MainActivity){
        mFireStore.collection(Constants.BOARDS).whereArrayContains(Constants.ASSIGNED_TO, getCurrentUserId()).get().addOnSuccessListener {
            document -> Log.i(activity.javaClass.simpleName, document.documents.toString())
            val boardList: ArrayList<Board> = ArrayList()
            for(i in document.documents){
                val board = i.toObject(Board::class.java)!!
                board.documentId = i.id
                boardList.add(board)
            }
            activity.populateBoardsListToUI(boardList)
        }.addOnFailureListener{
            e->
            activity.hideProgressDialog()
            Log.e(activity.javaClass.simpleName, "Error while creating board", e)
        }
    }

    fun updateUserProfileData(activity: MyProfileActivity, userHashMap: HashMap<String, Any>){
        mFireStore.collection(Constants.USERS).document(getCurrentUserId()).update(userHashMap).addOnSuccessListener {
            Log.i(activity.javaClass.simpleName, "Profile data Updated")
            Toast.makeText(activity, "Profile updated successfully", Toast.LENGTH_LONG).show()
            activity.profileUpdateSuccess()
        }.addOnFailureListener{
            e->activity.hideProgressDialog()
            Log.e(activity.javaClass.simpleName,"Error while creating a board", e)
            Toast.makeText(activity, "Error when updating the profile", Toast.LENGTH_LONG).show()

        }

    }

    fun loadUserData(activity: Activity, readBoardsList: Boolean = false){
        mFireStore.collection(Constants.USERS).document(getCurrentUserId()).get().addOnSuccessListener { document ->
            val loggedInUser = document.toObject(User::class.java)
            if (loggedInUser != null){
                when(activity){
                    is SignInActivity ->{
                        activity.signInSuccess(loggedInUser)
                    }
                    is MainActivity ->{
                        activity.updateNavigationUserDetails(loggedInUser, readBoardsList)
                    }
                    is MyProfileActivity ->{
                        activity.setUserDataInUI(loggedInUser)
                    }
                }

            }
        }.addOnFailureListener{
            e->
            when(activity){
                is SignInActivity ->{
                    activity.hideProgressDialog()
                }
                is MainActivity ->{
                    activity.hideProgressDialog()
                }
            }
            Log.e(activity.javaClass.simpleName, "Error occurred in LoadUserData")
        }
    }
}