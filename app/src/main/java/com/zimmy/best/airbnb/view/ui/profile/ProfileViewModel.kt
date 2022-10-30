package com.zimmy.best.airbnb.view.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.zimmy.best.airbnb.models.BookingDetails

class ProfileViewModel(private val repository: ProfileRepository) : ViewModel() {

    val paymentLeftLiveData: LiveData<Boolean>
        get() = repository.paymentsLeft

    val bookingsLiveData: LiveData<ArrayList<BookingDetails>>
        get() = repository.bookings

    fun checkBookings() {
        repository.checkPaymentsLeft()
    }

}