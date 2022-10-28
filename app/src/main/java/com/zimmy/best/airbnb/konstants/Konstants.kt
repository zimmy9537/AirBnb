package com.zimmy.best.airbnb.konstants

import java.util.*

class Konstants {
    companion object {

        //firebase
        const val UIDS = "Uids"
        const val USERS = "Users"
        const val DATA = "Data"
        const val GENERAL = "General"
        const val USERCOUNT = "userCount"
        const val HOSTINGMODEL1 = "HostingsModel1"
        const val BASICDETAILS = "BasicDetails"
        const val HOSTINGCODE = "hostingCode"
        const val HOSTINGDETAILS = "hostingDetails"
        const val FUTURE_GUESTS="FutureGuests"
        const val HOSTS="Hosts"
        const val BOOKINGREQUEST="BookingRequest"


        //shared preference
        const val PERSONAL = "Personal"
        const val NAME = "Name"
        const val EMAIL = "Email"

        //others
        const val FIRSTDATE="FirstDate"
        const val SECONDDATE="SecondDate"
        const val BOOKINGCODE=5

        fun codeGenerator(totalCharacters: Int): String {
            val alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890"
            val sb = StringBuilder()
            val random = Random()
            for (i in 0 until totalCharacters) {
                val index = random.nextInt(alphabet.length)
                val randomChar = alphabet[index]
                sb.append(randomChar)
            }
            return sb.toString()
        }
    }
}