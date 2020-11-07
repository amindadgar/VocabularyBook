package com.amindadgar.mydictionary.model.DictionaryApi

import com.google.gson.annotations.SerializedName

data class Phonetics(@SerializedName("text")
                val phonetic:String,
                @SerializedName("audio")
                val audio:String)