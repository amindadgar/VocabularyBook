package com.amindadgar.mydictionary.model.RoomDatabaseModel

import androidx.room.ColumnInfo

data class WordDefinitionTuple(
    @ColumnInfo(name = "word") val words:String,
    @ColumnInfo(name = "definitions") val definitions:String
)