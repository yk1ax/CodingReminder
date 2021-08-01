package com.yogesh.android.codingReminder.network

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.concurrent.TimeUnit


private const val CLIST_BASE_URL = "https://clist.by/api/v1/"

private val client = OkHttpClient.Builder()
    .connectTimeout(15, TimeUnit.SECONDS)
    .readTimeout(5, TimeUnit.SECONDS)
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(GsonConverterFactory.create())
    .baseUrl(CLIST_BASE_URL)
    .client(client)
    .build()

interface ClistApiService {

    @GET(value = "contest")
    suspend fun getContests(@Query("end__gt") end__gt:String,
                            @Query("order_by") order_by:String,
                            @Query("username") username:String,
                            @Query("api_key") api_key:String): ClistContestList
}

object ClistApi {
    val retrofitService: ClistApiService by lazy { retrofit.create(
        ClistApiService::class.java) }
}