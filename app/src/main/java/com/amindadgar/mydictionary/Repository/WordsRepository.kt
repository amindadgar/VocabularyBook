package com.amindadgar.mydictionary.Repository

import androidx.lifecycle.LiveData
import com.amindadgar.mydictionary.model.RoomDatabaseModel.*
import com.amindadgar.mydictionary.model.RoomDatabaseModel.DAO.WordsDao

class WordsRepository(private val wordDao:WordsDao) {
    val wordDefinition:LiveData<List<WordDefinitionTuple>> = wordDao.loadWordsDefinition()

    suspend fun insertData(words: Words, definition: Definition, phonetics: Phonetics, Synonym: Synonym){
        wordDao.insertAllData(words, definition, phonetics,Synonym)
    }

    suspend fun insertDefinition(definition: Definition){
        wordDao.insertDefinition(definition)
    }

    suspend fun insertPhonetics(phonetics: Phonetics){
        wordDao.insertPhonetics(phonetics)
    }
    suspend fun insertSynonym(synonym: Synonym){
        wordDao.insertSynonyms(synonym)
    }

    suspend fun insertWords(words: Words){
        wordDao.insertWord(words)
    }

    suspend fun deleteData() {
        wordDao.deleteAll()
    }

    suspend fun loadSynonym(id: Int):LiveData<List<String>>{
        return wordDao.loadSynonym(id)
    }
    suspend fun loadPhonetics(id: Int):LiveData<List<Phonetics>>{
        return wordDao.loadPhonetics(id)
    }
    suspend fun loadDefinitionExamples(id: Int):LiveData<List<Definition>>{
        return wordDao.loadDefinitionExamples(id)
    }


    suspend fun deleteWord(word: Words) = wordDao.deleteWord(word)
}