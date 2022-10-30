package com.zimmy.best.airbnb.models

data class Bill(val price: Double, val nights: Int, val serviceFee: Double, val taxes: Double) :
    java.io.Serializable {
    constructor() : this(0.0, 0, 0.0, 0.0)
}
