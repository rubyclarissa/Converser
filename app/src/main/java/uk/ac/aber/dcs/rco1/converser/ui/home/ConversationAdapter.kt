package uk.ac.aber.dcs.rco1.converser.ui.home

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import uk.ac.aber.dcs.rco1.converser.R

/**
 * TODO
 *
 * @property context
 * @property messageList
 */
class ConversationAdapter(
    val context: Context,
    val messageList: ArrayList<Message>,
): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var clickListener: View.OnClickListener? = null
    private val A = 1
    private val B = 2

    /**
     * TODO
     *
     * @constructor
     * TODO
     *
     * @param itemView
     */
    inner class LanguageAViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val originalMessageA: TextView = itemView.findViewById<TextView>(R.id.languageAOriginalMessage)
        val translatedMessageA: TextView = itemView.findViewById<TextView>(R.id.languageATranslatedMessage)
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
        val originalMessageB = itemView.findViewById<TextView>(R.id.languageBOriginalMessage)
        val translatedlMessageB = itemView.findViewById<TextView>(R.id.languageBTranslatedMessage)
    }

    /**
     * TODO
     *
     * @param position
     * @return
     */
    override fun getItemViewType(position: Int): Int {
        val currentMessage = messageList[position]

        return if (currentMessage.language == 'A'){
            A
        } else{
            B
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
                R.layout.translated_message_item_first_language,
                parent,
                false)
            return LanguageAViewHolder(view)
        } else{
            //inflate language b
            val view: View = LayoutInflater.from(context).inflate(
                R.layout.translated_message_item_second_language,
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

        val currentMessage = messageList[position]

        if (holder.javaClass == LanguageAViewHolder::class.java){
            //do stuff for language a view holder

            val viewHolder = holder as LanguageAViewHolder
            holder.originalMessageA.text = currentMessage.originalMessage
            holder.translatedMessageA.text = currentMessage.translatedMessage

        } else{
            //dostaff for language b view holder
            val viewHolder = holder as LanguageBViewHolder
            holder.originalMessageB.text = currentMessage.originalMessage
            holder.translatedlMessageB.text = currentMessage.translatedMessage
        }
    }

    /**
     * TODO
     *
     * @return
     */
    override fun getItemCount(): Int {
       return messageList.size
    }

}