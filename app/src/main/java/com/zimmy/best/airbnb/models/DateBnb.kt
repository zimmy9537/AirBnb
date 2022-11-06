package com.zimmy.best.airbnb.models

import java.text.SimpleDateFormat
import java.util.*

data class DateBnb(var day: Int, var month: Int, var year: Int) : java.io.Serializable {
    constructor() : this(0, 0, 0)

    companion object {
        fun setDate(longDate: Long): DateBnb {
            val df = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val formattedDate: String = df.format(longDate)
            val dayDate = formattedDate.slice(IntRange(0, 1)).toInt()
            val month = formattedDate.slice(IntRange(3, 4)).toInt()
            val year = formattedDate.slice(IntRange(6, 9)).toInt()
            val mCalendar = Calendar.getInstance()
            mCalendar[Calendar.YEAR] = year
            mCalendar[Calendar.MONTH] = month
            mCalendar[Calendar.DAY_OF_MONTH] = dayDate
            return DateBnb(dayDate, month, year)
        }

        fun sameDate(date1: DateBnb, date2: DateBnb): Boolean {
            if (date1.year == date2.year && date1.month == date2.month && date1.day == date2.day)
                return true
            return false
        }
    }
}
