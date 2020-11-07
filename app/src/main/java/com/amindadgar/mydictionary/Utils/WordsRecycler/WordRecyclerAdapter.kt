package com.amindadgar.mydictionary.Utils.WordsRecycler

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.amindadgar.mydictionary.R
import com.amindadgar.mydictionary.model.DictionaryApi.DictionaryData
import com.amindadgar.mydictionary.model.RoomDatabaseModel.WordDefinitionTuple

class WordRecyclerAdapter(private val context:Context,
                          private var data:ArrayList<WordDefinitionTuple>)
    :RecyclerView.Adapter<WordRecyclerAdapter.WordRecyclerViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WordRecyclerViewHolder {
        val inflater = LayoutInflater.from(context)
        return WordRecyclerViewHolder(
            inflater.inflate(R.layout.dictionary_recycler_row_item,parent,false)
        )
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: WordRecyclerViewHolder, position: Int) {
        holder.wordTextView.text = data[position].words
        holder.definitionTextView.text = data[position].definitions
    }

    inner class WordRecyclerViewHolder(itemView: View) :RecyclerView.ViewHolder(itemView) {
        val wordTextView: TextView = itemView.findViewById(R.id.word_TextView)
        val definitionTextView: TextView = itemView.findViewById(R.id.definition_TextView)

    }
    internal fun setWords(data: ArrayList<WordDefinitionTuple>){
        this.data = data
        notifyDataSetChanged()
    }
}