package com.zimmy.best.airbnb.chat

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.zimmy.best.airbnb.databinding.HostMessageItemBinding
import com.zimmy.best.airbnb.databinding.UserMessageItemBinding
import com.zimmy.best.airbnb.models.DateBnb
import com.zimmy.best.airbnb.models.Message
import java.text.SimpleDateFormat
import java.util.Date

class MessageAdapter(val context: Context, val messageList: ArrayList<Message>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val USER_ITEM = 1
    private val HOST_ITEM = 2

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == USER_ITEM) {
            val binding = UserMessageItemBinding.inflate(LayoutInflater.from(context))
            UserViewHolder(binding)
        } else {
            val binding = HostMessageItemBinding.inflate(LayoutInflater.from(context))
            HostViewHolder(binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        Log.d(ChatActivity::class.java.simpleName, "onBinding")
        val dateFormat = SimpleDateFormat("hh.mm aa")
        val today = DateBnb.setDate(Date().time)
        if (holder.javaClass == UserViewHolder::class.java) {
            val viewHolder = holder as UserViewHolder
            with(viewHolder) {
                with(messageList[position]) {
                    if (position > 0) {
                        val previousDay = DateBnb.setDate(messageList[position - 1].timeStamp)
                        val currentDay = DateBnb.setDate(this.timeStamp)
                        if (DateBnb.sameDate(currentDay, previousDay)) {
                            binding.linearLayout.visibility = View.GONE
                        } else {
                            if (DateBnb.sameDate(currentDay, today)) {
                                binding.dateSpecifier.text = "Today"
                            } else {
                                binding.dateSpecifier.text =
                                    "${currentDay.day}/${currentDay.month}/${currentDay.year}"
                            }
                        }
                    } else {
                        binding.linearLayout.visibility = View.GONE
                    }
                    binding.senderMessage.text = this.message
                    val time = dateFormat.format(Date(this.timeStamp))
                    binding.senderTime.text = time
                }
            }
        } else {
            val viewHolder = holder as HostViewHolder
            with(viewHolder) {
                with(messageList[position]) {
                    if (position > 0) {
                        val previousDay = DateBnb.setDate(messageList[position - 1].timeStamp)
                        val currentDay = DateBnb.setDate(this.timeStamp)
                        if (DateBnb.sameDate(currentDay, previousDay)) {
                            binding.linearLayout.visibility = View.GONE
                        } else {
                            if (DateBnb.sameDate(currentDay, today)) {
                                binding.dateSpecifier.text = "Today"
                            } else {
                                binding.dateSpecifier.text =
                                    "${currentDay.day}/${currentDay.month}/${currentDay.year}"
                            }
                        }
                    } else {
                        binding.linearLayout.visibility = View.GONE
                    }
                    binding.receiverMessage.text = this.message
                    val time = dateFormat.format(Date(this.timeStamp))
                    binding.receiverTime.text = time
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return messageList.size
    }

    override fun getItemViewType(position: Int): Int {
        val message = messageList[position]
        val uid = FirebaseAuth.getInstance().uid.toString()
        return if (message.senderId == uid) {
            USER_ITEM
        } else {
            HOST_ITEM
        }
    }

    inner class UserViewHolder(val binding: UserMessageItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    inner class HostViewHolder(val binding: HostMessageItemBinding) :
        RecyclerView.ViewHolder(binding.root)
}