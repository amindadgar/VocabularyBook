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
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import retrofit2.Response

class WordsViewModel(application: Application):AndroidViewModel(application) {
    private val repository:WordsRepository
    val allWords:LiveData<List<WordDefinitionTuple>>
    val context:Context = application.applicationContext
    private val TAG = "WordsViewModel"

    init {
        val wordDao = WordRoomDatabase.getInstance(application,viewModelScope).WordsDao()
        repository = WordsRepository(wordDao)
        allWords = repository.wordDefinition
    }

    fun insert(words: Words, definition: Definition, phonetics: Phonetics, Synonym: Synonym) = viewModelScope.launch {
        repository.insertData(words, definition, phonetics,Synonym)
    }

    private fun insertDefinition(definition: Definition) = viewModelScope.launch {
        repository.insertDefinition(definition)
    }
    private fun insertSynonym(synonym: Synonym) = viewModelScope.launch {
        repository.insertSynonym(synonym)
    }
    private fun insertPhonetics(phonetics: Phonetics) = viewModelScope.launch {
        repository.insertPhonetics(phonetics)
    }
    private fun insertWords(words: Words) = viewModelScope.launch {
        repository.insertWords(words)
    }
    fun getAllData(id: Int):LiveData<List<AllData>>{
        return runBlocking(viewModelScope.coroutineContext) {
             repository.getAllData(id)
        }
    }

    // if checkWords function return true means that we already have the word
    fun checkWords(word: String):Boolean{
        allWords.value!!.forEach {
            if (it.words == word)
                return true
        }
        return false
    }

    fun deleteAll(){
        // if all data was removed, reset numbering
        val editor = context.getSharedPreferences("sharedPrefs",Context.MODE_PRIVATE).edit()
        editor.remove("IdNum").apply()

        viewModelScope.launch {
            repository.deleteData()
        }
    }

    fun deleteWord(word: Words) {
        viewModelScope.launch {
            Log.d(TAG, "deleteWord: Deleting from Repo")
            repository.deleteWord(word)
        }
    }



    fun getWord(word:String,id:Int):Int{
        var returnValue = -100
        try {
            val request:Response<ArrayList<DictionaryData>> = DictionaryRetrofitBuilder.apiService.GetDefenitions(word).execute()
            if (request.body()!= null){
                Log.d("Dictionary", request.body().toString())
                Log.d("Dictionary is SuccessFull", request.isSuccessful.toString())

                insertDatas(request.body()!!,id)
                returnValue = request.code()
            }else{
                returnValue = request.code()
                Log.e("Body","is Empty")
            }

        }catch (ex:Exception){
            ex.printStackTrace()
        }
        return returnValue

    }
    private fun insertDatas(data :ArrayList<DictionaryData>,id:Int){
        this.insertWords(Words(id,data[0].word))
        var synonymString = ""

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
                        synonymString += synonyms
                        Log.d("Dictionary synonym",synonyms)
                    }
                }
            }
            if (synonymString == "")
                this.insertSynonym(Synonym(id,"No Synonyms available"))
            else
                this.insertSynonym(Synonym(id,synonymString))
        }

        // in case there was no phonetics available
        if (data[0].phonetics.isNotEmpty())
            for (phonetics in data[0].phonetics){
                Log.d("Dictionary phonetics",phonetics.phonetic)
                Log.d(TAG, "Dictionary phonetics: YES")
                this.insertPhonetics(Phonetics(
                    phonetics.phonetic,phonetics.audio,id
                ))
            }
        else
            this.insertPhonetics(Phonetics(
                "unavailable","unavailable",id
            ))
    }



}