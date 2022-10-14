package com.zimmy.best.airbnb.models

data class User(val name:String?,val email:String?) {
    constructor():this(null,null)
}