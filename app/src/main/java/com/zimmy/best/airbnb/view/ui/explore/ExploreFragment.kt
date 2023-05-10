package com.zimmy.best.airbnb.view.ui.explore

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
import com.zimmy.best.airbnb.adapter.ExploreAdapter
import com.zimmy.best.airbnb.databinding.FragmentExploreBinding
import com.zimmy.best.airbnb.view.ui.wishlists.WishListRepository
import com.zimmy.best.airbnb.view.ui.wishlists.WishListViewModelFactory
import com.zimmy.best.airbnb.view.ui.wishlists.WishListsViewModel

class ExploreFragment : Fragment(), OnRefreshListener {

    private var _binding: FragmentExploreBinding? = null
    private var LOG_TAG = ExploreFragment::class.java.simpleName
    private lateinit var exploreModel: ExploreViewModel
    private lateinit var repository: ExploreRepository

    private lateinit var wishModel: WishListsViewModel
    private lateinit var wishRepository: WishListRepository
    private lateinit var wishStringList: ArrayList<String>

    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        repository = ExploreRepository()
        exploreModel = ViewModelProvider(
            this,
            ExploreViewModelFactory(repository)
        )[ExploreViewModel::class.java]

        //fetch wish list
        wishRepository = WishListRepository()
        wishModel = ViewModelProvider(
            this,
            WishListViewModelFactory(wishRepository)
        )[WishListsViewModel::class.java]
        wishStringList = ArrayList()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentExploreBinding.inflate(inflater, container, false)
        val root: View = binding.root
        binding.listRv.layoutManager = LinearLayoutManager(context)
        binding.swipeRefreshL.setOnRefreshListener(this)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getData()
    }

    private fun getData() {

        //fetch wish list
        wishModel.getWishList()
        wishModel.wishStringLiveData.observe(viewLifecycleOwner, Observer {
            wishStringList = it
        })

        Log.d(LOG_TAG, "get data")
        exploreModel.explore()
        //observe data changes
        exploreModel.listLiveData.observe(viewLifecycleOwner, Observer {
            binding.listRv.adapter = context?.let { it1 -> ExploreAdapter(it, it1) }
        })
        //observe progress
        exploreModel.loaded.observe(viewLifecycleOwner, Observer {
            if (it) {
                binding.progress.visibility = View.GONE
            } else {
                binding.progress.visibility = View.VISIBLE
            }
        })
    }

    override fun onRefresh() {
        Toast.makeText(context, "Refreshed", Toast.LENGTH_SHORT).show()
        Handler().postDelayed(Runnable {
            binding.swipeRefreshL.isRefreshing = false
            getData()
        }, 2000)
    }
}