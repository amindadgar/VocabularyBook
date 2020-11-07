package com.amindadgar.mydictionary.model.DictionaryApi

import com.google.gson.annotations.SerializedName


data class DictionaryData(
    @SerializedName("word")
    val word:String,

    @SerializedName("meanings")
    val meanings: ArrayList<Meanings>,

    @SerializedName("phonetics")
    val phonetics: ArrayList<Phonetics>
)