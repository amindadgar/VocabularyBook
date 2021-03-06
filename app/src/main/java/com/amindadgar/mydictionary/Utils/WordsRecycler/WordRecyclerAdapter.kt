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

    private val TAG = "RecyclerView adapter"



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WordRecyclerViewHolder {
        val inflater = LayoutInflater.from(context)
        inflater.inflate(R.layout.dictionary_recycler_row_item, parent, false)

        return WordRecyclerViewHolder(
            inflater.inflate(R.layout.dictionary_recycler_row_item, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: WordRecyclerViewHolder, position: Int) {
        holder.wordTextView.text = data[position].words
        holder.definitionTextView.text = data[position].definitions
        holder.itemView.setOnClickListener { _ ->


            fragmentManager.beginTransaction()
                .replace(
                    R.id.FragmentContainer,
                    WordsInDetailFragment.newInstance(data[position].id,data[position].words)
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
        val size = data.size
        notifyDataSetChanged()
        return size
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

    // if the word entered was duplicate we would scroll to its position
    fun getItemPosition(word:String):Int{
        data.forEachIndexed { index, wordDefinitionTuple ->
            if (wordDefinitionTuple.words == word){
                return index
            }
        }
        // We Would NEVER reach here cause this function is called when the word is duplicated!
        return data.size - 1
    }
}