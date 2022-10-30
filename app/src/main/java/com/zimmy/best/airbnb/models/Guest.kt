package com.zimmy.best.airbnb.models

import java.io.Serializable

data class Guest(var adult: Int, var children: Int, var infant: Int, var pet: Int): Serializable {

    constructor():this(0,0,0,0)

    fun incrementAdult() {
        adult++
    }

    fun incrementChildren() {
        children++
    }

    fun incrementInfant() {
        infant++
    }

    fun incrementPet() {
        pet++
    }

    fun decrementAdult() {
        if (adult == 1) return
        adult--
    }

    fun decrementChildren() {
        if (children == 0) return
        children--
    }

    fun decrementInfant() {
        if (infant == 0) return
        infant--
    }

    fun decrementPet() {
        if (pet == 0) return
        pet--
    }
}