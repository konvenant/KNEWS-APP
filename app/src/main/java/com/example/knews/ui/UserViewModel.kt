package com.example.knews.ui

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.knews.NewsApplication
import com.example.knews.models.Article
import com.example.knews.models.Message
import com.example.knews.models.NewsResponse
import com.example.knews.models.NoticeCount
import com.example.knews.models.NotificationResponse
import com.example.knews.models.OnlineArticle
import com.example.knews.models.User
import com.example.knews.models.UserDetails
import com.example.knews.repository.UserRepository
import com.example.knews.util.Resource
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.Response
import java.io.File
import java.io.IOException
import kotlin.contracts.Returns

class UserViewModel(
    app: Application,
   val  userRepository: UserRepository
) : AndroidViewModel(app) {

    val userSavedArticle: MutableLiveData<Resource<List<OnlineArticle>>> = MutableLiveData()
    lateinit var savedArticleResponse : List<OnlineArticle>

    val verifyDetails: MutableLiveData<Resource<UserDetails>> = MutableLiveData()
    lateinit var verifyDetailsResponse : UserDetails

    val message: MutableLiveData<Resource<Message>> = MutableLiveData()
    var messageResponse : Message? = null

    val articleMessage: MutableLiveData<Resource<Message>> = MutableLiveData()
    var articleMessageResponse : Message? = null

    val addArticleMessage: MutableLiveData<Resource<Message>> = MutableLiveData()
    var addArticleMessageResponse : Message? = null

    val sentTokenMessage: MutableLiveData<Resource<Message>> = MutableLiveData()
    lateinit var sendTokenMessageResponse : Message

    val updateUserDetails: MutableLiveData<Resource<UserDetails>> = MutableLiveData()
    lateinit var updateUserResponse : UserDetails

    val userDetails: MutableLiveData<Resource<UserDetails>> = MutableLiveData()
    lateinit var userDetailsResponse : UserDetails

    val user: MutableLiveData<User> = MutableLiveData()

    val userNotification: MutableLiveData<Resource<NotificationResponse>> = MutableLiveData()
    lateinit var userNotificationResponse : NotificationResponse

    val userNotificationCount: MutableLiveData<Resource<NoticeCount>> = MutableLiveData()
    lateinit var userNotificationCountResponse : NoticeCount

    fun signUp(url:String,email:String,password:String) = viewModelScope.launch {
        safeSignUpCall(url, email, password)
    }

    fun login(url:String,email:String,password:String) = viewModelScope.launch {
        safeLoginCall(url, email, password)
    }

    fun verifyEmail(url:String,email:String,token:Int) = viewModelScope.launch {
        handleEmailTokenVerification(url,email,token)
    }

   fun updateUser(url:String,email:String,name:String,phone:String,city: String,country: String) =
       viewModelScope.launch {
           handleUpdateUser(url, email, name, phone, city, country)
       }

    fun verifyPassword(url:String,email:String,passwordToken:String) = viewModelScope.launch {
        handleVerifyPassword(url, email, passwordToken)
    }

    fun sendToken(url:String,email:String) = viewModelScope.launch {
        handleSendToken(url, email)
    }

    fun forgotPassword(email: String) = viewModelScope.launch {
        handleForgotPassword(email)
    }

    fun updatePassword(url:String,email:String,password: String) = viewModelScope.launch {
        handleUpdatePassword(url, email, password)
    }

    fun updateImage(url:String,imageFile: File) = viewModelScope.launch {
        handleUpdateImage(url,imageFile)
    }

    fun getUserDetails(email: String) = viewModelScope.launch {
        handleGetUserDetails(email)
    }

    fun getUserNotification(email: String) = viewModelScope.launch {
        handleGetUserNotification(email)
    }
    fun getUserNotificationCount(email: String) = viewModelScope.launch {
        handleGetUserNotificationCount(email)
    }

  fun getUserSavedArticle(email: String) = viewModelScope.launch {
      handleGetSavedArticle(email)
  }
    
    fun deleteSavedArticle(email: String,id: String) = viewModelScope.launch { 
        handleDeleteArticle(email, id)
    }
    
    fun saveArticle(apiUrl: String,email: String,author:String? = null,content:String? = null,
                    desc:String? = null,pub:String? = null,title:String? = null,
                    url: String? = null,urlToImage:String? = null,
                    sid: String? = null,name: String? = null
                    ,ids:String? = null) = viewModelScope.launch {
                        handleAddArticle(apiUrl, email, author, content, desc, pub, title, url,
                            urlToImage, sid, name, ids)
    }





    private suspend fun  safeSignUpCall(url:String, email:String, password:String) {
        message.postValue(Resource.Loading())
        try {
            if (hasInternetConnection()){
                val response = userRepository.signUp(url, email, password)
                message.postValue(handleSignUp(response))
            } else {
                message.postValue(Resource.Error("No internet connection"))
            }
        } catch (t:Throwable) {
            when (t) {
                is IOException -> message.postValue(Resource.Error("Network Failure"))
                else -> message.postValue(Resource.Error("Conversion Error"))
            }
        }
    }

    private fun handleSignUp(response: Response<Message>): Resource<Message>{
        if (response.isSuccessful){
            response.body()?.let { message ->
                messageResponse = message
                val messageError = Message("Error")
                return Resource.Success(messageResponse ?: messageError )
            }

        }
        val errorBody = response.errorBody()?.string()
        val jsonObject = JSONObject(errorBody)
        val error = jsonObject.getString("message")
        return Resource.Error(error)
    }



    private suspend fun  safeLoginCall(url:String, email:String, password:String) {
        userDetails.postValue(Resource.Loading())
        try {
            if (hasInternetConnection()){
                val response = userRepository.login(url, email, password)
                userDetails.postValue(handleLogin(response))
            } else {
                userDetails.postValue(Resource.Error("No internet connection"))
            }
        } catch (t:Throwable) {
            when (t) {
                is IOException -> userDetails.postValue(Resource.Error("Network Failure"))
                else -> userDetails.postValue(Resource.Error("Conversion Error"))
            }
        }
    }

    private fun handleLogin(response: Response<UserDetails>): Resource<UserDetails>{
        if (response.isSuccessful){
            response.body()?.let { response ->
                 userDetailsResponse = response
                return Resource.Success(userDetailsResponse)
            }
        }

        val errorBody = response.errorBody()?.string()
        val jsonObject = JSONObject(errorBody)
        val error = jsonObject.getString("message")
        return Resource.Error(error)
    }


    private suspend fun handleEmailTokenVerification(url:String, email:String, token:Int) {
        userDetails.postValue(Resource.Loading())
        try {
            val response = userRepository.verifyEmail(url, email, token)
            if (response.isSuccessful){
                response.body()?.let {
                    userDetailsResponse = it
                    userDetails.postValue(Resource.Success(userDetailsResponse))
                }
            } else{
                val errorBody = response.errorBody()?.string()
                val jsonObject = JSONObject(errorBody)
                val error = jsonObject.getString("message")
                userDetails.postValue(Resource.Error(error))
            }
        } catch (e: Exception){
            userDetails.postValue(Resource.Error(e.message.toString()))
        }
    }

    private suspend fun handleUpdateUser(url:String,email:String,name:String,phone:String,city: String,country: String) {
        userDetails.postValue(Resource.Loading())
        try {
            val response = userRepository.updateUser(url, email, name, phone, city, country)
            if (response.isSuccessful){
                response.body()?.let {
                    userDetailsResponse = it
                    userDetails.postValue(Resource.Success(userDetailsResponse))
                }
            } else{
                val errorBody = response.errorBody()?.string()
                val jsonObject = JSONObject(errorBody)
                val error = jsonObject.getString("message")
                userDetails.postValue(Resource.Error(error))
            }
        } catch (e: Exception){
            userDetails.postValue(Resource.Error(e.message.toString()))
        }
    }

    private suspend fun handleVerifyPassword(url:String,email:String,passwordToken:String) {
        userDetails.postValue(Resource.Loading())
        try {
            val response = userRepository.verifyPassword(url, email, passwordToken)
            if (response.isSuccessful){
                response.body()?.let {
                    userDetailsResponse = it
                    userDetails.postValue(Resource.Success(userDetailsResponse))
                }
            } else{
                val errorBody = response.errorBody()?.string()
                val jsonObject = JSONObject(errorBody)
                val error = jsonObject.getString("message")
                userDetails.postValue(Resource.Error(error))
            }
        } catch (e: Exception){
            userDetails.postValue(Resource.Error(e.message.toString()))
        }
    }

    private suspend fun handleSendToken(url:String,email:String) {
        sentTokenMessage.postValue(Resource.Loading())
        try {
            val response = userRepository.sendToken(url, email)
            if (response.isSuccessful){
                response.body()?.let {
                    sendTokenMessageResponse = it
                    sentTokenMessage.postValue(Resource.Success(sendTokenMessageResponse))
                }
            } else{
                val errorBody = response.errorBody()?.string()
                val jsonObject = JSONObject(errorBody)
                val error = jsonObject.getString("message")
                sentTokenMessage.postValue(Resource.Error(error))
            }
        } catch (e: Exception){
            sentTokenMessage.postValue(Resource.Error(e.message.toString()))
        }
    }

    private suspend fun handleForgotPassword(email: String) {
        userDetails.postValue(Resource.Loading())
        try {
            val response = userRepository.forgotPassword(email)
            if (response.isSuccessful){
                response.body()?.let {
                    userDetailsResponse = it
                    userDetails.postValue(Resource.Success(userDetailsResponse))
                }
            } else{
                val errorBody = response.errorBody()?.string()
                val jsonObject = JSONObject(errorBody)
                val error = jsonObject.getString("message")
                userDetails.postValue(Resource.Error(error))
            }
        } catch (e: Exception){
            userDetails.postValue(Resource.Error(e.message.toString()))
        }
    }

    private suspend fun handleUpdatePassword(url:String,email:String,password: String) {
        userDetails.postValue(Resource.Loading())
        try {
            val response = userRepository.updatePassword(url, email, password)
            if (response.isSuccessful){
                response.body()?.let {
                    userDetailsResponse = it
                    userDetails.postValue(Resource.Success(userDetailsResponse))
                }
            } else{
                val errorBody = response.errorBody()?.string()
                val jsonObject = JSONObject(errorBody)
                val error = jsonObject.getString("message")
                userDetails.postValue(Resource.Error(error))
            }
        } catch (e: Exception){
            userDetails.postValue(Resource.Error(e.message.toString()))
        }
    }

    private suspend fun handleUpdateImage(url: String,imageFile: File) {
        updateUserDetails.postValue(Resource.Loading())
        try {
            val requestFile = RequestBody.create("image/*".toMediaTypeOrNull(),imageFile)
            val imagePart = MultipartBody.Part.createFormData("image",imageFile.name,requestFile)
            val response = userRepository.updateImage(url,imagePart)
            if (response.isSuccessful){
                response.body()?.let {
                    updateUserResponse = it
                    updateUserDetails.postValue(Resource.Success(updateUserResponse))
                }
            } else{
                val errorBody = response.errorBody()?.string()
                val jsonObject = JSONObject(errorBody)
                val error = jsonObject.getString("message")
                updateUserDetails.postValue(Resource.Error(error))
            }
        } catch (e: Exception){
            updateUserDetails.postValue(Resource.Error(e.message.toString()))
        }
    }

    private suspend fun handleGetUserDetails(email: String) {
        userDetails.postValue(Resource.Loading())
        try {
            val response = userRepository.getUserDetails(email)
            if (response.isSuccessful){
                response.body()?.let {
                    userDetailsResponse = it
                    userDetails.postValue(Resource.Success(userDetailsResponse))
                }
            } else{
                val errorBody = response.errorBody()?.string()
                val jsonObject = JSONObject(errorBody)
                val error = jsonObject.getString("message")
                userDetails.postValue(Resource.Error(error))
            }
        } catch (e: Exception){
            userDetails.postValue(Resource.Error(e.message.toString()))
        }
    }

    private suspend fun handleGetUserNotification(email: String) {
        userNotification.postValue(Resource.Loading())
        try {
            val response = userRepository.getUserNotification(email)
            if (response.isSuccessful){
                response.body()?.let {
                    userNotificationResponse = it
                    userNotification.postValue(Resource.Success(userNotificationResponse))
                }
            } else{
                val errorBody = response.errorBody()?.string()
                val jsonObject = JSONObject(errorBody)
                val error = jsonObject.getString("message")
                userNotification.postValue(Resource.Error(error))
            }
        } catch (e: Exception){
            userNotification.postValue(Resource.Error(e.message.toString()))
        }
    }

    private suspend fun handleGetUserNotificationCount(email: String) {
        userNotificationCount.postValue(Resource.Loading())
        try {
            val response = userRepository.getUserNotificationCount(email)
            if (response.isSuccessful){
                response.body()?.let {
                    userNotificationCountResponse = it
                    userNotificationCount.postValue(Resource.Success(userNotificationCountResponse))
                }
            } else{
                val errorBody = response.errorBody()?.string()
                val jsonObject = JSONObject(errorBody!!)
                val error = jsonObject.getString("message")
                userNotificationCount.postValue(Resource.Error(error))
            }
        } catch (e: Exception){
            userNotificationCount.postValue(Resource.Error(e.message.toString()))
        }
    }

    
    private suspend fun handleGetSavedArticle(email: String){
        userSavedArticle.postValue(Resource.Loading())
        try {
            val response = userRepository.getSavedArticles(email)
            if (response.isSuccessful){
                response.body()?.let { 
                    savedArticleResponse = it
                    userSavedArticle.postValue(Resource.Success(savedArticleResponse))
                }
            } else{
                val errorBody = response.errorBody()?.string()
                val jsonObject = JSONObject(errorBody)
                val error = jsonObject.getString("message")
                userSavedArticle.postValue(Resource.Error(error))
            }
        } catch (e:Exception){
            userSavedArticle.postValue(Resource.Error(e.message.toString()))
        }
    }



    private suspend fun handleDeleteArticle(email:String,id:String) {
        articleMessage.postValue(Resource.Loading())
        try {
            val response = userRepository.deleteSavedArticle(email, id)
            if (response.isSuccessful){
                response.body()?.let {
                    articleMessageResponse = it
                    articleMessage.postValue(Resource.Success(articleMessageResponse!!))
                }
            } else{
                val errorBody = response.errorBody()?.string()
                val jsonObject = JSONObject(errorBody)
                val error = jsonObject.getString("message")
                articleMessage.postValue(Resource.Error(error))
            }
        } catch (e: Exception){
            articleMessage.postValue(Resource.Error(e.message.toString()))
        }
    }

    private suspend fun handleAddArticle(
        apiUrl: String,email: String,author:String? = null,content:String? = null,
        desc:String?,pub:String? = null,title:String? = null,url: String? = null,urlToImage:String? = null,
        sid: String? = null,name: String? = null,ids:String? = null
    ) {
        addArticleMessage.postValue(Resource.Loading())
        try {
            val response = userRepository.saveUserArticle(apiUrl, email, author, content, desc, pub, title, url, urlToImage, sid, name, ids)

            if (response.isSuccessful){
                response.body()?.let {
                    addArticleMessageResponse = it
                    addArticleMessage.postValue(Resource.Success(addArticleMessageResponse!!))
                }
            } else{
                val errorBody = response.errorBody()?.string()
                val jsonObject = JSONObject(errorBody!!)
                val error = jsonObject.getString("message")
                addArticleMessage.postValue(Resource.Error(error))
            }
        } catch (e: Exception){
            addArticleMessage.postValue(Resource.Error(e.message.toString()))
        }
    }
    
    

    private fun hasInternetConnection(): Boolean {
        val connectivityManager = getApplication<NewsApplication>().getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val activeNetwork = connectivityManager.activeNetwork ?: return false
            val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
            return when {
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else{
            connectivityManager.activeNetworkInfo?.run {
                return when(type) {
                    ConnectivityManager.TYPE_WIFI -> true
                    ConnectivityManager.TYPE_MOBILE -> true
                    ConnectivityManager.TYPE_ETHERNET -> true
                    else -> false
                }
            }
        }
        return false
    }
}