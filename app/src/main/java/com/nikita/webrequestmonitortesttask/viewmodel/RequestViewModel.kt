package com.nikita.webrequestmonitortesttask.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nikita.webrequestmonitortesttask.data.local.UserRequest
import com.nikita.webrequestmonitortesttask.repository.RequestRepository
import com.nikita.webrequestmonitortesttask.utils.ContextUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class RequestViewModel(private val repository: RequestRepository, private val contextUtils: ContextUtils) : ViewModel() {

    private val _allRequests = MutableStateFlow(emptyList<UserRequest>())
    val allRequests: StateFlow<List<UserRequest>> = _allRequests

    private val _isAccessibilityServiceEnabled = MutableStateFlow(false)
    val isAccessibilityServiceEnabled: StateFlow<Boolean> = _isAccessibilityServiceEnabled

    private val _showServiceButHasRequestsScreen = MutableStateFlow(false)
    val showServiceButHasRequestsScreen: StateFlow<Boolean> = _showServiceButHasRequestsScreen

    init {
        refreshRequests()
    }


    fun deleteRequest(id: Int) = viewModelScope.launch {
        repository.deleteRequest(id.toLong())
        refreshRequests()
    }

    fun refreshRequests() = viewModelScope.launch {
        repository.allRequestsFlow.collect { requests ->
            _allRequests.value = requests
        }
    }

    suspend fun getAllRequestsOnce(): List<UserRequest> {
        return repository.getAllRequestsOnce()
    }


    fun setAccessibilityServiceEnabled(isEnabled: Boolean) {
        _isAccessibilityServiceEnabled.value = isEnabled
    }

    fun showServiceButHasRequestsScreen() {
        _showServiceButHasRequestsScreen.value = true
    }

    fun checkAndRefreshRequests() {
        viewModelScope.launch {
            val hasRequests = getAllRequestsOnce().isNotEmpty()
            if (hasRequests && !_isAccessibilityServiceEnabled.value) {
                showServiceButHasRequestsScreen()
            } else {
                refreshRequests()
            }
        }
    }

    fun updateAccessibilityServiceStatus() {
        val isEnabled = contextUtils.checkAccessibilityServiceEnabled()
        setAccessibilityServiceEnabled(isEnabled)
    }
}