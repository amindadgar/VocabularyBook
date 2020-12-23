package com.amindadgar.mydictionary.activities

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.amindadgar.mydictionary.R
import com.amindadgar.mydictionary.Utils.AuthLogin
import com.amindadgar.mydictionary.Utils.WordsRecycler.RecyclerTouchListener
import com.amindadgar.mydictionary.Utils.WordsRecycler.WordRecyclerAdapter
import com.amindadgar.mydictionary.Utils.services.FloatingService
import com.amindadgar.mydictionary.model.RoomDatabaseModel.WordDefinitionTuple
import com.amindadgar.mydictionary.model.RoomDatabaseModel.Words
import com.andreseko.SweetAlert.SweetAlertDialog
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*


class MainActivity : AppCompatActivity() {

    // instantiate ViewModel
    private val wordsViewModel:WordsViewModel by viewModels()
    private var id:Int = 0
    private lateinit var sharePreferenceEditor: SharedPreferences.Editor
    lateinit var fab:FloatingActionButton
    private val TAG = "MainActivity"
    val REQUEST_AUDIO_CODE = 100
    private lateinit var recyclerViewAdapter:WordRecyclerAdapter
    var wordsData : ArrayList<WordDefinitionTuple> = arrayListOf(
        WordDefinitionTuple(
            0,
            "word",
            "definition"
        )
    )
    private var authLogin:AuthLogin? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val sharedPreferences = getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)

        fab = findViewById(R.id.addFabButton)

        setSupportActionBar(findViewById(R.id.toolbar))

        sharePreferenceEditor = sharedPreferences.edit()
        id = sharedPreferences.getInt("IdNum", 0)

        setUpRecyclerView()
        initFab()
        recyclerViewListener(recyclerViewAdapter)
        observeRecyclerViewScroll(recyclerView)


        // FireBase is not used yet!
//        val db = Firebase.firestore
//        /**        This part is For FireBase and it's commented
//         */
//        db.collection("words")
//                .get()
//                .addOnSuccessListener { result ->
//
//                    for (document in result) {
////                        val mytexts = textView.text
////                        textView.text = "$mytexts\n\nId: ${document.id}\t ${document.data}\n"
//                    }
//                }
//                .addOnFailureListener { e ->
////                    val mytexts = textView.text
////                    textView.text = "$mytexts \n\nFailed Syncing Data\n${e.printStackTrace()}"
//                }


    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_activity_menu, menu)
        return true
    }

    fun startLogin(){
        // initialize login class
        authLogin = AuthLogin(this)
        // start the login process
        authLogin!!.login()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if (intent != null) {
            val data = intent.getStringExtra("com.auth0.ACCESS_TOKEN")
            Log.d(TAG, "onNewIntent: $data")
        }else
            Log.d(TAG, "onNewIntent: No intent available")
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.settings_menu_item -> {

                true
            }
            R.id.user_profile_menu_item -> {
                startLogin()
                true
            }
            else -> false
        }
    }
    private fun setUpRecyclerView():Boolean{
        recyclerViewAdapter = WordRecyclerAdapter(this, arrayListOf(), supportFragmentManager)

        recyclerView.adapter = recyclerViewAdapter
        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.reverseLayout = false
        linearLayoutManager.stackFromEnd = true
        recyclerView.layoutManager = linearLayoutManager

        observeRecyclerViewData(linearLayoutManager)

        return true
    }

    // observe recyclerView to see change hide or show the fab
    private fun observeRecyclerViewScroll(recyclerView: RecyclerView){
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 1) {
                    setFabAsShown(false)
                }
                if (dy < -1)
                    setFabAsShown(true)
            }
        })
    }
    private fun setFabAsShown(show: Boolean){
        if (show)
            fab.show()
        else
            fab.hide()
    }
    private fun observeRecyclerViewData(linearLayoutManager: LinearLayoutManager):Boolean{
        wordsViewModel.allWords.observe(this, Observer { words ->
            words?.let {
                val data = wordsViewModel.initializeItems(words as ArrayList<WordDefinitionTuple>)
                val size = recyclerViewAdapter.setWords(data)
                linearLayoutManager.scrollToPosition(size - 1)
                wordsData = it as ArrayList<WordDefinitionTuple>

                startFloatingService()
            }
        })
        return true
    }

    private fun startFloatingService(command: String = "") {

        val intent = Intent(this, FloatingService::class.java)
        if (command.isNotBlank()) {
            intent.putExtra("com.amindadgar.mydictionary", command)
        }
        intent.putParcelableArrayListExtra("FloatingWindowExtra", wordsData)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            this.startForegroundService(intent)
        } else {
            this.startService(intent)
        }
    }

    // this function is used to change the appearance of voice icon
    private fun setVoiceIconAsListening(Listening: Boolean, voiceIcon: ImageView){
        if (Listening){
            voiceIcon.animate().apply {
                scaleX(1f)
                scaleY(1f)
                duration = 100
            }
        }else {
            voiceIcon.animate().apply {
                scaleX(0.8f)
                scaleY(0.8f)
                duration = 100
            }
        }

    }

    private fun initFab(){

        fab.setOnClickListener {
            val dialogView = setupDialogLayout()

            val dialog = SweetAlertDialog(this, SweetAlertDialog.NORMAL_TYPE)
                .setConfirmText("Ok")
                .setTitleText("Add Word")
            dialog.setCustomView(dialogView)


            val textView = (dialogView as LinearLayout).getChildAt(0)
            val voiceIcon = (dialogView as LinearLayout).getChildAt(1) as ImageView

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED){
                checkVoicePermission()
            }
            val speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
            intent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 100);
            intent.putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())

            voiceIcon.setOnClickListener {
                /**
                default scale is 0.8
                when user clicks it we would make icon bigger
                else if the user wanted to cancel listening while the speech is not ended the icon's scale is 1f so we would cancel it!

                 */
                if (voiceIcon.scaleX == 0.8f) {
                    setVoiceIconAsListening(true, voiceIcon)
                    speechRecognizer.startListening(intent)
                }
                else {
                    speechRecognizer.stopListening()
                }


            }

            speechRecognizer.setRecognitionListener(object : RecognitionListener {
                override fun onReadyForSpeech(p0: Bundle?) {}

                override fun onBeginningOfSpeech() {
                    Toast.makeText(this@MainActivity, "Listening ...", Toast.LENGTH_SHORT).show()
                }

                override fun onRmsChanged(p0: Float) {}

                override fun onBufferReceived(p0: ByteArray?) {
                    Log.d(TAG + "Voice", "onBufferReceived: ${p0.toString()}")
                }

                override fun onEndOfSpeech() {
                    Log.d(TAG, "onEndOfSpeech: Ended!")
                    speechRecognizer.stopListening()
                    setVoiceIconAsListening(false, voiceIcon)
                }

                override fun onError(p0: Int) {
                    Toast.makeText(
                        this@MainActivity,
                        "Error fetching data from server code:$p0",
                        Toast.LENGTH_LONG
                    ).show()
                }

                override fun onResults(p0: Bundle?) {
                    // just show the results as a Toast message
                    if (p0 != null) {
                        val data: ArrayList<String>? =
                            p0.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                        Toast.makeText(this@MainActivity, data!!.toString(), Toast.LENGTH_LONG)
                            .show()
                        (textView as TextView).text = data[0]
                    }

                }

                override fun onPartialResults(p0: Bundle?) {
                    Log.d(TAG + "voice", "onPartialResults: ")
                    if (p0 != null) {
                        val data: ArrayList<String>? =
                            p0.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                        Toast.makeText(this@MainActivity, data!!.toString(), Toast.LENGTH_LONG)
                            .show()
                        Log.d(TAG + "voice", "onPartialResults: $data")

                    }

                }

                override fun onEvent(p0: Int, p1: Bundle?) {
                    Log.d(TAG, "onEvent: $p0")
                }

            })


            setFabAsShown(false)
            dialog.show()

            // set dialog listeners
            dialog.setConfirmClickListener {
                dialogConfirmationButtonClick(textView, dialog)
            }
            dialog.setOnDismissListener {
                setFabAsShown(true)
            }
        }
    }

    private fun dialogConfirmationButtonClick(textView: View, dialog: SweetAlertDialog){
        Log.d("REQUEST", "DATA")
        var word = (textView as TextView).text.toString()

        if (!word.isBlank()) {
            // if the last of word contains space delete it
            if (word[word.length - 1] == ' ') {
                word = word.substring(0..word.length - 2)
            }

            request(word)
        }else{
            Toast.makeText(this, "Please enter your word", Toast.LENGTH_SHORT).show()
        }

        dialog.dismiss()
        setFabAsShown(true)
    }

    private fun request(word: String){
        progressBar.visibility = View.VISIBLE
        try {
            // first of all check if the word is duplicate or not!
            val wordIsAvailable = wordsViewModel.checkWords(word)
            if (!wordIsAvailable) {
                //get text from editText
                // request data and if there was no connection error save it to database
                CoroutineScope(Dispatchers.IO).launch {
                    var requestCode = -1

                    // if nothing was entered set request code -12
                    requestCode = if (!word.isBlank())
                        wordsViewModel.getWord(word, id)
                    else
                        -12

                    when (requestCode) {
                        200 -> {
                            //increase id and save it when the activity is paused
                            id++
                            sharePreferenceEditor.putInt("IdNum", id).apply()
                            withContext(Dispatchers.Main) {
                                progressBar.visibility = View.GONE
                            }
                        }
                        -12 -> {
                            withContext(Dispatchers.Main) {
                                Toast.makeText(this@MainActivity, "Empty word!", Toast.LENGTH_LONG)
                                    .show()
                                progressBar.visibility = View.GONE
                            }
                        }
                        else -> {
                            withContext(Dispatchers.Main) {
                                Toast.makeText(
                                    this@MainActivity,
                                    "Error fetching data\nCode: $requestCode",
                                    Toast.LENGTH_LONG
                                ).show()
                                progressBar.visibility = View.GONE
                            }
                        }
                    }
                }
            }else {
                progressBar.visibility = View.GONE
                Toast.makeText(this, "Duplicate word", Toast.LENGTH_LONG).show()
                val itemPosition = recyclerViewAdapter.getItemPosition(word)
                recyclerView.scrollToPosition(itemPosition)
            }

        }catch (ex: Exception){
            ex.printStackTrace()
            progressBar.visibility = View.GONE
            Toast.makeText(this, "Error ${ex.message}", Toast.LENGTH_LONG).show()

        }
    }

    private fun setupDialogLayout():View{
        // setup editText
        val editText = EditText(this)
        editText.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT,
            1f
        )
        editText.tag = "DialogEditText"
        //setup imageView
        val imageView = ImageView(this)
        imageView.setImageResource(R.drawable.icon_mic)
        imageView.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT, 6f
        )
        imageView.scaleX = 0.8f
        imageView.scaleY = 0.8f
        imageView.tag = "VoiceButton"

        //setup layout container
        val linearLayout = LinearLayout(this)
        linearLayout.orientation = LinearLayout.HORIZONTAL
        linearLayout.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        linearLayout.weightSum = 7f

        // add view's to linear layout
        linearLayout.addView(editText)
        linearLayout.addView(imageView)

        return linearLayout
    }

    private fun recyclerViewListener(adapter: WordRecyclerAdapter){

        recyclerView.addOnItemTouchListener(
            RecyclerTouchListener(
                this,
                recyclerView,
                object : RecyclerTouchListener.ClickListener {
                    override fun onClick(view: View?, position: Int) {}

                    override fun onLongClick(view: View?, position: Int) {

                        Log.d(TAG, "onBindViewHolder: Yes")
                        val dialog =
                            SweetAlertDialog(this@MainActivity, SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("Are you sure to delete this word?\nyour action cannot be undo")
                        dialog.confirmText = "Yes, delete it anyway."
                        dialog.cancelText = "No, keep it."
                        dialog.show()

                        dialog.setOnCancelListener {
                            dialog.dismissWithAnimation()
                        }
                        dialog.setConfirmClickListener {
                            val deletedWord: WordDefinitionTuple? = adapter.deleteWord(position)

                            Toast.makeText(
                                this@MainActivity,
                                "Deleting ${deletedWord!!.words}",
                                Toast.LENGTH_SHORT
                            ).show()

                            dialog.dismissWithAnimation()
                            // if we deleted the word we will delete it from database too

                            Log.d(TAG, "onLongClick: Deleted word ${deletedWord.id}")

                            wordsViewModel.deleteWord(Words(deletedWord.id, deletedWord.words))


                        }
                    }
                })
        )
    }
//    private fun setUpWordChooserView(wordsCount:Int):View {
//
//        //setup layout container
//        val linearLayout = LinearLayout(this)
//        linearLayout.orientation = LinearLayout.HORIZONTAL
//        linearLayout.layoutParams = LinearLayout.LayoutParams(
//            LinearLayout.LayoutParams.MATCH_PARENT,
//            LinearLayout.LayoutParams.MATCH_PARENT
//        )
//        linearLayout.weightSum = wordsCount.toFloat()
//
//        for (i in 0 until wordsCount) {
//            val textView = TextView(this)
//            textView.layoutParams = LinearLayout.LayoutParams(
//                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT,
//                1f
//            )
//            linearLayout.addView(textView)
//        }
//
//        return linearLayout
//
//    }

    private fun checkVoicePermission(){
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.RECORD_AUDIO),
            REQUEST_AUDIO_CODE
        )
    }


}