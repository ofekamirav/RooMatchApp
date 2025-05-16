package com.example.roomatchapp.presentation.login

import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.roomatchapp.data.local.session.UserSessionManager
import com.example.roomatchapp.data.remote.dto.IncompleteRegistrationException
import com.example.roomatchapp.data.remote.dto.LoginRequest
import com.example.roomatchapp.data.remote.dto.UserResponse
import com.example.roomatchapp.di.AppDependencies.userRepository
import com.example.roomatchapp.di.CloudinaryModel
import com.example.roomatchapp.domain.repository.UserRepository
import com.example.roomatchapp.presentation.register.RegistrationViewModel
import com.example.roomatchapp.utils.downloadImageBitmap
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class LoginState(
    val email: String = "",
    val emailError: String? = null,
    val password: String = "",
    val passwordError: String? = null,
)

class LoginViewModel(
    private val repository: UserRepository,
    private val sessionManager: UserSessionManager,
) : ViewModel() {
    private val _state = MutableStateFlow(LoginState())
    val state: StateFlow<LoginState> = _state.asStateFlow()
    val _googleSignInStatus = MutableStateFlow<String?>(null)
    val googleSignInStatus = _googleSignInStatus.asStateFlow()

    var isLoading by mutableStateOf(false)
        public set

    fun login(
        request: LoginRequest,
        onSuccess: (UserResponse) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                Log.d("TAG", "LoginViewModel-Login owner with request: ${request.email}, ${request.password}")
                val response = repository.login(request)
                Log.d("TAG", "LoginViewModel-User login response: $response")

                // Save session before proceeding
                sessionManager.saveUserSession(
                    token = response.token,
                    refreshToken = response.refreshToken,
                    userId = response.userId.toString(),
                    userType = response.userType
                )

                onSuccess(response)
                clearState()
            } catch (e: Exception) {
                Log.e("TAG", "LoginViewModel-Login failed: ${e.message}", e)
                onError(e.message ?: "Unknown error")
                clearState()
            }
        }
    }

    fun updateEmail(email: String) {
        _state.value = _state.value.copy(email = email)
    }

    fun updatePassword(password: String) {
        _state.value = _state.value.copy(
            password = password,
            passwordError = if (isValidPassword(password)) null else "Weak password"
        )
    }


    fun updateState(newState: LoginState) {
        _state.value = newState
    }

    fun clearState() {
        _state.value = LoginState()
    }

    fun googleSignIn(
        idToken: String,
        registerViewModel: RegistrationViewModel,
        context: Context
    ) {
        Log.d("TAG", "LoginViewModel-Google sign in with token: $idToken")
        viewModelScope.launch {
            isLoading = true
            try {
                val response = userRepository.googleSignIn(idToken)
                when(response.userType) {
                    "Roommate" ->
                        _googleSignInStatus.value = "ROOMMATE_SUCCESS"
                    "PropertyOwner" ->
                        _googleSignInStatus.value = "OWNER_SUCCESS"
                }
                    sessionManager.saveUserSession(
                    token = response.token,
                    refreshToken = response.refreshToken,
                    userId = response.userId.toString(),
                    userType = response.userType
                )
            } catch (e: IncompleteRegistrationException) {
                Log.e("TAG", "LoginViewModel-Google sign in catch: ${e.message}", e)

                val bitmap = downloadImageBitmap(e.profilePicture.toString())

                if (bitmap != null) {
                    CloudinaryModel().uploadImage(
                        bitmap = bitmap,
                        name = "google_profile_${System.currentTimeMillis()}",
                        folder = "roomatchapp/googleProfilePics",
                        onSuccess = { url ->
                            val uploadedUrl = url.toString()
                            registerViewModel.prefillGoogleData(
                                e.email,
                                e.fullName,
                                uploadedUrl
                            )
                        },
                        onError = {
                            Log.e("TAG", "Upload Error: $it")
                            registerViewModel.prefillGoogleData(
                                e.email,
                                e.fullName,
                                e.profilePicture
                            )
                            _googleSignInStatus.value = "NEED_REGISTRATION"
                        },
                        context = context
                    )
                }
                _googleSignInStatus.value = "NEED_REGISTRATION"

            } catch (e: Exception) {
                _googleSignInStatus.value = "ERROR $e"
            } finally {
                isLoading = false
            }
        }
    }

    fun resetGoogleSignInStatus(){
        _googleSignInStatus.value = null
    }

    //Validation
    fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun isValidPassword(password: String): Boolean {
        return password.length >= 6
    }

    // Check all fields
    fun validateAllFields(): Boolean {
        return isValidEmail(_state.value.email) &&
                isValidPassword(_state.value.password)
    }


}