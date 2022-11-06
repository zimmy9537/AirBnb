package com.zimmy.best.airbnb.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.zimmy.best.airbnb.konstants.Konstants
import java.io.Serializable

@Entity(tableName = Konstants.CHATS)
data class ChatDetail(
    @PrimaryKey
    val userUid: String,
    val hostUid: String,
    val hostName: String
): Serializable