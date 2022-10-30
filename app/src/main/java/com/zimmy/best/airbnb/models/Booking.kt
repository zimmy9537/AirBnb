package com.zimmy.best.airbnb.models

data class Booking(val booking:BookingDetails?,val bill:Bill?){
    constructor():this(null,null)
}
