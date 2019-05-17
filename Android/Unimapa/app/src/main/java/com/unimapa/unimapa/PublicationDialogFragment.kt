package com.unimapa.unimapa

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.support.v4.app.DialogFragment

/**
 * Created by flavio.matheus on 13/05/19.
 */
class PublicationDialogFragment : DialogFragment() {

    internal lateinit var listener: PublicationDialogListener

    interface PublicationDialogListener {
        fun onDialogPositiveClick(dialog: DialogFragment)
        fun onDialogNegativeClick(dialog: DialogFragment)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {


            val builder = AlertDialog.Builder(it)

            val inflater = requireActivity().layoutInflater

            val view = inflater.inflate(R.layout.publication_dialog, null)

            builder.setView(view)

            builder.setPositiveButton("Post", DialogInterface.OnClickListener{ dialog, id ->
                print("Post")
                listener.onDialogPositiveClick(this)
                dialog.dismiss()
            })

            builder.setNegativeButton("Cancel", DialogInterface.OnClickListener{dialog, id ->
                print("Cancel")
                listener.onDialogNegativeClick(this)
                dialog.cancel()
            })


            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            listener = context as PublicationDialogListener
        } catch (e: ClassCastException) {
            throw ClassCastException((context.toString() +
                    " must implement PublicationDialogListener"))
        }
    }
}