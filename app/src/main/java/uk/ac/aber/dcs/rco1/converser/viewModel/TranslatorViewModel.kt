package uk.ac.aber.dcs.rco1.converser.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import uk.ac.aber.dcs.rco1.converser.model.ConverserRepository
import uk.ac.aber.dcs.rco1.converser.model.home.TranslationItem

class TranslatorViewModel(application: Application) :
    AndroidViewModel(application){

        private val repository: ConverserRepository = ConverserRepository(application)
        var translationItems: LiveData<List<TranslationItem>> = getAllTranslationItems()
        //public get method which can only be set privately (read only from home fragment)
        private set


    private fun getAllTranslationItems(): LiveData<List<TranslationItem>>{
        return repository.getAllTranslationItems()
    }

    fun addTranslationItemToConversation(translationItem: TranslationItem){
        repository.insert(translationItem)
    }

    private fun deleteConversation(){
        repository.deleteAll()
    }
}