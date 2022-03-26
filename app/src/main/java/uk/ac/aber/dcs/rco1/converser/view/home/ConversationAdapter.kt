package uk.ac.aber.dcs.rco1.converser.view.home

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import uk.ac.aber.dcs.rco1.converser.R
import uk.ac.aber.dcs.rco1.converser.model.home.PositionInConversation
import uk.ac.aber.dcs.rco1.converser.model.home.TranslationItem

/**
 * TODO
 *
 * @property context
 * @property translationItemList
 */

const val FIRST_LANGUAGE_IN_COVERSATION = 1
const val SECOND_LANGUAGE_IN_COVERSATION = 2

class ConversationAdapter(
    val context: Context,
    val translationItemList: ArrayList<TranslationItem>,
): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var clickListener: View.OnClickListener? = null

    /**
     * TODO
     *
     * @constructor
     * TODO
     *
     * @param itemView
     */
    inner class LanguageAViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val originalTranslationItemA: TextView = itemView.findViewById<TextView>(R.id.languageAOriginalTranslationItem)
        val translatedTranslationItemA: TextView = itemView.findViewById<TextView>(R.id.languageATranslatedTranslationItem)
    }

    /**
     * TODO
     *
     * @constructor
     * TODO
     *
     * @param itemView
     */
    inner class LanguageBViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val originalTranslationItemB = itemView.findViewById<TextView>(R.id.languageBOriginalTranslationItem)
        val translatedTranslationItemB = itemView.findViewById<TextView>(R.id.languageBTranslatedTranslationItem)
    }

    /**
     * TODO
     *
     * @param position
     * @return
     */
    override fun getItemViewType(position: Int): Int {
        val currentTranslationItem = translationItemList[position]

        return if (currentTranslationItem.language == PositionInConversation.FIRST){
            FIRST_LANGUAGE_IN_COVERSATION
        } else{
            SECOND_LANGUAGE_IN_COVERSATION
        }
    }

    /**
     * TODO
     *
     * @param parent
     * @param viewType
     * @return
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        //if language A message
        if (viewType == 1){
            //inflate language a
            val view: View = LayoutInflater.from(context).inflate(
                R.layout.translated_translation_item_first_language,
                parent,
                false)
            return LanguageAViewHolder(view)
        } else{
            //inflate language b
            val view: View = LayoutInflater.from(context).inflate(
                R.layout.translated_translation_item_second_language,
                parent,
                false)
            return LanguageBViewHolder(view)
        }
    }

    /**
     * TODO
     *
     * @param holder
     * @param position
     */
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        val currentTranslationItem = translationItemList[position]

        if (holder.javaClass == LanguageAViewHolder::class.java){
            //do stuff for language a view holder

            val viewHolder = holder as LanguageAViewHolder
            holder.originalTranslationItemA.text = currentTranslationItem.originalTranslationItem
            holder.translatedTranslationItemA.text = currentTranslationItem.translatedTranslationItem

        } else{
            //dostaff for language b view holder
            val viewHolder = holder as LanguageBViewHolder
            holder.originalTranslationItemB.text = currentTranslationItem.originalTranslationItem
            holder.translatedTranslationItemB.text = currentTranslationItem.translatedTranslationItem
        }
    }

    /**
     * TODO
     *
     * @return
     */
    override fun getItemCount(): Int {
       return translationItemList.size
    }

}