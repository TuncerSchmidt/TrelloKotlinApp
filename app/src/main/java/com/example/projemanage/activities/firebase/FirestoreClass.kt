package com.example.projemanage.activities.firebase

import android.util.Log
import com.example.projemanage.activities.SignInActivity
import com.example.projemanage.activities.SignUpActivity
import com.example.projemanage.activities.models.User
import com.example.projemanage.activities.utils.Constants
import com.google.firebase.auth.FirebaseAuth
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

    fun SignInUser(activity: SignInActivity){
        mFireStore.collection(Constants.USERS).document(getCurrentUserId()).get().addOnSuccessListener { document ->
            val loggedInUser = document.toObject(User::class.java)
            if (loggedInUser != null){
                activity.signInSuccess(loggedInUser)
            }
        }.addOnFailureListener{
            e->
            Log.e(activity.javaClass.simpleName, "Error occurred")
        }
    }
}