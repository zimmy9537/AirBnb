package com.zimmy.best.airbnb.view.ui.explore

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ExploreViewModelFactory(private val repository: ExploreRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ExploreViewModel(repository) as T
    }
}