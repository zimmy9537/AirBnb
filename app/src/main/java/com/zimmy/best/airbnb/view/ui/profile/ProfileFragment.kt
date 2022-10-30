package com.zimmy.best.airbnb.view.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.zimmy.best.airbnb.databinding.FragmentProfileBinding
import com.zimmy.best.airbnb.konstants.Konstants
import com.zimmy.best.airbnb.models.BookingDetails
import com.zimmy.best.airbnb.view.payments.PaymentsLeftActivity

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var profileViewModel: ProfileViewModel
    private lateinit var repository: ProfileRepository
    private var paymentsLeft = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        repository = ProfileRepository()
        profileViewModel =
            ViewModelProvider(
                this,
                ProfileViewModelFactory(repository)
            )[ProfileViewModel::class.java]

        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getData()
        binding.payments.setOnClickListener {
            if (paymentsLeft) {
                Toast.makeText(context, "Some of your Payments are left", Toast.LENGTH_SHORT).show()
                val intent = Intent(context, PaymentsLeftActivity::class.java)
                startActivity(intent)
            } else {
                Toast.makeText(context, "No Payments are left", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getData() {
        profileViewModel.checkBookings()
        //observe data changes
        profileViewModel.paymentLeftLiveData.observe(viewLifecycleOwner) {
            paymentsLeft = it
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}