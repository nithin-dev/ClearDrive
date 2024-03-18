package com.example.cleardrive;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONException;
import org.json.JSONObject;

public class PaymentActivity extends AppCompatActivity implements PaymentResultListener {
    QuoteRequest quoteRequest;
    private DatabaseHelper databaseHelper;
    private UserModel userData;
    String name = "";
    String email = "";
    String mobile = "";
    float amountInCAD = 0.0F;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        databaseHelper = new DatabaseHelper(this);

        // Retrieve quote request data
        quoteRequest = retrieveQuoteRequestData();

        userData = databaseHelper.getUserData();
        Uri data = getIntent().getData();
        amountInCAD = SharedPreferencesManager.getAmountInCAD(PaymentActivity.this);

        Log.d("amount-->", String.valueOf(amountInCAD));
        Log.d("email-->", userData.getEmail());
        Log.d("mobile-->", userData.getMobile());

        if (amountInCAD >0 && !userData.getEmail().isEmpty()&& !userData.getMobile().isEmpty() && !userData.getFirstName().isEmpty()){
            processPayment();

        }
        else {
            CustomDialog.showErrorDialog(this, "Something went wrong, please try again later !");

        }

    }


    private QuoteRequest retrieveQuoteRequestData() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String quoteRequestJson = sharedPreferences.getString("quoteRequest", "");
        if (!quoteRequestJson.isEmpty()) {
            return new Gson().fromJson(quoteRequestJson, QuoteRequest.class);
        }
        return null;
    }

    private void processPayment() {
        if (quoteRequest != null) {

            //String samount = String.valueOf(quoteRequest.getAmountInCAD());
            String samount = String.valueOf(quoteRequest.getAmountInCAD());


            // rounding off the amount.
            int amount = Math.round(Float.parseFloat(samount) * 100);

            // initialize Razorpay account.
            Checkout checkout = new Checkout();

            // set your id as below
            checkout.setKeyID("rzp_test_hyUf5LrGeNJ9rp");

            // set image
            JSONObject object = new JSONObject();
            try {
                // to put name
                object.put("name", userData.getFirstName());

                // put description
                object.put("description", "payment");

                // to set theme color
                object.put("theme.color", "");

                // put the currency
                object.put("currency", "CAD");

                // put amount
                object.put("amount", amount);

                // put mobile number
                object.put("prefill.contact", userData.getMobile());

                // put email
                object.put("prefill.email", userData.getEmail());

                // open razorpay to checkout activity
                checkout.open(PaymentActivity.this, object);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            // Handle the case where quoteRequest is null
            CustomDialog.showErrorDialog(this, "Quote request is null!");
        }
    }


    //razor pay mobile - 9526998405
    //sample card-->4718 6091 0820 4366
    //razor pay-->https://dashboard.razorpay.com/app/reports

    @Override
    public void onPaymentSuccess(String s) {
        CustomDialog.showSuccessDialog(this, "Payment successful");
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onPaymentError(int i, String s) {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        finish();
        CustomDialog.showErrorDialog(this, "Payment Failed due to error : " + s);

        Toast.makeText(this, "Payment Failed due to error : " + s, Toast.LENGTH_SHORT).show();
    }
}