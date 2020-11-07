package com.amindadgar.mydictionary.Utils.WordsApi

import com.amindadgar.mydictionary.model.DictionaryApi.DictionaryData
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path

interface DictionaryApiService {
    @GET("entries/en/{word}")
    @Headers("User-Agent:dictionaryApp")
    fun GetDefenitions(@Path("word") word:String): Call<ArrayList<DictionaryData>>
}