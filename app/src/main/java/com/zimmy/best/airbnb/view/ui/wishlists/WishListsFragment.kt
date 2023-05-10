package com.zimmy.best.airbnb.view.ui.wishlists

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.zimmy.best.airbnb.adapter.ExploreAdapter
import com.zimmy.best.airbnb.databinding.FragmentWishlistsBinding

class WishListsFragment : Fragment() {

    private var _binding: FragmentWishlistsBinding? = null
    private val binding get() = _binding!!
    private var LOG_TAG = WishListsFragment::class.java.simpleName
    private lateinit var wishModel: WishListsViewModel
    private lateinit var repository: WishListRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        repository = WishListRepository()
        wishModel = ViewModelProvider(
            this,
            WishListViewModelFactory(repository)
        )[WishListsViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWishlistsBinding.inflate(inflater, container, false)
        binding.listRv.layoutManager = LinearLayoutManager(context)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getData()
    }

    private fun getData() {
        wishModel.getWishList()
        wishModel.listLiveData.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                if (it.isEmpty()) {
                    binding.emptyText.visibility = View.VISIBLE
                } else {
                    binding.emptyText.visibility = View.GONE
                    binding.listRv.adapter = context?.let { it1 -> ExploreAdapter(it, it1) }
                }
            }
        })

        wishModel.loaded.observe(viewLifecycleOwner, Observer {
            if (it) {
                binding.progress.visibility = View.GONE
            } else {
                binding.progress.visibility = View.VISIBLE
            }
        })

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}