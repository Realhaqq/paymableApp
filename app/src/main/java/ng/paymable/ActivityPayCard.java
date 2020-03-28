package ng.paymable;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

import co.paystack.android.BuildConfig;
import co.paystack.android.Paystack;
import co.paystack.android.PaystackSdk;
import co.paystack.android.Transaction;
import co.paystack.android.exceptions.ExpiredAccessCodeException;
import co.paystack.android.model.Card;
import co.paystack.android.model.Charge;
import ng.paymable.others.MySingleton;
import ng.sessions.SessionHandlerUser;

public class ActivityPayCard extends AppCompatActivity {


    ViewDialog viewDialog;
    SessionHandlerUser sessionHandlerUser;
    TextView btn_save;
    EditText card_number, card_mm, card_cvv, card_name, card_yyyy;
    ProgressDialog dialog;
    private Charge charge;
    private Transaction transaction;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_card);

        viewDialog = new ViewDialog(this);
        sessionHandlerUser = new SessionHandlerUser(getApplicationContext());



        if (co.paystack.android.BuildConfig.DEBUG && (Config.paystack_saver_url.equals(""))) {
            throw new AssertionError("Please set a backend url before running the sample");
        }
        if (BuildConfig.DEBUG && (Config.paystack_public_key.equals(""))) {
            throw new AssertionError("Please set a public key before running the sample");
        }


        PaystackSdk.setPublicKey(Config.paystack_public_key);

        btn_save = findViewById(R.id.btn_save);

        card_cvv = findViewById(R.id.card_cvv);
        card_mm = findViewById(R.id.card_mm);
        card_name = findViewById(R.id.card_name);
        card_number = findViewById(R.id.card_number);
        card_yyyy = findViewById(R.id.card_yyyy);


        //initialize sdk
        PaystackSdk.initialize(getApplicationContext());

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    startAFreshCharge(false);
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), String.format("An error occurred while charging card: %s %s", e.getClass().getSimpleName(), e.getMessage()), Toast.LENGTH_LONG).show();

                }
            }
        });

    }



    private void startAFreshCharge(boolean local) {
        charge = new Charge();
        charge.setCard(loadCardFromForm());

//        dialog = new ProgressDialog(getActivity());
//        dialog.setMessage("Performing transaction... please wait");
//        dialog.show();
        viewDialog.showDialog();

        if (local) {
            charge.setAmount(50);
            charge.setEmail(sessionHandlerUser.getUserDetail().getEmail());
            charge.setReference("ChargedFromAndroid_" + Calendar.getInstance().getTimeInMillis());
            try {
                charge.putCustomField("Charged From", "Android SDK");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            chargeCard();
        } else {
            GetAcessCode();
        }
    }




    /**
     * Method to validate the form, and set errors on the edittexts.
     */
    private Card loadCardFromForm() {
        //validate fields
        Card card;

        String cardNum = card_number.getText().toString().trim();

        //build card object with ONLY the number, update the other fields later
        card = new Card.Builder(cardNum, 0, 0, "").build();
        String cvc = card_cvv.getText().toString().trim();
        //update the cvc field of the card
        card.setCvc(cvc);

        //validate expiry month;
        String sMonth = card_mm.getText().toString().trim();
        int month = 0;
        try {
            month = Integer.parseInt(sMonth);
        } catch (Exception ignored) {
        }

        card.setExpiryMonth(month);

        String sYear = card_yyyy.getText().toString().trim();
        int year = 0;
        try {
            year = Integer.parseInt(sYear);
        } catch (Exception ignored) {
        }
        card.setExpiryYear(year);

        return card;
    }

    @Override
    public void onPause() {
        super.onPause();

//        if ((dialog != null) && dialog.isShowing()) {
//            dialog.dismiss();
//        }
//        dialog = null;

//        viewDialog.hideDialog();
    }

    private void chargeCard() {
        transaction = null;
//        viewDialog.showDialog();
        PaystackSdk.chargeCard(this, charge, new Paystack.TransactionCallback() {
            // This is called only after transaction is successful
            @Override
            public void onSuccess(Transaction transaction) {
//                dismissDialog();

                viewDialog.hideDialog();

                transaction = transaction;
//                mTextError.setText(" ");
//                Toast.makeText(getApplicationContext(), transaction.getReference(), Toast.LENGTH_LONG).show();
//                updateTextViews();

                String orderid, ref, amount, userid;

                orderid = getIntent().getStringExtra("orderid");
                ref = getIntent().getStringExtra("ref");
                amount = getIntent().getStringExtra("amount");
                userid = getIntent().getStringExtra("userid");
                ProcessOrder(orderid, ref, amount, userid);
//                getContext().verifyOnServer().execute(transaction.getReference());
            }

            // This is called only before requesting OTP
            // Save reference so you may send to server if
            // error occurs with OTP
            // No need to dismiss dialog
            @Override
            public void beforeValidate(Transaction transaction) {
                viewDialog.hideDialog();
                transaction = transaction;
//                Toast.makeText(getApplicationContext(), transaction.getReference(), Toast.LENGTH_LONG).show();
//                updateTextViews();
            }

            @Override
            public void onError(Throwable error, Transaction transaction) {

                ViewDialogAlert alert = new ViewDialogAlert();
                alert.showDialog(ActivityPayCard.this, "Transaction Error, Please try again");
                finish();
                // If an access code has expired, simply ask your server for a new one
                // and restart the charge instead of displaying error
                transaction = transaction;
                if (error instanceof ExpiredAccessCodeException) {
                    startAFreshCharge(false);
                    chargeCard();
                    return;
                }

//                dismissDialog();
                viewDialog.hideDialog();

                if (transaction.getReference() != null) {
                    Toast.makeText(getApplicationContext(), transaction.getReference() + " concluded with error: " + error.getMessage(), Toast.LENGTH_LONG).show();
//                    mTextError.setText(String.format("%s  concluded with error: %s %s", transaction.getReference(), error.getClass().getSimpleName(), error.getMessage()));
//                    new MainActivity.verifyOnServer().execute(transaction.getReference());
                } else {
                    Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
//                    mTextError.setText(String.format("Error: %s %s", error.getClass().getSimpleName(), error.getMessage()));
                }
//                updateTextViews();
            }

        });
    }

    private void ProcessOrder(String orderid, String ref, String amount, String userid) {
        viewDialog.showDialog();
        Intent intent = getIntent();
        String url_ = Config.url+"kobopay/card_process.php?userid="+ sessionHandlerUser.getUserDetail().getUserid() + "&orderid=" + orderid + "&ref=" + ref + "&amount=" + amount;

        Log.d("uttt", url_);
        RequestQueue requestQueue= Volley.newRequestQueue(getApplicationContext());
        StringRequest stringRequest=new StringRequest(Request.Method.GET, url_, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                viewDialog.hideDialog();

                try{
                    JSONObject jsonObject=new JSONObject(response);

                    if (jsonObject.getInt("status") == 0) {
                        ViewDialogAlert alert = new ViewDialogAlert();
                        alert.showDialog(ActivityPayCard.this, jsonObject.getString("message"));
                        finish();
                    } else{
                        ViewDialogAlert alert = new ViewDialogAlert();
                        alert.showDialog(ActivityPayCard.this, jsonObject.getString("message"));
                        finish();
                    }

                }catch (JSONException e){e.printStackTrace();}
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                viewDialog.hideDialog();

                error.printStackTrace();
            }
        });
        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        requestQueue.add(stringRequest);

    }


    private void GetAcessCode() {
//        viewDialog.showDialog();
        String url_ = Config.paystack_saver_url;
        JSONObject request = new JSONObject();
        JsonObjectRequest jsArrayRequest = new JsonObjectRequest
                (Request.Method.POST, url_, request, new Response.Listener<JSONObject>() {
                    @SuppressLint("ResourceAsColor")
                    @Override
                    public void onResponse(JSONObject response) {
//                        viewDialog.hideDialog();
                        try {
                            if (response.getInt("status") == 0) {

                                charge.setAccessCode(response.getString("code"));
                                chargeCard();
//                                txtbalance.setText("₦" + response.getString("balance"));

                            } else {

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
//                        viewDialog.hideDialog();
                    }
                });
        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsArrayRequest);
    }



}