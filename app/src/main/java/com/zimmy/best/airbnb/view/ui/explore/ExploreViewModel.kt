package com.zimmy.best.airbnb.view.ui.explore

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.zimmy.best.airbnb.models.BasicDetails

class ExploreViewModel(private val repository: ExploreRepository) : ViewModel() {

    val listLiveData: LiveData<ArrayList<BasicDetails>>
        get() = repository.listedList

    val loaded: LiveData<Boolean>
        get()=repository.ifLoaded

    fun explore() {
        repository.explore()
    }

}