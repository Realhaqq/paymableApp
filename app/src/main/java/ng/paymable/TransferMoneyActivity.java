package ng.paymable;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import Adapters.SaveCardAdapter;
import ng.paymable.adapters.CardPayAdapter;
import ng.paymable.models.Cards;
import ng.paymable.others.MySingleton;
import ng.paymable.others.RecyclerTouchListener;
import ng.paymable.others.RequestHandler;
import ng.sessions.SessionHandlerUser;

public class TransferMoneyActivity extends AppCompatActivity {
    TextView txt;
    TextView one,two,three,four,five,six,seven,eight,nine,zero;
    ImageView clear, add;
    EditText amount, account_no;
    String bankname, bankcode, accountnumber, accountname, vamount;

    private int wallet_balance;

    SessionHandlerUser sessionHandlerUser;
    private BottomSheetBehavior mBehavior;
    private BottomSheetDialog mBottomSheetDialog;
    private View bottom_sheet;
    ViewDialog viewDialog;

    List<Cards> GetListAdapter;
    Cards getcardsAdapter;
    JsonArrayRequest jsonArrayRequest;
    RequestQueue requestQueue;
    private RecyclerView recyclerView;
    RecyclerView.Adapter recylerViewAdapter;
    private SaveCardAdapter bAdapter;
    Spinner sp_bank;

    ArrayList<String> comp_list;
    ArrayList<String> _ids = new ArrayList<String>();


    TextView new_card;
    ImageView back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer);
        viewDialog = new ViewDialog(this);
        sessionHandlerUser = new SessionHandlerUser(getApplicationContext());



//        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        txt=findViewById(R.id.txt);

        account_no = findViewById(R.id.account_no);
        amount = findViewById(R.id.amount);
        sp_bank = findViewById(R.id.sp_bank);


        comp_list = new ArrayList<>();


        CheckBalance();

        LoadSpinnerData();

        txt.setText("Send Money");
        back= findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        findViewById(R.id.btn_continue).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String amountt = amount.getText().toString();
                String accountno = account_no.getText().toString();

                if(amountt.isEmpty()){

                }else{
//
//                    showBottomSheetDialog();

                    VerifyBank(amountt, accountno, bankcode, bankname);
                }


            }
        });


        sp_bank.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                bankcode =_ids.get(i);
                String nname=sp_bank.getSelectedItem().toString();
                bankname =sp_bank.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });



        bottom_sheet = findViewById(R.id.bottom_sheet);
        mBehavior = BottomSheetBehavior.from(bottom_sheet);

    }




//    Bottom Sheet Pay
    private void showBottomSheetDialog(final String accountname, final String accountnumber, final String bankcode, String bankname, final int vamount) {
    if (mBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
        mBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }

    final View view = getLayoutInflater().inflate(R.layout.sheet_account_detail, null);

    (view.findViewById(R.id.bt_close)).setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            mBottomSheetDialog.hide();
        }
    });




    TextView amnt, bank, acct_name, acct_no;
    new_card = view.findViewById(R.id.btn_continue);
    amnt = view.findViewById(R.id.amount);
    bank = view.findViewById(R.id.bank_name);
    acct_name = view.findViewById(R.id.account_name);
    acct_no = view.findViewById(R.id.account_no);


        amnt.setText("AMOUNT:   ₦" + vamount);
        bank.setText("BANK:   " + bankname);
        acct_name.setText("ACCOUNT NAME:   " + accountname);
        acct_no.setText("ACCOUNT NO:   " + accountnumber);

//    new_card.setVisibility(View.GONE);
    new_card.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            if(wallet_balance < vamount){
                Toast.makeText(getApplicationContext(), "Insufficient Wallet Balance", Toast.LENGTH_LONG).show();
            }else{
                ProcessOrder(vamount, accountname, accountnumber, bankcode);
            }

        }
    });
//
//        (view.findViewById(R.id.user_new_card)).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//            }
//        });



    mBottomSheetDialog = new BottomSheetDialog(this);
    mBottomSheetDialog.setContentView(view);
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        mBottomSheetDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
    }


    // set background transparent
    ((View) view.getParent()).setBackgroundColor(getResources().getColor(android.R.color.transparent));

    mBottomSheetDialog.show();
    mBottomSheetDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
        @Override
        public void onDismiss(DialogInterface dialog) {
            mBottomSheetDialog = null;
        }
    });

}




    private void LoadSpinnerData() {
        viewDialog.showDialog();
        RequestQueue requestQueue=Volley.newRequestQueue(getApplicationContext());
        StringRequest stringRequest=new StringRequest(Request.Method.GET, "https://paymable.ng/rubies_json/banklist.json", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                viewDialog.hideDialog();
                try{
                    JSONObject jsonObject=new JSONObject(response);
//                    if(jsonObject.getInt("success")==1){
                    JSONArray jsonArray=jsonObject.getJSONArray("banklist");
                    for(int i=0;i<jsonArray.length();i++){
                        JSONObject jsonObject1=jsonArray.getJSONObject(i);
                        String value=jsonObject1.getString("bankname");
                        String id=jsonObject1.getString("bankcode");
//                        String url=jsonObject1.getString("url");
                        _ids.add(id);
//                        _amount.add(amount);
                        comp_list.add(value);

//                        }
                    }
                    sp_bank.setAdapter(new ArrayAdapter<String>(TransferMoneyActivity.this, android.R.layout.simple_spinner_dropdown_item, comp_list));
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



    private void VerifyBank(String amountt, String account_no, final String bankcodee, String banknamee) {
        viewDialog.showDialog();
        Intent intent = getIntent();
        String url_ = Config.url+"rubies/bank_verify.php?userid="+ sessionHandlerUser.getUserDetail().getUserid() + "&accountno=" + account_no + "&bankcode="+ bankcodee + "&bankname=" + banknamee + "&amount=" + amountt;
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
                        accountnumber = jsonObject.getString("accountnumber");
                        accountname = jsonObject.getString("accountname");
                        bankcode = jsonObject.getString("bankcode");
                        bankname = jsonObject.getString("bankname");
                        vamount = String.valueOf(Integer.parseInt(jsonObject.getString("amount")));


                        showBottomSheetDialog(accountname, accountnumber, bankcode, bankname, Integer.parseInt(vamount));
                    } else if(jsonObject.getInt("status") == 1) {

                        ViewDialogAlert alert = new ViewDialogAlert();
                        alert.showDialog(TransferMoneyActivity.this, jsonObject.getString("message"));

                    }else{

                        ViewDialogAlert alert = new ViewDialogAlert();
                        alert.showDialog(TransferMoneyActivity.this, jsonObject.getString("message"));

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


    private void ProcessOrder(int vamount, String accountname, String accountnumber, String bankcode){
        viewDialog.showDialog();
        Intent intent = getIntent();
        String url_ = Config.url+"rubies/bank_process.php?userid="+ sessionHandlerUser.getUserDetail().getUserid() + "&accountnumber=" + accountnumber + "&accountname=" + accountname + "&amount=" + vamount + "&bankcode=" + bankcode + "&bankname=" + bankname;
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
                        alert.showDialog(TransferMoneyActivity.this, jsonObject.getString("message"));
                    } else{
                        ViewDialogAlert alert = new ViewDialogAlert();
                        alert.showDialog(TransferMoneyActivity.this, jsonObject.getString("message"));
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
