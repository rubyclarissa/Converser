package uk.ac.aber.dcs.rco1.converser.view.home

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.fragment.app.DialogFragment
import uk.ac.aber.dcs.rco1.converser.R
import java.lang.IllegalStateException

class DownloadLanguageModelDialogFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater
            builder.setView(inflater.inflate(R.layout.fragment_downloading_language_model_dialog, null))
            builder.setCancelable(false)
            builder.create()
        } ?:throw IllegalStateException("Activity cannot be null")
    }
}