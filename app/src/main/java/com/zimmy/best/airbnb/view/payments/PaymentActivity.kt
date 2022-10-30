package com.zimmy.best.airbnb.view.payments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.zimmy.best.airbnb.databinding.ActivityPaymentBinding
import com.zimmy.best.airbnb.konstants.Konstants
import com.zimmy.best.airbnb.models.Bill
import com.zimmy.best.airbnb.models.Booking
import com.zimmy.best.airbnb.models.BookingDetails
import com.zimmy.best.airbnb.view.HomeActivity

class PaymentActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPaymentBinding
    private lateinit var bill: Bill
    private lateinit var bookingDetail: BookingDetails
    private lateinit var hostUid: String

    private lateinit var userReference: DatabaseReference
    private lateinit var hostReference: DatabaseReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPaymentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        bill = intent.getSerializableExtra(Konstants.BILL) as Bill
        bookingDetail = intent.getSerializableExtra(Konstants.BOOKINGREQUEST) as BookingDetails
        hostUid = intent.getSerializableExtra(Konstants.UIDS).toString()

        binding.success.setOnClickListener {
            saveUserBooking()
            saveMoneyHost()
            deletePaymentLeft()
            val intent = Intent(this, HomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        binding.failure.setOnClickListener {
            Toast.makeText(this, "payment failure, please retry", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun deletePaymentLeft() {
        val hostReference =
            FirebaseDatabase.getInstance().reference.child(Konstants.HOSTS).child(hostUid)
                .child(Konstants.PAYMENTLEFT)
        hostReference.removeValue()
        val userReference =
            FirebaseDatabase.getInstance().reference.child(Konstants.USERS)
                .child(bookingDetail.user_uid).child(Konstants.PAYMENTLEFT)
        userReference.removeValue()
    }

    private fun saveMoneyHost() {
        val booking = Booking(bookingDetail, bill)
        hostReference =
            FirebaseDatabase.getInstance().reference.child(Konstants.HOSTS).child(hostUid)
                .child(Konstants.CONFIRMBOOKING).child(bookingDetail.booking_id)
        hostReference.setValue(booking)
    }

    private fun saveUserBooking() {
        val uid = FirebaseAuth.getInstance().uid.toString()
        val booking = Booking(bookingDetail, bill)
        userReference =
            FirebaseDatabase.getInstance().reference.child(Konstants.USERS).child(uid)
                .child(Konstants.CONFIRMBOOKING).child(bookingDetail.booking_id)
        userReference.setValue(booking)
    }
}