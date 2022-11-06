package com.zimmy.best.airbnb.chat.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.zimmy.best.airbnb.konstants.Konstants
import com.zimmy.best.airbnb.models.ChatDetail

@Dao
interface ChatDao {

    @Insert
    suspend fun insertChat(chatDetail: ChatDetail)

    @Query("SELECT * FROM ${Konstants.CHATS}")
    fun getChats(): LiveData<List<ChatDetail>>

    //todo add delete annotation when logout functionality added
}