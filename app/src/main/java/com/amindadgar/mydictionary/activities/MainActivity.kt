package com.amindadgar.mydictionary.activities

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.amindadgar.mydictionary.R
import com.amindadgar.mydictionary.Utils.WordsRecycler.RecyclerTouchListener
import com.amindadgar.mydictionary.Utils.WordsRecycler.WordRecyclerAdapter
import com.amindadgar.mydictionary.model.RoomDatabaseModel.WordDefinitionTuple
import com.amindadgar.mydictionary.model.RoomDatabaseModel.Words
import com.andreseko.SweetAlert.SweetAlertDialog
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class MainActivity : AppCompatActivity() {

    private lateinit var wordsViewModel:WordsViewModel
    private var id:Int = 0
    private lateinit var sharePreferenceEditor: SharedPreferences.Editor
    lateinit var fab:FloatingActionButton
    private val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val sharedPreferences = getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)

        fab = findViewById(R.id.addFabButton)

        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar!!.setDisplayShowTitleEnabled(false)

        sharePreferenceEditor = sharedPreferences.edit()
        id = sharedPreferences.getInt("IdNum", 0)

        wordsViewModel = ViewModelProviders.of(this).get(WordsViewModel::class.java)

        val recyclerViewAdapter = WordRecyclerAdapter(this, arrayListOf(), supportFragmentManager)

        recyclerView.adapter = recyclerViewAdapter
        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.reverseLayout = false
        linearLayoutManager.stackFromEnd = true
        recyclerView.layoutManager = linearLayoutManager

        val db = Firebase.firestore


        wordsViewModel.allWords.observe(this, Observer { words ->
            words?.let {
                recyclerViewAdapter.setWords(words as ArrayList<WordDefinitionTuple>)
                linearLayoutManager.scrollToPosition(words.size - 1)

            }

        })

        initFab()
        recyclerViewListener(recyclerViewAdapter)



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



    private fun initFab(){

        fab.setOnClickListener {
            val dialogView = setupDialogLayout()

            val dialog = SweetAlertDialog(this, SweetAlertDialog.NORMAL_TYPE)
                .setConfirmText("Ok")
                .setTitleText("Add Word")
            dialog.setCustomView(dialogView)


            fab.animate().apply {
                rotation(360f)
                scaleX(0f)
                scaleY(0f)
                duration = 1000
            }
            dialog.show()


            dialog.setConfirmClickListener {
                Log.d("REQUEST", "DATA")
                val textView = (dialogView as LinearLayout).getChildAt(0)
                var word = (textView as TextView).text.toString()

                // if the last of word contains space delete it
                if (word[word.length - 1] == ' ') {
                    word = word.substring(0..word.length - 2)
                }
                request(word)

                dialog.dismiss()
                fab.animate().apply {
                    rotation(0f)
                    scaleX(1f)
                    scaleY(1f)
                    duration = 1000
                }
            }
            dialog.setOnDismissListener {
                fab.animate().apply {
                    rotation(0f)
                    scaleX(1f)
                    scaleY(1f)
                    duration = 1000
                }
            }
        }
    }

    private fun request(word: String){
        progressBar.visibility = View.VISIBLE
        try {
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
                        withContext(Dispatchers.Main){
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
        //setup imageView
        val imageView = ImageView(this)
        imageView.setImageResource(R.drawable.icon_mic)
        imageView.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT, 6f
        )
        imageView.scaleX = 0.8f
        imageView.scaleY = 0.8f

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

                            Toast.makeText(this@MainActivity,"Deleting ${deletedWord!!.words}",Toast.LENGTH_SHORT).show()

                            dialog.dismissWithAnimation()
                            // if we deleted the word we will delete it from database too

                            Log.d(TAG, "onLongClick: Deleted word ${deletedWord.id}")

                            wordsViewModel.deleteWord(Words(deletedWord.id, deletedWord.words))


                        }
                    }
                })
        )
    }

}