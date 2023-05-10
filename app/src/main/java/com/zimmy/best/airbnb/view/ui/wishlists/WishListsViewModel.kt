package com.zimmy.best.airbnb.view.ui.wishlists

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.zimmy.best.airbnb.models.BasicDetails

class WishListsViewModel(private val repository: WishListRepository) : ViewModel() {


    val listLiveData: LiveData<ArrayList<BasicDetails>>
        get() = repository.listedList

    val loaded: LiveData<Boolean>
        get() = repository.ifLoaded

    val wishStringLiveData: LiveData<ArrayList<String>>
        get() = repository.wishStringList

    fun getWishList() {
        repository.getWishList()
    }
}