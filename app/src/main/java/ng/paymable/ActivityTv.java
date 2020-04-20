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
    private String service, service_category_id, idno, vname, vamount, aamount, url, type;

    private String orderid, ref, data;
    private int amounttobepaid;

    private Spinner sp_type, sp_tv_type, sp_tv_type2;
    ArrayList<String> name;
    ArrayList<String> type_sp;
    ArrayList<String> meter_type_list;

    ArrayList<String> _ids = new ArrayList<String>();
    ArrayList<String> _amount = new ArrayList<String>();
    ArrayList<String> type_id = new ArrayList<String>();

    ImageView back;
    TextView txt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tv);
        btn_continue = findViewById(R.id.btn_continue);
        txt= findViewById(R.id.txt);
        back= findViewById(R.id.back);
        txt.setText("CableTV");
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });



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

        sp_type.setAdapter(spinnerArrayAdapter);



        sp_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String comp = sp_type.getSelectedItem().toString();


                text_lyt.setText(comp);

                String tv;
                if(comp.contains("DSTV")){
                    service = "dstv";
                    service_category_id = "14";
                    tv = "dstv";
                }else if(comp.contains("GOTV")){
                    service = "gotv";
                    tv = "gotv";
                    service_category_id = "15";
                }else{
                    service = "startimes";
                    tv = "startimes";
                    service_category_id = "17";
                }
                name.clear();
                _amount.clear();
                _ids.clear();
                LoadSpinnerData(tv);

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });



        sp_tv_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {


                String id=_ids.get(i);
                idno=_ids.get(i);

                String aaamount=_amount.get(i);
                aaamount=_amount.get(i);

                amount.setText(aaamount);

                type = sp_tv_type.getSelectedItem().toString();





            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });




    }


    private void displayDialog(final String service_category_id, final String service, String ref, final int vamount, final String vname, final String type, final String ccardno)
    {
        final Dialog d=new Dialog(ActivityTv.this);
        d.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        d.setContentView(R.layout.dialog_cardno_option);
        d.setCancelable(true);
        d.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(d.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        d.setContentView(R.layout.dialog_cardno_option);

        TextView txtamout, txtmeterno, txtname, txtaddress, txtproduct;
        txtamout = d.findViewById(R.id.txtamount);
        txtaddress = d.findViewById(R.id.txtaddress);
        txtmeterno = d.findViewById(R.id.textmeterno);
        txtname = d.findViewById(R.id.txtname);
        txtproduct = d.findViewById(R.id.txtproduct);
        txtamout.setText("AMOUNT:   ₦" + vamount);
        txtaddress.setText("TYPE:   " + type);
        txtmeterno.setText("CARDNO:   " + ccardno);
        txtname.setText("CARD NAME:   " + vname);
        txtproduct.setText("SERVICE:   " + service);
        d.findViewById(R.id.btn_continue).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(wallet_balance < vamount){
                    Toast.makeText(getApplicationContext(), "Insufficient Wallet Balance", Toast.LENGTH_LONG).show();
                }else{
                    d.dismiss();
                    ProcessOrder(vamount, service_category_id, service, vname, type, ccardno);
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



    private void LoadSpinnerData(String tv) {
        viewDialog.showDialog();
        RequestQueue requestQueue=Volley.newRequestQueue(getApplicationContext());
        StringRequest stringRequest=new StringRequest(Request.Method.GET, "https://paymable.ng/rubies_json/" + tv + ".json", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                viewDialog.hideDialog();
                try{
                    JSONObject jsonObject=new JSONObject(response);
//                    if(jsonObject.getInt("success")==1){
                    JSONArray jsonArray=jsonObject.getJSONArray("productcategories");
                    for(int i=0;i<jsonArray.length();i++){
                        JSONObject jsonObject1=jsonArray.getJSONObject(i);
                        String value=jsonObject1.getString("name");
                        String id=jsonObject1.getString("bundleCode");
                        String amount=jsonObject1.getString("amount");
                        _ids.add(id);
                        _amount.add(amount);
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




    private void CheckOrder(String amount, final String ccardno) {
        viewDialog.showDialog();
        Intent intent = getIntent();
        String url_ = Config.url+"rubies/tv_verify.php?userid="+ sessionHandlerUser.getUserDetail().getUserid() + "&service=" + service + "&service_category_id="+ service_category_id + "&cardno=" + ccardno + "&amount=" + amount + "&type=" + type;
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
//                        orderid = jsonObject.getString("orderid");
                        type = jsonObject.getString("type");
                        ref = jsonObject.getString("ref");
//                        data = jsonObject.getString("ddata");
//                        amounttobepaid = Integer.parseInt(jsonObject.getString("amount"));

                        service_category_id = jsonObject.getString("service_category_id");
                        ref = jsonObject.getString("ref");
                        vname = jsonObject.getString("name");
                        vamount = String.valueOf(Integer.parseInt(jsonObject.getString("amount")));
//                        Toast.makeText(getApplicationContext(), "message2" +  jsonObject.getString("amount"), Toast.LENGTH_LONG).show();

                        displayDialog(service_category_id, service, ref, Integer.parseInt(vamount), vname, type, ccardno);
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



    private void ProcessOrder(int vamount, String service_category_id, String service, String vname, String type, String ccardno){
        viewDialog.showDialog();
        Intent intent = getIntent();
        String url_ = Config.url+"rubies/tv_process.php?userid="+ sessionHandlerUser.getUserDetail().getUserid() + "&service_category_id=" + service_category_id + "&idno=" + idno + "&amount=" + vamount + "&type=" + type + "&cardno=" + ccardno + "&service=" + service + "&name=" + vname;
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
