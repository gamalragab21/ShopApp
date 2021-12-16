package com.developers.shopapp.repositories

import com.developers.shopapp.data.newtwork.ApiShopService
import com.developers.shopapp.entities.AuthModel
import com.developers.shopapp.entities.MyResponse
import com.developers.shopapp.entities.User
import com.developers.shopapp.helpers.Resource
import com.developers.shopapp.helpers.safeCall
import com.developers.shopapp.qualifiers.IOThread
import com.developers.shopapp.utils.Constants
import com.developers.shopapp.utils.Utils.getTimeStamp
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.collections.HashMap

class AuthenticationRepository @Inject constructor(
    private val apiShopService: ApiShopService,
    @IOThread
    private val dispatcher: CoroutineDispatcher
) {

    suspend fun loginUser(email: String, password: String): Resource<MyResponse<String>> =
        withContext(dispatcher) {
            safeCall {
                val hasmap = HashMap<String, String>()
                hasmap["email"] = email
                hasmap["password"] = password
                val result = apiShopService.loginUser(hasmap)
                Resource.Success(result)
            }
        }


    suspend fun createAccount(
        username: String,
        email: String,
        mobile: String,
        password: String,
        imageUrl: String,
        latLong: LatLng
    ): Resource<MyResponse<String>> = withContext(dispatcher) {
        safeCall {

            val user = User(
                username = username,
                email = email,
                mobile = mobile,
                password = password,
                image = imageUrl,
                createAt = getTimeStamp(),
                latitude = latLong.latitude,
                longitude = latLong.longitude
            )

            val result = apiShopService.register(user)
            Resource.Success(result)
        }
    }

    suspend fun logout(): Resource<AuthModel> = withContext(dispatcher) {
        safeCall {
            val result = apiShopService.logout()
            Resource.Success(result)
        }
    }

    suspend fun verifyEmail(email: String): Resource<AuthModel> = withContext(dispatcher) {
        safeCall {
            val hashMap = HashMap<String, String>()
            hashMap["email"] = email
            val result = apiShopService.verifyEmail(hashMap)
            Resource.Success(result)
        }
    }

    suspend fun verifyCode(email: String, code: String): Resource<AuthModel> =
        withContext(dispatcher) {
            safeCall {
                val hashMap = HashMap<String, String>()
                hashMap["email"] = email
                hashMap["code"] = code
                val result = apiShopService.verifyCode(hashMap)
                Resource.Success(result)
            }
        }

    suspend fun resetPassword(password: String, email: String, code: String): Resource<AuthModel> =
        withContext(dispatcher) {
            safeCall {
                val hashMap = HashMap<String, String>()
                hashMap["email"] = email
                hashMap["code"] = code
                hashMap["password"] = password

                val result = apiShopService.resetPassword(hashMap)
                Resource.Success(result)
            }
        }

    suspend fun findUserById(userId: Int): Resource<User> = withContext(dispatcher) {
        safeCall {
            val result = apiShopService.findUserById(userId)
            Resource.Success(result)
        }
    }
}