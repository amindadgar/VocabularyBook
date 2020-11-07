package com.amindadgar.mydictionary.activities

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.amindadgar.mydictionary.Repository.WordsRepository
import com.amindadgar.mydictionary.Utils.Database.WordRoomDatabase
import com.amindadgar.mydictionary.Utils.WordsApi.DictionaryRetrofitBuilder
import com.amindadgar.mydictionary.model.DictionaryApi.DictionaryData
import com.amindadgar.mydictionary.model.RoomDatabaseModel.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class WordsViewModel(application: Application):AndroidViewModel(application) {
    private val repository:WordsRepository
    val allWords:LiveData<List<WordDefinitionTuple>>
    val context:Context = application.applicationContext

    init {
        val wordDao = WordRoomDatabase.getInstance(application,viewModelScope).WordsDao()
        repository = WordsRepository(wordDao)
        allWords = repository.wordDefinition
    }

    fun insert(words: Words, definition: Definition, phonetics: Phonetics, Synonym: Synonym) = viewModelScope.launch {
        repository.insertData(words, definition, phonetics,Synonym)
    }

    fun insertDefinition(definition: Definition) = viewModelScope.launch {
        repository.insertDefinition(definition)
    }
    fun insertSynonym(synonym: Synonym) = viewModelScope.launch {
        repository.insertSynonym(synonym)
    }
    fun insertPhonetics(phonetics: Phonetics) = viewModelScope.launch {
        repository.insertPhonetics(phonetics)
    }
    fun insertWords(words: Words) = viewModelScope.launch {
        repository.insertWords(words)
    }

    fun deleteAll(){
        // if all data was removed, reset numbering
        val editor = context.getSharedPreferences("sharedPrefs",Context.MODE_PRIVATE).edit()
        editor.remove("IdNum").apply()

        viewModelScope.launch {
            repository.deleteData()
        }
    }
    fun getWord(word:String,id:Int):Int{
        var returnValue = false
        val request:Response<ArrayList<DictionaryData>> = DictionaryRetrofitBuilder.apiService.GetDefenitions(word).execute()
        if (request.body()!= null){
            Log.d("Dictionary", request.body().toString())
            Log.d("Dictionary is SuccessFull", request.isSuccessful.toString())

            insertDatas(request.body()!!,id)
        }
        return request.code()

    }
    private fun insertDatas(data :ArrayList<DictionaryData>,id:Int){
        this.insertWords(Words(id,data[0].word))

        for (meanings in data[0].meanings){
            for (definition in meanings.defenitions){
                Log.d("Dictionary id",id.toString())

                // because api is having changing fields we should consider checking values that is null or not null !
                if (!definition.definition.isNullOrEmpty()) {
                    if (definition.example.isNullOrEmpty()){
                        // if we don't have sample sentence we will insert empty string
                        Log.d("Dictionary sample Sentence","no sentence")
                        this.insertDefinition(
                            Definition(
                                definition = definition.definition,
                                sampleSentence = "",
                                word_id = id
                            )
                        )
                    }else {
                        this.insertDefinition(
                            Definition(
                                definition = definition.definition,
                                sampleSentence = definition.example,
                                word_id = id
                            )
                        )
                    }
                    Log.d("Dictionary definition",definition.definition)
                }
                if (!definition.synonyms.isNullOrEmpty()) {
                    for (synonyms in definition.synonyms) {
                        Log.d("Dictionary synonym",synonyms)
                        this.insertSynonym(Synonym(id,synonyms))
                    }
                }
            }
        }
        for (phonetics in data[0].phonetics){
            Log.d("Dictionary phonetics",phonetics.phonetic)
            this.insertPhonetics(Phonetics(
                phonetics.phonetic,phonetics.audio,id
            ))
        }
    }

}