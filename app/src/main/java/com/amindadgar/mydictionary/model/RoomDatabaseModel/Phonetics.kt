package com.amindadgar.mydictionary.model.RoomDatabaseModel

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    tableName = "Phonetics",
    primaryKeys = ["word_id","text"],
    foreignKeys = [
        ForeignKey(entity = Words::class,
            parentColumns = ["id"],
            childColumns = ["word_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Phonetics(
    @ColumnInfo(name = "text")
    val text:String,

    @ColumnInfo(name = "audio")
    val audio:String,

    @ColumnInfo(name = "word_id")
    val word_id:Int

)