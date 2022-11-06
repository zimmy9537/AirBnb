package com.zimmy.best.airbnb.chat.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.zimmy.best.airbnb.models.ChatDetail

@Database(entities = [ChatDetail::class], version = 1)
abstract class ChatDatabase: RoomDatabase() {
    abstract fun chatDao() : ChatDao
}