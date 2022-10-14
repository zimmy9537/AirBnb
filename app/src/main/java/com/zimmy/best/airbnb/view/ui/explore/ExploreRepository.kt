package com.zimmy.best.airbnb.view.ui.explore

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.zimmy.best.airbnb.konstants.Konstants
import com.zimmy.best.airbnb.models.BasicDetails

class ExploreRepository {

    private var listLiveData = MutableLiveData<ArrayList<BasicDetails>>()
    val listedList: LiveData<ArrayList<BasicDetails>>
        get() = listLiveData

    private var ifLoadedData = MutableLiveData<Boolean>()
    val ifLoaded: LiveData<Boolean>
        get() = ifLoadedData

    fun explore() {
        ifLoadedData.postValue(false)
        val tempList: ArrayList<BasicDetails> = ArrayList()
        val exploreReference =
            FirebaseDatabase.getInstance().reference.child(Konstants.HOSTINGMODEL1)
        exploreReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var size = snapshot.childrenCount
                for (hostUid in snapshot.children) {
                    exploreReference.child(hostUid.key.toString()).child(Konstants.BASICDETAILS)
                        .addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                size--
                                val basicDetails = snapshot.getValue(BasicDetails::class.java)
                                if (basicDetails != null)
                                    tempList.add(basicDetails)
                                if (size == 0L) {
                                    listLiveData.postValue(tempList)
                                    ifLoadedData.postValue(true)
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {
                                Log.d(
                                    ExploreRepository::class.java.simpleName,
                                    "database error ${error.message}"
                                )
                            }
                        })
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d(ExploreRepository::class.java.simpleName, "database error ${error.message}")
            }

        })
    }
}