package uk.ac.aber.dcs.rco1.converser.view.downloadLanguages

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ListView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import uk.ac.aber.dcs.rco1.converser.R

class DownloadLanguagesAdapter(
    val context: Context,
    val languages: MutableList<String>
): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val language: TextView = itemView.findViewById<TextView>(R.id.languageTextView)
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
    }

    override fun getItemCount(): Int {
        return languages.size
    }


}