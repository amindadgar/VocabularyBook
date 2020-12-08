package com.amindadgar.mydictionary.model.RoomDatabaseModel.DAO

import androidx.lifecycle.LiveData
import androidx.room.*
import com.amindadgar.mydictionary.model.RoomDatabaseModel.*

@Dao
interface WordsDao {
    @Query("SELECT id,word, definitions FROM Words JOIN Definition ON (Words.id = Definition.word_id)")
    fun loadWordsDefinition():LiveData<List<WordDefinitionTuple>>

    @Query("SELECT synonym from Synonym where word_id = :id")
    fun loadSynonym(id: Int):LiveData<List<String>>

    @Query("SELECT * from Definition where word_id = :id")
    fun loadDefinitionExamples(id: Int):LiveData<List<Definition>>

    @Query("SELECT * from phonetics where word_id = :id")
    fun loadPhonetics(id: Int):LiveData<List<Phonetics>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAllData(words: Words, definition: Definition, phonetics: Phonetics, Synonym: Synonym)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertSynonyms(synonym: Synonym)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertDefinition(definition: Definition)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPhonetics(phonetics: Phonetics)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertWord(words: Words)

    @Delete
    suspend fun deleteWord(vararg words: Words)

    @Query("DELETE FROM words")
    suspend fun deleteAll()

}