package com.zimmy.best.airbnb.models

import java.text.SimpleDateFormat
import java.util.*

data class DateBnb(var day: Int, var month: Int, var year: Int) {
    constructor() : this(0, 0, 0)

    companion object {
        public fun setDate(longDate: Long): DateBnb {
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
    }

}
