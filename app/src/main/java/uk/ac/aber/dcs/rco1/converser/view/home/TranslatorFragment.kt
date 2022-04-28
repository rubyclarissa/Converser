package uk.ac.aber.dcs.rco1.converser.view.home

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import android.text.Editable
import android.text.TextUtils.isEmpty
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.isEmpty
import androidx.core.view.isNotEmpty
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.common.model.RemoteModelManager
import com.google.mlkit.nl.translate.*
import uk.ac.aber.dcs.rco1.converser.R
import uk.ac.aber.dcs.rco1.converser.model.ConverserRepository
import uk.ac.aber.dcs.rco1.converser.databinding.FragmentTranslatorBinding
import uk.ac.aber.dcs.rco1.converser.model.home.PositionInConversation
import uk.ac.aber.dcs.rco1.converser.model.home.TranslationItem
import uk.ac.aber.dcs.rco1.converser.view.dialogs.ConfirmConversationRefreshDialogFragment
import uk.ac.aber.dcs.rco1.converser.view.dialogs.DownloadLanguageModelDialogFragment
import uk.ac.aber.dcs.rco1.converser.viewModel.TranslatorViewModel
import java.io.File
import java.lang.Exception
import kotlin.collections.ArrayList

//file name for the file containing the list of currently downloaded language models
const val DOWNLOADED_MODELS_TEXT_FILE = "downloadedModelsFile.txt"

/**
 * Home screen where translations and conversation takes place
 * Implements Fragment() - the constructor for a fragment factory
 */
class TranslatorFragment : Fragment() {

    private lateinit var translatorFragmentBinding: FragmentTranslatorBinding

    //used instead of the depreciated startActivityForResult in speak method
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>

    //grab UI elements
    private lateinit var translateButton: ImageButton
    private lateinit var micFAB: ImageButton
    private lateinit var swapButton: ImageButton
    private lateinit var translationItemRecyclerView: RecyclerView
    private lateinit var inputText: EditText
    private lateinit var sourceSpinner: Spinner
    private lateinit var targetSpinner: Spinner
    private lateinit var leftLanguageButton: Button
    private lateinit var rightLanguageButton: Button

    //adapter for the conversation recycler view
    private lateinit var conversationAdapter: ConversationAdapter

    //list of translations in a conversation - now just used for scrolling action
    private lateinit var translationItemList: ArrayList<TranslationItem>

    private var sourceLanguage: String = ""
    private var targetLanguage: String = ""
    private var stringToTranslate: String = ""

    //initialise positionInConversation codes to English by default
    private var sourceLanguageCode = TranslateLanguage.ENGLISH
    private var targetLanguageCode = TranslateLanguage.ENGLISH

    private lateinit var translator: Translator

    //used for identifying which positionInConversation is being translated
    private var positionInConversation: PositionInConversation = PositionInConversation.SECOND
    private lateinit var languageA: String
    private lateinit var languageB: String

    //explicit language model management
    private val languageModelManager: RemoteModelManager = RemoteModelManager.getInstance()
    private var downloadedModels: List<String> = listOf("")

    //TODO: change later as do not want this in home fragment
    private lateinit var repository: ConverserRepository

    private var oldConversationData: LiveData<List<TranslationItem>>? = null

    //uses property delegate from fragment ktx artefact instead of making an object
    private val translatorViewModel: TranslatorViewModel by viewModels()

    //tts - not implemented yet
    private lateinit var tts: TextToSpeech

    //read downloaded language model codes from text file
    private lateinit var downloadedModelsTextFile: File

    /**
     * Instantiates a view for the fragment
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        translatorFragmentBinding = FragmentTranslatorBinding.inflate(inflater, container, false)

        return translatorFragmentBinding.root
    }

    /**
     * Sets up logic that operates on the view immediately returned from onCreateView
     *
     * @param view
     * @param savedInstanceState
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //create empty list of items
        translationItemList = ArrayList()

        //set up language selection spinners
        setupSpinners()
        //get the UI elements via the binding mechanism
        getUIElements()
        //get speech data and put into the edit text box
        captureSpeechData()
        //configure the adapter for the conversation recycler view
        setUpConversationRecyclerAdapter()
        //set up with the view model class for MVVM
        setUpTranslatorViewModel()

        //when app first loads, hide the quick-switch buttons
        if (translationItemRecyclerView.isEmpty()) {
            translatorFragmentBinding.quickLanguageSelections.isVisible = false
        }

        stringToTranslate = inputText.text.toString()

        //set up on click events
        setListenerForLanguageSwap()
        setListenerForTranslation()
        setListenerForSpeechRecording()
        setListenerForActiveLanguageButton()

        //set up the repository to enable communication with the database
        repository = ConverserRepository(requireActivity().application)
    }

    /**
     * Reads downloaded language codes from the downloaded language models text file
     * Called when fragment becomes visible
     */
    override fun onStart() {
        super.onStart()

        downloadedModelsTextFile = File(requireContext().filesDir, DOWNLOADED_MODELS_TEXT_FILE)
        readDownloadedModelsDataFromFile()
    }

    /**
     * Reads all lines in the downloaded language models text file
     *
     */
    private fun readDownloadedModelsDataFromFile() {
        if (downloadedModelsTextFile.exists()) {
            downloadedModels = downloadedModelsTextFile.readLines()
        }
    }

    /**
     * Writes each language code corresponding to a downloaded language model to the text file
     * Each is written as a new line in the text file so that readLines() can be used to read
     * Writes raw bytes to the file
     * TODO: Implement FileWriter instead?
     */
    private fun writeDownloadedModelsDataToFile() {
        try {
            val fileOutputStream =
                requireContext().openFileOutput(DOWNLOADED_MODELS_TEXT_FILE, MODE_PRIVATE)
            for (model in downloadedModels) {
                fileOutputStream.write(model.toByteArray())
                fileOutputStream.write(System.lineSeparator().toByteArray())
            }
            fileOutputStream.close()
        } catch (e: Exception) {
            Log.i("TAG", "failure writing to downloaded models file")
        }
    }

    /**
     * Saves a list of downloaded models to file when screens change, app closed etc as a failsafe
     * in case it was written for any reason when a downloaded model list was changed
     */
    override fun onPause() {
        super.onPause()
        writeDownloadedModelsDataToFile()
    }

    /**
     * Sets up the view model related to this fragment so that MVVM can be integrated
     *
     */
    private fun setUpTranslatorViewModel() {
        //ask view model for the list of translator items
        val translationItems = translatorViewModel.translationItems

        //if it returns a new list of items then remove the observers associated with old list
        if (oldConversationData != translationItems) {
            oldConversationData?.removeObservers(viewLifecycleOwner)
            oldConversationData = translationItems
        }

        /* viewLifecycleOwner instead of 'this' - inherited from superclass (only want to observe
        items if there is a UI. could say 'this' but at some points the fragment won't have UI
        components e.g. before its built / after its destroyed - so exception would be thrown.
        This will only trigger observer if there is an UI - guaranteed to always work
         */
        if (!translationItems.hasObservers()) {
            translationItems.observe(viewLifecycleOwner) { translations ->
                conversationAdapter.changeDataSet(translations.toMutableList())
            }
        }
    }

    /**
     * Sets up an adapter for the conversation recycler view
     * Uses a linear layout manager to create a vertical list of items
     */
    private fun setUpConversationRecyclerAdapter() {
        conversationAdapter = ConversationAdapter(requireContext())
        translationItemRecyclerView.adapter = conversationAdapter
        val conversationLayoutManager = LinearLayoutManager(context)
        translationItemRecyclerView.layoutManager = conversationLayoutManager
    }

    /**
     * Sets up the microphone FAB to record speech when it is tapped
     * Single tap and long taps are both handled
     */
    private fun setListenerForSpeechRecording() {
        //support if user presses and holds
        micFAB.setOnLongClickListener {
            speak(translatorFragmentBinding.recordVoiceFAB)
        }
        //support if user just presses
        micFAB.setOnClickListener {
            speak(translatorFragmentBinding.recordVoiceFAB)
        }
    }

    //get model for a positionInConversation
    /**
     * Gets a language model for translation
     *
     * @param languageCode - the language code for the language to be downloaded e.g. "en"
     * @return A language model for the language
     */
    private fun getLanguageModel(languageCode: String): TranslateRemoteModel {
        return TranslateRemoteModel.Builder(languageCode).build()
    }

    /**
     * Downloads a language model
     * Shows the downloading dialog whilst downloading
     * Gets the model, sets up the conditions for downloading and downloads the model
     * Closes the dialog when it is completed then updates the list of downloaded models
     *
     * @param languageCode - the language code for the language to be downloaded e.g. "en"
     */
    private fun downloadLanguage(languageCode: String) {
        val dialog = DownloadLanguageModelDialogFragment()
        dialog.show(this.parentFragmentManager, "download dialog")

        val model = getLanguageModel(languageCode)
        val conditions = DownloadConditions.Builder()
            .requireWifi()
            .build()

        languageModelManager.download(model, conditions).addOnCompleteListener {
            dialog.dismiss()
        }
        getDownloadedModels()
    }

    /**
     * Updates the list of downloaded language models
     * Transforms the list into a list of languages rather than TranslatorRemoteModels
     */
    private fun getDownloadedModels() {
        languageModelManager.getDownloadedModels(TranslateRemoteModel::class.java)
            .addOnSuccessListener { models ->
                //TODO: sort alphabetically
                downloadedModels = models.map { it.language }
            }
    }

    /**
     * Deletes a language model
     * Gets the language model for the language and gets the model manager to delete it
     * List of downloaded models is updated afterwards
     *
     * @param languageCode - the language code of the language model to be deleted
     */
    private fun deleteLanguage(languageCode: String) {
        val model = getLanguageModel(languageCode)
        languageModelManager.deleteDownloadedModel(model)
            .addOnCompleteListener {
                getDownloadedModels()
            }
    }

    /**
     * Downloads a language model if it is not already on the device
     * Checks if the language code is in the list of downloaded language models
     * If it is not, it is downloaded, the list of downloaded language models is updated
     * The list of downloaded language models is then saved
     *
     * @param languageCode - the language code on the language model to be downloaded
     */
    private fun downloadModelIfNotOnDevice(languageCode: String) {
        getDownloadedModels()
        if (!downloadedModels.contains(languageCode)) {
            Log.i("TAG", "Downloaded models:" + downloadedModels)
            downloadLanguage(languageCode)
            getDownloadedModels()
            writeDownloadedModelsDataToFile()
        }
    }

    /**
     * Makes the quick language swap buttons visible if they are not already
     */
    private fun makeQuickLanguageSelectionsVisible() {
        if (!translatorFragmentBinding.quickLanguageSelections.isVisible) {
            translatorFragmentBinding.quickLanguageSelections.isVisible = true
        }
    }

    /**
     * Performs translation when the translate button is tapped
     * Options for the translation are set up with source and target languages
     * If text is provided for translation, language models are downloaded and translation occurs
     * Translation item is then added to the conversation
     */
    private fun setListenerForTranslation() {
        translateButton.setOnClickListener {

            makeQuickLanguageSelectionsVisible()

            //create translator object with configurations for source and target languages
            val options = TranslatorOptions.Builder()
                .setSourceLanguage(sourceLanguageCode)
                .setTargetLanguage(targetLanguageCode)
                .build()

            //create a translator object with the target and source languages set
            translator = Translation.getClient(options)

            //get lifecycle to garbage collect when no longer in use
            lifecycle.addObserver(translator)

            //set up conditions for downloading positionInConversation models
            val conditions = DownloadConditions.Builder()
                .requireWifi()
                .build()

            //if text input is not empty
            if (!isEmpty(inputText.text)) {

                downloadModelIfNotOnDevice(sourceLanguageCode)
                downloadModelIfNotOnDevice(targetLanguageCode)

                //download the language models if they are not already downloaded - failsafe
                translator.downloadModelIfNeeded(conditions)
                    .addOnSuccessListener {
                        Log.i("TAG", "Downloaded model successfully")
                    }
                    .addOnFailureListener {
                        Log.e("TAG", "Model could not be downloaded")
                    }
                    //once models are downloaded
                    .continueWith { downloads ->

                        if (downloads.isSuccessful) {

                            //show toast message if it is the first item in a new conversation
                            if (translationItemList.size == 0) {
                                Toast.makeText(activity, "Translating", Toast.LENGTH_SHORT).show()
                            }

                            //translate the input text using the translator model that was just created
                            translator.translate(translatorFragmentBinding.textBox.text.toString())
                                .addOnSuccessListener { translatedText ->
                                    Log.i("TAG", "Translation is " + translatedText as String)

                                    //check if positionInConversation A (first item translated) or B
                                    setPositionInConversation()
                                    //add translation item into the conversation
                                    addTranslationItem(translatedText)
                                    //autoscroll to bottom of message list
                                    scrollToLastTranslationItemAdded()
                                    //clear the text box ready for new input
                                    clearInput()
                                }
                                .addOnFailureListener {
                                    Log.e("TAG", "Translation failed")
                                }
                        }
                    }
            }
            // no input text to translate
            else {
                Toast.makeText(activity, "No text to translate", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * Removes text from the text box
     */
    private fun clearInput() {
        inputText.text.clear()
    }

    /**
     * Scrolls the recycler view to the last item translated (bottom of screen)
     */
    private fun scrollToLastTranslationItemAdded() {
        translationItemRecyclerView.smoothScrollToPosition(
            translationItemList.size - 1
        )

    }

    /**
     * Inserts a translation item into the database
     * Makes object with an id, original text, translated text and the convo position (first/second)
     *
     * @param translatedText - the translated text to insert
     */
    private fun addTranslationItem(translatedText: String?) {
        val originalTranslationItem = inputText.text.toString()
        val translationItemObject = TranslationItem(
            0,
            originalTranslationItem,
            translatedText,
            positionInConversation
        )

        /* keep track of translation items prior to DB integration - used for finding size of
        item list to set position for scrolling behaviour at the moment - need to remove later
         */
        translationItemList.add(translationItemObject)

        /* Use the view model to add item. This then tells the adapter that a message has been
        added, so it can update the UI
         */
        translatorViewModel.addTranslationItemToConversation(
            translationItemObject
        )
    }

    /**
     * Sets the position of a translation item in a conversation
     * first (language A) = source language that was translated first in a conversation
     * second (language B) = the other language
     *
     */
    private fun setPositionInConversation() {
        when {
            /* initially empty conversation - set language of the first item that is translated to
            be language 'A' in the conversation
             */
            conversationAdapter.itemCount == 0 -> {
                languageA = sourceLanguage
                languageB = targetLanguage
                positionInConversation = PositionInConversation.FIRST
            }

            /* non-empty conversation and language being translated is the same as language A
            and target language is the as language B
             */
            (conversationAdapter.itemCount > 0) && (sourceLanguage == languageA)
                    && (targetLanguage == languageB) -> {

                positionInConversation =
                    PositionInConversation.FIRST
            }

            /* non-empty conversation and language being translated is the same as language B
            and target language is the as language A
             */
            (conversationAdapter.itemCount > 0) && (sourceLanguage == languageB)
                    && (targetLanguage == languageA) -> {

                positionInConversation =
                    PositionInConversation.SECOND
            }

            //new languages have been selected so restart the conversation
            else -> {
                //set new positionInConversation
                languageA = sourceLanguage
                languageB = targetLanguage
                positionInConversation = PositionInConversation.FIRST
                restartConversation()
            }
        }
    }


    /**
     * Sets a listener when the swap button is pressed
     * Swaps the source and target languages around
     * Updates languages displayed in the spinners
     * Updates active quick-switch language button
     */
    private fun setListenerForLanguageSwap() {
        swapButton.setOnClickListener {
            //get source language and put in temp target
            val tempTargetLanguage = sourceSpinner.selectedItemPosition
            //get target language and put in source spinner
            sourceSpinner.setSelection(targetSpinner.selectedItemPosition)
            //put temp target in target spinner
            targetSpinner.setSelection(tempTargetLanguage)

            //set source and target variables
            sourceLanguage = sourceSpinner.selectedItem.toString().uppercase()
            targetLanguage = targetSpinner.selectedItem.toString().uppercase()

            //update the active quick switch language button
            if (translationItemRecyclerView.isNotEmpty()) {
                swapButtonBackgroundColor(leftLanguageButton, rightLanguageButton)
            }

        }
    }

    /**
     * Swaps the background colours for quick switch buttons to indicate the source language
     *
     * @param buttonA - Language A in the conversation
     * @param buttonB - Language B in the conversation
     */
    private fun swapButtonBackgroundColor(buttonA: Button, buttonB: Button) {
        //button A is the currently active button
        if (buttonA.backgroundTintList == ContextCompat.getColorStateList(
                requireContext(),
                R.color.active_button
            )
        ) {
            //set button A to be unactive
            buttonA.backgroundTintList = ContextCompat.getColorStateList(
                requireContext(),
                R.color.unactive_button
            )
            //set button B to be active
            buttonB.backgroundTintList = ContextCompat.getColorStateList(
                requireContext(),
                R.color.active_button
            )
        }
        //button B is the currently active button
        else {
            //set button A to be active
            buttonA.backgroundTintList = ContextCompat.getColorStateList(
                requireContext(),
                R.color.active_button
            )
            //set button B to be unactive
            buttonB.backgroundTintList = ContextCompat.getColorStateList(
                requireContext(),
                R.color.unactive_button
            )
        }
    }

    /**
     * Sets up listeners for the quick switch buttons to handle logic-related reconfigurations
     *
     */
    private fun setListenerForActiveLanguageButton() {
        //when language A in the conversation is selected, set the button to be active
        leftLanguageButton.setOnClickListener {
            //set background colour of left button to active
            leftLanguageButton.backgroundTintList = ContextCompat.getColorStateList(
                requireContext(),
                R.color.active_button
            )

            //set background colour of other button to inactive
            rightLanguageButton.backgroundTintList = ContextCompat.getColorStateList(
                requireContext(),
                R.color.unactive_button
            )

            /* Similar functionality to the language swap functionality
            Source and target languages are swapped and spinners are updated
             */
            val tempTargetLanguage = sourceSpinner.selectedItemPosition
            //get target language and put in source spinner
            sourceSpinner.setSelection(targetSpinner.selectedItemPosition)
            //put temp target in target
            targetSpinner.setSelection(tempTargetLanguage)
            //update source and target language variables
            sourceLanguage = sourceSpinner.selectedItem.toString().uppercase()
            targetLanguage = targetSpinner.selectedItem.toString().uppercase()
        }

        //when language B in the conversation is selected, set the button to be active
        rightLanguageButton.setOnClickListener {
            leftLanguageButton.backgroundTintList = ContextCompat.getColorStateList(
                requireContext(),
                R.color.unactive_button
            )
            //set the other button to be unactive
            rightLanguageButton.backgroundTintList = ContextCompat.getColorStateList(
                requireContext(),
                R.color.active_button
            )

            //handle logic-related components as with the other button
            val tempTargetLanguage = sourceSpinner.selectedItemPosition
            sourceSpinner.setSelection(targetSpinner.selectedItemPosition)
            targetSpinner.setSelection(tempTargetLanguage)
            sourceLanguage = sourceSpinner.selectedItem.toString().uppercase()
            targetLanguage = targetSpinner.selectedItem.toString().uppercase()
        }
    }

    /**
     * Sets the text for the languages in the quick switch language buttons
     * Handles the case where language A and B are not initially set for the conversation
     * Also handles the case where language A and  B have been set (conversation has begun)
     * TODO: switch language in button before item is translated after selecting new language(s)
     */
    private fun setInitialButtonLanguages() {
        if (translationItemRecyclerView.isEmpty()) {
            leftLanguageButton.text = sourceSpinner.selectedItem.toString()
            rightLanguageButton.text = targetSpinner.selectedItem.toString()
        } else {
            leftLanguageButton.text = languageA
            rightLanguageButton.text = languageB
        }
    }


    /**
     * Uses Intent mechanism to update the input text field with speech data when it is provided
     * Speech recognition data is obtained through call to onActivityResult (asynchronous call)
     * StartActivityForResult contract - generic contract that takes an intent and
     *  returns an activityResult. extracts results code and intent - once speech has been provided
     */
    private fun captureSpeechData() {
        lateinit var speechData: ArrayList<Editable>
        //register for the results of the activity launched in speak()
        activityResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result: ActivityResult? ->
            //if data is available and not null, display data in edit text view
            if (result!!.resultCode == Activity.RESULT_OK && result.data != null) {
                speechData =
                    result.data!!.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                            //TODO: check this cast or use alternative mechanism
                            as ArrayList<Editable>
                //put the captured speech into the text box
                putTextDataInTextBox(speechData)
            }
        }
    }

    /**
     * Puts data captured from speech recording into the text box
     *
     * @param speechData - data captured from the speech recognition
     */
    private fun putTextDataInTextBox(speechData: ArrayList<Editable>) {
        //append speech data into the text box rather than overwriting it
        inputText.text = inputText.text.append(speechData[0])
    }

    /**
     * Initialise the UI elements using the view binding mechanism
     */
    private fun getUIElements() {
        translationItemRecyclerView = translatorFragmentBinding.conversationRecyclerView
        inputText = translatorFragmentBinding.textBox
        translateButton = translatorFragmentBinding.translateButton
        micFAB = translatorFragmentBinding.recordVoiceFAB
        sourceSpinner = translatorFragmentBinding.sourceLanguageSpinner
        targetSpinner = translatorFragmentBinding.targetLanguageSpinner
        swapButton = translatorFragmentBinding.swapLanguagesButton
        leftLanguageButton = translatorFragmentBinding.leftLanguageButton
        rightLanguageButton = translatorFragmentBinding.rightLanguageButton
    }

    /**
     * Set up the source and target language spinners using string array resources
     */
    private fun setupSpinners() {
        setupSpinner(
            view,
            translatorFragmentBinding.sourceLanguageSpinner,
            R.array.sourceLanguages
        )

        setupSpinner(
            view,
            translatorFragmentBinding.targetLanguageSpinner,
            R.array.targetLanguages
        )
    }


    /**
     * Use speech RecognizerIntent mechanism to get speech input from the user
     * TODO: An alternative would be to use SpeechRecognizer if time
     *
     * @param view
     */
    private fun speak(view: View): Boolean {
        //Starts an activity that will prompt the user for speech and send it through a speech recognizer.
        val speechRecognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        speechRecognizerIntent.putExtra(
            //Informs the recognizer which speech model to prefer when performing
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            //Use a language model based on free-form speech recognition.
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )
        //This tag informs the recognizer to perform speech recognition in a positionInConversation
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, sourceLanguageCode)
        //message to see in dialogue box when clicking speech button and waiting for speech input
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Please speak now")

        try {
            //launch activity results mechanism to perform an action using intent
            activityResultLauncher.launch(speechRecognizerIntent)
            // Toast.makeText(this, "Speech recognition available", Toast.LENGTH_SHORT).show()
        } catch (exp: ActivityNotFoundException) {

        }
        return true
    }

    /**
     * Create an instance of the repository when this fragment is created
     * Makes sure it is empty by deleting all contents when app is opened
     *
     * @param savedInstanceState
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        repository = ConverserRepository(requireActivity().application)
        repository.deleteAll()
        super.onCreate(savedInstanceState)
    }

    /**
     * Refreshes a conversation so that a new one can begin with new languages set
     * If the recycler view is not empty the the refresh dialog is displayed
     * Sets the text in teh quick switch buttons to be the new languages
     */
    private fun restartConversation() {
        //TODO: fix so that dialog buttons work and this is called before restarting conversation
        if (!translationItemRecyclerView.isEmpty()) {
            val restartDialog = ConfirmConversationRefreshDialogFragment()
            restartDialog.show(this.parentFragmentManager, "refresh dialog")
        }

        //TODO: fix to changes languages when new languages are selected in spinners
        setInitialButtonLanguages()

        //clear the translation items from the previous conversation
        translationItemList.clear()
        repository.deleteAll()

        //clean up the translator object if one was created
        if (this::translator.isInitialized) {
            translator.close()
        }
        //TODO: fix to use change dataset
        conversationAdapter.notifyDataSetChanged()
    }

    /**
     * Deletes all the language models on the device
     * TODO: use when downloaded languages feature is developed or in a button in the meantime
     *
     * @param models - all the language models that are downloaded
     */
    fun deleteAllModels(models: List<String>) {
        for (model in models) {
            deleteLanguage(model)
        }
    }

    //TODO: Handle text-to-speech
    /* private fun SetListenerForConvertTextToSpeechLangA(){
        conversationAdapter.langAclickListener = View.OnClickListener { view ->
            //do tts for lang a
            tts = TextToSpeech(requireContext(), TextToSpeech.OnInitListener {
                if (it == TextToSpeech.SUCCESS){
                    tts.language = Locale.ENGLISH
                    tts.speak()
                }
            })
        }
     }*/


    ////////////////////////////////////////////////////////


    /**
     * Restarts a conversation and deletes the database when app is closed
     * TODO: move out of here as this is called with configuration changes as well as app closure
     */
    override fun onDestroyView() {
        //deleteAllModels(downloadedModels)
        restartConversation()
        context?.deleteDatabase("converser_database")
        super.onDestroyView()
    }

    /**
     * Sets up an individual spinner to handle selection of languages
     *
     * @param view - the current view
     * @param spinner - The spinner being set up (source or target positionInConversation lists)
     * @param arrayResourceId - The array of languages stored as a strings array value
     */
    private fun setupSpinner(view: View?, spinner: Spinner, arrayResourceId: Int) {
        //default value first item in string array
        spinner.setSelection(1)
        //use predefined layout for spinner
        val adapter =
            ArrayAdapter.createFromResource(
                requireContext(), arrayResourceId,
                android.R.layout.simple_spinner_item
            )

        //use predefined layout for spinner dropdown list
        adapter.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )

        //assign adapter to spinner
        spinner.adapter = adapter

        //define behaviour when an item is selected
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                //set the language from the spinner selection
                setLanguageFromSpinnerSelection(spinner)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

        }
    }

    /**
     * Sets the language (source or target) to the selected item in a spinner
     *
     * @param spinner - the spinner to get language selection from
     */
    private fun setLanguageFromSpinnerSelection(spinner: Spinner) {
        if (spinner == sourceSpinner) {
            //get language selected in the spinners
            sourceLanguage = sourceSpinner.selectedItem.toString()
            sourceLanguageCode = translatorViewModel.setLanguageCode(sourceSpinner)
        } else if (spinner == targetSpinner) {
            targetLanguageCode = translatorViewModel.setLanguageCode(targetSpinner)
            targetLanguage = targetSpinner.selectedItem.toString()
        }

        //set the language text in the quick switch buttons
        setInitialButtonLanguages()
    }

    /**
     * Creates a dialog for downloading a language
     *
     */
    fun showNoticeDialog() {
        // Create an instance of the dialog fragment and show it
        val dialog = DownloadLanguageModelDialogFragment()
        dialog.show(this.parentFragmentManager, "download dialog")
    }


}
