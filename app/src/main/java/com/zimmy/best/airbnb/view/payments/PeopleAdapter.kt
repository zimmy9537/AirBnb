package com.zimmy.best.airbnb.view.payments

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.zimmy.best.airbnb.chat.ChatActivity
import com.zimmy.best.airbnb.databinding.ItemPersonBinding
import com.zimmy.best.airbnb.konstants.Konstants
import com.zimmy.best.airbnb.models.ChatDetail

class PeopleAdapter(val context: Context, val chatList: ArrayList<ChatDetail>) :
    RecyclerView.Adapter<PeopleAdapter.PeopleViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PeopleViewHolder {
        val binding = ItemPersonBinding.inflate(LayoutInflater.from(context))
        return PeopleViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PeopleViewHolder, position: Int) {
        with(holder) {
            with(chatList[position]) {
                binding.personItemName.text = this.hostName

                binding.root.setOnClickListener {
                    val intent = Intent(context, ChatActivity::class.java)
                    intent.putExtra(Konstants.CHATS, this)
                    context.startActivity(intent)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return chatList.size
    }

    inner class PeopleViewHolder(val binding: ItemPersonBinding) :
        RecyclerView.ViewHolder(binding.root)
}