package com.zimmy.best.airbnb.adapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.zimmy.best.airbnb.databinding.BookingCheckItemBinding
import com.zimmy.best.airbnb.konstants.Konstants
import com.zimmy.best.airbnb.models.BookingDetails
import com.zimmy.best.airbnb.view.payments.BeforePaymentActivity
import com.zimmy.best.airbnb.view.payments.HostViewModel
import com.zimmy.best.airbnb.view.payments.PaymentsLeftActivity
import com.zimmy.best.airbnb.view.ui.profile.ProfileRepository
import com.zimmy.best.airbnb.view.ui.profile.ProfileViewModel
import com.zimmy.best.airbnb.view.ui.profile.ProfileViewModelFactory

class BookingCheckAdapter(private val guestList: ArrayList<BookingDetails>, val context: Context) :
    RecyclerView.Adapter<BookingCheckAdapter.BookingViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookingViewHolder {
        val binding = BookingCheckItemBinding.inflate(LayoutInflater.from(context))
        return BookingViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BookingViewHolder, position: Int) {
        with(holder) {
            with(guestList[position]) {

                var uid="uid"
                var basicPhoto = "photoUrl"
                val hostViewModel =
                    ViewModelProvider(context as ViewModelStoreOwner)[HostViewModel::class.java]
                hostViewModel.getHostUid(hosting_id)

                hostViewModel.hostUidMutableLiveData.observe(context as LifecycleOwner) {
                    if (it != null) {
                        uid=it
                        val hostReference =
                            FirebaseDatabase.getInstance().reference.child(Konstants.HOSTS)
                                .child(uid)
                        hostReference.child(Konstants.HOSTINGS_MODEL1).child(this.hosting_id)
                            .child(Konstants.BASIC_PHOTO)
                            .addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    basicPhoto = snapshot.getValue(String::class.java).toString()
                                    Log.d("photo", " url-> $basicPhoto")
                                    Picasso.get().load(basicPhoto).into(binding.basicImage)
                                }

                                override fun onCancelled(error: DatabaseError) {
                                    Log.d(
                                        PaymentsLeftActivity::class.java.simpleName,
                                        "database error occurred ${error.message}"
                                    )
                                }
                            })
                    }
                }


                Log.d(BookingCheckAdapter::class.java.simpleName, "got here")
                var count = 0
                val adults = guest?.adult
                val children = guest?.children
                if (adults != null)
                    count += adults
                if (children != null)
                    count += children
                binding.name.text = user_name
                binding.details.text = hostingDetail
                binding.phoneNumber.text = user_phone
                binding.guest.text = "$count Persons"
                binding.root.setOnClickListener {
                    val intent = Intent(context, BeforePaymentActivity::class.java)
                    intent.putExtra(Konstants.BOOKINGREQUEST, this)
                    intent.putExtra(Konstants.BASIC_PHOTO, basicPhoto)
                    intent.putExtra(Konstants.UIDS,uid)
                    context.startActivity(intent)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return guestList.size
    }

    class BookingViewHolder(val binding: BookingCheckItemBinding) : ViewHolder(binding.root)
}