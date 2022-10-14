package com.zimmy.best.airbnb.view

import android.content.Intent
import android.graphics.Paint
import com.zimmy.best.airbnb.R
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.*
import com.zimmy.best.airbnb.adapter.PhotoAdapter
import com.zimmy.best.airbnb.contracts.DateContracts
import com.zimmy.best.airbnb.databinding.ActivityDetailBinding
import com.zimmy.best.airbnb.konstants.Konstants
import com.zimmy.best.airbnb.models.BasicDetails
import com.zimmy.best.airbnb.models.DateBnb
import com.zimmy.best.airbnb.models.HostingDetails

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding

    private lateinit var hostingCode: String
    private lateinit var exploreReference: DatabaseReference
    private lateinit var basicDetails: BasicDetails
    private lateinit var hostingDetails: HostingDetails
    private var LOG_TAG = DetailActivity::class.java.simpleName
    private var isDateSelected = false
    private var contract = registerForActivityResult(DateContracts()) {
        val datePair = it
        performDateCalculations(datePair)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        initView()

        hostingCode = intent.getStringExtra(Konstants.HOSTINGCODE).toString()
        exploreReference = FirebaseDatabase.getInstance().reference.child(Konstants.HOSTINGMODEL1)
            .child(hostingCode)

        exploreReference.child(Konstants.BASICDETAILS)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    basicDetails = snapshot.getValue(BasicDetails::class.java)!!
                    binding.addressTv.text = basicDetails.address
                    binding.titleTv.text = basicDetails.title
                    binding.reviews.text = basicDetails.reviews.toString()
                    binding.rating.text = basicDetails.rating.toString()
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d(LOG_TAG, "database error ${error.message}")
                }
            })

        exploreReference.child(Konstants.HOSTINGDETAILS)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    hostingDetails = snapshot.getValue(HostingDetails::class.java)!!
                    for (offer in hostingDetails.detailMap!!) {
                        if (offer.value) {
                            val roomView =
                                layoutInflater.inflate(R.layout.string_item, null, false)
                            val offerTv = roomView.findViewById<TextView>(R.id.value)
                            offerTv.text = offer.key
                            binding.offerLl.addView(roomView)
                        }
                    }
                    for (room in hostingDetails.roomList!!) {
                        val roomView = layoutInflater.inflate(R.layout.string_item, null, false)
                        binding.roomLl.addView(roomView)
                        val roomTv = roomView.findViewById<TextView>(R.id.value)
                        roomTv.text = room
                    }
                    val photoList = hostingDetails.photoList
                    if (photoList != null) {
                        binding.photoRv.adapter = PhotoAdapter(photoList, this@DetailActivity)
                        binding.photoRv.layoutManager = LinearLayoutManager(this@DetailActivity)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d(LOG_TAG, "database error ${error.message}")
                }
            })
    }

    private fun initView() {
        binding.selectDateTv.paintFlags = Paint.UNDERLINE_TEXT_FLAG
        binding.checkAvailability.setOnClickListener {
            contract.launch(Intent(this, DatePickerActivity::class.java))
        }
        binding.reserve.setOnClickListener {
            if (isDateSelected) {
                startActivity(Intent(this, BookingActivity::class.java))
            } else {
                Toast.makeText(this, "check for the availability first", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun performDateCalculations(datePair: Pair<Long, Long>?) {
        if (datePair != null) {
            Log.d(
                LOG_TAG,
                "${DateBnb.setDate(datePair.first)} || ${DateBnb.setDate(datePair.second)}"
            )
            val firstDate = DateBnb.setDate(datePair.first)
            val secondDate = DateBnb.setDate(datePair.second)
            binding.selectDateTv.text =
                "${firstDate.day}/${firstDate.month} - ${secondDate.day}/${secondDate.month}"
            binding.reserve.setBackgroundResource(R.color.bnb)
            isDateSelected = true
        }
    }
}