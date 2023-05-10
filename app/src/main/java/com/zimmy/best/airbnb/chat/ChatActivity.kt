package com.zimmy.best.airbnb.chat

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.zimmy.best.airbnb.databinding.ActivityChatBinding
import com.zimmy.best.airbnb.konstants.Konstants
import com.zimmy.best.airbnb.models.ChatDetail
import com.zimmy.best.airbnb.models.Message
import java.util.Date

class ChatActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChatBinding
    private lateinit var chatDetail: ChatDetail
    private lateinit var messageList: ArrayList<Message>
    private lateinit var room: String
    private lateinit var userUid: String
    private lateinit var chatReference: DatabaseReference
    private var LOG_TAG = ChatActivity::class.java.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)
        chatDetail = intent.getSerializableExtra(Konstants.CHATS) as ChatDetail

        //setting up toolbar
        setSupportActionBar(binding.toolbarChat)
        binding.receiverNameToolbar.text = chatDetail.hostName


        userUid = FirebaseAuth.getInstance().uid.toString()
        room = chatDetail.hostUid + userUid
        chatReference = FirebaseDatabase.getInstance().reference.child(Konstants.CHATS)
        messageList = ArrayList()
        binding.chatRv.adapter = MessageAdapter(this, messageList)
        binding.chatRv.layoutManager = LinearLayoutManager(this)

        chatReference.child(room).child(Konstants.MESSAGE)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    messageList.clear()
                    for (messageCode in snapshot.children) {
                        val message = messageCode.getValue(Message::class.java)
                        if (message != null) {
                            messageList.add(message)
                            Log.d(LOG_TAG, "$message")
                        } else {
                            Log.d(LOG_TAG, "message null")
                        }
                    }
                    for(message in messageList){
                        Log.d(LOG_TAG,"message:- ${message.message}")
                    }
                    binding.progress.visibility = View.GONE
                    (binding.chatRv.adapter as MessageAdapter).notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d(LOG_TAG, "database error ${error.message}")
                }
            })

        binding.back.setOnClickListener {
            finish()
        }

        binding.sendButton.setOnClickListener {
            val messageString = binding.messageEditText.text.toString()
            if (messageString.isEmpty()) {
                Toast.makeText(this, "message cannot be empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            binding.messageEditText.text.clear()
            val message = Message(messageString, userUid, Date().time)
            val room = chatDetail.hostUid + userUid
            val chataReference =
                FirebaseDatabase.getInstance().reference.child(Konstants.CHATS).child(room)
                    .child(Konstants.MESSAGE)
            chataReference.push().setValue(message)
        }
    }
}