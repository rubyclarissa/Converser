package uk.ac.aber.dcs.rco1.converser.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.google.mlkit.common.model.RemoteModelManager
import com.google.mlkit.nl.translate.TranslateLanguage
import uk.ac.aber.dcs.rco1.converser.model.ConverserRepository
import uk.ac.aber.dcs.rco1.converser.model.home.TranslationItem

class TranslatorViewModel(application: Application) :
    AndroidViewModel(application) {

    val sourceLanguage: LiveData<String>? = null
    val targetLanguage: LiveData<String>? = null
    val sourceText: LiveData<String>? = null
    val translatedText: LiveData<String>? = null

    val sourceLanguageCode : String = TranslateLanguage.ENGLISH
    val targetLanguageCode : String = TranslateLanguage.ENGLISH

    private val languageModelManager: RemoteModelManager = RemoteModelManager.getInstance()
    private var downloadedModels: List<String> = listOf("")

    private val repository: ConverserRepository = ConverserRepository(application)
    var translationItems: LiveData<List<TranslationItem>> = getAllTranslationItems()
        //public get method which can only be set privately (read only from home fragment)
        private set


    private fun getAllTranslationItems(): LiveData<List<TranslationItem>> {
        return repository.getAllTranslationItems()
    }

    fun addTranslationItemToConversation(translationItem: TranslationItem) {
        repository.insert(translationItem)
    }

    private fun deleteConversation() {
        repository.deleteAll()
    }


}