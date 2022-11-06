package com.zimmy.best.airbnb.chat.database

import android.content.Context
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.room.Room
import com.zimmy.best.airbnb.konstants.Konstants
import com.zimmy.best.airbnb.models.ChatDetail

class ChatDetailViewModel : ViewModel() {

    var mutableLiveData: MutableLiveData<ArrayList<ChatDetail>>? = null

    fun getDetails(context: Context, lifecycleOwner: LifecycleOwner) {
        if (mutableLiveData == null) {
            mutableLiveData = MutableLiveData<ArrayList<ChatDetail>>()
            loadData(context, lifecycleOwner)
        }
    }

    private fun loadData(context: Context, lifecycleOwner: LifecycleOwner) {
        var chatDetailList: ArrayList<ChatDetail>
        val database: ChatDatabase = Room.databaseBuilder(
            context.applicationContext,
            ChatDatabase::class.java,
            Konstants.CHATS
        ).build()

        chatDetailList = ArrayList()
        database.chatDao().getChats().observe(lifecycleOwner, Observer {
            chatDetailList = it as ArrayList<ChatDetail>
            mutableLiveData?.postValue(chatDetailList)
        })
    }
}