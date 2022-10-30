package com.zimmy.best.airbnb.view.ui.profile

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.zimmy.best.airbnb.konstants.Konstants
import com.zimmy.best.airbnb.models.BookingDetails

class ProfileRepository {

    private var paymentsLeftLiveData = MutableLiveData<Boolean>()
    val paymentsLeft: LiveData<Boolean>
        get() = paymentsLeftLiveData

    private var bookingsLiveData = MutableLiveData<ArrayList<BookingDetails>>()
    val bookings: LiveData<ArrayList<BookingDetails>>
        get() = bookingsLiveData

    fun checkPaymentsLeft() {
        val userReference = FirebaseDatabase.getInstance().reference.child(Konstants.USERS)
            .child(FirebaseAuth.getInstance().uid.toString()).child(Konstants.PAYMENTLEFT)
        userReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val bookings = ArrayList<BookingDetails>()
                    paymentsLeftLiveData.postValue(true)
                    for (booking in snapshot.children) {
                        Log.d(ProfileRepository::class.java.simpleName, "$booking")
                        val bookingDetails = booking.getValue(BookingDetails::class.java)
                        if (bookingDetails != null)
                            bookings.add(bookingDetails)
                    }
                    bookingsLiveData.postValue(bookings)
                } else {
                    paymentsLeftLiveData.postValue(false)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d(ProfileFragment::class.java.simpleName, "database error ${error.message}")
            }
        })
    }
}