package com.amindadgar.mydictionary.model.RoomDatabaseModel

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Words")

data class Words(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id:Int,

    @ColumnInfo(name = "word")
    val word:String

)