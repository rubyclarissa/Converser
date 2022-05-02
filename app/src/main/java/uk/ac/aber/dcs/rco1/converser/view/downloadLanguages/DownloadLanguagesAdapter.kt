package uk.ac.aber.dcs.rco1.converser.view.downloadLanguages

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import uk.ac.aber.dcs.rco1.converser.R

/**
 * Adapter class for the recycler view mechanism for the downloaded languages screen
 *
 * @property context - the context associated with the download languages fragment passed in
 * @property languages -  the list of languages available for downloading
 *
 * @author Ruby Osborne (rco1)
 * @version 1.0 (release)
 */
class DownloadLanguagesAdapter(
    val context: Context,
    val languages: MutableList<String>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    /**
     * View holder class required by the recycler mechanism
     * Sets the string of text, download button image and is default to be not downloaded
     *
     * @param itemView - the view for a language list item
     */
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val language: TextView = itemView.findViewById(R.id.language_text_view)
        val downloadButton: ImageButton = itemView.findViewById(R.id.download_button)
        var isDownloaded: Boolean = false
    }

    /**
     * Called when a view holder is created for a language item
     *
     * @param parent
     * @param viewType - the  type of view, which is always a list item view
     * @return a view holder with a view layout passed as a parameter
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view: View = LayoutInflater.from(context).inflate(
            R.layout.language_list_item, parent,
            false
        )
        return ViewHolder(view)
    }

    /**
     * Called when an item is to be displayed in the recycler view
     *
     * @param holder - the view holder for the item
     * @param position - the position of the item in the list of languages
     */
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        //get the language item to display
        val langItem = languages[position]
        //set the view holder for the item
        val viewHolder = holder as ViewHolder
        //set the text for the language
        viewHolder.language.text = langItem


        /*TODO: fix as always set to false
         create click listener val and set button click listener in view holder class instead
         then handle in fragment / view model for fragment
    viewHolder.downloadButton.setOnClickListener {
        if (viewHolder.isDownloaded){
            viewHolder.downloadButton.setImageResource(R.drawable.ic_baseline_download_24)
            viewHolder.isDownloaded = false
        }
        if(!viewHolder.isDownloaded){
            viewHolder.downloadButton.setImageResource(R.drawable.ic_baseline_download_done_24)
            viewHolder.isDownloaded = true
        }
    }*/
    }

    /**
     * Gets the number of languages in the list of languages available for download
     *
     * @return the number of languages
     */
    override fun getItemCount(): Int {
        return languages.size
    }


}