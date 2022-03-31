package uk.ac.aber.dcs.rco1.converser.data

import android.app.Application
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import uk.ac.aber.dcs.rco1.converser.model.home.TranslationItem

class ConverserRepository(application: Application) {

    //not null as there will always be a DB
    private val translationItemDao = ConverserRoomDatabase.getDatabase(application)!!.translationItemDao()
    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    //use coroutines to start operation
    fun insert(translationItem: TranslationItem){
        coroutineScope.launch {
            translationItemDao.insertSingleMessage(translationItem)
        }
    }

    fun deleteAll(){
        coroutineScope.launch {
            translationItemDao.deleteAll()
        }
    }

    //as these return live data, they are automatically run in another thread

    fun getAllTranslationItems() = translationItemDao.getAllMessages()

}