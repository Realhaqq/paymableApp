package ng.paymable;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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

import ng.paymable.others.MySingleton;
import ng.sessions.SessionHandlerUser;

public class ActivityAirtime extends AppCompatActivity {
    private LinearLayout lyt_mtn, lyt_airtel, lyt_glo, lyt_etisalat;
    private TextView text_lyt;
    Button btn_continue;

    private int wallet_balance;
    EditText amount, phone;

    ViewDialog viewDialog;
    SessionHandlerUser sessionHandlerUser;
    private String servicename, phoneid, amountid;

    private String orderid, type, ref, data;
    private int amounttobepaid;

    ImageView back;
    TextView txt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_airtime);
        btn_continue = findViewById(R.id.btn_continue);


        txt= findViewById(R.id.txt);
        back= findViewById(R.id.back);
        txt.setText("Airtime");
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        viewDialog = new ViewDialog(this);
        sessionHandlerUser = new SessionHandlerUser(getApplicationContext());

        CheckBalance();
        viewDialog = new ViewDialog(this);
        lyt_mtn = findViewById(R.id.lyt_mtn);
        lyt_airtel = findViewById(R.id.lyt_airtel);
        lyt_etisalat = findViewById(R.id.lyt_etisalat);
        lyt_glo = findViewById(R.id.lyt_glo);
        text_lyt = findViewById(R.id.text_lyt);



        phone = findViewById(R.id.phone);
        amount = findViewById(R.id.amount);

        btn_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



//                CheckOrder(amount.getText().toString(), phone.getText().toString());

                displayDialog(amount.getText().toString(), phone.getText().toString(), servicename);

//                CreateOrder(amount.getText().toString(), phone.getText().toString());
            }
        });
        text_lyt.setText("MTN Airtime VTU");
        servicename = "MTN";
//        phoneid = "293";
//        amountid = "294";
        lyt_mtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                text_lyt.setText("MTN Airtime VTU");
                servicename = "mtn";
//                phoneid = "293";
//                amountid = "294";
            }
        });

        lyt_glo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                text_lyt.setText("GLO Airtime VTU");
                servicename = "GLO";
//                phoneid = "295";
//                amountid = "296";
            }
        });

        lyt_etisalat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                text_lyt.setText("9MOBILE Airtime VTU");
                servicename = "9MOBILE";
//                phoneid = "297";
//                amountid = "298";

            }
        });

        lyt_airtel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                text_lyt.setText("AIRTEL Airtime VTU");
                servicename = "AIRTEL";
//                phoneid = "299";
//                amountid = "300";
            }
        });
    }


    private void displayDialog(final String amountt, final String phone, final String servicename)
    {
        final Dialog d=new Dialog(ActivityAirtime.this);
        d.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        d.setContentView(R.layout.dialog_payment_option);
        d.setCancelable(true);
        d.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(d.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        d.setContentView(R.layout.dialog_payment_option);

        TextView amount, datta;
        amount = d.findViewById(R.id.amount);
        datta = d.findViewById(R.id.textdata);
        amount.setText("AMOUNT:   ₦" + amountt);
        datta.setText("DATA:   " + phone);

        d.findViewById(R.id.lyt_wallet).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(wallet_balance < amounttobepaid){
                    Toast.makeText(getApplicationContext(), "Insufficient Wallet Balance", Toast.LENGTH_LONG).show();
                }else{
                    d.dismiss();
                    ProcessOrder(amountt, phone, servicename);

                }
            }
        });

        d.findViewById(R.id.lyt_credit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                d.dismiss();
                int amount = amounttobepaid;
                Intent intent = new Intent(ActivityAirtime.this,ActivityPayCard.class);
                intent.putExtra("orderid", orderid);
                intent.putExtra("ref", ref);
                intent.putExtra("myamount", String.valueOf(amounttobepaid));
                intent.putExtra("amount", String.valueOf(amounttobepaid));
//                intent.putExtra("myamount", );
                intent.putExtra("userid", sessionHandlerUser.getUserDetail().getUserid());
                startActivity(intent);

            }
        });
        d.findViewById(R.id.bt_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                d.dismiss();
            }
        });

        d.show();
        d.getWindow().setAttributes(lp);
    }


//
//    private void CheckOrder(String amount, String phone) {
//        viewDialog.showDialog();
//        Intent intent = getIntent();
//        String url_ = Config.url+"kobopay/init.php?userid="+ sessionHandlerUser.getUserDetail().getUserid() + "&servicename=" + servicename + "&phoneid="+ phoneid + "&amountid="+ amountid + "&amount=" + amount + "&phone=" + phone;
//        RequestQueue requestQueue= Volley.newRequestQueue(getApplicationContext());
//        StringRequest stringRequest=new StringRequest(Request.Method.GET, url_, new Response.Listener<String>() {
//            @Override
//            public void onResponse(String response) {
//                viewDialog.hideDialog();
//                try{
//                    JSONObject jsonObject=new JSONObject(response);
//
//                    if (jsonObject.getInt("status") == 0) {
//                        orderid = jsonObject.getString("orderid");
//                        type = jsonObject.getString("type");
//                        ref = jsonObject.getString("ref");
//                        data = jsonObject.getString("ddata");
//                        amounttobepaid = Integer.parseInt(jsonObject.getString("amount"));
////                        Toast.makeText(getApplicationContext(), "message2" +  jsonObject.getString("amount"), Toast.LENGTH_LONG).show();
//
//                        displayDialog(orderid, type, ref, amounttobepaid, data);
//                    } else if(jsonObject.getInt("status") == 1) {
//
//                        ViewDialogAlert alert = new ViewDialogAlert();
//                        alert.showDialog(ActivityAirtime.this, jsonObject.getString("message"));
//
////                                Toast.makeText(getApplicationContext(), "message2" +  response.getString("message"), Toast.LENGTH_LONG).show();
//                    }else{
//
//                        ViewDialogAlert alert = new ViewDialogAlert();
//                        alert.showDialog(ActivityAirtime.this, jsonObject.getString("message"));
//
////                                Toast.makeText(getApplicationContext(), "message" +  response.getString("message"), Toast.LENGTH_LONG).show();
//                    }
//
//                }catch (JSONException e){e.printStackTrace();}
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                error.printStackTrace();
//                viewDialog.hideDialog();
//
//            }
//        });
//        int socketTimeout = 30000;
//        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
//        stringRequest.setRetryPolicy(policy);
//        requestQueue.add(stringRequest);
//    }
//
//

    private void ProcessOrder(String amount, String phone, String servicename){
        viewDialog.showDialog();
        Intent intent = getIntent();
        String url_ = Config.url+"rubies/airtime_process.php?userid="+ sessionHandlerUser.getUserDetail().getUserid() + "&product=" + servicename + "&phone=" + phone + "&amount=" + amount;
        RequestQueue requestQueue= Volley.newRequestQueue(getApplicationContext());
        StringRequest stringRequest=new StringRequest(Request.Method.GET, url_, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                viewDialog.hideDialog();

                try{
                    JSONObject jsonObject=new JSONObject(response);

                    if (jsonObject.getInt("status") == 0) {
                        ViewDialogAlert alert = new ViewDialogAlert();
                        alert.showDialog(ActivityAirtime.this, jsonObject.getString("message"));
                    } else{
                        ViewDialogAlert alert = new ViewDialogAlert();
                        alert.showDialog(ActivityAirtime.this, jsonObject.getString("message"));
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


    private void CheckBalance() {
//        viewDialog.showDialog();
        String url_ = Config.user_wallet+"?userid="+ sessionHandlerUser.getUserDetail().getUserid();
        JSONObject request = new JSONObject();
        JsonObjectRequest jsArrayRequest = new JsonObjectRequest
                (Request.Method.POST, url_, request, new Response.Listener<JSONObject>() {
                    @SuppressLint("ResourceAsColor")
                    @Override
                    public void onResponse(JSONObject response) {
//                        viewDialog.hideDialog();
                        try {
                            if (response.getInt("status") == 0) {

                                wallet_balance = Integer.parseInt(response.getString("balance"));

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
        MySingleton.getInstance(this).addToRequestQueue(jsArrayRequest);
    }
}
