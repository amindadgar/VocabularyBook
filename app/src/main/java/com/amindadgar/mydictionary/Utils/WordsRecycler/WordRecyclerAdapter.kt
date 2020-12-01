package com.amindadgar.mydictionary.Utils.WordsRecycler

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.RecyclerView
import com.amindadgar.mydictionary.R
import com.amindadgar.mydictionary.fragments.WordsInDetailFragment
import com.amindadgar.mydictionary.model.RoomDatabaseModel.WordDefinitionTuple
import com.andreseko.SweetAlert.SweetAlertDialog

class WordRecyclerAdapter(
    private val context: Context,
    private var data: ArrayList<WordDefinitionTuple>,
    private val fragmentManager: FragmentManager
)
    :RecyclerView.Adapter<WordRecyclerAdapter.WordRecyclerViewHolder>() {

    private val wordsData:ArrayList<WordDefinitionTuple> = arrayListOf()
    private val TAG = "RecyclerView adapter"

    init {
        initializeItems()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WordRecyclerViewHolder {
        val inflater = LayoutInflater.from(context)
        inflater.inflate(R.layout.dictionary_recycler_row_item, parent, false)

        return WordRecyclerViewHolder(
            inflater.inflate(R.layout.dictionary_recycler_row_item, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return wordsData.size
    }

    override fun onBindViewHolder(holder: WordRecyclerViewHolder, position: Int) {
        holder.wordTextView.text = wordsData[position].words
        holder.definitionTextView.text = wordsData[position].definitions
        holder.itemView.setOnClickListener { _ ->


            fragmentManager.beginTransaction()
                .replace(
                    R.id.FragmentContainer,
                    WordsInDetailFragment.newInstance(wordsData[position].id,wordsData[position].words)
                )
                .addToBackStack(null)
                .commit()
        }
    }

    inner class WordRecyclerViewHolder(itemView: View) :RecyclerView.ViewHolder(itemView) {
        val wordTextView: TextView = itemView.findViewById(R.id.word_TextView)
        val definitionTextView: TextView = itemView.findViewById(R.id.definition_TextView)

    }
    // set new words and return it's position
    internal fun setWords(data: ArrayList<WordDefinitionTuple>):Int{
        this.data = data
        val size = initializeItems()
        notifyDataSetChanged()
        return size
    }
    private fun initializeItems():Int{
        var i = 0
        var lastWord = ""
        wordsData.clear()
        data.forEach{ wordDefinitionTuple ->
            if (lastWord != wordDefinitionTuple.words){
                lastWord = wordDefinitionTuple.words
                wordsData.add(i++,wordDefinitionTuple)
            }else{
                val tempIndex = i - 1
                wordsData[tempIndex] = WordDefinitionTuple(wordsData[tempIndex].id
                    ,wordsData[tempIndex].words
                    ,wordsData[tempIndex].definitions + "\n\n" + wordDefinitionTuple.definitions)
                Log.d(TAG, "initializeItems id: ${wordsData[tempIndex].id}")
                Log.d(TAG, "initializeItems definition: ${wordsData[tempIndex].definitions}")
            }
            Log.d(TAG, "WORD: ${wordDefinitionTuple.words}")
        }
        return wordsData.size
    }
    fun deleteWord(position: Int):WordDefinitionTuple{
        // save data in tmp variable to return it
        // by returning data we will tell that we removed the selected item
        Log.d(TAG, "deleteWord: Deleting from recyclerView")
        val tmp = data[position]
        data.removeAt(position)
        notifyDataSetChanged()
        return tmp
    }
}