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
import android.widget.ImageView;
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
    private String product, meterid, amountid, metertype, idno;

    private String service_category_id, name, ref, address, vmeterno, vproduct, vamount;
    private int amounttobepaid;

    private Spinner sp_type, sp_meter_type;
    ArrayList<String> comp_list;
    ArrayList<String> meter_type_list;

    ArrayList<String> _ids = new ArrayList<String>();
    ArrayList<String> _amount = new ArrayList<String>();

    ImageView back;
    TextView txt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_electricity);
        btn_continue = findViewById(R.id.btn_continue);

        txt= findViewById(R.id.txt);
        back= findViewById(R.id.back);
        txt.setText("Electricity");
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        comp_list = new ArrayList<>();
        viewDialog = new ViewDialog(this);
        sessionHandlerUser = new SessionHandlerUser(getApplicationContext());

        CheckBalance();
        viewDialog = new ViewDialog(this);
        sp_type = findViewById(R.id.sp_type);
        sp_meter_type = findViewById(R.id.sp_meter_type);



//        final String[] companies={"KEDCO","AEDC","EEDC","IBEDC","JED","ikeja-electricity","EKEDC","PHEDC"};
//        final String[] meter_type={"PREPAID", "POSTPAID"};

        meterno = findViewById(R.id.meterno);
        text_lyt = findViewById(R.id.text_lyt);
        amount = findViewById(R.id.amount);


        LoadSpinnerData();
        btn_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                String aamount = amount.getText().toString();
                int a = Integer.parseInt(amount.getText().toString());
                String meter = meterno.getText().toString();


                if (meter.isEmpty()) {
                    meterno.setError("Enter Phone Number");
                    meterno.requestFocus();
                }else if(aamount.isEmpty()) {
                    amount.setError("Enter Phone Number");
                    amount.requestFocus();
                }else if (a < 500){
                    Toast.makeText(getApplicationContext(), "Minimum Amount is 500 Naira", Toast.LENGTH_LONG).show();

                }  else
                 {
                    CheckOrder(aamount, meter);
                }
            }
        });





        sp_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                idno =_ids.get(i);
                String nname=sp_type.getSelectedItem().toString();
                product =sp_type.getSelectedItem().toString();

                text_lyt.setText(nname);

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


    }


    private void displayDialog(final String service_category_id, final String vmeterno, String ref, final String vproduct, final String name, String address, final int vamount)
    {
        final Dialog d=new Dialog(ActivityElectricity.this);
        d.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        d.setContentView(R.layout.dialog_meter_verify);
        d.setCancelable(true);
        d.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(d.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        d.setContentView(R.layout.dialog_meter_verify);

        TextView txtamout, txtmeterno, txtname, txtaddress, txtproduct;
        txtamout = d.findViewById(R.id.txtamount);
        txtaddress = d.findViewById(R.id.txtaddress);
        txtmeterno = d.findViewById(R.id.textmeterno);
        txtname = d.findViewById(R.id.txtname);
        txtproduct = d.findViewById(R.id.txtproduct);
        txtamout.setText("AMOUNT:   ₦" + vamount);
        txtaddress.setText("ADDRESS:   " + address);
        txtmeterno.setText("METERNO:   " + vmeterno);
        txtname.setText("METER NAME:   " + name);
        txtproduct.setText("SERVICE:   " + vproduct);
        d.findViewById(R.id.btn_continue).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(wallet_balance < vamount){
                    Toast.makeText(getApplicationContext(), "Insufficient Wallet Balance", Toast.LENGTH_LONG).show();
                }else{
                    d.dismiss();
                    ProcessOrder(service_category_id, vmeterno, vproduct, name, vamount);
//                    Toast.makeText(getApplicationContext(), "Yes", Toast.LENGTH_LONG).show();

                }
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


    private void LoadSpinnerData() {
        viewDialog.showDialog();
        RequestQueue requestQueue=Volley.newRequestQueue(getApplicationContext());
        StringRequest stringRequest=new StringRequest(Request.Method.GET, "https://paymable.ng/rubies_json/elect_type.json", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                viewDialog.hideDialog();
                try{
                    JSONObject jsonObject=new JSONObject(response);
//                    if(jsonObject.getInt("success")==1){
                    JSONArray jsonArray=jsonObject.getJSONArray("servicecategory");
                    for(int i=0;i<jsonArray.length();i++){
                        JSONObject jsonObject1=jsonArray.getJSONObject(i);
                        String value=jsonObject1.getString("billername");
                        String id=jsonObject1.getString("service_category_id");
//                        String url=jsonObject1.getString("url");
                        _ids.add(id);
//                        _amount.add(amount);
                        comp_list.add(value);

//                        }
                    }
                    sp_type.setAdapter(new ArrayAdapter<String>(ActivityElectricity.this, android.R.layout.simple_spinner_dropdown_item, comp_list));
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


    private void CheckOrder(String amount, String meternoo) {
        viewDialog.showDialog();
        Intent intent = getIntent();
        String url_ = Config.url+"rubies/elect_verify.php?userid="+ sessionHandlerUser.getUserDetail().getUserid() + "&product=" + product + "&service_category_id="+ idno + "&meterno=" + meternoo + "&amount=" + amount;
        url_ = url_.replaceAll(" ", "%20");

//        Log.d("tttt", url_);
        RequestQueue requestQueue= Volley.newRequestQueue(getApplicationContext());
        StringRequest stringRequest=new StringRequest(Request.Method.GET, url_, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                viewDialog.hideDialog();
                try{
                    JSONObject jsonObject=new JSONObject(response);

                    if (jsonObject.getInt("status") == 0) {
                        service_category_id = jsonObject.getString("service_category_id");
                        vmeterno = jsonObject.getString("meterno");
                        ref = jsonObject.getString("ref");
                        vproduct = jsonObject.getString("product");
                        name = jsonObject.getString("name");
                        address = jsonObject.getString("address");
                        vamount = String.valueOf(Integer.parseInt(jsonObject.getString("amount")));
//                        Toast.makeText(getApplicationContext(), "message2" +  jsonObject.getString("amount"), Toast.LENGTH_LONG).show();

                        displayDialog(service_category_id, vmeterno, ref, vproduct, name, address, Integer.parseInt(vamount));
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



    private void ProcessOrder(String service_category_id, String vmeterno, String vproduct, String name, int vamount){
        viewDialog.showDialog();
        Intent intent = getIntent();
        String url_ = Config.url+"rubies/elect_process.php?userid="+ sessionHandlerUser.getUserDetail().getUserid() + "&service_category_id=" + service_category_id + "&product=" + vproduct + "&amount=" + vamount + "&name=" + name + "&meterno=" + vmeterno;
        url_ = url_.replaceAll(" ", "%20");
                Log.d("tttt", url_);

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
