package com.zimmy.best.airbnb.adapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.zimmy.best.airbnb.databinding.ListedItemBinding
import com.zimmy.best.airbnb.konstants.Konstants
import com.zimmy.best.airbnb.models.BasicDetails
import com.zimmy.best.airbnb.view.DetailActivity

class ExploreAdapter(
    private val listedList: ArrayList<BasicDetails>,
    private val context: Context
) :
    RecyclerView.Adapter<ExploreAdapter.ListingViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListingViewHolder {
        val binding = ListedItemBinding.inflate(LayoutInflater.from(context), parent, false)
        return ListingViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ListingViewHolder, position: Int) {
        with(holder) {
            with(listedList[position]) {
                Picasso.get().load(this.basicPhoto).into(binding.basicImage)
                binding.address.text = this.address
                binding.title.text = this.title
                if (this.rating.equals(0.0)) {
                    binding.rating.text = "New"
                } else {
                    binding.rating.text = this.rating.toString()
                }
                binding.price.text = buildString {
                    append("\u00a3")
                    append(this@with.price)
                }
                binding.root.setOnClickListener {
                    Log.d("intent", "call")
                    val intent = Intent(context, DetailActivity::class.java)
                    intent.putExtra(Konstants.HOSTINGCODE, this.hostingCode)
                    context.startActivity(intent)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return listedList.size
    }

    inner class ListingViewHolder(val binding: ListedItemBinding) :
        RecyclerView.ViewHolder(binding.root)
}