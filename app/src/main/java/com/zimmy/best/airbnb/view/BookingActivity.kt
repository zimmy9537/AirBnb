package com.zimmy.best.airbnb.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.squareup.picasso.Picasso
import com.zimmy.best.airbnb.databinding.ActivityBookingBinding
import com.zimmy.best.airbnb.konstants.Konstants
import com.zimmy.best.airbnb.models.BasicDetails
import com.zimmy.best.airbnb.models.DateBnb
import java.math.BigDecimal

class BookingActivity : AppCompatActivity() {

    private lateinit var firstDateBnb: DateBnb
    private lateinit var secondDateBnb: DateBnb
    private lateinit var basicDetails: BasicDetails

    private var days = 0
    private var netTotal = 0.0
    private var serviceFee = 0.0
    private var taxes = 0.0
    private var totalGbp = 0.0
    private var LOG_TAG = BookingActivity::class.java.simpleName

    var addMessage = false
    var addPhone = false

    private lateinit var binding: ActivityBookingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBookingBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        firstDateBnb = intent.getSerializableExtra(Konstants.FIRSTDATE) as DateBnb
        secondDateBnb = intent.getSerializableExtra(Konstants.SECONDDATE) as DateBnb
        basicDetails = intent.getSerializableExtra(Konstants.BASICDETAILS) as BasicDetails

        initView()
    }

    private fun initView() {
        //basic details
        binding.title.text = basicDetails.title
        binding.address.text = basicDetails.address
        binding.ratingReview.text = "${basicDetails.rating}(${basicDetails.reviews})"
        Picasso.get().load(basicDetails.basicPhoto).into(binding.primaryImage)

        //your trip
        binding.dates.text = getDate(firstDateBnb, secondDateBnb)
        binding.guests.text = "1 guest"
        priceCalculations()

        binding.addMessage.setOnClickListener {
            Toast.makeText(
                this@BookingActivity,
                "Diversion ahead, Work in progress",
                Toast.LENGTH_SHORT
            ).show()
            addMessage = true
            binding.messageCb.isChecked = true
        }
        binding.addPhone.setOnClickListener {
            Toast.makeText(
                this@BookingActivity,
                "Diversion ahead, Work in progress",
                Toast.LENGTH_SHORT
            ).show()
            addPhone = true
            binding.phoneCb.isChecked = true
        }

        binding.requestBooking.setOnClickListener {
            Toast.makeText(this@BookingActivity, "Booking successful", Toast.LENGTH_SHORT).show()
        }
    }

    private fun priceCalculations() {
        //price details
        days = getTotalDays(firstDateBnb, secondDateBnb)
        netTotal = BigDecimal(basicDetails.price).multiply(BigDecimal(days.toString())).toDouble()
        Log.d(LOG_TAG, "net total $netTotal")
        binding.priceNightTv.text =
            "\u00a3${basicDetails.price} * $days nights"
        binding.priceNightTotalTv.text = "\u00a3${netTotal}"
        serviceFeeAndTax()
        totalCalculation()
    }

    private fun totalCalculation() {
        totalGbp = basicDetails.price * days
        totalGbp += serviceFee
        totalGbp += taxes
        binding.totalAmtTv.text = "\u00a3$totalGbp"
    }

    private fun serviceFeeAndTax() {
//        serviceFee = basicDetails.price.times(days).times(0.14)
        val temp=0.14
        serviceFee =
            BigDecimal(netTotal).multiply(BigDecimal(temp.toString())).toDouble()
        Log.d(LOG_TAG,"service fee $serviceFee")
        binding.serviceAmtTv.text = "\u00a3$serviceFee"
        taxes = netTotal.times(0.12)
        binding.taxesAmtTv.text = "\u00a3$taxes"
    }

    private fun getDate(firstDateBnb: DateBnb, secondDateBnb: DateBnb): String {
        return "${firstDateBnb.day}/${firstDateBnb.month}-${secondDateBnb.day}/${secondDateBnb.month}"
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
}