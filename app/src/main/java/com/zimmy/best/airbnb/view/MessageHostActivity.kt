package com.zimmy.best.airbnb.view

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.room.Room
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.zimmy.best.airbnb.chat.database.ChatDatabase
import com.zimmy.best.airbnb.databinding.ActivityMessageHostBinding
import com.zimmy.best.airbnb.konstants.Konstants
import com.zimmy.best.airbnb.models.ChatDetail
import com.zimmy.best.airbnb.models.Message
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.Date

class MessageHostActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMessageHostBinding
    private lateinit var hostUid: String
    private lateinit var userUid: String
    private lateinit var room: String
    private lateinit var hostName: String
    private lateinit var chatReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMessageHostBinding.inflate(layoutInflater)
        setContentView(binding.root)
        hostUid = intent.getStringExtra(Konstants.UIDS).toString()
        userUid = FirebaseAuth.getInstance().uid.toString()
        room = hostUid + userUid
        chatReference = FirebaseDatabase.getInstance().reference.child(Konstants.CHATS)
        findHostName(hostUid)

        binding.send.setOnClickListener {
            val message = binding.messageEt.text.toString()
            if (message.isEmpty()) {
                Toast.makeText(this, "Message canot be empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            sendMessage(message, userUid)
            saveChatDetailToDatabase(hostUid)
            val intent = Intent()
            intent.putExtra(Konstants.DATA, true)
            setResult(Activity.RESULT_OK, intent)
            finish()
        }

        binding.back.setOnClickListener {
            finish()
        }
    }

    private fun findHostName(hostUid: String) {
        val hostReference =
            FirebaseDatabase.getInstance().reference.child(Konstants.HOSTS).child(hostUid)
        hostReference.child(Konstants.DATA).child(Konstants.NAME)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    hostName = snapshot.getValue(String::class.java).toString()
                    Log.d(MessageHostActivity::class.java.simpleName, "host name $hostName")
                    binding.progress.visibility = View.GONE
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d(
                        MessageHostActivity::class.java.simpleName,
                        "database error ${error.message}"
                    )
                    binding.progress.visibility = View.GONE
                }
            })
    }

    private fun saveChatDetailToDatabase(hostUid: String) {
        val database = Room.databaseBuilder(
            this@MessageHostActivity.applicationContext,
            ChatDatabase::class.java,
            Konstants.CHATS
        ).build()
        GlobalScope.launch {
            val chatDetail = ChatDetail(userUid, hostUid, hostName)
            database.chatDao().insertChat(chatDetail)
        }
    }

    private fun sendMessage(messageString: String, userUid: String) {
        val message = Message(messageString, userUid, Date().time)
        chatReference.child(room).child(Konstants.MESSAGE).push().setValue(message)
        Toast.makeText(this, "message sent successfully", Toast.LENGTH_SHORT).show()
    }
}