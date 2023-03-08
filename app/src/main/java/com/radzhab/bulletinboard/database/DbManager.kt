package com.radzhab.bulletinboard.database

import android.util.Log
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.radzhab.bulletinboard.data.Ad

class DbManager {
    val db = Firebase.database.getReference("main")
    val auth = Firebase.auth

    fun publishAd(ad: Ad) {
        Log.d("MyLog", "auth.currentUser = ${auth.currentUser} ")

        if (auth.uid != null) {
            db.child(ad.key ?: "empty").child(auth.uid!!).child("ad").setValue(ad)
        }
    }

    fun readDaraFromDb() {
        db.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (item in snapshot.children) {
                    val ad = item.children.iterator().next().child("ad").getValue(Ad::class.java)
                    Log.d("MyLog", "${ad}")
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
    }
}