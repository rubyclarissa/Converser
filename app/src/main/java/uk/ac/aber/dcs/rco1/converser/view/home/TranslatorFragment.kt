package uk.ac.aber.dcs.rco1.converser.view.home

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import uk.ac.aber.dcs.rco1.converser.R
import uk.ac.aber.dcs.rco1.converser.model.ConverserRepository
import uk.ac.aber.dcs.rco1.converser.databinding.FragmentTranslatorBinding
import uk.ac.aber.dcs.rco1.converser.model.home.PositionInConversation
import uk.ac.aber.dcs.rco1.converser.model.home.TranslationItem
import uk.ac.aber.dcs.rco1.converser.viewModel.TranslatorViewModel
import kotlin.collections.ArrayList

/**
 * TODO
 *
 */
class TranslatorFragment : Fragment() {

    private lateinit var homeFragmentBinding: FragmentTranslatorBinding

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

    //list of translations in a conversation
    private lateinit var translationItemList: ArrayList<TranslationItem>

    private var sourceLanguage: String = ""
    private var targetLanguage: String = ""
    private var stringToTranslate: String = ""

    //initialise positionInConversation codes to English by default
    var sourceLanguageCode = TranslateLanguage.ENGLISH
    var targetLanguageCode = TranslateLanguage.ENGLISH

    private lateinit var translator: Translator

    //used for identifying which positionInConversation is being translated
    private var positionInConversation: PositionInConversation = PositionInConversation.SECOND
    private lateinit var languageA: String
    private lateinit var languageB: String

    private val languageModelManager: RemoteModelManager = RemoteModelManager.getInstance()
    private var downloadedModels: List<String> = listOf("")

    //TODO: change later as dont want in home fragment
    private lateinit var repository: ConverserRepository

    //needed for dialogue support
    //private val supportFragmentManager = parentFragmentManager

    private val coroutineScope = CoroutineScope(Dispatchers.Default)

    private var oldConversationData: LiveData<List<TranslationItem>>? = null

    //uses proprety delegate from fragment ktx artefact instead of making an object
    val translatorViewModel: TranslatorViewModel by viewModels()

    /**
     * TODO
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
        homeFragmentBinding = FragmentTranslatorBinding.inflate(inflater, container, false)

        translationItemList = ArrayList()

        //set up language selection spinners
        setupSpinners()
        //get the UI elements via the binding mechanism
        getUIElements()
        //get speech data and put into the edit text box
        captureSpeechData()
        setUpConversationRecyclerAdapter()
        setUpTranslatorViewModel()

        if (translationItemRecyclerView.isEmpty()){
            homeFragmentBinding.quickLanguageSelections.isVisible = false
        }

        stringToTranslate = inputText.text.toString()

        //set up on click events
        setListenerForLanguageSwap()
        setListenerForTranslation()
        setListenerForSpeechRecording()
        setListenerForInitialActiveLanguageButton()

        repository = ConverserRepository(requireActivity().application)

        return homeFragmentBinding.root
    }


    private fun setUpTranslatorViewModel() {
        //viewmodel set up
        //ask view model for list of translator items
        val translationItems = translatorViewModel.translationItems
        //if it returns a new list object then remove observers associated with old one
        if (oldConversationData != translationItems) {
            oldConversationData?.removeObservers(viewLifecycleOwner)
            oldConversationData = translationItems
        }
        if (!translationItems.hasObservers()) {
            //viewLifecycleOwner instead of 'this' - inherited from superclass (only want to observe
            //items if there is a UI. could say 'this' but at some points the fragment want have UI
            //components e.g. before its built / after destroyed - will get exception thrown. viewlco
            //will only trigger observer if there is an UI - will always work
            translationItems.observe(viewLifecycleOwner) { translations ->
                conversationAdapter.changeDataSet(translations.toMutableList())
            }
        }
    }

    private fun setUpConversationRecyclerAdapter() {
        //find and configure conversation recycler view
        conversationAdapter = ConversationAdapter(requireContext())
        translationItemRecyclerView.adapter = conversationAdapter
        val conversationLayoutManager = LinearLayoutManager(context)
        //conversationLayoutManager.orientation = LinearLayoutManager.VERTICAL
        translationItemRecyclerView.layoutManager = conversationLayoutManager
    }

    private fun setListenerForSpeechRecording() {
        //support if user presses and holds
        micFAB.setOnLongClickListener {
            speak(homeFragmentBinding.recordVoice)
        }
        //support if user just presses
        micFAB.setOnClickListener {
            speak(homeFragmentBinding.recordVoice)
        }
    }

    //get model for a positionInConversation
    private fun getLanguageModel(languageCode: String): TranslateRemoteModel {
        return TranslateRemoteModel.Builder(languageCode).build()
    }

    // download a positionInConversation model
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

    // update list of downloaded models
    private fun getDownloadedModels() {
        languageModelManager.getDownloadedModels(TranslateRemoteModel::class.java)
            .addOnSuccessListener { models ->
                //TODO: sort alphabetically
                downloadedModels = models.map { it.language }
            }
    }

    // delete a model and update downloaded models
    private fun deleteLanguage(languageCode: String) {
        val model = getLanguageModel(languageCode)
        languageModelManager.deleteDownloadedModel(model)
            .addOnCompleteListener {
                getDownloadedModels()
            }
    }

    private fun downloadModelIfNotOnDevice(languageCode: String){
        getDownloadedModels()
        if (!downloadedModels.contains(languageCode)) {
            Log.i("TAG", "Downloaded models:" + downloadedModels)
            downloadLanguage(languageCode)
            getDownloadedModels()
        }
    }

    private fun setListenerForTranslation() {
        translateButton.setOnClickListener {

            if (!homeFragmentBinding.quickLanguageSelections.isVisible){
                    homeFragmentBinding.quickLanguageSelections.isVisible = true
            }
            //create translator object with configurations for source and target languages
            val options = TranslatorOptions.Builder()
                .setSourceLanguage(sourceLanguageCode)
                .setTargetLanguage(targetLanguageCode)
                .build()

            //create a translator object with the target and source languages set
            translator = Translation.getClient(options)
            lifecycle.addObserver(translator)

            //set up conditions for downloading positionInConversation models
            val conditions = DownloadConditions.Builder()
                .requireWifi()
                .build()

            //if text input is not empty
            if (!isEmpty(inputText.text)) {

                downloadModelIfNotOnDevice(sourceLanguageCode)
                downloadModelIfNotOnDevice(targetLanguageCode)


                //download the language models if they are not already downloaded
                translator.downloadModelIfNeeded(conditions)
                    .addOnSuccessListener {
                        Log.i("TAG", "Downloaded model successfully")
                    }
                    .addOnFailureListener {
                        Log.e("TAG", "Model could not be downloaded")
                    }
                    //put translation in here as it works if language model needs downloading
                    //once models are downloaded,
                    .continueWith { downloads ->

                        if (downloads.isSuccessful) {

                            if (translationItemList.size == 0){
                                Toast.makeText(activity, "Translating", Toast.LENGTH_SHORT).show()
                            }
                            //translate the input text using the translator model that was just created
                            translator.translate(homeFragmentBinding.textBox.text.toString())
                                .addOnSuccessListener { translatedText ->
                                    Log.i("TAG", "Translation is " + translatedText as String)

                                    //check if positionInConversation A (first item translated) or B
                                    setPositionInConversation()
                                    addTranslationItem(translatedText)
                                    //autoscroll to bottom of message list
                                    scrollToLastTranslationItemAdded()
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

    private fun clearInput() {
        inputText.text.clear()
    }

    private fun scrollToLastTranslationItemAdded() {
        if (translationItemRecyclerView.isEmpty()){
            translationItemRecyclerView.smoothScrollToPosition(
                translationItemList.size - 1
                //conversationAdapter.itemCount
            )
        } else{
            translationItemRecyclerView.smoothScrollToPosition(
                //translationItemList.size - 1
                translationItemList.size -1)
        }

    }

    private fun addTranslationItem(translatedText: String?) {
        //add a message to the message list (original and translated)
        val originalTranslationItem = inputText.text.toString()
        val translationItemObject = TranslationItem(
            0,
            originalTranslationItem,
            translatedText,
            positionInConversation
        )
        translationItemList.add(translationItemObject)
        // repository.insert(translationItemObject)

        //tell the adapter that a message has been added, so it can update the UI
        //TODO: use different mechanism to notify change
        // conversationAdapter.notifyDataSetChanged()
        translatorViewModel.addTranslationItemToConversation(
            translationItemObject
        )
    }

    //set position of translation item in conversation for conversation handling
    private fun setPositionInConversation() {
        when {
            //initial empty conversation - set language of the first item that is translated
            //to be language 'A' in the coverstaion - speech bubbles
            conversationAdapter.itemCount == 0 -> {
                languageA = sourceLanguage
                languageB = targetLanguage
                positionInConversation = PositionInConversation.FIRST
            }
            (conversationAdapter.itemCount > 0) && (sourceLanguage == languageA)
                    && (targetLanguage == languageB) -> {

                positionInConversation =
                    PositionInConversation.FIRST
            }

            (conversationAdapter.itemCount > 0) && (sourceLanguage == languageB)
                    && (targetLanguage == languageA) -> {

                positionInConversation =
                    PositionInConversation.SECOND
            }
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
     */
    private fun setListenerForLanguageSwap() {
        swapButton.setOnClickListener {
            //swap text in spinners
            //get source language and put in temp target
            val tempTargetLanguage = sourceSpinner.selectedItemPosition
            //get target language and put in source
            sourceSpinner.setSelection(targetSpinner.selectedItemPosition)
            //put temp target in target
            targetSpinner.setSelection(tempTargetLanguage)

            sourceLanguage = sourceSpinner.selectedItem.toString().uppercase()
            targetLanguage = targetSpinner.selectedItem.toString().uppercase()

            if  (translationItemRecyclerView.isNotEmpty()){
                swapButtonBackgroundColor(leftLanguageButton, rightLanguageButton)
            }

        }
    }

    private fun swapButtonBackgroundColor(buttonA: Button, buttonB: Button){
        if (buttonA.backgroundTintList == ContextCompat.getColorStateList(
                requireContext(),
                R.color.active_button)){
            buttonA.backgroundTintList = ContextCompat.getColorStateList(
                requireContext(),
                R.color.unactive_button)
            buttonB.backgroundTintList = ContextCompat.getColorStateList(
                requireContext(),
                R.color.active_button)
        }
       else {
            buttonA.backgroundTintList = ContextCompat.getColorStateList(
                requireContext(),
                R.color.active_button)
            buttonB.backgroundTintList = ContextCompat.getColorStateList(
                requireContext(),
                R.color.unactive_button)
        }
    }

    //when conversation is empty
    private fun setListenerForInitialActiveLanguageButton() {
        leftLanguageButton.setOnClickListener {
            //set background colour of left button to active
            leftLanguageButton.backgroundTintList = ContextCompat.getColorStateList(
                requireContext(),
                R.color.active_button
            )

            //set background colour of left button to inactive
            rightLanguageButton.backgroundTintList = ContextCompat.getColorStateList(
                requireContext(),
                R.color.unactive_button
            )

            //do simmilar to swap button
            //swap text in spinners
            //get source language and put in temp target
            val tempTargetLanguage = sourceSpinner.selectedItemPosition
            //get target language and put in source
            sourceSpinner.setSelection(targetSpinner.selectedItemPosition)
            //put temp target in target
            targetSpinner.setSelection(tempTargetLanguage)

            sourceLanguage = sourceSpinner.selectedItem.toString().uppercase()
            targetLanguage = targetSpinner.selectedItem.toString().uppercase()
        }

        rightLanguageButton.setOnClickListener {
            leftLanguageButton.backgroundTintList = ContextCompat.getColorStateList(
                requireContext(),
                R.color.unactive_button
            )

            rightLanguageButton.backgroundTintList = ContextCompat.getColorStateList(
                requireContext(),
                R.color.active_button
            )

            //do simmilar to swap button
            //swap text in spinners
            //get source language and put in temp target
            val tempTargetLanguage = sourceSpinner.selectedItemPosition
            //get target language and put in source
            sourceSpinner.setSelection(targetSpinner.selectedItemPosition)
            //put temp target in target
            targetSpinner.setSelection(tempTargetLanguage)

            sourceLanguage = sourceSpinner.selectedItem.toString().uppercase()
            targetLanguage = targetSpinner.selectedItem.toString().uppercase()
        }
    }

    //when conversation is empty
    //fix to switch language in button before item is translated after selecting new language(s)
    private fun setInitialButtonLanguages() {
        if  (translationItemRecyclerView.isEmpty()) {
            leftLanguageButton.text = sourceSpinner.selectedItem.toString()
            rightLanguageButton.text = targetSpinner.selectedItem.toString()
        } else{
            leftLanguageButton.text = languageA
            rightLanguageButton.text = languageB
        }
    }

    //when conversation has begun
    private fun setActiveConversationButtonLanguages(){

    }

    //when conversation has begun
    private fun setListenerForActiveLanguageButton() {
        leftLanguageButton.setOnClickListener {
            //set background colour of left button to active
            leftLanguageButton.backgroundTintList = ContextCompat.getColorStateList(
                requireContext(),
                R.color.active_button
            )

            //set background colour of left button to inactive
            rightLanguageButton.backgroundTintList = ContextCompat.getColorStateList(
                requireContext(),
                R.color.unactive_button
            )

            //do simmilar to swap button
        }

        rightLanguageButton.setOnClickListener {
            leftLanguageButton.backgroundTintList = ContextCompat.getColorStateList(
                requireContext(),
                R.color.unactive_button
            )

            rightLanguageButton.backgroundTintList = ContextCompat.getColorStateList(
                requireContext(),
                R.color.active_button
            )

            //do simmilar to swap button
        }
    }


    /**
     * Uses Intent mechanism to update the input text field with speech data when it is provided
     *
     */
    private fun captureSpeechData(){
        lateinit var speechData: ArrayList<Editable>
        activityResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result: ActivityResult? ->
            //if data is available, display data in edit text view
            if (result!!.resultCode == Activity.RESULT_OK && result.data != null) {
                speechData =
                    result.data!!.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                            //TODO: check the cast or use alternative mechanism
                            as ArrayList<Editable>
               // inputText.text = speechData[0]
                putTextDataInTextBox(speechData)
            }
        }
    }

    private fun putTextDataInTextBox(speechData: ArrayList<Editable>){
        inputText.text = inputText.text.append(speechData[0])
    }

    /**
     * Initialise the UI elements using the binding mechanism
     *
     */
    private fun getUIElements() {
        translationItemRecyclerView = homeFragmentBinding.conversationRecyclerView
        inputText = homeFragmentBinding.textBox
        translateButton = homeFragmentBinding.translateButton
        micFAB = homeFragmentBinding.recordVoice
        sourceSpinner = homeFragmentBinding.sourceLanguageSpinner
        targetSpinner = homeFragmentBinding.targetLanguageSpinner
        swapButton = homeFragmentBinding.swapLanguages
        leftLanguageButton = homeFragmentBinding.leftLanguageButton
        rightLanguageButton = homeFragmentBinding.rightLanguageButton
    }

    /**
     * Set up the source and target positionInConversation spinners using appropriate resources
     *
     */
    private fun setupSpinners() {
        setupSpinner(
            view,
            homeFragmentBinding.sourceLanguageSpinner,
            R.array.sourceLanguages
        )

        setupSpinner(
            view,
            homeFragmentBinding.targetLanguageSpinner,
            R.array.targetLanguages
        )
    }


    /**
     * Use speech recognizer intent mechanism to get speech input from the user
     * An alternative would be to use Speech Recognizer
     *
     * @param view
     */
    private fun speak(view: View): Boolean {
        //Starts an activity that will prompt the user for speech and send it through a speech recognizer.
        val speechRecognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        speechRecognizerIntent.putExtra(
            //Informs the recognizer which speech model to prefer when performing
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            //Use a positionInConversation model based on free-form speech recognition.
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


    override fun onCreate(savedInstanceState: Bundle?) {
        repository = ConverserRepository(requireActivity().application)
        repository.deleteAll()
        super.onCreate(savedInstanceState)
    }


    private fun restartConversation() {
        val restartDialog = ConfirmConversationRefreshDialog()
        restartDialog.show(this.parentFragmentManager, "refresh dialog")
        //TODO: fix to changes languages when new ones selected instead of rhis
        setInitialButtonLanguages()
        translationItemList.clear()
        repository.deleteAll()
        //TODO: move somewhere else and check if not in downloaded list
       // deleteLanguage(sourceLanguageCode)
        //deleteLanguage(targetLanguageCode)
        translator.close()
        conversationAdapter.notifyDataSetChanged()
    }

    fun deleteAllModels(models: List<String>){
        for (model in models){
            deleteLanguage(model)
        }
    }

    ////////////////////////////////////////////////////////

    //TODO: fix so that conversation is deleted before closing app instead of in ocreate
    override fun onDestroyView() {
        deleteAllModels(downloadedModels)
        restartConversation()
        //delete database rather than just its contents - does this get recreated upon refresh
        //or just restart of the app?
        context?.deleteDatabase("converser_database")
        super.onDestroyView()
    }

    /**
     * Set up an individual spinner to provide a selection of languages
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
                setLanguageFromSpinnerSelection(spinner)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

        }
    }


    private fun setLanguageFromSpinnerSelection(spinner: Spinner) {
        if (spinner == sourceSpinner) {
            //get language selected in the spinners
            sourceLanguage = sourceSpinner.selectedItem.toString()
            sourceLanguageCode = translatorViewModel.setLanguageCode(sourceSpinner)
        } else if (spinner == targetSpinner) {
            targetLanguageCode = translatorViewModel.setLanguageCode(targetSpinner)
            targetLanguage = targetSpinner.selectedItem.toString()
        }
        //if (translationItemRecyclerView.isEmpty()){
            setInitialButtonLanguages()
        //}
    }

    fun showNoticeDialog() {
        // Create an instance of the dialog fragment and show it
        val dialog = DownloadLanguageModelDialogFragment()
        dialog.show(this.parentFragmentManager, "download dialog")
    }

}
