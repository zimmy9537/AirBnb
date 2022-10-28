package com.zimmy.best.airbnb.models

data class DatePair(
    val firstDate: DateBnb?,
    val secondDate: DateBnb?
) {
    constructor() : this(null, null)
}