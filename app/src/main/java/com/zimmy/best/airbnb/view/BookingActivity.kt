package com.zimmy.best.airbnb.view

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import com.zimmy.best.airbnb.contracts.ChatContract
import com.zimmy.best.airbnb.contracts.DateContracts
import com.zimmy.best.airbnb.databinding.ActivityBookingBinding
import com.zimmy.best.airbnb.databinding.GuestDialogBinding
import com.zimmy.best.airbnb.konstants.Konstants
import com.zimmy.best.airbnb.models.*
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class BookingActivity : AppCompatActivity() {

    private lateinit var hostingReference: DatabaseReference
    private lateinit var hostReference: DatabaseReference

    private lateinit var userPreference: SharedPreferences

    private lateinit var firstDateBnb: DateBnb
    private lateinit var secondDateBnb: DateBnb
    private lateinit var basicDetails: BasicDetails
    var firstTimeStamp: Long? = 0
    var secondTimeStamp: Long? = 0
    private lateinit var bookingCode: String
    private lateinit var phoneNumber: String

    private var days = 0
    private var netTotal = 0.0
    private var serviceFee = 0.0
    private var taxes = 0.0
    private var totalGbp = 0.0
    private var LOG_TAG = BookingActivity::class.java.simpleName
    private var contract = registerForActivityResult(DateContracts()) {
        val datePair = it
        performDateCalculations(datePair)
    }
    private var chatContract = registerForActivityResult(ChatContract()) {
        if (it) {
            binding.messageCb.isChecked = true
            binding.addMessage.visibility = View.GONE
        } else {
            Toast.makeText(
                this,
                "please send a appropriate message to host regarding the booking",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
    private val df: DecimalFormat = DecimalFormat("0.00")

    companion object {
        private val guest = Guest(1, 0, 0, 0)
    }

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

        hostingReference = FirebaseDatabase.getInstance().reference.child(Konstants.HOSTINGMODEL1)
            .child(basicDetails.hostingCode)
        bookingCode = Konstants.codeGenerator(Konstants.BOOKINGCODE)
        userPreference = getSharedPreferences(Konstants.PERSONAL, Context.MODE_PRIVATE)

        df.roundingMode = RoundingMode.UP
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
            val intent = Intent(this, MessageHostActivity::class.java)
            intent.putExtra(Konstants.UIDS, basicDetails.hostUid)
            chatContract.launch(intent)
        }
        binding.addPhone.setOnClickListener {
            Toast.makeText(
                this@BookingActivity,
                "Diversion ahead, Work in progress",
                Toast.LENGTH_SHORT
            ).show()
            addPhone = true
            binding.phoneCb.isChecked = true
            phoneNumber = "9537830943"
        }

        binding.requestBooking.setOnClickListener {
            checkDates()
        }

        binding.dateEdit.setOnClickListener {
            contract.launch(Intent(this, DatePickerActivity::class.java))
        }

        binding.guestEdit.setOnClickListener {
            bottomSheetDialogAdvance()
        }
    }

    private fun dateContains(date: Long, datePair: Pair<Long, Long>): Boolean {
        if (datePair.first <= date && date <= datePair.second) return true
        return false
    }

    private fun checkDates() {
        var dateString = "${firstDateBnb.day}/${firstDateBnb.month}/${firstDateBnb.year}"
        val dfTime = SimpleDateFormat("d/MM/yyyy", Locale.getDefault())
        firstTimeStamp = dfTime.parse(dateString)?.time
        dateString = "${secondDateBnb.day}/${secondDateBnb.month}/${secondDateBnb.year}"
        secondTimeStamp = dfTime.parse(dateString)?.time
        val guestList: ArrayList<Pair<Long, Long>> = ArrayList()
        hostingReference.child(Konstants.FUTURE_GUESTS)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        for (guests in snapshot.children) {
                            hostingReference.child(Konstants.FUTURE_GUESTS)
                                .child(guests.key.toString())
                                .addListenerForSingleValueEvent(object : ValueEventListener {
                                    override fun onDataChange(snapshot: DataSnapshot) {
                                        guestList.add(snapshot.getValue(Pair::class.java) as Pair<Long, Long>)
                                        val pair =
                                            snapshot.getValue(Pair::class.java) as Pair<Long, Long>
                                        Log.d(
                                            DatePickerActivity::class.java.simpleName,
                                            "guest ${guests.key.toString()} has booking from ${pair.first} to ${pair.second}"
                                        )
                                        if (dateContains(firstTimeStamp!!, pair) || dateContains(
                                                secondTimeStamp!!,
                                                pair
                                            )
                                        ) {
                                            Toast.makeText(
                                                this@BookingActivity,
                                                "Booking overlap",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }

                                    override fun onCancelled(error: DatabaseError) {
                                        Log.d(
                                            DatePickerActivity::class.java.simpleName,
                                            "database error $error "
                                        )
                                    }
                                })
                        }

                        // safe to book
                        proceedBooking()

                    } else {
                        //no guests, safe to book
                        Log.d(DatePickerActivity::class.java.simpleName, "no guest exists")
                        proceedBooking()

                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d(DatePickerActivity::class.java.simpleName, "database error $error ")
                }
            })
    }

    private fun proceedBooking() {
        val datePair: Pair<Long?, Long?> = Pair(firstTimeStamp, secondTimeStamp)
        hostingReference.child(Konstants.FUTURE_GUESTS).child(bookingCode).setValue(datePair)
        val bookingDetails = BookingDetails(
            DatePair(firstDateBnb, secondDateBnb),
            FirebaseAuth.getInstance().uid.toString(),
            userPreference.getString(Konstants.NAME, null).toString(),
            guest,
            bookingCode,
            phoneNumber,
            basicDetails.hostingCode,
            basicDetails.title + "\n" + basicDetails.address
        )
        hostReference = FirebaseDatabase.getInstance().reference.child(Konstants.HOSTS)
        hostReference.child(basicDetails.hostUid).child(Konstants.BOOKINGREQUEST).child(bookingCode)
            .setValue(bookingDetails)
        startActivity(Intent(this, DoneActivity::class.java))
    }

    private fun bottomSheetDialogAdvance() {
        class GuestDialog(context: Context) : Dialog(context) {
            lateinit var binding: GuestDialogBinding
            override fun onCreate(savedInstanceState: Bundle?) {
                super.onCreate(savedInstanceState)
                binding = GuestDialogBinding.inflate(layoutInflater)
                setContentView(binding.root)
                binding.plusAdult.setOnClickListener {
                    guest.incrementAdult()
                }
                binding.minusAdult.setOnClickListener {
                    if (guest.adult == 1) {
                        Toast.makeText(
                            context,
                            "At least 1 Adult should accompany",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@setOnClickListener
                    }
                    guest.decrementAdult()
                }
                binding.plusChildren.setOnClickListener {
                    guest.incrementChildren()
                }
                binding.minusChildren.setOnClickListener {
                    guest.decrementChildren()
                }
                binding.plusInfant.setOnClickListener {
                    guest.incrementInfant()
                }
                binding.minusInfant.setOnClickListener {
                    guest.decrementInfant()
                }
                binding.plusPet.setOnClickListener {
                    guest.incrementPet()
                }
                binding.minusPet.setOnClickListener {
                    guest.decrementPet()
                }
            }
        }

        val guestDialog = GuestDialog(this)
        guestDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        guestDialog.show()
        guestDialog.binding.saveBt.setOnClickListener {
            guestDialog.dismiss()
            this.binding.guests.text = checkGuest()
        }
    }

    private fun checkGuest(): String {
        var guestCount = 0
        guestCount += guest.adult
        guestCount += guest.children
        var guestString = "$guestCount Guest"
        if (guest.infant > 0) {
            guestString += ",${guest.infant} Infant"
        }
        if (guest.pet > 0) {
            guestString += ",${guest.pet} Pet"
        }
        return guestString
    }

    private fun priceCalculations() {
        //price details
        days = getTotalDays(firstDateBnb, secondDateBnb)
        netTotal = df.format(BigDecimal(basicDetails.price).multiply(BigDecimal(days.toString())))
            .toDouble()
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
        val temp = 0.14
        serviceFee = df.format(BigDecimal(netTotal).multiply(BigDecimal(temp))).toDouble()
        Log.d(LOG_TAG, "service fee $serviceFee")
        binding.serviceAmtTv.text = "\u00a3$serviceFee"
        taxes = df.format(netTotal.times(0.12)).toDouble()
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

    private fun performDateCalculations(datePair: Pair<Long, Long>?) {
        if (datePair != null) {
            Log.d(
                LOG_TAG,
                "${DateBnb.setDate(datePair.first)} || ${DateBnb.setDate(datePair.second)}"
            )
            firstDateBnb = DateBnb.setDate(datePair.first)
            secondDateBnb = DateBnb.setDate(datePair.second)
            binding.dates.text = getDate(firstDateBnb, secondDateBnb)
            priceCalculations()
            Log.v(LOG_TAG, "TOTAL GBP $totalGbp")
        }
    }
}