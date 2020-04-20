package ng.paymable;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
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
import java.util.HashMap;

import co.paystack.android.BuildConfig;
import co.paystack.android.Paystack;
import co.paystack.android.PaystackSdk;
import co.paystack.android.Transaction;
import co.paystack.android.exceptions.ExpiredAccessCodeException;
import co.paystack.android.model.Card;
import co.paystack.android.model.Charge;
import ng.paymable.others.MySingleton;
import ng.paymable.others.RequestHandler;
import ng.sessions.SessionHandlerUser;

public class ActivityWalletPayCard extends AppCompatActivity {


    ViewDialog viewDialog;
    SessionHandlerUser sessionHandlerUser;
    TextView btn_save;
    EditText card_number, card_mm, card_cvv, card_name, card_yyyy;
    ProgressDialog dialog;
    private Charge charge;
    private Transaction transaction;
    ImageView back;
    TextView txt;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_card);

        viewDialog = new ViewDialog(this);
        sessionHandlerUser = new SessionHandlerUser(getApplicationContext());

        txt= findViewById(R.id.txt);
        back= findViewById(R.id.back);
        txt.setText("Deposit With Credit Card");
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        if (BuildConfig.DEBUG && (Config.paystack_saver_url.equals(""))) {
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
            charge.setAmount(Integer.parseInt(getIntent().getStringExtra("amount")));
            charge.setEmail(sessionHandlerUser.getUserDetail().getEmail());
            charge.setReference("ChargedFromAndroid_" + Calendar.getInstance().getTimeInMillis());
            try {
                charge.setAmount(Integer.parseInt(getIntent().getStringExtra("amount")));
                charge.setEmail(sessionHandlerUser.getUserDetail().getEmail());
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
//        viewDialog.showDialog();
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

        viewDialog.hideDialog();
    }

    private void chargeCard() {
        transaction = null;
        viewDialog.showDialog();
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

                Log.d("ttt", transaction.getReference());

                VerifyRef(transaction.getReference());



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
                alert.showDialog(ActivityWalletPayCard.this, "Transaction Error, Please try again");
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




    private void VerifyRef(final String reference) {
        viewDialog.showDialog();
        class verifyref extends AsyncTask<Bitmap,Void,String> {

            RequestHandler rh = new RequestHandler();

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                viewDialog.hideDialog();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                viewDialog.hideDialog();

                View layout = getLayoutInflater().inflate(R.layout.toast_custom, (ViewGroup) findViewById(R.id.custom_toast_layout_id));
                TextView text = layout.findViewById(R.id.text);
                text.setText(s);
                Toast toast = new Toast(getApplicationContext());
                toast.setDuration(Toast.LENGTH_LONG);
                toast.setView(layout);
                toast.show();

                Intent intent = new Intent(getApplicationContext(), HomePay2Activity.class);
                startActivity(intent);
                finish();

//                if(s.contains("success")){
//                    String orderid, ref, amount, userid;
//
//                    orderid = getIntent().getStringExtra("orderid");
//                    ref = getIntent().getStringExtra("ref");
//                    amount = getIntent().getStringExtra("amount");
//                    userid = getIntent().getStringExtra("userid");
//                    ProcessOrder(orderid, ref, amount, userid);
//                }

            }

            @Override
            protected String doInBackground(Bitmap... params) {
                HashMap<String,String> data = new HashMap<>();

                data.put("ref", reference);
                data.put("userid", String.valueOf(sessionHandlerUser.getUserDetail().getUserid()));
                data.put("email", sessionHandlerUser.getUserDetail().getEmail());

                String result = rh.sendPostRequest(Config.url + "pay/wallet_card_verify.php?ref=" + reference,data);

                return result;
            }
        }

        verifyref ui = new verifyref();
        ui.execute();


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