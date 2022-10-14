package com.zimmy.best.airbnb.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.zimmy.best.airbnb.R
import com.zimmy.best.airbnb.databinding.CalendarCellBinding
import java.text.SimpleDateFormat
import java.util.*


internal class CalendarAdapter(
    private val daysOfMonth: ArrayList<String>,
    private val guestList: ArrayList<Pair<Long, Long>>,
    private val month: Int,
    private val year: Int,
    private val onItemAdapterListener: OnItemListener,
    private var context: Context
) :
    RecyclerView.Adapter<CalendarAdapter.CalendarViewHolder>() {

    init {
        Log.d(CalendarAdapter::class.java.simpleName, "month $month and year $year")
    }


    @NonNull
    override fun onCreateViewHolder(@NonNull parent: ViewGroup, viewType: Int): CalendarViewHolder {
        val view = CalendarCellBinding.inflate(LayoutInflater.from(context), parent, false)
//        val layoutParams: ViewGroup.LayoutParams = view.root.layoutParams
//        layoutParams.height = (parent.height * 0.166666666).toInt()
        return CalendarViewHolder(view, onItemAdapterListener)
    }

    override fun onBindViewHolder(@NonNull holder: CalendarViewHolder, position: Int) {
        with(holder) {
            binding.cellDayText.text = daysOfMonth[position]
            if (daysOfMonth[position] != "") {
                val df = SimpleDateFormat("d/MM/yyyy", Locale.getDefault())
                val dateString = "${daysOfMonth[position]}/$month/$year"
                val timeStamp = df.parse(dateString)?.time
                Log.d("date and time", "$dateString and $timeStamp")
                if (timeStamp != null) {
                    for (guest in guestList) {
                        if (timeStamp >= guest.first && timeStamp <= guest.second) {
                            //mark it with some color
                            binding.cellLayout.setBackgroundResource(R.drawable.circle_khokhla)
                            break
                        }
                    }
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return daysOfMonth.size
    }

    interface OnItemListener {
        fun onItemClick(position: Int, dayText: String?, binding:CalendarCellBinding)
    }

    inner class CalendarViewHolder(
        val binding: CalendarCellBinding,
        private var onItemListener: OnItemListener
    ) : RecyclerView.ViewHolder(binding.root), View.OnClickListener {
        init {
            this.onItemListener = onItemAdapterListener
            binding.root.setOnClickListener(this)
        }

        override fun onClick(view: View?) {
            onItemListener.onItemClick(adapterPosition, binding.cellDayText.text.toString(),binding)
        }
    }
}
