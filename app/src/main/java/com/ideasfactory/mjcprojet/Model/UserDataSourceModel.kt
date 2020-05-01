package com.ideasfactory.mjcprojet.Model

import android.util.Log
import com.google.firebase.database.*
import java.util.*
import kotlin.collections.ArrayList

object UserDataSourceModel : Observable() {

    private var mValueDataListener: ValueEventListener ?= null
    private var userDataSourceList : ArrayList<UserDataSource> = ArrayList()

    private fun getDatabaseRef() : DatabaseReference? {
        return FirebaseDatabase.getInstance().getReference("mjc_users_datasource")
    }

    init {
        if (mValueDataListener !=null ){
            getDatabaseRef()?.removeEventListener(mValueDataListener!!)
        }
        mValueDataListener = null
        Log.i("UserModel", "value listener nul")

        mValueDataListener = object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                if(p0 != null){
                    Log.i("UserModel", "data update canceled = ${p0.message}")
                }
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                try{
                    Log.i("UserModel","Data updated")
                    val data : ArrayList<UserDataSource> = ArrayList()
                    if(dataSnapshot != null){
                        for (snapShot : DataSnapshot in dataSnapshot.children){
                            try {
                                //data.add(UserDataSource(snapShot))
                            }catch (e : Exception){
                                e.printStackTrace()
                            }
                        }
                        userDataSourceList = data
                        Log.i("UserModel","dataUpdated with ${userDataSourceList.size} elements in cache")
                        setChanged()
                        notifyObservers()
                    }
                }catch (e : java.lang.Exception){
                    e.printStackTrace()
                }
            }

        }
        getDatabaseRef()?.addValueEventListener(mValueDataListener!!)



    }

    fun getData(): ArrayList<UserDataSource>?{
        return userDataSourceList
    }
}