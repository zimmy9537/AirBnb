package com.zimmy.best.airbnb.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.squareup.picasso.Picasso
import com.zimmy.best.airbnb.databinding.PhotoItemBinding

class PhotoAdapter(val photoList: ArrayList<String>, val context: Context) :
    RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val binding = PhotoItemBinding.inflate(LayoutInflater.from(context), parent, false)
        return PhotoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        with(holder) {
            with(photoList[position]) {
                Picasso.get().load(this).into(binding.value)
            }
        }
    }

    override fun getItemCount(): Int {
        return photoList.size
    }

    inner class PhotoViewHolder(val binding: PhotoItemBinding) : ViewHolder(binding.root)
}