package com.amindadgar.mydictionary.model.RoomDatabaseModel

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(tableName = "Synonym",
    foreignKeys = [
        ForeignKey(entity = Words::class,
            parentColumns = ["id"],
            childColumns = ["word_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    primaryKeys = ["word_id","synonym"]
)
data class Synonym(
    @ColumnInfo(name = "word_id")
    val word_id:Int,

    @ColumnInfo(name = "synonym")
    val synonym: String)