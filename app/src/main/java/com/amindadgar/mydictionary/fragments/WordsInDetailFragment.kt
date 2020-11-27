package com.amindadgar.mydictionary.fragments

import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.Observer
import com.amindadgar.mydictionary.R
import java.io.IOException
import java.lang.IllegalArgumentException

class WordsInDetailFragment : Fragment() {

    companion object {
        fun newInstance(id:Int,word:String): WordsInDetailFragment{
            return WordsInDetailFragment().apply {
                arguments = Bundle().apply {
                    putInt("ID",id)
                    putString("WordString",word)
                }
            }
        }
    }

    private lateinit var viewModel: WordsInDetailViewModel

    private lateinit var definitionText:TextView
    private lateinit var sampleSentenceText:TextView
    private lateinit var phoneticsText:TextView
    private lateinit var synonymText:TextView
    private lateinit var wordTextView: TextView
    private lateinit var soundIcon:ImageView
    private lateinit var mediaPlayer: MediaPlayer

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
        wordTextView = view.findViewById(R.id.word_TextView)
        soundIcon = view.findViewById(R.id.sound_play)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(WordsInDetailViewModel::class.java)

        val id = requireArguments().getInt("ID")
        val word = requireArguments().getString("WordString")
        wordTextView.text = word
        var soundUri = ""
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
                    definitionText.text =  "${definitionText.text} \n${it.definition}\n"
                if (lastSampleSentence != it.example_sentence)
                    sampleSentenceText.text = "${sampleSentenceText.text} \n${it.example_sentence}\n"
                if (lastPhonetics != it.phoneticText) {
                    phoneticsText.text = "${phoneticsText.text}\n${it.phoneticText}\n"
                    soundUri = it.phoneticAudio
                }
                if (lastSynonym != it.synonym)
                    synonymText.text = "${synonymText.text}\n${it.synonym}"

                lastDefinition = it.definition
                lastPhonetics = it.phoneticText
                lastSampleSentence = it.example_sentence
                lastSynonym = it.synonym
            }
        })

        soundIcon.setOnClickListener {
            initializeAudio(Uri.parse(soundUri))
        }
    }

    fun initializeAudio(uri: Uri){
        try {

            mediaPlayer = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
                )
                setDataSource(requireActivity().applicationContext, uri)
                prepare()
                start()
            }
        }catch (ex:IOException){
            ex.printStackTrace()
        }catch (ex:IllegalArgumentException){
            ex.printStackTrace()
        }

    }

    override fun onStart() {
        super.onStart()
        mediaPlayer = MediaPlayer()
    }
    override fun onStop() {
        super.onStop()
        mediaPlayer.release()
    }

}