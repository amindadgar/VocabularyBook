package com.amindadgar.mydictionary.model.DictionaryApi

import com.google.gson.annotations.SerializedName


data class Meanings(
    @SerializedName("partOfSpeech")
    val partOfSpeech:String,
    @SerializedName("definitions")
    val defenitions: ArrayList<Defenition> )