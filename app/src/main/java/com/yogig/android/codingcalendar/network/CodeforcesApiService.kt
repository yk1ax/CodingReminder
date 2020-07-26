package com.yogig.android.codingcalendar.network

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import java.io.IOException
import java.util.concurrent.TimeUnit


private const val CODEFORCES_BASE_URL = "https://codeforces.com/api/"

private val client = OkHttpClient.Builder()
    .connectTimeout(15, TimeUnit.SECONDS)
    .readTimeout(5, TimeUnit.SECONDS)
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(GsonConverterFactory.create())
    .baseUrl(CODEFORCES_BASE_URL)
    .client(client)
    .build()

interface CodeforcesApiService {

    @Throws(IOException::class)
    @GET(value = "contest.list")
    suspend fun getContests(): CodeforcesContestList
}

object CodeforcesApi {
    val retrofitService: CodeforcesApiService by lazy { retrofit.create(
        CodeforcesApiService::class.java) }
}