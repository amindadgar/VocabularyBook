package com.amindadgar.mydictionary.model.RoomDatabaseModel

import android.R.color
import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo


data class WordDefinitionTuple(
    @ColumnInfo(name = "id") val id: Int,
    @ColumnInfo(name = "word") val words: String,
    @ColumnInfo(name = "definitions") val definitions: String
):Parcelable{
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readString()!!
    ) {

    }
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(words)
        parcel.writeString(definitions)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<WordDefinitionTuple> {
        override fun createFromParcel(parcel: Parcel): WordDefinitionTuple {
            return WordDefinitionTuple(parcel)
        }

        override fun newArray(size: Int): Array<WordDefinitionTuple?> {
            return arrayOfNulls(size)
        }
    }
}