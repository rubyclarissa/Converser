package uk.ac.aber.dcs.rco1.converser.view.downloadLanguages

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import uk.ac.aber.dcs.rco1.converser.R
import uk.ac.aber.dcs.rco1.converser.databinding.FragmentDownloadLanguagesBinding

class DownloadLanguagesFragment : Fragment() {

    /*companion object {
        fun newInstance() = DownloadLanguagesFragment()
    }*/

    private lateinit var downloadLanguagesFragmentBinding: FragmentDownloadLanguagesBinding


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        downloadLanguagesFragmentBinding = FragmentDownloadLanguagesBinding.inflate(inflater, container, false)

        // private lateinit var viewModel: DownloadLanguagesViewModel
        var languagesRecyclerView: RecyclerView = downloadLanguagesFragmentBinding.languagesRecyclerView

        val languagesList = resources.getStringArray(R.array.allLanguages)
        val languages: MutableList<String> = languagesList.toMutableList()

        val languagesAdapter = DownloadLanguagesAdapter(requireContext(), languages)
        languagesRecyclerView.adapter = languagesAdapter
        val languagesLayoutManager = LinearLayoutManager(context)
        languagesRecyclerView.layoutManager = languagesLayoutManager


       // return inflater.inflate(R.layout.fragment_download_languages, container, false)
        return downloadLanguagesFragmentBinding.root
    }

   /* override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this)[DownloadLanguagesViewModel::class.java]
        // TODO: Use the ViewModel
    }*/

}