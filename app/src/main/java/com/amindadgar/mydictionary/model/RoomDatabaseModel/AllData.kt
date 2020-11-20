package com.amindadgar.mydictionary.model.RoomDatabaseModel

import androidx.room.ColumnInfo
import androidx.room.DatabaseView


@DatabaseView("SELECT id,word,definitions,example,synonym,audio,text FROM Words JOIN Definition JOIN Synonym JOIN Phonetics WHERE Words.id = Definition.word_id AND Synonym.word_id = Words.id AND Phonetics.word_id = Words.id")
data class AllData(
    @ColumnInfo(name = "id") val id:Int,
    @ColumnInfo(name = "word") val word:String,
    @ColumnInfo(name = "definitions") val definition:String,
    @ColumnInfo(name = "text") val phoneticText:String,
    @ColumnInfo(name = "audio") val phoneticAudio:String,
    @ColumnInfo(name = "synonym") val synonym:String,
    @ColumnInfo(name = "example") val example_sentence:String

)