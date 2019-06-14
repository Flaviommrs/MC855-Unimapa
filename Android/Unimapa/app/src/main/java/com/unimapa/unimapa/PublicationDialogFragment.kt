package com.unimapa.unimapa

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.widget.TextView
import org.w3c.dom.Text
import android.widget.EditText
import android.widget.Spinner
import android.widget.ArrayAdapter
import com.unimapa.unimapa.dataBase.MapaDataBase

/**
 * Created by flavio.matheus on 13/05/19.
 */
class PublicationDialogFragment : DialogFragment() {

    internal lateinit var listener: PublicationDialogListener

    private var MDB: MapaDataBase? = null

    interface PublicationDialogListener {
        fun onDialogPositiveClick(dialog: DialogFragment, description: String, selectedMap: String, title: String, id: Int)
        fun onDialogNegativeClick(dialog: DialogFragment)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {

            MDB = MapaDataBase(this.context)

            var maps = MDB!!.data

            val builder = AlertDialog.Builder(it)

            val inflater = requireActivity().layoutInflater

            val view = inflater.inflate(R.layout.publication_dialog, null)

            val spinner = view.findViewById<Spinner>(R.id.mapa) as Spinner

            var arraySpinner = mutableListOf<String>()

            for(map in maps){
                arraySpinner.add(map.getName()!!)
            }

            val adapter = ArrayAdapter<String>(this.context, android.R.layout.simple_spinner_item, arraySpinner)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter

            builder.setView(view)

            builder.setPositiveButton("Post") { dialog, id ->
                print("Post")

                val title = (dialog as AlertDialog).findViewById<EditText>(R.id.title) as EditText
                val edit = (dialog as AlertDialog).findViewById<EditText>(R.id.descrição) as EditText
                val spinner = (dialog as AlertDialog).findViewById<Spinner>(R.id.mapa) as Spinner

                var id = -1

                for(map in maps){
                    if(map.getName().equals(spinner.selectedItem.toString())){
                        id = map.getId()!!
                    }
                }

                listener.onDialogPositiveClick(this, edit.text.toString(), spinner.selectedItem.toString(), title.text.toString(), id)
            }

            builder.setNegativeButton("Cancel", DialogInterface.OnClickListener{dialog, id ->
                print("Cancel")
                listener.onDialogNegativeClick(this)
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