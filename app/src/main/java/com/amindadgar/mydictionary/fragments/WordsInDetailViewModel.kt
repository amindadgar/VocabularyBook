package com.amindadgar.mydictionary.fragments

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.amindadgar.mydictionary.Repository.WordsRepository
import com.amindadgar.mydictionary.Utils.Database.WordRoomDatabase
import com.amindadgar.mydictionary.model.RoomDatabaseModel.Definition
import com.amindadgar.mydictionary.model.RoomDatabaseModel.Phonetics
import kotlinx.coroutines.runBlocking

class WordsInDetailViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: WordsRepository

    init {
        val wordDao = WordRoomDatabase.getInstance(application,viewModelScope).WordsDao()
        repository = WordsRepository(wordDao)
    }


    fun getSynonym(id: Int):LiveData<List<String>>{
        return runBlocking(viewModelScope.coroutineContext){
            repository.loadSynonym(id)
        }
    }
    fun getDefinitionExamples(id: Int):LiveData<List<Definition>>{
        return runBlocking(viewModelScope.coroutineContext){
            repository.loadDefinitionExamples(id)
        }
    }
    fun getPhonetics(id: Int): LiveData<List<Phonetics>>{
        return runBlocking(viewModelScope.coroutineContext){
            repository.loadPhonetics(id)
        }
    }
}