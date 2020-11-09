package com.fareez.opentriviaapp.network

import com.fareez.opentriviaapp.model.QuestionCountModel
import com.fareez.opentriviaapp.model.QuestionModel
import com.fareez.opentriviaapp.model.TokenModel
import io.reactivex.Observable
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiServices {

    // GET questions based on category
    @GET("/api.php")
    fun getQuestions(
        @Query("category") category: Int,
        @Query("amount") amount: Int,
        @Query("token") token: String): Observable<QuestionModel>

    // GET new API Token
    @GET("/api_token.php")
    fun resetToken(
            @Query("command") command: String): Observable<TokenModel>

    // GET questions count
    @GET("/api_count_global.php")
    fun getQuestionCount(): Observable<QuestionCountModel>

    companion object{
        fun getServices(): ApiServices{

            val logger = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
            val okHttp = OkHttpClient.Builder().addInterceptor(logger)

            val retrofit = Retrofit.Builder()
                .baseUrl("https://opentdb.com")
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(MoshiConverterFactory.create())
                .client(okHttp.build())
                .build()
            return retrofit.create(ApiServices::class.java)
        }
    }

}