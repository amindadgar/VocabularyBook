package com.amindadgar.mydictionary.model.RoomDatabaseModel

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    tableName = "Definition",
    primaryKeys = ["word_id","definitions"]
    ,foreignKeys = [
        ForeignKey(entity = Words::class,
            parentColumns = ["id"],
            childColumns = ["word_id"],
            onDelete = ForeignKey.CASCADE)
    ]
)

data class Definition (

    @ColumnInfo(name = "definitions")
    val definition: String,

    @ColumnInfo(name = "example")
    val sampleSentence:String,

    @ColumnInfo(name = "word_id")
    val word_id:Int
)