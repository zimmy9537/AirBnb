package com.zimmy.best.airbnb.models

class User(val name:String?,val email:String?) {
    constructor():this(null,null)
}