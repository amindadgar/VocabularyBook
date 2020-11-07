package com.amindadgar.mydictionary.activities

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.amindadgar.mydictionary.R
import com.amindadgar.mydictionary.Utils.WordsApi.DictionaryRetrofitBuilder
import com.amindadgar.mydictionary.Utils.WordsRecycler.WordRecyclerAdapter
import com.amindadgar.mydictionary.model.DictionaryApi.DictionaryData
import com.amindadgar.mydictionary.model.RoomDatabaseModel.*
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {

    private lateinit var wordsViewModel:WordsViewModel
    private var id:Int = 0
    private lateinit var sharePreferenceEditor: SharedPreferences.Editor
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val sharedPreferences = getSharedPreferences("sharedPrefs",Context.MODE_PRIVATE)

        sharePreferenceEditor = sharedPreferences.edit()
        id = sharedPreferences.getInt("IdNum",0)

        wordsViewModel = ViewModelProviders.of(this).get(WordsViewModel::class.java)

        val recyclerViewAdapter = WordRecyclerAdapter(this, arrayListOf())
        recyclerView.adapter = recyclerViewAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        val db = Firebase.firestore

        add_word_editText_container.endIconImageButton.setOnClickListener {
            progressBar.visibility = View.VISIBLE

            try {
                Log.d("Dictionary ID",id.toString())
                //get text from editText
                val word = "${add_word_EditText.text}"
                // request data and if there was no connection error save it to database
                CoroutineScope(Dispatchers.IO).launch {

                    val requestCode = wordsViewModel.getWord(word,id)
                    if (requestCode == 200){
                        //increase id and save it when the activity is paused
                        id++
                        sharePreferenceEditor.putInt("IdNum",id).apply()
                        withContext(Dispatchers.Main){
                            progressBar.visibility = View.GONE
                        }
                    }else {
                        withContext(Dispatchers.Main){
                            Toast.makeText(this@MainActivity,"Error fetching data\nCode: $requestCode",Toast.LENGTH_LONG).show()
                            progressBar.visibility = View.GONE
                        }
                    }
                }

            }catch (ex:Exception){
                ex.printStackTrace()
                progressBar.visibility = View.GONE
                Toast.makeText(this,"Error ${ex.message}",Toast.LENGTH_LONG).show()

            }
        }

        wordsViewModel.allWords.observe(this, Observer { words ->
            words?.let {
                recyclerViewAdapter.setWords(words as ArrayList<WordDefinitionTuple>)
            }

        })



        /**        This part is For FireBase and it's commented
         */
        db.collection("words")
                .get()
                .addOnSuccessListener { result ->

                    for (document in result) {
//                        val mytexts = textView.text
//                        textView.text = "$mytexts\n\nId: ${document.id}\t ${document.data}\n"
                    }
                }
                .addOnFailureListener { e ->
//                    val mytexts = textView.text
//                    textView.text = "$mytexts \n\nFailed Syncing Data\n${e.printStackTrace()}"
                }


    }

    override fun onPause() {
        super.onPause()

    }




}