package uk.ac.aber.dcs.rco1.converser.view.home

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import uk.ac.aber.dcs.rco1.converser.R
import java.lang.IllegalStateException

class DownloadLanguageModelDialogFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater
            builder.setView(inflater.inflate(R.layout.fragment_downloading_language_model_dialog, null))
            isCancelable = false
            builder.create()
        } ?:throw IllegalStateException("Activity cannot be null")
    }

}