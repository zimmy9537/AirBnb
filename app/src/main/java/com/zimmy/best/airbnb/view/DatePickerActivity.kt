package com.zimmy.best.airbnb.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import com.zimmy.best.airbnb.R
import com.zimmy.best.airbnb.adapter.CalendarAdapter
import com.zimmy.best.airbnb.databinding.ActivityDatePickerBinding
import com.zimmy.best.airbnb.databinding.CalendarCellBinding
import com.zimmy.best.airbnb.konstants.Konstants
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList


class DatePickerActivity : AppCompatActivity(), CalendarAdapter.OnItemListener {

    private lateinit var binding: ActivityDatePickerBinding


    private lateinit var selectedDate: LocalDate
    private lateinit var datePair: Pair<Long, Long>
    private var dateSelected: Boolean = false
    private lateinit var hostingCode: String
    private lateinit var guestList: ArrayList<Pair<Long, Long>>
    private var firstDate: Long = 0L
    private var secondDate: Long = 0L
    private lateinit var firstDateBinding: CalendarCellBinding
    private lateinit var secondDateBinding: CalendarCellBinding
    private lateinit var hostingReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDatePickerBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        selectedDate = LocalDate.now()
        guestList = ArrayList()
        setMonthView()

        hostingCode = intent.getStringExtra(Konstants.HOSTINGCODE).toString()

        hostingReference = FirebaseDatabase.getInstance().reference.child(Konstants.HOSTINGMODEL1)
            .child(hostingCode)


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
                                    }

                                    override fun onCancelled(error: DatabaseError) {
                                        Log.d(
                                            DatePickerActivity::class.java.simpleName,
                                            "database error $error "
                                        )
                                    }
                                })
                        }
                    } else {
                        //no guests
                        Log.d(DatePickerActivity::class.java.simpleName, "no guest exists")

                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d(DatePickerActivity::class.java.simpleName, "database error $error ")
                }
            })


        binding.cancel.setOnClickListener {
            finish()
        }

        binding.saveAndNext.setOnClickListener {
            if (!dateSelected) {
                Toast.makeText(this, "please select the dates for your stay", Toast.LENGTH_SHORT)
                    .show()
                val intent = Intent()
                setResult(Activity.RESULT_CANCELED, intent)
            } else {
                val intent = Intent()
                intent.putExtra(Konstants.DATA, datePair)
                setResult(Activity.RESULT_OK, intent)
                finish()
            }
        }
    }

    private fun setMonthView() {
        binding.monthYearTV.text = monthYearFromDate(selectedDate)
        val daysInMonth = daysInMonthArray(selectedDate)
        val calendarAdapter = CalendarAdapter(
            daysInMonth,
            guestList,
            selectedDate.monthValue,
            selectedDate.year,
            this,
            this
        )
        val layoutManager: RecyclerView.LayoutManager = GridLayoutManager(applicationContext, 7)
        binding.calendarRecyclerView.layoutManager = layoutManager
        binding.calendarRecyclerView.adapter = calendarAdapter
    }

    private fun daysInMonthArray(date: LocalDate): ArrayList<String> {
        val daysInMonthArray: ArrayList<String> = ArrayList()
        val yearMonth: YearMonth = YearMonth.from(date)
        val daysInMonth: Int = yearMonth.lengthOfMonth()
        val firstOfMonth: LocalDate = selectedDate.withDayOfMonth(1)
        val dayOfWeek: Int = firstOfMonth.dayOfWeek.value
        for (i in 1..42) {
            if (i <= dayOfWeek || i > daysInMonth + dayOfWeek) {
                daysInMonthArray.add("")
            } else {
                daysInMonthArray.add((i - dayOfWeek).toString())
            }
        }
        return daysInMonthArray
    }

    private fun monthYearFromDate(date: LocalDate): String {
        val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("MMMM yyyy")
        return date.format(formatter)
    }

    fun previousMonthAction(view: View?) {
        selectedDate = selectedDate.minusMonths(1)
        setMonthView()
    }

    fun nextMonthAction(view: View?) {
        selectedDate = selectedDate.plusMonths(1)
        setMonthView()
    }

    override fun onItemClick(position: Int, dayText: String?, binding: CalendarCellBinding) {
        if (dayText != "") {
            val message = "Selected Date " + dayText + " " + monthYearFromDate(selectedDate)
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            val df = SimpleDateFormat("d/MM/yyyy", Locale.getDefault())
            val dateString = "$dayText/${selectedDate.monthValue}/${selectedDate.year}"
            val timeStamp = df.parse(dateString)?.time
            Log.d("selected date and time", "$dateString and $timeStamp")
            if (timeStamp != null) {
                var conflict = false
                for (guest in guestList) {
                    if (timeStamp >= guest.first && timeStamp <= guest.second) {
                        conflict = true
                        break
                    }
                }
                if (conflict) {
                    Toast.makeText(this, "conflict", Toast.LENGTH_SHORT).show()
                } else {
                    Log.d(DatePickerActivity::class.java.simpleName, "no conflict enjoy")
                    if (firstDate == 0L && secondDate == 0L) {
                        firstDate = timeStamp
                        firstDateBinding = binding
                        binding.cellLayout.setBackgroundResource(R.drawable.circle_filled)
                        dateSelected = false
                    } else if (secondDate == 0L) {
                        if (firstDate == timeStamp) {
                            Toast.makeText(this, "Select some unique date", Toast.LENGTH_SHORT)
                                .show()
                            return
                        }
                        secondDate = timeStamp
                        secondDateBinding = binding
                        binding.cellLayout.setBackgroundResource(R.drawable.circle_filled)
                        if (firstDate > secondDate) {
                            firstDate = firstDate.xor(secondDate)
                            secondDate = firstDate.xor(secondDate)
                            firstDate = firstDate.xor(secondDate)
                        }
                        datePair = Pair(firstDate, secondDate)
                        dateSelected = true
                    } else {
                        firstDateBinding.cellLayout.setBackgroundResource(0)
                        secondDateBinding.cellLayout.setBackgroundResource(0)
                        firstDate = timeStamp
                        firstDateBinding = binding
                        firstDateBinding.cellLayout.setBackgroundResource(R.drawable.circle_filled)
                        secondDate = 0L
                        dateSelected = false
                    }
                }
            }
        }
    }
}