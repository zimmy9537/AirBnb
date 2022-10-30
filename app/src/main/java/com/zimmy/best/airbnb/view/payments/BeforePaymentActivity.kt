package com.zimmy.best.airbnb.view.payments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.zimmy.best.airbnb.databinding.ActivityBookingBinding
import com.zimmy.best.airbnb.konstants.Konstants
import com.zimmy.best.airbnb.models.Bill
import com.zimmy.best.airbnb.models.BookingDetails
import com.zimmy.best.airbnb.models.DateBnb
import java.math.BigDecimal
import java.text.DecimalFormat

class BeforePaymentActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBookingBinding
    private lateinit var booking: BookingDetails
    private lateinit var basicPhoto: String
    private lateinit var uid: String

    private lateinit var firstDateBnb: DateBnb
    private lateinit var secondDateBnb: DateBnb

    private var days = 0
    private var netTotal = 0.0
    private var serviceFee = 0.0
    private var taxes = 0.0
    private var totalGbp = 0.0
    private var price = 0.0

    private val df: DecimalFormat = DecimalFormat("0.00")

    private var LOG_TAG = BeforePaymentActivity::class.java.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBookingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewSetUp()

        booking = intent.getSerializableExtra(Konstants.BOOKINGREQUEST) as BookingDetails
        basicPhoto = intent.getSerializableExtra(Konstants.BASIC_PHOTO).toString()
        uid = intent.getSerializableExtra(Konstants.UIDS).toString()

        firstDateBnb = booking.datePair!!.firstDate!!
        secondDateBnb = booking.datePair!!.secondDate!!
        Picasso.get().load(basicPhoto).into(binding.primaryImage)
        binding.title.text = booking.hostingDetail.substringBefore('\n')
        binding.address.text = booking.hostingDetail.substringAfter('\n')
        binding.dates.text =
            getDate(booking.datePair!!.firstDate!!, booking.datePair!!.secondDate!!)
        binding.guests.text = checkGuest()
        priceCalculations()
        checkPrice()

        binding.requestBooking.setOnClickListener {
            val intent = Intent(this, PaymentActivity::class.java)
            intent.putExtra(Konstants.BOOKINGREQUEST, booking)
            val bill = Bill(price, days, serviceFee, taxes)
            intent.putExtra(Konstants.BILL, bill)
            intent.putExtra(Konstants.UIDS,uid)
            startActivity(intent)
        }
    }

    private fun checkPrice() {
        binding.progress.visibility = View.VISIBLE
        val hostReference = FirebaseDatabase.getInstance().reference.child(Konstants.HOSTINGMODEL1)
            .child(booking.hosting_id).child(Konstants.BASICDETAILS).child(Konstants.PRICE)
        hostReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    price = snapshot.getValue(Double::class.java)!!.toDouble()
                    binding.progress.visibility = View.GONE
                    priceCalculations()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d(LOG_TAG, "database error ${error.message}")
            }
        })
    }

    private fun checkGuest(): String {
        var guestCount = 0
        guestCount += booking.guest!!.adult
        guestCount += booking.guest!!.children
        var guestString = "$guestCount Guest"
        if (booking.guest!!.infant > 0) {
            guestString += ",${booking.guest!!.infant} Infant"
        }
        if (booking.guest!!.pet > 0) {
            guestString += ",${booking.guest!!.pet} Pet"
        }
        return guestString
    }

    private fun priceCalculations() {
        //price details
        days = getTotalDays(firstDateBnb, secondDateBnb)
        netTotal = df.format(BigDecimal(price).multiply(BigDecimal(days.toString())))
            .toDouble()
        Log.d(LOG_TAG, "net total $netTotal")
        binding.priceNightTv.text =
            "\u00a3${price} * $days nights"
        binding.priceNightTotalTv.text = "\u00a3${netTotal}"
        serviceFeeAndTax()
        totalCalculation()
    }

    private fun totalCalculation() {
        totalGbp = price * days
        totalGbp += serviceFee
        totalGbp += taxes
        totalGbp = df.format(totalGbp).toDouble()
        binding.totalAmtTv.text = "\u00a3$totalGbp"
    }

    private fun serviceFeeAndTax() {
        val temp = 0.14
        serviceFee = df.format(BigDecimal(netTotal).multiply(BigDecimal(temp))).toDouble()
        Log.d(LOG_TAG, "service fee $serviceFee")
        binding.serviceAmtTv.text = "\u00a3$serviceFee"
        taxes = df.format(netTotal.times(0.12)).toDouble()
        binding.taxesAmtTv.text = "\u00a3$taxes"
    }

    private fun getTotalDays(firstDateBnb: DateBnb, secondDateBnb: DateBnb): Int {
        var days = 0
        val year = secondDateBnb.year - firstDateBnb.year
        val month = secondDateBnb.month - firstDateBnb.month
        val date = secondDateBnb.day - firstDateBnb.day
        days += (year * 365)
        days += (month * 28)
        days += date
        return days
    }

    private fun getDate(firstDateBnb: DateBnb, secondDateBnb: DateBnb): String {
        return "${firstDateBnb.day}/${firstDateBnb.month}-${secondDateBnb.day}/${secondDateBnb.month}"
    }

    private fun viewSetUp() {
        binding.titleString.text = "Make Payment"
        binding.dateEdit.visibility = View.GONE
        binding.guestEdit.visibility = View.GONE
        binding.textview3.visibility = View.GONE
        binding.linearLayout7.visibility = View.GONE
        binding.linearLayout8.visibility = View.GONE
        binding.textview5.visibility = View.GONE
        binding.cardText.text = "Pay"
    }
}