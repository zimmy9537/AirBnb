package com.zimmy.best.airbnb.view.ui.wishlists

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.zimmy.best.airbnb.view.ui.explore.ExploreViewModel

class WishListViewModelFactory(private val repository: WishListRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return WishListsViewModel(repository) as T
    }
}