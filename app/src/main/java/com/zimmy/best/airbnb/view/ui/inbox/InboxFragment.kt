package com.zimmy.best.airbnb.view.ui.inbox

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.recyclerview.widget.LinearLayoutManager
import com.zimmy.best.airbnb.chat.database.ChatDetailViewModel
import com.zimmy.best.airbnb.databinding.FragmentInboxBinding
import com.zimmy.best.airbnb.models.ChatDetail
import com.zimmy.best.airbnb.view.payments.PeopleAdapter

class InboxFragment : Fragment() {

    private var _binding: FragmentInboxBinding? = null
    private var LOG_TAG = InboxFragment::class.java.simpleName
    private lateinit var chatModel: ChatDetailViewModel
    private lateinit var linearLayoutManager: LinearLayoutManager
    private val binding get() = _binding!!
    private lateinit var chatList: ArrayList<ChatDetail>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        chatModel =
            ViewModelProvider(context as ViewModelStoreOwner)[ChatDetailViewModel::class.java]
        linearLayoutManager = LinearLayoutManager(context)
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInboxBinding.inflate(inflater, container, false)
        binding.chatRv.layoutManager = linearLayoutManager
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(InboxFragment::class.java.simpleName, "set data")
        setData()
    }

    private fun setData() {
        binding.progressBarMain.visibility = View.VISIBLE
        chatModel.getDetails(context!!, context as LifecycleOwner)
        chatModel.mutableLiveData?.observe(viewLifecycleOwner, Observer {
            if (it == null) {
                Log.d(InboxFragment::class.java.simpleName, "mo messages")
                return@Observer
            }
            for (chatDetail in it) {
                Log.d(LOG_TAG, "Chat Detail $chatDetail")
            }
            binding.progressBarMain.visibility = View.GONE
            chatList = it
            binding.chatRv.adapter = PeopleAdapter(context!!, chatList)
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}