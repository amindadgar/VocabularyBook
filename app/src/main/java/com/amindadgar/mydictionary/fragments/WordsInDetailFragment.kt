package com.amindadgar.mydictionary.fragments

import android.graphics.drawable.AnimationDrawable
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.animation.Animation
import androidx.fragment.app.Fragment
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.viewModels
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

    // instantiate ViewModel with this fragment
    val viewModel:WordsInDetailViewModel by viewModels<WordsInDetailViewModel>()


    private lateinit var definitionText:TextView
    private lateinit var phoneticsText:TextView
    private lateinit var synonymText:TextView
    private lateinit var wordTextView: TextView
    private lateinit var soundIcon:com.airbnb.lottie.LottieAnimationView
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
        phoneticsText = view.findViewById(R.id.phonetics_text)
        synonymText = view.findViewById(R.id.synonym_text)
        wordTextView = view.findViewById(R.id.word_TextView)
        soundIcon = view.findViewById(R.id.sound_play)
        fragmentLayout = view.findViewById(R.id.wordsInDetailFragmentLayout)
        fragmentLayout.initializeFragmentManager(requireActivity().supportFragmentManager)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)


        val id = requireArguments().getInt("ID")
        val word = requireArguments().getString("WordString")
        wordTextView.text = word
        var soundUri = ""
        Log.d("APPLICATION id",id.toString())


        viewModel.getDefinitionExamples(id).observe(viewLifecycleOwner, Observer { dataList ->
            var stringBuilder = ""
            dataList.forEach { data ->
                stringBuilder += "${data.definition}\n"
                if (data.sampleSentence.isNotBlank())
                    stringBuilder += "ex: ${data.sampleSentence}\n\n"
                else
                    stringBuilder +="\n"

            }
            definitionText.text = stringBuilder
        })
        viewModel.getPhonetics(id).observe(viewLifecycleOwner, Observer { list ->
            // first item is phonetics text
            // second is phonetics uri
            list.forEach {
                phoneticsText.text = it.text
                soundUri = it.audio
            }
        })
        viewModel.getSynonym(id).observe(viewLifecycleOwner, Observer { list ->
            var synonyms = ""
            list.forEach {
                synonyms = "$it\n"
            }
            synonymText.text = synonyms
        })

        soundIcon.setOnClickListener {
            Toast.makeText(requireActivity(),"playing audio ...",Toast.LENGTH_LONG).show()
            changeVolumeButton(true)
            initializeAudio(Uri.parse(soundUri))
        }
    }


    private fun initializeAudio(uri: Uri){
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
        if (enable) {
            soundIcon.playAnimation()
        }
        else
            soundIcon.pauseAnimation()
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