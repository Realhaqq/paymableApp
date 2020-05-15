package ng.paymable;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;

import java.util.HashMap;

import co.paystack.android.Paystack;
import co.paystack.android.PaystackSdk;
import co.paystack.android.Transaction;
import co.paystack.android.model.Card;
import co.paystack.android.model.Charge;
import co.paystack.android.ui.OtpActivity;
import ng.paymable.others.RequestHandler;
import ng.sessions.SessionHandlerUser;

public class PayStackMain extends AppCompatActivity {

    /*String  cardNumber  =   "50606 66666 66666 6666";
    int expiryMonth  =   11;
    int expiryYear  =  19;
    String  cvv =   "123";*/

    EditText    card_number,    expiry_month,   expiry_year,    cvv, amount,    email,  account_number, account_name,   bank_name;

    //Card    card    =   new Card(cardNumber,    expiryMonth,    expiryYear, cvv);

    Button test;
    TextView    status;

    private ProgressDialog  progressDialog;
    private Context context;

    ViewDialog viewDialog;
    SessionHandlerUser sessionHandlerUser;

    TextView pay_now, txt;
    ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_card);
        viewDialog = new ViewDialog(this);
        sessionHandlerUser = new SessionHandlerUser(getApplicationContext());
        PaystackSdk.initialize(getApplicationContext());
        txt=findViewById(R.id.txt);

        txt.setText("Fund Wallet");
        back= findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });



        card_number =   findViewById(R.id.card_number);
        expiry_month    =   findViewById(R.id.card_mm);
        expiry_year =   findViewById(R.id.card_yyyy);
        cvv =   findViewById(R.id.card_cvv);


        pay_now = findViewById(R.id.btn_save);
        pay_now.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String ccard_number = card_number.getText().toString();
                String exp_month = card_number.getText().toString();
                String exp_year = card_number.getText().toString();
                String ccvv = card_number.getText().toString();

                if(ccard_number.isEmpty()){
                    card_number.setError("Enter Card Number");
                    card_number.requestFocus();
                }else if(exp_month.isEmpty()){
                    expiry_month.setError("Enter Expiry month");
                    expiry_month.requestFocus();

                }else if(exp_year.isEmpty()){
                    expiry_year.setError("Enter Expiry Year");
                    expiry_year.requestFocus();
                }else if(ccvv.isEmpty()){
                    cvv.setError("Enter CVV");
                    cvv.requestFocus();
                }else{

                    payStack();

                }


            }
        });

//        Log.d("myid", String.valueOf(sessionHandlerUser.getUserDetail().getUserid()));


        progressDialog  =   new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Processing Transaction...");
    }

    private void payStack() {
//        progressDialog.show();
        viewDialog.showDialog();

        String  cardNumber  =   card_number.getText().toString();
        int expiryMonth =   Integer.parseInt(expiry_month.getText().toString());
        int expiryYear  =   Integer.parseInt(expiry_year.getText().toString());
        String  cardCVV =   cvv.getText().toString();
        int transactionAmount   =   Integer.parseInt(getIntent().getStringExtra("amount"));
        String  emailText   =   sessionHandlerUser.getUserDetail().getEmail();

        if (TextUtils.isEmpty(cardNumber))  {
            card_number.setError("Please enter your card number");
        }   if (TextUtils.isEmpty(String.valueOf(expiryMonth))) {
            expiry_month.setError("Please enter your card expiry date");
        }

        Card    card    =   new Card(cardNumber,    expiryMonth,    expiryYear, cardCVV);

        if (card.isValid()) {
            Charge  charge  =   new Charge();
            charge.setCard(card);
            charge.setAmount(transactionAmount  *   100);
            charge.setEmail(emailText);
            charge.setBearer(charge.getBearer());

            if (transactionAmount   >=   100)    {
                charge.setTransactionCharge(10  *   100);
            }   else if (transactionAmount  <   99)    {
                charge.setTransactionCharge(5   *   100);
            }

            PaystackSdk.chargeCard(PayStackMain.this, charge, new Paystack.TransactionCallback() {
                @Override
                public void onSuccess(Transaction transaction) {

                    Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_LONG).show();
//                    status.setText("Success!");
//                    showSuccessDialog(transaction);
                    VerifyRef(transaction.getReference());

//                    status.setTextColor(getResources().getColor(R.color.colorGreen));
//                    progressDialog.dismiss()
                    viewDialog.hideDialog();
                    clearData();
                }

                @Override
                public void beforeValidate(Transaction transaction) {
//                    progressDialog.dismiss();

//                    viewDialog.hideDialog();
                }

                @Override
                public void onError(Throwable error, Transaction transaction) {
                    Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_LONG).show();
//
//                    status.setText("Error: "    +   error.getMessage());
//                    status.setTextColor(getResources().getColor(R.color.colorAccent));
//                    progressDialog.dismiss();
                    viewDialog.hideDialog();
//                    showFailureDialog(error,    transaction);
                }
            });
        }
    }

    private void showFailureDialog(Throwable error, Transaction transaction) {
        AlertDialog.Builder builder;

        if (Build.VERSION.SDK_INT   >=  Build.VERSION_CODES.LOLLIPOP)   {
            builder =   new AlertDialog.Builder(new ContextThemeWrapper(PayStackMain.this,   R.style.AppTheme));
        }   else {
            builder =   new AlertDialog.Builder(new ContextThemeWrapper(PayStackMain.this,   R.style.AppTheme));
        }

        builder.setTitle("Error!")
                .setMessage("Transaction Error: "   +   error   +   transaction +   "Please try again!")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        clearData();
                    }
                });
        builder.show();
    }

    private void showSuccessDialog(final Transaction transaction) {
        AlertDialog.Builder builder;

        if (Build.VERSION.SDK_INT   >=  Build.VERSION_CODES.LOLLIPOP)   {
            builder =   new AlertDialog.Builder(new ContextThemeWrapper(PayStackMain.this,  R.style.AppTheme));
        }   else {
            builder =   new AlertDialog.Builder(new ContextThemeWrapper(PayStackMain.this,  R.style.AppTheme));
        }

        builder.setTitle("Success!")
                .setMessage("Transaction Completed, please check your email.");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                VerifyRef(transaction.getReference());
            }
        });
        builder.show();
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

                String result = rh.sendPostRequest(Config.url + "pay/wallet_card_verify.php?ref=" + reference + "&userid=" + sessionHandlerUser.getUserDetail().getUserid() + "&amount=" + amount,data);

                return result;
            }
        }

        verifyref ui = new verifyref();
        ui.execute();


    }
    private void clearData() {
        card_number.getText().clear();
        expiry_month.getText().clear();
        expiry_year.getText().clear();
        cvv.getText().clear();
//        amount.getText().clear();
//        email.getText().clear();
//        account_number.getText().clear();
//        account_name.getText().clear();
//        bank_name.getText().clear();
    }
}