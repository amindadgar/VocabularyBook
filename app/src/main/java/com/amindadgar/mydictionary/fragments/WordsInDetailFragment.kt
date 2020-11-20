package com.amindadgar.mydictionary.fragments

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.Observer
import com.amindadgar.mydictionary.R

class WordsInDetailFragment : Fragment() {

    companion object {
        fun newInstance(id:Int): WordsInDetailFragment{
            return WordsInDetailFragment().apply {
                arguments = Bundle().apply {
                    putInt("ID",id)
                }
            }
        }
    }

    private lateinit var viewModel: WordsInDetailViewModel

    private lateinit var definitionText:TextView
    private lateinit var sampleSentenceText:TextView
    private lateinit var phoneticsText:TextView
    private lateinit var synonymText:TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.words_in_detail_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        definitionText = view.findViewById(R.id.definition_text)
        sampleSentenceText = view.findViewById(R.id.sample_sentence_text)
        phoneticsText = view.findViewById(R.id.phonetics_text)
        synonymText = view.findViewById(R.id.synonym_text)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(WordsInDetailViewModel::class.java)

        val id = requireArguments().getInt("ID")
        Log.d("APPLICATION id",id.toString())

        // this variables are set for repeated values !
        var lastDefinition = ""
        var lastPhonetics = ""
        var lastSampleSentence = ""
        var lastSynonym = ""
        viewModel.getAllData(id).observe(viewLifecycleOwner, Observer { allDataList ->
            allDataList.forEach {
                //check that values won't repeat !
                // because THE database View is not set properly our data is repeated!

                Log.d("APPLICATION Definition",it.definition)
                if (lastDefinition != it.definition)
                    definitionText.text =  "${definitionText.text} \n${it.definition}"
                if (lastSampleSentence != it.example_sentence)
                    sampleSentenceText.text = "${sampleSentenceText.text} \n${it.example_sentence}"
                if (lastPhonetics != it.phoneticText)
                    phoneticsText.text = "${phoneticsText.text}\n${it.phoneticText}"
                if (lastSynonym != it.synonym)
                    synonymText.text = "${synonymText.text}\n${it.synonym}"

                lastDefinition = it.definition
                lastPhonetics = it.phoneticText
                lastSampleSentence = it.example_sentence
                lastSynonym = it.synonym
            }
        })
    }

}