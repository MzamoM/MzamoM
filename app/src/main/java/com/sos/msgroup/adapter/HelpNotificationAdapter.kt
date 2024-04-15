package com.sos.msgroup.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.PopupWindow
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sos.msgroup.R
import com.sos.msgroup.model.HelpNotification
import com.sos.msgroup.ui.MapsActivity
import com.squareup.picasso.Picasso
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



            if (helpNotificationList[position].userProfilePic != null && helpNotificationList[position].userProfilePic.isNotEmpty()) {
                Picasso.get().load(helpNotificationList[position].userProfilePic)
                    .placeholder(R.drawable.ic_default_user)
                    .into(image)
            }


            itemView.setOnClickListener { _ ->
                val intent = Intent(context, MapsActivity::class.java)
                intent.putExtra("helpRequested", helpNotificationList[position])
                context.startActivity(intent)
            }

            image.setOnClickListener { _ ->
                showPopup(helpNotificationList[position])
            }

        }
    }

    fun showPopup(helpData:HelpNotification) {
        val popupView: View = LayoutInflater.from(context).inflate(R.layout.user_help_profile_popup, null)
        val popupWindow = PopupWindow(
            popupView,
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )

        val btnDismiss = popupView.findViewById<View>(R.id.btnClosePopUp) as Button
        var userPictureImageView = popupView.findViewById<View>(R.id.profileImagesSettingPopUp) as de.hdodenhof.circleimageview.CircleImageView
        var userFullNameTextView = popupView.findViewById<View>(R.id.tvUserHelpFullName) as TextView


        userFullNameTextView.text = helpData.firstName +" "+ helpData.lastName

        if(helpData.userProfilePic !=null && helpData.userProfilePic.isNotEmpty()){
            Picasso.get().load(helpData.userProfilePic)
                .placeholder(R.drawable.ic_default_user)
                .into(userPictureImageView)
        }

        btnDismiss.setOnClickListener { popupWindow.dismiss() }
        popupWindow.showAsDropDown(popupView, 0, 0)
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