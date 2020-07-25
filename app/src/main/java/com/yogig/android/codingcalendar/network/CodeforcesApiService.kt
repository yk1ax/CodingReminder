package com.yogig.android.codingcalendar.network

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET


private const val CODEFORCES_BASE_URL = "https://codeforces.com/api/"

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(GsonConverterFactory.create())
    .baseUrl(CODEFORCES_BASE_URL)
    .build()

interface CodeforcesApiService {

    @GET(value = "contest.list")
    suspend fun getContests(): CodeforcesContestList
}

object CodeforcesApi {
    val retrofitService: CodeforcesApiService by lazy { retrofit.create(
        CodeforcesApiService::class.java) }
}