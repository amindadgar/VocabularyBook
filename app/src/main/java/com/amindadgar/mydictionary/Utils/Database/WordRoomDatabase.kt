package com.amindadgar.mydictionary.Utils.Database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.amindadgar.mydictionary.model.RoomDatabaseModel.DAO.WordsDao
import com.amindadgar.mydictionary.model.RoomDatabaseModel.Definition
import com.amindadgar.mydictionary.model.RoomDatabaseModel.Phonetics
import com.amindadgar.mydictionary.model.RoomDatabaseModel.Synonym
import com.amindadgar.mydictionary.model.RoomDatabaseModel.Words
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


// Annotates class to be a Room Database with a table (entity) of the Word class
@Database(
    entities = [Words::class,Definition::class,Phonetics::class,Synonym::class]
    ,version = 1
    ,exportSchema = false
)
abstract class WordRoomDatabase :RoomDatabase(){
    abstract fun WordsDao():WordsDao

    private class WordsDatabaseCallback(private val coroutineScope: CoroutineScope):RoomDatabase.Callback() {
        override fun onOpen(db: SupportSQLiteDatabase) {
            super.onOpen(db)
            Instance!!.let { database ->
                coroutineScope.launch {
//                    val wordsDao = database.WordsDao()
//                    wordsDao.insertAllData(Words(0,"Hello")
//                        , Definition("Greeting","test",0)
//                        , Phonetics("HELLO","SampleUrl",0)
//                    )
                }
            }

        }

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)

        }
    }


    companion object {
        @Volatile
        private var Instance:WordRoomDatabase? = null

        fun getInstance(context: Context,scope: CoroutineScope):WordRoomDatabase {

            return Instance ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    WordRoomDatabase::class.java,
                    "word_database"
                ).build()
               Instance = instance
                instance

            }
        }
    }

}