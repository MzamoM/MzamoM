package com.sos.msgroup.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sos.msgroup.EditUserActivity
import com.sos.msgroup.R
import com.sos.msgroup.model.User

class UserAdapter(var context: Context, var usersList: ArrayList<User>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private inner class ViewHolder internal constructor(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        var textViewUserFullName: TextView = itemView.findViewById(R.id.tv_UserFullName)
        var imageViewStoreLogo: ImageView = itemView.findViewById(R.id.img_UserImage)

        fun bind(position: Int) {

            if (usersList[position].firstName != null && usersList[position].firstName!!.isNotEmpty()) {
                textViewUserFullName.text = usersList[position].firstName + " "+usersList[position].lastName
            }

            itemView.setOnClickListener { _ ->
                val intent = Intent(context, EditUserActivity::class.java)
                intent.putExtra("user_data", usersList[position])
                context.startActivity(intent)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context).inflate(R.layout.layout_users, parent, false)
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ViewHolder).bind(position)
    }

    override fun getItemCount(): Int {
        return usersList.size
    }
}