package com.developers.shopapp.ui.viewmodels

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chaos.view.PinView
import com.developers.shopapp.entities.AuthModel
import com.developers.shopapp.entities.MyResponse
import com.developers.shopapp.helpers.Event
import com.developers.shopapp.helpers.MyValidation
import com.developers.shopapp.helpers.Resource
import com.developers.shopapp.repositories.AuthenticationRepository
import com.developers.shopapp.qualifiers.MainThread
import com.developers.shopapp.utils.Constants
import com.developers.shopapp.utils.Constants.TAG
import com.developers.shopapp.utils.Utils.buildAlertMessageNoGps
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.textfield.TextInputLayout
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthenticationViewModel @Inject constructor(
    @ApplicationContext val context: Context,
    private val repository: AuthenticationRepository,
    @MainThread
    private val dispatcher: CoroutineDispatcher

) : ViewModel() {


    private val _loginUserStatus =
        MutableStateFlow<Event<Resource<MyResponse<String>>>>(Event(Resource.Init()))
    val authUserStatus: MutableStateFlow<Event<Resource<MyResponse<String>>>> = _loginUserStatus

    private val _createAccountStatus =
        MutableStateFlow<Event<Resource<MyResponse<String>>>>(Event(Resource.Init()))
    val createAccountStatus: MutableStateFlow<Event<Resource<MyResponse<String>>>> = _createAccountStatus

    private val _logoutStatus = MutableStateFlow<Event<Resource<AuthModel>>>(Event(Resource.Init()))
    val logoutStatus: MutableStateFlow<Event<Resource<AuthModel>>> = _logoutStatus

    private val _verifyEmailStatus = MutableStateFlow<Event<Resource<AuthModel>>>(Event(Resource.Init()))
    val verifyEmailStatus: MutableStateFlow<Event<Resource<AuthModel>>> = _verifyEmailStatus

    private val _verifyCodeStatus = MutableStateFlow<Event<Resource<AuthModel>>>(Event(Resource.Init()))
    val verifyCodeStatus: MutableStateFlow<Event<Resource<AuthModel>>> = _verifyCodeStatus

    private val _resetPasswordStatus = MutableStateFlow<Event<Resource<AuthModel>>>(Event(Resource.Init()))
    val resetPasswordStatus: MutableStateFlow<Event<Resource<AuthModel>>> = _resetPasswordStatus


    fun loginUser(
        inputTextLayoutEmail: TextInputLayout,
        inputTextLayoutPassword: TextInputLayout,
        latLong: LatLng?
    ) {
        viewModelScope.launch(dispatcher) {
            val email = inputTextLayoutEmail.editText!!.text.toString()
            val password = inputTextLayoutPassword.editText!!.text.toString()
            when {
                latLong == null->{
                    _loginUserStatus.emit(Event(Resource.Error("PLZ,Open Your Location")))
                }
                email.isEmpty() -> {
                    _loginUserStatus.emit(Event(Resource.Error("E-mail is require")))
                    inputTextLayoutEmail.isHelperTextEnabled = true
                    inputTextLayoutEmail.helperText = "Require*"
                }

                !MyValidation.isValidEmail(email = email) -> {
                    _loginUserStatus.emit(Event(Resource.Error("E-mail is not valid")))
                    inputTextLayoutEmail.isHelperTextEnabled = true
                    inputTextLayoutEmail.helperText = "not valid"
                }
                password.isEmpty() -> {
                    _loginUserStatus.emit(Event(Resource.Error("Password is require")))
                    inputTextLayoutPassword.isHelperTextEnabled = true
                    inputTextLayoutPassword.helperText = "Require*"
                }
                !MyValidation.validatePass(context, inputTextLayoutPassword) -> {
                    _loginUserStatus.emit(Event(Resource.Error(inputTextLayoutPassword.helperText.toString())))
                }
                else -> {
                    _loginUserStatus.emit(Event(Resource.Loading()))
                    Log.i(TAG, "loginUser: Sucess")
                    val result = repository.loginUser(email, password)
                    _loginUserStatus.emit(Event(result))
                }
            }
        }

    }

    fun createAccount(
        inputTextLayoutUserName: TextInputLayout,
        inputTextLayoutEmail: TextInputLayout,
        inputTextLayoutMobile: TextInputLayout,
        inputTextLayoutPassword: TextInputLayout,
        latLong: LatLng?
    ) {
        viewModelScope.launch(dispatcher) {
            val username = inputTextLayoutUserName.editText!!.text.toString()
            val email = inputTextLayoutEmail.editText!!.text.toString()
            val mobile = inputTextLayoutMobile.editText!!.text.toString()
            val password = inputTextLayoutPassword.editText!!.text.toString()
            when {
                username.isEmpty() -> {
                    _createAccountStatus.emit(Event(Resource.Error("Username is require")))
                    inputTextLayoutUserName.isHelperTextEnabled = true
                    inputTextLayoutUserName.helperText = "Require*"
                }

                email.isEmpty() -> {
                    _createAccountStatus.emit(Event(Resource.Error("E-mail is require")))
                    inputTextLayoutEmail.isHelperTextEnabled = true
                    inputTextLayoutEmail.helperText = "Require*"
                }

                !MyValidation.isValidEmail(email = email) -> {
                    _createAccountStatus.emit(Event(Resource.Error("E-mail is not valid")))
                    inputTextLayoutEmail.isHelperTextEnabled = true
                    inputTextLayoutEmail.helperText = "not valid"
                }
                mobile.isEmpty() -> {
                    _createAccountStatus.emit(Event(Resource.Error("Password is require")))
                    inputTextLayoutMobile.isHelperTextEnabled = true
                    inputTextLayoutMobile.helperText = "Require*"
                }
                !MyValidation.validateMobile(mobile) -> {
                    _createAccountStatus.emit(Event(Resource.Error("Phone Number is not valid")))
                    inputTextLayoutMobile.isHelperTextEnabled = true
                    inputTextLayoutMobile.helperText = "Mobile not valid"
                }

                password.isEmpty() -> {
                    _createAccountStatus.emit(Event(Resource.Error("Password is require")))
                    inputTextLayoutPassword.isHelperTextEnabled = true
                    inputTextLayoutPassword.helperText = "Require*"
                }
                !MyValidation.validatePass(context, inputTextLayoutPassword) -> {
                    _createAccountStatus.emit(Event(Resource.Error(inputTextLayoutPassword.helperText.toString())))
                }
                latLong==null ->{
                    _createAccountStatus.emit(Event(Resource.Error("PLZ,Open Your Location")))

                }
                else -> {
                    _createAccountStatus.emit(Event(Resource.Loading()))
                    val result = repository.createAccount(
                        username,
                        email,
                        mobile,
                        password,
                        Constants.IMAGE_URL,
                        latLong
                    )
                    _createAccountStatus.emit(Event(result))
                }
            }
        }


    }

    fun logout() {
        viewModelScope.launch(dispatcher) {
            _logoutStatus.emit(Event(Resource.Loading()))
            val result = repository.logout()
            _logoutStatus.emit(Event(result))
        }
    }

    fun verifyEmail(inputTextLayoutEmail: TextInputLayout) {
        viewModelScope.launch(dispatcher) {
            val email = inputTextLayoutEmail.editText!!.text.toString()
            when {
                email.isEmpty() -> {
                    _loginUserStatus.emit(Event(Resource.Error("E-mail is require")))
                    inputTextLayoutEmail.isHelperTextEnabled = true
                    inputTextLayoutEmail.helperText = "Require*"
                }

                !MyValidation.isValidEmail(email = email) -> {
                    _loginUserStatus.emit(Event(Resource.Error("E-mail is not valid")))
                    inputTextLayoutEmail.isHelperTextEnabled = true
                    inputTextLayoutEmail.helperText = "not valid"
                }
                else -> {
                    _verifyEmailStatus.emit(Event(Resource.Loading()))
                    val result = repository.verifyEmail(email)
                    _verifyEmailStatus.emit(Event(result))
                }
            }
        }

    }

    fun verifyCode(email: String, codeEmail: PinView) {
        viewModelScope.launch(dispatcher) {
            val code = codeEmail.text.toString()
            when {
                code.isEmpty() -> {
                    _verifyCodeStatus.emit(Event(Resource.Error("Code is require")))
                }

                code.length<4 -> {
                    _verifyCodeStatus.emit(Event(Resource.Error("Code content 4 numbers")))
                }
                else -> {
                    _verifyCodeStatus.emit(Event(Resource.Loading()))
                    val result = repository.verifyCode(email,code)
                    _verifyCodeStatus.emit(Event(result))
                }
            }
        }

    }

    fun resetPassword(inputTextLayoutPassword: TextInputLayout, email: String, code: String) {
           viewModelScope.launch {
               val password=inputTextLayoutPassword.editText?.text.toString()
               when{
                   password.isEmpty() -> {
                       _resetPasswordStatus.emit(Event(Resource.Error("Password is require")))
                       inputTextLayoutPassword.isHelperTextEnabled = true
                       inputTextLayoutPassword.helperText = "Require*"
                   }
                   !MyValidation.validatePass(context, inputTextLayoutPassword) -> {
                       _resetPasswordStatus.emit(Event(Resource.Error(inputTextLayoutPassword.helperText.toString())))
                   }
                   else ->{
                       _resetPasswordStatus.emit(Event(Resource.Loading()))
                       val result = repository.resetPassword(password,email,code)
                       _resetPasswordStatus.emit(Event(result))
                   }
               }

           }
    }

    fun showNoGpsDialog(context: Context) {
        buildAlertMessageNoGps(context);
    }
}