package uk.ac.aber.dcs.rco1.converser.view.home

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import uk.ac.aber.dcs.rco1.converser.R
import java.lang.IllegalStateException

class ConfirmConversationRefreshDialog: DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater
            builder.setTitle("Refresh Conversation?")
            .setMessage("You will lose this conversation forever. Do you wish to continue?")
                .setPositiveButton("OK", DialogInterface.OnClickListener{dialog, id ->
                    //clicked refresh
                    //refresh convo
                })
                .setNegativeButton("Cancel", DialogInterface.OnClickListener { dialogInterface, id ->
                    //clicked cancel
                    //cancel refresh
                })

            isCancelable = false
            builder.create()
        } ?:throw IllegalStateException("Activity cannot be null")
    }

}