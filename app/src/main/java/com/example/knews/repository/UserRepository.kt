package com.example.knews.repository

import com.example.knews.api.UserRetrofitInstance
import okhttp3.MultipartBody

class UserRepository {
    suspend fun signUp(url:String,email:String,password:String) =
        UserRetrofitInstance.api.signUp(url, email, password)

    suspend fun login(url:String,email:String,password:String) =
        UserRetrofitInstance.api.login(url, email, password)

    suspend fun updateUser(url:String,email:String,name:String,phone:String,city: String,country: String) =
        UserRetrofitInstance.api.updateUser(url, email, name, phone, city, country)

    suspend fun verifyEmail(url:String,email:String,token:Int) =
        UserRetrofitInstance.api.verifyEmail(url, email, token)

    suspend fun verifyPassword(url:String,email:String,passwordToken:String) =
        UserRetrofitInstance.api.verifyPassword(url, email, passwordToken)

    suspend fun sendToken(url:String,email:String) =
        UserRetrofitInstance.api.sendToken(url, email)

    suspend fun forgotPassword(email: String) =
        UserRetrofitInstance.api.forgotPassword(email)

    suspend fun updatePassword(url:String,email:String,password: String) =
        UserRetrofitInstance.api.updatePassword(url, email, password)

    suspend fun updateImage(url:String,image: MultipartBody.Part) =
        UserRetrofitInstance.api.updateImage(url,image)

    suspend fun getUserNotification(email: String) =
        UserRetrofitInstance.api.getUserNotifications(email)

    suspend fun getUserNotificationCount(email: String) =
        UserRetrofitInstance.api.getUserNotificationCount(email)

    suspend fun getUserDetails(email: String) =
        UserRetrofitInstance.api.getUserDetails(email)

    suspend fun getSavedArticles(email: String) =
        UserRetrofitInstance.api.getUserSavedArticle(email)

    suspend fun saveUserArticle(apiUrl: String,email: String,author:String? = null,content:String? = null,
                                desc:String? = null,pub:String? = null,
                                title:String? = null,url: String? = null,
                                urlToImage:String?  = null,
                                sid: String? = null,name: String? = null,ids:String? = null
    ) = UserRetrofitInstance.api.saveUserArticle(apiUrl,email,author,content,desc,pub,title,
        url, urlToImage, sid, name, ids)

    suspend fun deleteSavedArticle(email: String,id: String) =
        UserRetrofitInstance.api.deleteSavedArticle(email, id)

}
