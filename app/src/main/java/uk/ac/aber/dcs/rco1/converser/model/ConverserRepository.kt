package uk.ac.aber.dcs.rco1.converser.model

import android.app.Application
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import uk.ac.aber.dcs.rco1.converser.model.home.TranslationItem

/**
 * This repository is an abstraction layer the database communication
 * It uses a translation item DAO to perform operations on the database
 *
 * @param application - reference to the app context which is required to get the database
 */
class ConverserRepository(application: Application) {

    //not null as there will always be a DB
    private val translationItemDao =
        ConverserRoomDatabase.getDatabase(application)!!.translationItemDao()

    //use IO thread for database operations that don't use live data
    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    /**
     * Inserts a translation item into the db by querying the translation item DAO
     * uses coroutines to start operation in IO thread
     * @param translationItem - the translation item to insert
     */
    fun insert(translationItem: TranslationItem) {
        coroutineScope.launch {
            translationItemDao.insertSingleTranslationItem(translationItem)
        }
    }

    /**
     * deletes all translation items from the database by querying the translation item DAO
     * uses coroutines to start operation in IO thread
     */
    fun deleteAll() {
        coroutineScope.launch {
            translationItemDao.deleteAll()
        }
    }

    /**
     * Gets all translation items from the db by querying the translation item DAO
     * live data returned means this is automatically run in another thread
     * @return list of translation items as live data
     */
    fun getAllTranslationItems() = translationItemDao.getAllTranslationItems()

}