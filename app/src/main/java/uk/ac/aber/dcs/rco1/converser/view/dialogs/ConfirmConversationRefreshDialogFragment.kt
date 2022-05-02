package uk.ac.aber.dcs.rco1.converser.view.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import java.lang.IllegalStateException

/**
 * A dialog for refreshing a conversation (to be called when a new conversation is started)
 *
 * @author Ruby Osborne (rco1)
 * @version 1.0 (release)
 *
 */
class ConfirmConversationRefreshDialogFragment : DialogFragment() {

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
            builder.setTitle("Refresh Conversation?")
                .setMessage("You will lose this conversation forever. Do you wish to continue?")
                .setPositiveButton("OK", DialogInterface.OnClickListener { dialog, id ->
                    //TODO: handle clicked refresh
                    //TODO: handle refresh convo
                })
                .setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, id ->
                    //TODO: handle clicked cancel
                    //TODO: handle cancel refresh
                })
            //stop user from being able to tap on the background and dismiss the dialog
            isCancelable = false
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

}