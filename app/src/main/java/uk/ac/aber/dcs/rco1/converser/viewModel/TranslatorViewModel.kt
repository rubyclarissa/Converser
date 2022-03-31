package uk.ac.aber.dcs.rco1.converser.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import uk.ac.aber.dcs.rco1.converser.data.ConverserRepository

class TranslatorViewModel(application: Application) :
    AndroidViewModel(application){

        private val repository: ConverserRepository = ConverserRepository(application)
    var translationItems = repository.getAllTranslationItems()
        //public get method which can only be set privately (read only from home fragment)
        private set
}