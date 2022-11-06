package com.zimmy.best.airbnb.models

data class Message(
    var message: String,
    val senderId: String,
    val timeStamp: Long = 0
) {
    constructor() : this("", "", 0L)
}