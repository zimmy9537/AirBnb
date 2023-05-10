package com.zimmy.best.airbnb.view.ui.wishlists

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.zimmy.best.airbnb.konstants.Konstants
import com.zimmy.best.airbnb.models.BasicDetails

class WishListRepository {

    private var listLiveData = MutableLiveData<ArrayList<BasicDetails>>()
    val listedList: LiveData<ArrayList<BasicDetails>>
        get() = listLiveData


    private var ifLoadedData = MutableLiveData<Boolean>()
    val ifLoaded: LiveData<Boolean>
        get() = ifLoadedData

    private var wishStringLiveData = MutableLiveData<ArrayList<String>>()
    val wishStringList: LiveData<ArrayList<String>>
        get() = wishStringLiveData

    fun getWishList() {
        ifLoadedData.postValue(false)
        //fill in the liked items into wishlist
        val wishList: ArrayList<String> = ArrayList()
        val tempList: ArrayList<BasicDetails> = ArrayList()
        val userUid = FirebaseAuth.getInstance().uid.toString()

        val wishReference =
            FirebaseDatabase.getInstance().reference.child(Konstants.USERS).child(userUid)
                .child(Konstants.WISHLIST)
        wishReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (hostingCode in snapshot.children) {
                    wishList.add(hostingCode.key.toString())
                }
                wishStringLiveData.postValue(wishList)
                var size = snapshot.childrenCount
                val hostingReference =
                    FirebaseDatabase.getInstance().reference.child(Konstants.HOSTINGMODEL1)
                for (wish in wishList) {
                    hostingReference.child(wish).child(Konstants.BASICDETAILS)
                        .addValueEventListener(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                val basicDetail = snapshot.getValue(BasicDetails::class.java)
                                if (basicDetail != null)
                                    tempList.add(basicDetail)
                                size--
                                if (size == 0L) {
                                    listLiveData.postValue(tempList)
                                    ifLoadedData.postValue(true)
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {
                                Log.d(
                                    WishListRepository::class.java.simpleName,
                                    "database error ${error.message}"
                                )
                            }
                        })
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d(WishListRepository::class.java.simpleName, "database error ${error.message}")
            }
        })
    }
}