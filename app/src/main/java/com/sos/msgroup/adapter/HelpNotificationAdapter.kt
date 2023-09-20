package com.sos.msgroup.adapter

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sos.msgroup.R
import com.sos.msgroup.model.HelpNotification
import com.sos.msgroup.ui.MapsActivity
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*


class HelpNotificationAdapter(
    var context: Context,
    var helpNotificationList: ArrayList<HelpNotification>,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private inner class ViewHolder internal constructor(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        var name: TextView = itemView.findViewById(R.id.TvNames)
        var date: TextView = itemView.findViewById(R.id.TvDateTime)
        var image: ImageView = itemView.findViewById(R.id.ImgLocation)
        var imageIconStatus: ImageView = itemView.findViewById(R.id.ImgIconStatus)

        fun bind(position: Int) {

            val fmt: NumberFormat = NumberFormat.getCurrencyInstance()

            name.text =
                helpNotificationList[position].firstName + " " + helpNotificationList[position].lastName
            date.text = epochToIso(helpNotificationList[position].time.toLong())

            if (helpNotificationList[position].viewed) {
                imageIconStatus.visibility = View.GONE
            } else {
                imageIconStatus.visibility = View.VISIBLE
            }

            itemView.setOnClickListener { _ ->
                val intent = Intent(context, MapsActivity::class.java)
                intent.putExtra("helpRequested", helpNotificationList[position])
                context.startActivity(intent)
            }

        }
    }

    private fun epochToIso(dobInMiles: Long): String {
        return dobInMiles?.let {
            val sdf = SimpleDateFormat("d MMM, yyyy HH:mm:ss", Locale.getDefault())
            sdf.format(dobInMiles)
        } ?: "Not Found"
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context).inflate(R.layout.layout_request, parent, false)
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ViewHolder).bind(position)
    }

    override fun getItemCount(): Int = helpNotificationList.size
}