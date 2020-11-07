package com.amindadgar.mydictionary.model.DictionaryApi

import com.google.gson.annotations.SerializedName

data class Defenition(
    @SerializedName("definition")
    val definition:String,
    @SerializedName("example")
    val example:String
    ,@SerializedName("synonyms")
    val synonyms:ArrayList<String>?=null)