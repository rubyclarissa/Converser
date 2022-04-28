package uk.ac.aber.dcs.rco1.converser.view.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import uk.ac.aber.dcs.rco1.converser.R
import java.lang.IllegalStateException

/**
 * A dialog for downloading a language model onto the device
 * Uses a custom layout
 *
 */
class DownloadLanguageModelDialogFragment : DialogFragment() {

    /**
     * Called when the dialog is created
     *
     * @param savedInstanceState
     * @return the fragment activity for the dialog
     */
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            //create an alert dialog
            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater
            //use the custom layout as the view
            builder.setView(inflater.inflate(R.layout.fragment_downloading_language_model_dialog, null))
            //stop user from being able to tap on the background and dismiss the dialog
            isCancelable = false
            builder.create()
        } ?:throw IllegalStateException("Activity cannot be null")
    }

}