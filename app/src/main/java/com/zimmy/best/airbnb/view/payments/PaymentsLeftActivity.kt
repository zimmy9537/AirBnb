package com.zimmy.best.airbnb.view.payments

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.zimmy.best.airbnb.R
import com.zimmy.best.airbnb.adapter.BookingCheckAdapter
import com.zimmy.best.airbnb.databinding.ActivityPaymentsLeftBinding
import com.zimmy.best.airbnb.models.BookingDetails
import com.zimmy.best.airbnb.view.ui.profile.ProfileRepository
import com.zimmy.best.airbnb.view.ui.profile.ProfileViewModel
import com.zimmy.best.airbnb.view.ui.profile.ProfileViewModelFactory

class PaymentsLeftActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPaymentsLeftBinding

    private lateinit var bookings: ArrayList<BookingDetails>
    private lateinit var profileViewModel: ProfileViewModel
    private lateinit var repository: ProfileRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPaymentsLeftBinding.inflate(layoutInflater)
        setContentView(binding.root)

        bookings = ArrayList()
        binding.listRv.layoutManager = LinearLayoutManager(this)

        repository = ProfileRepository()
        repository.checkPaymentsLeft()
        profileViewModel =
            ViewModelProvider(
                this,
                ProfileViewModelFactory(repository)
            )[ProfileViewModel::class.java]

        profileViewModel.bookingsLiveData.observe(this, Observer {
            bookings = it
            Log.d(PaymentsLeftActivity::class.java.simpleName, "bookings $it")
            binding.listRv.adapter = BookingCheckAdapter(bookings, this)
        })
    }
}