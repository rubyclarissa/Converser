package uk.ac.aber.dcs.rco1.converser.view.downloadLanguages

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import uk.ac.aber.dcs.rco1.converser.R
import uk.ac.aber.dcs.rco1.converser.databinding.FragmentDownloadLanguagesBinding

/**
 * Screen to download and delete languages for offline use
 *
 * @author Ruby Osborne (rco1)
 * @version 1.0 (release)
 */
class DownloadLanguagesFragment : Fragment() {

    private lateinit var downloadLanguagesFragmentBinding: FragmentDownloadLanguagesBinding

    /**
     * Creates a view for this fragment
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

        downloadLanguagesFragmentBinding =
            FragmentDownloadLanguagesBinding.inflate(inflater, container, false)

        //get the recycler view
        val languagesRecyclerView: RecyclerView =
            downloadLanguagesFragmentBinding.languagesRecyclerView
        //get the list of languages to display in recycler view
        val languagesList = resources.getStringArray(R.array.allLanguages)
        val languages: MutableList<String> = languagesList.toMutableList()
        //set up the adapter for the recycler view  mechanism
        val languagesAdapter = DownloadLanguagesAdapter(requireContext(), languages)
        languagesRecyclerView.adapter = languagesAdapter
        //set the layout for the recycler view to be a vertical list
        val languagesLayoutManager = LinearLayoutManager(context)
        languagesRecyclerView.layoutManager = languagesLayoutManager

        return downloadLanguagesFragmentBinding.root
    }

    /* override fun onActivityCreated(savedInstanceState: Bundle?) {
         super.onActivityCreated(savedInstanceState)
         viewModel = ViewModelProvider(this)[DownloadLanguagesViewModel::class.java]
         // TODO: Use a ViewModel class to handle logic
     }*/

}