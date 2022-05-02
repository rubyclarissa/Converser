package uk.ac.aber.dcs.rco1.converser.view.home

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import uk.ac.aber.dcs.rco1.converser.R
import uk.ac.aber.dcs.rco1.converser.model.home.PositionInConversation
import uk.ac.aber.dcs.rco1.converser.model.home.TranslationItem

//constants for assigning the 2 different types of view in a conversation
const val FIRST_LANGUAGE_IN_CONVERSATION = 1
const val SECOND_LANGUAGE_IN_CONVERSATION = 2

/**
 * Adapter class for the recycler view mechanism in the translator fragment
 *
 * @property context - the context associated with the translator fragment that gets passed in
 *
 * @author Ruby Osborne (rco1)
 * @version 1.0 (release)
 */
class ConversationAdapter(
    val context: Context,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var translationItemList: List<TranslationItem> = listOf()

    //click listeners to wire up the TTS buttons
    var langAclickListener: View.OnClickListener? = null
    var langBclickListener: View.OnClickListener? = null


    /**
     * View holder class language A translation items (first language translated in a conversation)
     *
     * @constructor - sets the click listener for the TTS button
     *
     * @param itemView - the view for a language A translation item (first language)
     */
    inner class LanguageAViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val originalTranslationItemA: TextView =
            itemView.findViewById(R.id.language_A_original_translation_item)
        val translatedTranslationItemA: TextView =
            itemView.findViewById(R.id.language_A_translated_translation_item)
        val ttsButton: ImageButton = itemView.findViewById(R.id.text_to_speech_button)

        init {
            ttsButton.setOnClickListener(langAclickListener)
        }
    }

    /**
     * View holder class language B translation items (second language translated in a conversation)
     *
     * @constructor - sets the click listener for the TTS button
     *
     * @param itemView - the view for a language B translation item (second language)
     */
    inner class LanguageBViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val originalTranslationItemB: TextView =
            itemView.findViewById(R.id.language_B_original_translation_item)
        val translatedTranslationItemB: TextView =
            itemView.findViewById(R.id.language_B_translated_translation_item)
        val ttsButton: ImageButton = itemView.findViewById(R.id.text_to_speech_button)

        init {
            ttsButton.setOnClickListener(langBclickListener)
        }
    }

    /**
     * Gets the correct type of item view for a translation based on its position in a conversation
     *
     * @param position - the position of the current item in the list of translation items
     * @return the constant value - either the first or second language in a conversation
     */
    override fun getItemViewType(position: Int): Int {
        //get the item from list of items
        val currentTranslationItem = translationItemList[position]
        //return the number corresponding whether the item is first or second in the conversation
        return if (currentTranslationItem.language == PositionInConversation.FIRST) {
            FIRST_LANGUAGE_IN_CONVERSATION
        } else {
            SECOND_LANGUAGE_IN_CONVERSATION
        }
    }

    /**
     * Called when a view holder is created for a translation item
     *
     * @param parent
     * @param viewType - 1 or 2 depending on the items position in the conversation
     * @return a view holder for the corresponding view type with a layout passed as a parameter
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        //if language A message
        return if (viewType == 1) {
            //create layout for language A
            val view: View = LayoutInflater.from(context).inflate(
                R.layout.translated_translation_item_first_language,
                parent,
                false
            )
            //return a view holder for language A
            LanguageAViewHolder(view)
        } else {
            //create layout for language B
            val view: View = LayoutInflater.from(context).inflate(
                R.layout.translated_translation_item_second_language,
                parent,
                false
            )
            //return a view holder for language B
            LanguageBViewHolder(view)
        }
    }

    /**
     * Called when an item is to be displayed in the recycler view
     *
     * @param holder - the view holder for the item
     * @param position - the position of the item in the list of translation items
     */
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        //get the translation item to bind
        val currentTranslationItem = translationItemList[position]

        //do stuff for language A view holder
        if (holder.javaClass == LanguageAViewHolder::class.java) {
            val viewHolder = holder as LanguageAViewHolder
            //set the original and translated text
            viewHolder.originalTranslationItemA.text =
                currentTranslationItem.originalTranslationItem
            viewHolder.translatedTranslationItemA.text =
                currentTranslationItem.translatedTranslationItem
        }
        //do staff for language B view holder
        else {
            val viewHolder = holder as LanguageBViewHolder
            //set the original and translated text
            viewHolder.originalTranslationItemB.text =
                currentTranslationItem.originalTranslationItem
            viewHolder.translatedTranslationItemB.text =
                currentTranslationItem.translatedTranslationItem
        }
    }

    /**
     * Gets the total number of translation items in a conversation
     *
     * @return the number of translation items
     */
    override fun getItemCount(): Int {
        return translationItemList.size
    }

    /**
     * Updates the list of translation items
     *
     * @param dataSet - the new list of translation items
     */
    fun changeDataSet(dataSet: List<TranslationItem>) {
        this.translationItemList = dataSet
        notifyDataSetChanged()
    }

}