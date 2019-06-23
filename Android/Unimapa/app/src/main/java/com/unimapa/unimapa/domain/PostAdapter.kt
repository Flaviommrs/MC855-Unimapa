package com.unimapa.unimapa.domain

import android.content.Context
import android.graphics.drawable.Drawable
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView

import com.unimapa.unimapa.R

class PostAdapter(private var context: Context, private var postList: ArrayList<Post>) : BaseAdapter() {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        lateinit var holder : ViewHolder
        lateinit var view: View

        if(convertView == null) {
            holder = ViewHolder()
            var inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

            view = inflater.inflate(R.layout.post_item, null, true)

            holder.title = view.findViewById(R.id.title)
            holder.message = view.findViewById(R.id.message)

            view.tag = holder
        } else {
            view = convertView
            holder = view.tag as ViewHolder
        }

        holder.title.text = postList[position].getTitle()
        holder.message.text = postList[position].getMessage()

        return view
    }

    override fun getItem(p0: Int): Any {
        return postList[p0]
    }

    override fun getItemId(p0: Int): Long {
        return 0
    }

    override fun getCount(): Int {
        return postList.size
    }

    private class ViewHolder {

        lateinit var title: TextView
        lateinit var message: TextView
        lateinit var icon: ImageView
    }

}