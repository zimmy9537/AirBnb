package com.zimmy.best.airbnb.models

import java.io.Serializable

data class DatePair(
    val firstDate: DateBnb?,
    val secondDate: DateBnb?
): Serializable {
    constructor() : this(null, null)
}