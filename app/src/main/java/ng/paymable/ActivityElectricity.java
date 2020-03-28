package ng.paymable;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import ng.paymable.others.MySingleton;
import ng.paymable.others.RequestHandler;
import ng.sessions.SessionHandlerUser;

public class ActivityElectricity extends AppCompatActivity {
    private LinearLayout lyt_mtn, lyt_airtel, lyt_glo, lyt_etisalat;
    private TextView text_lyt;
    Button btn_continue;

    private int wallet_balance;
    EditText amount, meterno;

    ViewDialog viewDialog;
    SessionHandlerUser sessionHandlerUser;
    private String servicename, meterid, amountid, metertype, idno;

    private String orderid, type, ref, data;
    private int amounttobepaid;

    private Spinner sp_type, sp_meter_type;
    ArrayList<String> comp_list;
    ArrayList<String> meter_type_list;

    ArrayList<String> _ids = new ArrayList<String>();
    ArrayList<String> _amount = new ArrayList<String>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_electricity);
        btn_continue = findViewById(R.id.btn_continue);


//        data_list = new ArrayList<>();
        viewDialog = new ViewDialog(this);
        sessionHandlerUser = new SessionHandlerUser(getApplicationContext());

        CheckBalance();
        viewDialog = new ViewDialog(this);
        sp_type = findViewById(R.id.sp_type);
        sp_meter_type = findViewById(R.id.sp_meter_type);



        final String[] companies={"KEDCO","AEDC","EEDC","IBEDC","JED","ikeja-electricity","EKEDC","PHEDC"};
        final String[] meter_type={"PREPAID", "POSTPAID"};

        meterno = findViewById(R.id.meterno);
        text_lyt = findViewById(R.id.text_lyt);
        amount = findViewById(R.id.amount);

        btn_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                String aamount = amount.getText().toString();
                String meter = meterno.getText().toString();


                if (meter.isEmpty()) {
                    meterno.setError("Enter Phone Number");
                    meterno.requestFocus();
                }else if(aamount.isEmpty()) {
                    amount.setError("Enter Phone Number");
                    amount.requestFocus();
                }else {
                    CheckOrder(aamount, meter);
                }
            }
        });

        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_dropdown_item, companies);
        spinnerArrayAdapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item );

// Spinner spinYear = (Spinner)findViewById(R.id.spin);
        sp_type.setAdapter(spinnerArrayAdapter);

        ArrayAdapter<String> spinnerArrayAdapter2 = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_dropdown_item, meter_type);
        spinnerArrayAdapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item );

// Spinner spinYear = (Spinner)findViewById(R.id.spin);
        sp_meter_type.setAdapter(spinnerArrayAdapter2);



        sp_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String comp = sp_type.getSelectedItem().toString();

                servicename = comp;

                text_lyt.setText(comp);

//                Toast.makeText(getApplicationContext(), comp, Toast.LENGTH_LONG).show();

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        sp_meter_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String comp = sp_meter_type.getSelectedItem().toString();

                if (sp_type.getSelectedItem().toString().contains("KEDCO")){
                    idno = "384";
                    meterid = "385";
                    amountid = "386";

                    if(sp_meter_type.getSelectedItem().toString().contains("PREPAID"))
                    {
                        metertype = "626";
                    }else{
                        metertype = "627";
                    }
                }else if (sp_type.getSelectedItem().toString().contains("AEDC")){
                    idno = "282";
                    meterid = "241";
                    amountid = "242";

                    if(sp_meter_type.getSelectedItem().toString().contains("PREPAID"))
                    {
                        metertype = "354";
                    }else{
                        metertype = "371";
                    }
                }else if (sp_type.getSelectedItem().toString().contains("EEDC")){
                    idno = "287";
                    meterid = "288";
                    amountid = "289";

                    if(sp_meter_type.getSelectedItem().toString().contains("PREPAID"))
                    {
                        metertype = "355";
                    }else{
                        metertype = "356";
                    }
                }else if (sp_type.getSelectedItem().toString().contains("IBEDC")){
                    idno = "290";
                    meterid = "291";
                    amountid = "292";

                    if(sp_meter_type.getSelectedItem().toString().contains("PREPAID"))
                    {
                        metertype = "307";
                    }else{
                        metertype = "308";
                    }
                }else if (sp_type.getSelectedItem().toString().contains("JED")){
                    idno = "250";
                    meterid = "249";
                    amountid = "280";

                    if(sp_meter_type.getSelectedItem().toString().contains("PREPAID"))
                    {
                        metertype = "347";
                    }else{
                        metertype = "348";
                    }
                }else if (sp_type.getSelectedItem().toString().contains("ikeja-electricity")){
                    idno = "247";
                    meterid = "246";
                    amountid = "248";

                    if(sp_meter_type.getSelectedItem().toString().contains("PREPAID"))
                    {
                        metertype = "349";
                    }else{
                        metertype = "350";
                    }
                }else if (sp_type.getSelectedItem().toString().contains("EKEDC")){
                    idno = "244";
                    meterid = "243";
                    amountid = "245";

                    if(sp_meter_type.getSelectedItem().toString().contains("PREPAID"))
                    {
                        metertype = "352";
                    }else{
                        metertype = "353";
                    }
                }else if (sp_type.getSelectedItem().toString().contains("PHEDC")){
                    idno = "255";
                    meterid = "254";
                    amountid = "256";

                    if(sp_meter_type.getSelectedItem().toString().contains("PREPAID"))
                    {
                        metertype = "345";
                    }else{
                        metertype = "346";
                    }
                }


//                Toast.makeText(getApplicationContext(), comp, Toast.LENGTH_LONG).show();

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }


    private void displayDialog(final String orderid, String type, final String ref, final int amounttobepaid, String data)
    {
        final Dialog d=new Dialog(ActivityElectricity.this);
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
        amount.setText("AMOUNT:   ₦" + amounttobepaid);
        datta.setText("DATA:   " + data);
        d.findViewById(R.id.lyt_wallet).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(wallet_balance < amounttobepaid){
                    Toast.makeText(getApplicationContext(), "Insufficient Wallet Balance", Toast.LENGTH_LONG).show();
                }else{
                    d.dismiss();
                    ProcessOrder(orderid, ref, amounttobepaid);
//                    Toast.makeText(getApplicationContext(), "Yes", Toast.LENGTH_LONG).show();

                }
            }
        });

        d.findViewById(R.id.lyt_credit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                d.dismiss();
                Intent intent = new Intent(ActivityElectricity.this, ActivityPayCard.class);
                intent.putExtra("orderid", orderid);
                intent.putExtra("ref", ref);
                intent.putExtra("amount", amounttobepaid);
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



    private void CheckOrder(String amount, String meternoo) {
        viewDialog.showDialog();
        Intent intent = getIntent();
        String url_ = Config.url+"kobopay/elect_init.php?userid="+ sessionHandlerUser.getUserDetail().getUserid() + "&servicename=" + servicename + "&amountid=" + amountid + "&idno="+ idno + "&type="+ metertype + "&meterid=" + meterid + "&meterno=" + meternoo + "&amount=" + amount;

        Log.d("tttt", url_);
        RequestQueue requestQueue= Volley.newRequestQueue(getApplicationContext());
        StringRequest stringRequest=new StringRequest(Request.Method.GET, url_, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                viewDialog.hideDialog();
                try{
                    JSONObject jsonObject=new JSONObject(response);

                    if (jsonObject.getInt("status") == 0) {
                        orderid = jsonObject.getString("orderid");
                        type = jsonObject.getString("type");
                        ref = jsonObject.getString("ref");
                        data = jsonObject.getString("ddata");
                        amounttobepaid = Integer.parseInt(jsonObject.getString("amount"));
//                        Toast.makeText(getApplicationContext(), "message2" +  jsonObject.getString("amount"), Toast.LENGTH_LONG).show();

                        displayDialog(orderid, type, ref, amounttobepaid, data);
                    } else if(jsonObject.getInt("status") == 1) {

                        ViewDialogAlert alert = new ViewDialogAlert();
                        alert.showDialog(ActivityElectricity.this, jsonObject.getString("message"));

//                                Toast.makeText(getApplicationContext(), "message2" +  response.getString("message"), Toast.LENGTH_LONG).show();
                    }else{

                        ViewDialogAlert alert = new ViewDialogAlert();
                        alert.showDialog(ActivityElectricity.this, jsonObject.getString("message"));

//                                Toast.makeText(getApplicationContext(), "message" +  response.getString("message"), Toast.LENGTH_LONG).show();
                    }

                }catch (JSONException e){e.printStackTrace();}
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                viewDialog.hideDialog();

            }
        });
        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        requestQueue.add(stringRequest);
    }



    private void ProcessOrder(String orderid, String ref, int amount){
        viewDialog.showDialog();
        Intent intent = getIntent();
        String url_ = Config.url+"kobopay/kobo_process.php?userid="+ sessionHandlerUser.getUserDetail().getUserid() + "&orderid=" + orderid + "&ref=" + ref + "&amount=" + amount;
        RequestQueue requestQueue= Volley.newRequestQueue(getApplicationContext());
        StringRequest stringRequest=new StringRequest(Request.Method.GET, url_, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                viewDialog.hideDialog();

                try{
                    JSONObject jsonObject=new JSONObject(response);

                    if (jsonObject.getInt("status") == 0) {
                        ViewDialogAlert alert = new ViewDialogAlert();
                        alert.showDialog(ActivityElectricity.this, jsonObject.getString("message"));
                    } else{
                        ViewDialogAlert alert = new ViewDialogAlert();
                        alert.showDialog(ActivityElectricity.this, jsonObject.getString("message"));
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
