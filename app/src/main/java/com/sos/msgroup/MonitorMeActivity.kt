package com.sos.msgroup

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.sos.msgroup.model.MonitorMe


class MonitorMeActivity : AppCompatActivity() {

    private lateinit var linearLayoutEhailing : LinearLayout
    private lateinit var linearLayoutMunicipal : LinearLayout
    private lateinit var linearLayoutGovernment : LinearLayout
    private lateinit var linearLayoutBusinessFleet : LinearLayout
    private lateinit var linearLayoutCourier : LinearLayout
    private lateinit var linearLayoutPersonal : LinearLayout
    private lateinit var buttonPay : Button

    private lateinit var database: DatabaseReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_monitor_me)

        linearLayoutEhailing = findViewById(R.id.ll_ehailing)
        linearLayoutMunicipal = findViewById(R.id.ll_municipal)
        linearLayoutGovernment = findViewById(R.id.ll_government)
        linearLayoutBusinessFleet = findViewById(R.id.ll_business_fleet)
        linearLayoutCourier = findViewById(R.id.ll_courier)
        linearLayoutPersonal = findViewById(R.id.ll_personal)
        buttonPay = findViewById(R.id.btnMonitorMePay)




        val spinner = findViewById<Spinner>(R.id.spMonitorCategories)

        val spinnerItems = resources.getStringArray(R.array.spinner_items)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, spinnerItems)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter


        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
               /* val selectedItem = spinnerItems[position]
                Toast.makeText(this@MonitorMeActivity, "Selected item: $selectedItem", Toast.LENGTH_SHORT).show()*/

                when (position) {
                    1 -> {
                        linearLayoutEhailing.visibility = View.VISIBLE
                        linearLayoutMunicipal.visibility = View.GONE
                        linearLayoutGovernment.visibility = View.GONE
                        linearLayoutBusinessFleet.visibility = View.GONE
                        linearLayoutCourier.visibility = View.GONE
                        linearLayoutPersonal.visibility = View.GONE
                    }
                    2 -> {
                        linearLayoutMunicipal.visibility = View.VISIBLE
                        linearLayoutEhailing.visibility = View.GONE
                        linearLayoutGovernment.visibility = View.GONE
                        linearLayoutBusinessFleet.visibility = View.GONE
                        linearLayoutCourier.visibility = View.GONE
                        linearLayoutPersonal.visibility = View.GONE
                    }
                    3 -> {
                        linearLayoutGovernment.visibility = View.VISIBLE
                        linearLayoutEhailing.visibility = View.GONE
                        linearLayoutMunicipal.visibility = View.GONE
                        linearLayoutBusinessFleet.visibility = View.GONE
                        linearLayoutCourier.visibility = View.GONE
                        linearLayoutPersonal.visibility = View.GONE
                    }
                    4 -> {
                        linearLayoutBusinessFleet.visibility = View.VISIBLE
                        linearLayoutEhailing.visibility = View.GONE
                        linearLayoutMunicipal.visibility = View.GONE
                        linearLayoutGovernment.visibility = View.GONE
                        linearLayoutCourier.visibility = View.GONE
                        linearLayoutPersonal.visibility = View.GONE
                    }
                    5 -> {
                        linearLayoutCourier.visibility = View.VISIBLE
                        linearLayoutEhailing.visibility = View.GONE
                        linearLayoutMunicipal.visibility = View.GONE
                        linearLayoutGovernment.visibility = View.GONE
                        linearLayoutBusinessFleet.visibility = View.GONE
                        linearLayoutPersonal.visibility = View.GONE
                    }
                    6 -> {
                        linearLayoutPersonal.visibility = View.VISIBLE
                        linearLayoutEhailing.visibility = View.GONE
                        linearLayoutMunicipal.visibility = View.GONE
                        linearLayoutGovernment.visibility = View.GONE
                        linearLayoutBusinessFleet.visibility = View.GONE
                        linearLayoutCourier.visibility = View.GONE
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Do nothing
            }
        }


        buttonPay.setOnClickListener {



            if (linearLayoutEhailing.isShown) {

                var textInputEditTextMake: TextInputEditText =  findViewById(R.id.txt_hailingMake)
                var textInputEditTextModel: TextInputEditText =  findViewById(R.id.txt_hailingModel)
                var textInputEditTextLicensePlate: TextInputEditText =  findViewById(R.id.txt_hailingLicense)

                if(textInputEditTextLicensePlate.text != null && textInputEditTextModel.text != null&& textInputEditTextMake.text != null){

                    var monitor = MonitorMe("",textInputEditTextLicensePlate.text.toString(),textInputEditTextModel.text.toString(),textInputEditTextMake.text.toString(),"","","","","","","")
                    sendMonitorMeNotification(monitor)

                    openMonitorMePay()
                }


            }else  if (linearLayoutMunicipal.isShown){

            }else  if (linearLayoutGovernment.isShown){

            }else  if (linearLayoutBusinessFleet.isShown){

            }else  if (linearLayoutCourier.isShown){

            }else  if (linearLayoutPersonal.isShown){

            }

        }

        initializeDbRef()

    }

    private fun initializeDbRef() {
        database = FirebaseDatabase.getInstance().reference
    }

    private fun openMonitorMePay() {
        val url = "https://payf.st/hltfw"
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(url)
        startActivity(intent)
    }

    private fun sendMonitorMeNotification(monitor: MonitorMe){

        database.child("monitors").child(monitor.monitorMeId).setValue(monitor.userId).addOnSuccessListener {
            showMsg("The admin will call you short to finalise, your request")
        }.addOnFailureListener {
            showMsg(it.toString())
        }

    }

    private fun showMsg(msg: String) {
        val toast = Toast.makeText(this, msg, Toast.LENGTH_LONG)
        toast.show()
    }
}