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

public class ActivityData extends AppCompatActivity {
    private LinearLayout lyt_mtn, lyt_airtel, lyt_glo, lyt_etisalat;
    private TextView text_lyt;
    Button btn_continue;

    private int wallet_balance;
    EditText amount, phone;

    ViewDialog viewDialog;
    SessionHandlerUser sessionHandlerUser;
    private String servicename, phoneid, dataid, datatype;

    private String orderid, type, ref, data;
    private int amounttobepaid;

    private Spinner sp_type;
    ArrayList<String> data_list;

    ArrayList<String> _ids = new ArrayList<String>();
    ArrayList<String> _amount = new ArrayList<String>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data);
        btn_continue = findViewById(R.id.btn_continue);


        data_list = new ArrayList<>();
        viewDialog = new ViewDialog(this);
        sessionHandlerUser = new SessionHandlerUser(getApplicationContext());

        CheckBalance();
        viewDialog = new ViewDialog(this);
        lyt_mtn = findViewById(R.id.lyt_mtn);
        lyt_airtel = findViewById(R.id.lyt_airtel);
        lyt_etisalat = findViewById(R.id.lyt_etisalat);
        lyt_glo = findViewById(R.id.lyt_glo);
        text_lyt = findViewById(R.id.text_lyt);
        sp_type = findViewById(R.id.sp_type);



        phone = findViewById(R.id.phone);
        amount = findViewById(R.id.amount);

        btn_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                String aamount = amount.getText().toString();
                String pphone = phone.getText().toString();


                if (pphone.isEmpty()) {
                    phone.setError("Enter Phone Number");
                    phone.requestFocus();
                }else {
                    CheckOrder(aamount, pphone);
                }
            }
        });


        sp_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String id=_ids.get(i);
                datatype = id;
                String aamount=_amount.get(i);
                amount.setText(aamount);

//                datatype = sp_type.getSelectedItem().toString();

//                Toast.makeText(getApplicationContext(), id, Toast.LENGTH_LONG).show();

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        text_lyt.setText("MTN Data Bundles");
        servicename = "mtn-data";
        LoadSpinnerData(servicename);
        phoneid = "316";
        dataid = "317";
        lyt_mtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                text_lyt.setText("MTN Data Budnles");
                servicename = "mtn-data";
                LoadSpinnerData(servicename);
                phoneid = "316";
                dataid = "317";
            }
        });

        lyt_glo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                text_lyt.setText("GLO Data Budnles ");
                servicename = "glo-data";
                LoadSpinnerData(servicename);
                phoneid = "318";
                dataid = "319";
            }
        });

        lyt_etisalat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                text_lyt.setText("9MOBILE Data Bundles");
                servicename = "etisalat-data";
                LoadSpinnerData(servicename);
                phoneid = "322";
                dataid = "323";

            }
        });

        lyt_airtel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                text_lyt.setText("AIRTEL Airtime VTU");
                servicename = "airtel-data";
                LoadSpinnerData(servicename);
                phoneid = "320";
                dataid = "321";
            }
        });
    }

    private void LoadSpinnerData(String t) {
        viewDialog.showDialog();
        RequestQueue requestQueue=Volley.newRequestQueue(getApplicationContext());
        StringRequest stringRequest=new StringRequest(Request.Method.GET, "https://paymable.ng/json/" + t + ".json", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                viewDialog.hideDialog();
                try{
                    JSONObject jsonObject=new JSONObject(response);
//                    if(jsonObject.getInt("success")==1){
                        JSONArray jsonArray=jsonObject.getJSONArray("agentServiceDropdownData");
                        for(int i=0;i<jsonArray.length();i++){
                            JSONObject jsonObject1=jsonArray.getJSONObject(i);
                            String value=jsonObject1.getString("value");
                            String id=jsonObject1.getString("id");
                            String amount=jsonObject1.getString("amount");
                            _ids.add(id);
                            _amount.add(amount);
                            data_list.add(value);

//                        }
                    }
                    sp_type.setAdapter(new ArrayAdapter<String>(ActivityData.this, android.R.layout.simple_spinner_dropdown_item, data_list));
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


    private void displayDialog(final String orderid, String type, final String ref, final int amounttobepaid, String data)
    {
        final Dialog d=new Dialog(ActivityData.this);
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
                Intent intent = new Intent(ActivityData.this, ActivityPayCard.class);
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



    private void CheckOrder(String amount, String phone) {
        viewDialog.showDialog();
        Intent intent = getIntent();
        String url_ = Config.url+"kobopay/data_init.php?userid="+ sessionHandlerUser.getUserDetail().getUserid() + "&servicename=" + servicename + "&phoneid="+ phoneid + "&dataid="+ dataid + "&type=" + datatype + "&phone=" + phone;

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
                        alert.showDialog(ActivityData.this, jsonObject.getString("message"));

//                                Toast.makeText(getApplicationContext(), "message2" +  response.getString("message"), Toast.LENGTH_LONG).show();
                    }else{

                        ViewDialogAlert alert = new ViewDialogAlert();
                        alert.showDialog(ActivityData.this, jsonObject.getString("message"));

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
                        alert.showDialog(ActivityData.this, jsonObject.getString("message"));
                    } else{
                        ViewDialogAlert alert = new ViewDialogAlert();
                        alert.showDialog(ActivityData.this, jsonObject.getString("message"));
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