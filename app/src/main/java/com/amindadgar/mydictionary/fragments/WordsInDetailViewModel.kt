package com.amindadgar.mydictionary.fragments

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.amindadgar.mydictionary.Repository.WordsRepository
import com.amindadgar.mydictionary.Utils.Database.WordRoomDatabase
import com.amindadgar.mydictionary.model.RoomDatabaseModel.AllData
import kotlinx.coroutines.runBlocking

class WordsInDetailViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: WordsRepository

    init {
        val wordDao = WordRoomDatabase.getInstance(application,viewModelScope).WordsDao()
        repository = WordsRepository(wordDao)
    }

    fun getAllData(id: Int): LiveData<List<AllData>> {
        return runBlocking(viewModelScope.coroutineContext) {
            repository.getAllData(id)
        }
    }
}