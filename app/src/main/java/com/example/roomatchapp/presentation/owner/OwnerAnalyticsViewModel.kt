package com.example.roomatchapp.presentation.owner


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.roomatchapp.data.local.session.UserSessionManager
import com.example.roomatchapp.data.model.AnalyticsResponse
import com.example.roomatchapp.domain.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

data class AnalyticsState(
    val analytics: AnalyticsResponse? = null
)

class OwnerAnalyticsViewModel(
    private val userRepository: UserRepository,
    private val ownerId: String,
    private val userSessionManager: UserSessionManager
): ViewModel()  {
    private val _analyticsResponse = MutableStateFlow(AnalyticsState())
    val analyticsResponse: MutableStateFlow<AnalyticsState> = _analyticsResponse

    private val _message = MutableStateFlow("")
    val message: MutableStateFlow<String> = _message

    init{
        viewModelScope.launch {
            val updated = userSessionManager.consumeUpdatedPreferencesFlag()
            loadAnalytics(ownerId,updated)
        }
    }

    private suspend fun loadAnalytics(ownerId: String,forceRefresh: Boolean = false) {
        val analytics = userRepository.getOwnerAnalytics(ownerId,forceRefresh)
        if (analytics != null) {
            _analyticsResponse.value = AnalyticsState(analytics)
        }else{
            _analyticsResponse.value = AnalyticsState(null)
            _message.value = "No analytics found"
        }
    }

}