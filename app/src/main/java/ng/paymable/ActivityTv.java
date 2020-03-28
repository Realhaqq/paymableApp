package ng.paymable;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
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

import ng.paymable.others.MySingleton;
import ng.sessions.SessionHandlerUser;

public class ActivityTv extends AppCompatActivity {
    private LinearLayout lyt_mtn, lyt_airtel, lyt_glo, lyt_etisalat;
    private TextView text_lyt;
    Button btn_continue;

    private int wallet_balance;
    EditText amount, cardno;

    ViewDialog viewDialog;
    SessionHandlerUser sessionHandlerUser;
    private String servicename, cardid, bid, url, typeid;

    private String orderid, type, ref, data;
    private int amounttobepaid;

    private Spinner sp_type, sp_tv_type, sp_tv_type2;
    ArrayList<String> name;
    ArrayList<String> type_sp;
    ArrayList<String> meter_type_list;

    ArrayList<String> _url = new ArrayList<String>();
    ArrayList<String> type_amount = new ArrayList<String>();
    ArrayList<String> type_id = new ArrayList<String>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tv);
        btn_continue = findViewById(R.id.btn_continue);


        name = new ArrayList<>();
        type_sp = new ArrayList<>();
        viewDialog = new ViewDialog(this);
        sessionHandlerUser = new SessionHandlerUser(getApplicationContext());

        CheckBalance();
        viewDialog = new ViewDialog(this);
        sp_type = findViewById(R.id.sp_type);
        sp_tv_type = findViewById(R.id.sp_tv_type);
        sp_tv_type2 = findViewById(R.id.sp_tv_type2);



//        final String[] companies={"KEDCO","AEDC","EEDC","IBEDC","JED","ikeja-electricity","EKEDC","PHEDC"};
        final String[] tv_type={"DSTV", "GOTV", "STARTIMES"};

        cardno = findViewById(R.id.smartcardno);
        text_lyt = findViewById(R.id.text_lyt);
        amount = findViewById(R.id.amount);

        btn_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                String aamount = amount.getText().toString();
                String ccardno = cardno.getText().toString();


                if (ccardno.isEmpty()) {
                    cardno.setError("Enter Phone Number");
                    cardno.requestFocus();
                }else if(aamount.isEmpty()) {
                    amount.setError("Enter Phone Number");
                    amount.requestFocus();
                }else {
                    CheckOrder(aamount, ccardno);
                }
            }
        });

        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_dropdown_item, tv_type);
        spinnerArrayAdapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item );

// Spinner spinYear = (Spinner)findViewById(R.id.spin);
        sp_type.setAdapter(spinnerArrayAdapter);



        sp_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String comp = sp_type.getSelectedItem().toString();

                servicename = comp;
                text_lyt.setText(comp);

                String tv;
                if(comp.contains("DSTV")){
                    tv = "dstv";
                }else if(comp.contains("GOTV")){
                    tv = "gotv";
                }else{
                    tv = "startimes";
                }
                name.clear();
                _url.clear();
                LoadSpinnerData(tv);

//                Toast.makeText(getApplicationContext(), comp, Toast.LENGTH_LONG).show();

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });



        sp_tv_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {


                String id=_url.get(i);
                url=_url.get(i);

                if(id.contains("dstv-premium")){
                    cardid = "265";
                    bid = "266";
                }else if(id.contains("others")){
                    cardid = "267";
                    bid = "268";
                }else if(id.contains("dstv-access")){
                    cardid = "263";
                    bid = "264";
                }else if(id.contains("dstv-family")){
                    cardid = "261";
                    bid = "262";
                }else if(id.contains("dstv-compact")){
                    cardid = "259";
                    bid = "260";
                }else if(id.contains("dstv-premium")){
                    cardid = "258";
                    bid = "257";
                }else if(id.contains("dstv-boxoffice")){
                    cardid = "285";
                    bid = "286";
                }else if(id.contains("top-up")){
                    cardid = "339";
                    bid = "340";
                }else if(id.contains("dstv-padi")){
                    cardid = "374";
                    bid = "375";
                }else if(id.contains("dstv-yanga")){
                    cardid = "376";
                    bid = "377";
                }else if(id.contains("dstv-confam")){
                    cardid = "378";
                    bid = "379";
                }

//                Toast.makeText(getApplicationContext(), id, Toast.LENGTH_LONG).show();
                type_sp.clear();
                LoadSpinnerData2(id);


            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        sp_tv_type2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

//                Toast.makeText(getApplicationContext(), cardid, Toast.LENGTH_LONG).show();


                String amountt=type_amount.get(i);
                typeid =type_id.get(i);

                amount.setText(amountt);
//
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }


    private void displayDialog(final String orderid, String type, final String ref, final int amounttobepaid, String data)
    {
        final Dialog d=new Dialog(ActivityTv.this);
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
                Intent intent = new Intent(ActivityTv.this, ActivityPayCard.class);
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



    private void LoadSpinnerData(String tv) {
        viewDialog.showDialog();
        RequestQueue requestQueue=Volley.newRequestQueue(getApplicationContext());
        StringRequest stringRequest=new StringRequest(Request.Method.GET, "https://paymable.ng/json/" + tv + "-type.json", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                viewDialog.hideDialog();
                try{
                    JSONObject jsonObject=new JSONObject(response);
//                    if(jsonObject.getInt("success")==1){
                    JSONArray jsonArray=jsonObject.getJSONArray("serviceList");
                    for(int i=0;i<jsonArray.length();i++){
                        JSONObject jsonObject1=jsonArray.getJSONObject(i);
                        String value=jsonObject1.getString("name");
//                        String id=jsonObject1.getString("id");
                        String url=jsonObject1.getString("url");
                        _url.add(url);
//                        _amount.add(amount);
                        name.add(value);

//                        }
                    }
                    sp_tv_type.setAdapter(new ArrayAdapter<String>(ActivityTv.this, android.R.layout.simple_spinner_dropdown_item, name));
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



    private void LoadSpinnerData2(String url) {
        viewDialog.showDialog();
        RequestQueue requestQueue=Volley.newRequestQueue(getApplicationContext());
        StringRequest stringRequest=new StringRequest(Request.Method.GET, "https://paymable.ng/json/" + url +".json", new Response.Listener<String>() {
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
                        type_amount.add(amount);
                        type_id.add(id);
                        type_sp.add(value);

//                        }
                    }
                    sp_tv_type2.setAdapter(new ArrayAdapter<String>(ActivityTv.this, android.R.layout.simple_spinner_dropdown_item, type_sp));
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


    private void CheckOrder(String amount, String ccardno) {
        viewDialog.showDialog();
        Intent intent = getIntent();
        String url_ = Config.url+"kobopay/tv_init.php?userid="+ sessionHandlerUser.getUserDetail().getUserid() + "&url=" + url + "&cardid=" + cardid + "&cardno="+ ccardno + "&type="+ typeid + "&bid=" + bid + "&amount=" + amount;

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
                        alert.showDialog(ActivityTv.this, jsonObject.getString("message"));

//                                Toast.makeText(getApplicationContext(), "message2" +  response.getString("message"), Toast.LENGTH_LONG).show();
                    }else{

                        ViewDialogAlert alert = new ViewDialogAlert();
                        alert.showDialog(ActivityTv.this, jsonObject.getString("message"));

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
                        alert.showDialog(ActivityTv.this, jsonObject.getString("message"));
                    } else{
                        ViewDialogAlert alert = new ViewDialogAlert();
                        alert.showDialog(ActivityTv.this, jsonObject.getString("message"));
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
