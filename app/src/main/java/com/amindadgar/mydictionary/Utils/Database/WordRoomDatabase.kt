package com.amindadgar.mydictionary.Utils.Database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.amindadgar.mydictionary.model.RoomDatabaseModel.*
import com.amindadgar.mydictionary.model.RoomDatabaseModel.DAO.WordsDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


// Annotates class to be a Room Database with a table (entity) of the Word class
@Database(
    entities = [Words::class,Definition::class,Phonetics::class,Synonym::class]
    ,version = 2
    ,exportSchema = false

)
abstract class WordRoomDatabase :RoomDatabase(){
    abstract fun WordsDao():WordsDao

    private class WordsDatabaseCallback(private val coroutineScope: CoroutineScope):RoomDatabase.Callback() {
        override fun onOpen(db: SupportSQLiteDatabase) {
            super.onOpen(db)
            Instance!!.let { database ->
                coroutineScope.launch {

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
                )
                    .addMigrations(MIGRATION_1_2)
                    .addMigrations(MIGRATION_2_3)
                    .build()
               Instance = instance
                instance

            }
        }
        val MIGRATION_1_2: Migration = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("CREATE VIEW IF NOT EXISTS `AllData` AS SELECT id,word,definitions,example,synonym,audio,text FROM Words JOIN Definition JOIN Synonym JOIN Phonetics WHERE Words.id = Definition.word_id AND Synonym.word_id = Words.id AND Phonetics.word_id = Words.id")
            }
        }
        val MIGRATION_2_3: Migration = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("DROP VIEW `AllData`")
            }
        }
    }


}