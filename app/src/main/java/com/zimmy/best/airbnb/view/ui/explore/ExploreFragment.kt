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

class ExploreFragment : Fragment(), OnRefreshListener {

    private var _binding: FragmentExploreBinding? = null
    private var LOG_TAG = ExploreFragment::class.java.simpleName
    private lateinit var exploreModel: ExploreViewModel
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var repository: ExploreRepository

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        repository = ExploreRepository()
        exploreModel = ViewModelProvider(
            this,
            ExploreViewModelFactory(repository)
        )[ExploreViewModel::class.java]
        linearLayoutManager = LinearLayoutManager(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentExploreBinding.inflate(inflater, container, false)
        val root: View = binding.root
        binding.listRv.layoutManager = linearLayoutManager
        binding.swipeRefreshL.setOnRefreshListener(this)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(LOG_TAG, "onViewCreated")
        getData()
    }

    private fun getData() {
        Log.d(LOG_TAG, "get data")
        exploreModel.explore()
        //observe data changes
        exploreModel.listLiveData.observe(viewLifecycleOwner, Observer {
            for (basicDetail in it) {
                Log.d(LOG_TAG, "basicDetails ${basicDetail.title}, ${basicDetail.address}")
            }

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