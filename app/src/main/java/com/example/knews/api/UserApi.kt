package com.example.knews.api

import com.example.knews.models.Article
import com.example.knews.models.Message
import com.example.knews.models.NoticeCount
import com.example.knews.models.NotificationResponse
import com.example.knews.models.OnlineArticle
import com.example.knews.models.UserDetails
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Url

interface UserApi {
    @FormUrlEncoded
    @POST
    suspend fun signUp(
        @Url url: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): Response<Message>

    @FormUrlEncoded
    @POST
    suspend fun login(
        @Url url: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): Response<UserDetails>

    @FormUrlEncoded
    @PUT
    suspend fun updateUser(
        @Url url: String,
        @Field("email") email: String,
        @Field("name") name: String,
        @Field("phone") phone: String,
        @Field("city") city: String,
        @Field("country") country: String
    ): Response<UserDetails>

    @FormUrlEncoded
    @POST
    suspend fun verifyEmail(
        @Url url: String,
        @Field("email") email: String,
        @Field("token") token: Int
    ): Response<UserDetails>

    @FormUrlEncoded
    @POST
    suspend fun verifyPassword(
        @Url url: String,
        @Field("email") email: String,
        @Field("passwordToken") passwordToken: String
    ): Response<UserDetails>

    @FormUrlEncoded
    @POST
    suspend fun sendToken(
        @Url url: String,
        @Field("email") email: String
    ): Response<Message>


    @POST("auth/forgotPassword/{email}")
    suspend fun forgotPassword(@Path("email")email: String): Response<UserDetails>


    @FormUrlEncoded
    @PUT
    suspend fun updatePassword(
        @Url url: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): Response<UserDetails>


    @Multipart
    @PUT
    suspend fun updateImage(
        @Url url: String,
        @Part image:MultipartBody.Part
    ): Response<UserDetails>



    @GET("user/notification/{email}")
    suspend fun getUserNotifications(@Path("email")email: String): Response<NotificationResponse>

    @GET("user/notificationCount/{email}")
    suspend fun getUserNotificationCount(@Path("email")email: String): Response<NoticeCount>

    @GET("user/details/{email}")
    suspend fun getUserDetails(@Path("email")email: String): Response<UserDetails>


    @GET("article/list/{email}")
    suspend fun getUserSavedArticle(@Path("email")email: String): Response<List<OnlineArticle>>

    @FormUrlEncoded
    @POST
    suspend fun saveUserArticle(
        @Url apiUrl: String,
        @Field("email") email: String,
        @Field("author")author: String? = null,
        @Field("content")content: String? = null,
        @Field("description")desc: String? = null,
        @Field("publishedAt")publishedAt: String? = null,
        @Field("title")title: String? = null,
        @Field("url")url: String? = null,
        @Field("urlToImage")urlToImage: String? = null,
        @Field("sid")sid: String? = null,
        @Field("name")name: String? = null,
        @Field("id")ids: String? = null
    ) : Response<Message>

    @DELETE("article/delete/{email}/{id}")
    suspend fun  deleteSavedArticle(
        @Path("email") email: String,
        @Path("id") id:String
    ) : Response<Message>

}