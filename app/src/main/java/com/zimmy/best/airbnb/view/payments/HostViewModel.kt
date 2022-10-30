package com.zimmy.best.airbnb.view.payments

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.zimmy.best.airbnb.konstants.Konstants

class HostViewModel : ViewModel() {

    val hostUidMutableLiveData: MutableLiveData<String> = MutableLiveData()

    fun getHostUid(hosting_id: String) {
        val hostingReference =
            FirebaseDatabase.getInstance().reference.child(Konstants.HOSTINGMODEL1)
                .child(hosting_id)
                .child(Konstants.BASICDETAILS)
        hostingReference.child(Konstants.HOSTUID)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val uid = snapshot.getValue(String::class.java).toString()
                    hostUidMutableLiveData.postValue(uid)
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d(PaymentActivity::class.java.simpleName, "database error ${error.message}")
                }
            })
    }
}