package com.example.roadcast.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.roadcast.network.ApiMethods
import kotlinx.coroutines.launch

class MainActivityViewModel(private val apiMethods: ApiMethods) : ViewModel() {

    private var _response: MutableLiveData<BaseResponse> = MutableLiveData()
    private var _loading: MutableLiveData<Boolean> = MutableLiveData()
    private var _error: MutableLiveData<String> = MutableLiveData()

    val response: LiveData<BaseResponse> = _response
    val loading: LiveData<Boolean> = _loading
    val error: LiveData<String> = _error



    fun getEntries() {
        viewModelScope.launch {
            runCatching {
                emitUIState(showProgress = true)
                apiMethods.getEntries()
            }.onSuccess {
                emitUIState(showProgress = false)
                if (it.isSuccessful && it.body() != null) {
                    emitUIState(response = it.body())
                } else {
                    emitUIState(error = it.message())
                }
            }.onFailure {
                emitUIState(showProgress = false, error = it.localizedMessage)
            }
        }
    }

    private fun emitUIState(
        showProgress: Boolean? = false,
        response: BaseResponse? = null,
        error: String? = null
    ) {
        if (showProgress != null) _loading.postValue(showProgress)
        if (response != null) _response.postValue(response)
        if (error != null) _error.postValue(error)
    }

}