package com.sos.msgroup;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.flutterwave.raveandroid.RavePayActivity;
import com.flutterwave.raveandroid.RaveUiManager;
import com.flutterwave.raveandroid.rave_java_commons.RaveConstants;

public class SubscriptionFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View root = inflater.inflate(R.layout.fragment_subscription, container, false);
        payment(root);

        return root;
    }

    private void payment(View ui) {

        String amount = "149";
        String email = "thammy202@gmail.com";
        String publicKey = "FLWPUBK_TEST-c27b28c39d97de7da1b3023be3461f60-X";
        String encryptionKey = "FLWSECK_TESTe10e69d67726";
        String txRef = "This is a text txRef";
        String narration = "Test narration";
        String currency = "ZAR";
        String fName = "Thamsanqa";
        String lName = "Shabalala";
        String phoneNumber = "0824382247";


        new RaveUiManager(getActivity())
                .setAmount(Double.parseDouble(amount))
                .setCurrency(currency)
                .setfName(fName)
                .setlName(lName)
                .setEmail(email)
                .setPublicKey(publicKey)
                .setEncryptionKey(encryptionKey)
                .setTxRef(txRef)
                .setPhoneNumber(phoneNumber, false)
                    .acceptCardPayments(true)
                    .allowSaveCardFeature(true)
                    .initialize();
    }

    @Override
    public  void onActivityResult(int requestCode, int resultCode, Intent data) {
        /*
         *  We advise you to do a further verification of transaction's details on your server to be
         *  sure everything checks out before providing service or goods.
         */
        if (requestCode == RaveConstants.RAVE_REQUEST_CODE && data != null) {
            String message = data.getStringExtra("response");
            if (resultCode == RavePayActivity.RESULT_SUCCESS) {
                Toast.makeText(getActivity(), "SUCCESS " + message, Toast.LENGTH_SHORT).show();
            }
            else if (resultCode == RavePayActivity.RESULT_ERROR) {
                Toast.makeText(getActivity(), "ERROR " + message, Toast.LENGTH_SHORT).show();
            }
            else if (resultCode == RavePayActivity.RESULT_CANCELLED) {
                Toast.makeText(getActivity(), "CANCELLED " + message, Toast.LENGTH_SHORT).show();
            }
        }
        else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}