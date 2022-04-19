package uk.ac.aber.dcs.rco1.converser.view.downloadLanguages

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import uk.ac.aber.dcs.rco1.converser.R

class DownloadLanguagesAdapter(
    val context: Context,
    val languages: MutableList<String>
): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val language: TextView = itemView.findViewById<TextView>(R.id.language_text_view)
        val downloadButton: ImageButton = itemView.findViewById<ImageButton>(R.id.download_button)
        var isDownloaded: Boolean = false
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view: View = LayoutInflater.from(context).inflate(R.layout.language_list_item, parent,
            false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val langItem = languages[position]
        val viewHolder = holder as ViewHolder
        viewHolder.language.text = langItem

        //change button image to downloaded/non downloaded when clicked
        //TODO: fix as always set to false.
        // create click listener val and set button click listener in view holder class instead
        // then handle in fragment / viewmodel for fragement
        viewHolder.downloadButton.setOnClickListener {
            if (viewHolder.isDownloaded){
                viewHolder.downloadButton.setImageResource(R.drawable.ic_baseline_download_24)
                viewHolder.isDownloaded = false
            }
            if(!viewHolder.isDownloaded){
                viewHolder.downloadButton.setImageResource(R.drawable.ic_baseline_download_done_24)
                viewHolder.isDownloaded = true
            }
        }
    }

    override fun getItemCount(): Int {
        return languages.size
    }


}