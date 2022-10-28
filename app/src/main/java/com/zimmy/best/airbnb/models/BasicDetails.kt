package com.zimmy.best.airbnb.models

import java.io.Serializable

data class BasicDetails(
    var option: Int,
    var title: String,
    var address: String,
    var longitude: String,
    var latitude: String,
    var price: Double,
    var basicPhoto: String,
    var available: Boolean,
    var rating: Double,
    var reviews: Int,
    var totalRated: Int,
    var hostingCode: String,
    var hostUid: String
) : Serializable {
    constructor(
        option: Int,
        title: String,
        address: String,
        longitude: String,
        latitude: String,
        price: Double,
        rating: Double,
        reviews: Int,
        totalRated: Int,
        hostingCode: String,
        hostUid: String
    ) : this(
        option,
        title,
        address,
        longitude,
        latitude,
        price,
        "",
        false,
        rating,
        reviews,
        totalRated,
        hostingCode,
        hostUid
    )

    constructor() : this(0, "", "", "", "", 0.0, "", false, 0.0, 0, 0, "", "")
}
