package uk.ac.aber.dcs.rco1.converser.model

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import uk.ac.aber.dcs.rco1.converser.model.home.TranslationItem
import uk.ac.aber.dcs.rco1.converser.model.home.TranslationItemDao

/**
 * A database which supports translation item entities
 * Implements Room which requires this to be an abstract class
 */
@Database(entities = [TranslationItem::class], version = 1)
abstract class ConverserRoomDatabase : RoomDatabase() {

    /**
     * Provides a way to get hold of a TranslationItemDao object - Room generates the code
     * @return
     */
    abstract fun translationItemDao(): TranslationItemDao

    /**
     * Allows a way to make object of the DB as room insists on this being an abstract class
     */
    companion object {
        private var instance: ConverserRoomDatabase? = null

        /**
         * Builds a database object using a database builder
         *
         * @param context - context required, will be called with application context
         * @return a converser room db
         */
        fun getDatabase(context: Context): ConverserRoomDatabase? {
            // critical code: must be carried out from start -> finish with no interruptions
            synchronized(this) {
                /*create db if it does not exist - first time app is loaded
                   creates the db object with a reference to the application and a db object
                 */
                if (instance == null) {
                    instance =
                        Room.databaseBuilder<ConverserRoomDatabase>(
                            context.applicationContext,
                            ConverserRoomDatabase::class.java,
                            "converser_database"
                        )
                            //TODO: handle migrations if needed later
                            .build()
                }
                //it will never return null - will always have a db
                return instance!!
            }
        }
    }
}