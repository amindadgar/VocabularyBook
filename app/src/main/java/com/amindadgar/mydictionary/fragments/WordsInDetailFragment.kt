package com.amindadgar.mydictionary.fragments

import android.annotation.SuppressLint
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.animation.Animation
import android.widget.Button
import androidx.fragment.app.Fragment
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.VelocityTrackerCompat.getXVelocity
import androidx.core.view.VelocityTrackerCompat.getYVelocity
import androidx.lifecycle.Observer
import com.amindadgar.mydictionary.R
import com.amindadgar.mydictionary.Utils.UiUtils.MyScrollView
import com.labo.kaji.fragmentanimations.MoveAnimation
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
    private var TAG = "WordsInDetailFragment"
    private lateinit var viewModel: WordsInDetailViewModel

    private lateinit var definitionText:TextView
    private lateinit var sampleSentenceText:TextView
    private lateinit var phoneticsText:TextView
    private lateinit var synonymText:TextView
    private lateinit var wordTextView: TextView
    private lateinit var soundIcon:ImageView
    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var fragmentLayout:MyScrollView

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
        fragmentLayout = view.findViewById(R.id.wordsInDetailFragmentLayout)
        fragmentLayout.initializeFragmentManager(requireActivity().supportFragmentManager)
    }

//    @SuppressLint("ClickableViewAccessibility")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(WordsInDetailViewModel::class.java)

//        fragmentLayout.setOnTouchListener { view, motionEvent ->
//            when(motionEvent.action){
//                MotionEvent.ACTION_MOVE -> {
//                    val actionType = motionEvent.action
//                    Log.d(TAG, "onActivityCreated: ACTION_OUTSIDE")
//                    true
//                }
//                else -> true
//            }
//        }


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
                    phoneticsText.text = "${phoneticsText.text}\n${it.phoneticText}"
                    soundUri = it.phoneticAudio
                }
                if (lastSynonym != it.synonym)
                    synonymText.text = "${synonymText.text}  ${it.synonym}"

                lastDefinition = it.definition
                lastPhonetics = it.phoneticText
                lastSampleSentence = it.example_sentence
                lastSynonym = it.synonym
            }
        })

        soundIcon.setOnClickListener {
            changeVolumeButton(true)
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
            mediaPlayer.setOnCompletionListener {
                changeVolumeButton(false)
            }
        }catch (ex:IOException){
            ex.printStackTrace()
            changeVolumeButton(false)
            Toast.makeText(requireActivity(),"Error playing audio",Toast.LENGTH_LONG).show()
        }catch (ex:IllegalArgumentException){
            ex.printStackTrace()
            changeVolumeButton(false)
            Toast.makeText(requireActivity(),"Error playing audio",Toast.LENGTH_LONG).show()
        }

    }
    // this functions are to change the behaviour of volume button when to start voice or end it!
    private fun changeVolumeButton(enable:Boolean){
        if (enable)
            soundIcon.animate().apply {
                alpha(2f)
                scaleY(1.3f)
                scaleX(1.3f)
                duration = 100
            }.start()
        else
            soundIcon.animate().apply {
                alpha(1f)
                scaleY(1f)
                scaleX(1f)
                duration = 400
            }.start()

    }

    override fun onCreateAnimation(transit: Int, enter: Boolean, nextAnim: Int): Animation? {
        return if (enter)
            MoveAnimation.create(MoveAnimation.UP,enter,500)
        else
            MoveAnimation.create(MoveAnimation.DOWN,enter,500)
    }

    override fun onStart() {
        super.onStart()
        mediaPlayer = MediaPlayer()
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause: YES")
        fragmentLayout.disposeFragmentManager()
    }
    override fun onStop() {
        super.onStop()
        mediaPlayer.release()
    }

}