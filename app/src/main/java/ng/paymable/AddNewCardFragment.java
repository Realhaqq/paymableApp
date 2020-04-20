package ng.paymable;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
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
import ng.paystack.MainActivity;
import ng.sessions.SessionHandlerUser;

public class AddNewCardFragment extends Fragment {

    View view;


    ViewDialog viewDialog;
    SessionHandlerUser sessionHandlerUser;
    TextView btn_save;
    EditText card_number, card_mm, card_cvv, card_name, card_yyyy;
    ProgressDialog dialog;
    private Charge charge;
    private Transaction transaction;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_add_new_card,container,false);

        viewDialog = new ViewDialog(getActivity());
        sessionHandlerUser = new SessionHandlerUser(getContext());
        if (co.paystack.android.BuildConfig.DEBUG && (Config.paystack_saver_url.equals(""))) {
            throw new AssertionError("Please set a backend url before running the sample");
        }
        if (BuildConfig.DEBUG && (Config.paystack_public_key.equals(""))) {
            throw new AssertionError("Please set a public key before running the sample");
        }


        PaystackSdk.setPublicKey(Config.paystack_public_key);

        btn_save = view.findViewById(R.id.btn_save);

        card_cvv = view.findViewById(R.id.card_cvv);
        card_mm = view.findViewById(R.id.card_mm);
        card_name = view.findViewById(R.id.card_name);
        card_number = view.findViewById(R.id.card_number);
        card_yyyy = view.findViewById(R.id.card_yyyy);


        //initialize sdk
        PaystackSdk.initialize(getContext());

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    startAFreshCharge(false);
                } catch (Exception e) {
                    Toast.makeText(getContext(), String.format("An error occurred while charging card: %s %s", e.getClass().getSimpleName(), e.getMessage()), Toast.LENGTH_LONG).show();

                }
            }
        });


        return view;
    }



    private void startAFreshCharge(boolean local) {
        charge = new Charge();
        charge.setCard(loadCardFromForm());

//        dialog = new ProgressDialog(getActivity());
//        dialog.setMessage("Performing transaction... please wait");
//        dialog.show();
        viewDialog.showDialog();

        if (local) {
            charge.setAmount(1);
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
        PaystackSdk.chargeCard(getActivity(), charge, new Paystack.TransactionCallback() {
            // This is called only after transaction is successful
            @Override
            public void onSuccess(Transaction transaction) {
//                dismissDialog();

                viewDialog.hideDialog();

                transaction = transaction;
//                mTextError.setText(" ");
                Toast.makeText(getContext(), transaction.getReference(), Toast.LENGTH_LONG).show();
//                updateTextViews();

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
                Toast.makeText(getContext(), transaction.getReference(), Toast.LENGTH_LONG).show();
//                updateTextViews();
            }

            @Override
            public void onError(Throwable error, Transaction transaction) {

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
                    Toast.makeText(getContext(), transaction.getReference() + " concluded with error: " + error.getMessage(), Toast.LENGTH_LONG).show();
//                    mTextError.setText(String.format("%s  concluded with error: %s %s", transaction.getReference(), error.getClass().getSimpleName(), error.getMessage()));
//                    new MainActivity.verifyOnServer().execute(transaction.getReference());
                } else {
                    Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_LONG).show();
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

                    View layout = getLayoutInflater().inflate(R.layout.toast_custom, (ViewGroup) view.findViewById(R.id.custom_toast_layout_id));
                    TextView text = layout.findViewById(R.id.text);
                    text.setText(s);
                    Toast toast = new Toast(getContext());
                    toast.setDuration(Toast.LENGTH_LONG);
                    toast.setView(layout);
                    toast.show();

                }

                @Override
                protected String doInBackground(Bitmap... params) {
                    HashMap<String,String> data = new HashMap<>();

                    data.put("ref", reference);
                    data.put("userid", String.valueOf(sessionHandlerUser.getUserDetail().getUserid()));
                    data.put("email", sessionHandlerUser.getUserDetail().getEmail());

                    String result = rh.sendPostRequest(Config.url + "pay/save_card.php",data);

                    return result;
                }
            }

            verifyref ui = new verifyref();
            ui.execute();


    }

    private void dismissDialog() {
        if ((dialog != null) && dialog.isShowing()) {
            dialog.dismiss();
        }
    }




    private boolean isEmpty(String s) {
        return s == null || s.length() < 1;
    }

    private class fetchAccessCodeFromServer extends AsyncTask<String, Void, String> {
        private String error;

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (result != null) {
                charge.setAccessCode(result);
                chargeCard();
            } else {
                Toast.makeText(getContext(), String.format("There was a problem getting a new access code form the backend: %s", error), Toast.LENGTH_LONG).show();
                dismissDialog();
            }
        }

        @Override
        protected String doInBackground(String... ac_url) {
            try {
                URL url = new URL(ac_url[0]);
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(
                                url.openStream()));

                String inputLine;
                inputLine = in.readLine();
                in.close();
                return inputLine;
            } catch (Exception e) {
                error = e.getClass().getSimpleName() + ": " + e.getMessage();
            }
            return null;
        }
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
        MySingleton.getInstance(getActivity()).addToRequestQueue(jsArrayRequest);
    }
}






