package com.amindadgar.mydictionary.Utils.WordsApi

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object DictionaryRetrofitBuilder {
    private const val BASE_URL = "https://api.dictionaryapi.dev/api/v2/"

    private fun getRetrofit():Retrofit{

        val okHttpClient = OkHttpClient.Builder()
            .build()
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
    }


    val apiService: DictionaryApiService = getRetrofit()
        .create(DictionaryApiService::class.java)
}
