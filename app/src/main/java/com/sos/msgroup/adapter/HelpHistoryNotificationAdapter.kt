package com.sos.msgroup.adapter
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sos.msgroup.R
import com.sos.msgroup.model.HelpNotification
import com.sos.msgroup.ui.MapsActivity
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*


class HelpHistoryNotificationAdapter(var context: Context, var helpNotificationList: ArrayList<HelpNotification>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private inner class ViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        var name: TextView = itemView.findViewById(R.id.tvHistoryComment)
        var date: TextView = itemView.findViewById(R.id.tvHistoryDateTime)

        fun bind(position: Int) {

            val fmt: NumberFormat = NumberFormat.getCurrencyInstance()

            name.text = helpNotificationList[position].comment
            date.text = epochToIso(helpNotificationList[position].time.toLong())


            itemView.setOnClickListener { _ ->
                val intent = Intent(context, MapsActivity::class.java)
                intent.putExtra("helpRequested", helpNotificationList[position])
                context.startActivity(intent)
            }

        }
    }

    private fun epochToIso(dobInMiles: Long): String {
        return dobInMiles.let {
            val sdf = SimpleDateFormat("d MMM, yyyy HH:mm:ss", Locale.getDefault())
            sdf.format(dobInMiles)
        } ?: "Not Found"
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context).inflate(R.layout.layout_request_history, parent, false)
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ViewHolder).bind(position)
    }

    override fun getItemCount(): Int = helpNotificationList.size
}