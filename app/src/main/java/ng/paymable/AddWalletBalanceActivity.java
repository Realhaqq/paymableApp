package ng.paymable;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import Adapters.SaveCardAdapter;
import ng.paymable.adapters.CardAdapter;
import ng.paymable.adapters.CardPayAdapter;
import ng.paymable.models.Cards;
import ng.paymable.others.RecyclerTouchListener;
import ng.paymable.others.RequestHandler;
import ng.sessions.SessionHandlerUser;

public class AddWalletBalanceActivity extends AppCompatActivity {
    TextView txt;
    TextView one,two,three,four,five,six,seven,eight,nine,zero;
    ImageView clear, add;
    EditText edtxt;
    String nos,number="";

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_wallet_balance);
        viewDialog = new ViewDialog(this);
        sessionHandlerUser = new SessionHandlerUser(getApplicationContext());

        one=findViewById(R.id.one);
        two=findViewById(R.id.two);
        three=findViewById(R.id.three);
        four=findViewById(R.id.four);
        five=findViewById(R.id.five);
        six=findViewById(R.id.six);
        seven=findViewById(R.id.seven);
        eight=findViewById(R.id.eight);
        nine=findViewById(R.id.nine);
        zero=findViewById(R.id.zero);
        clear=findViewById(R.id.clear);
        edtxt=findViewById(R.id.edtxt);

//        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        txt=findViewById(R.id.txt);

        txt.setText("Wallet");

        findViewById(R.id.btn_add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String amount = edtxt.getText().toString();

                if(amount.isEmpty()){

                }else{
//
                    showBottomSheetDialog();
                }

            }
        });


//        one.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//
//                number = number +"1";
//                edtxt.setText(number);
//                Log.e("number", number);
//
//            }
//        });
//        two.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                nos= (String) two.getText();
//                edtxt.setText(nos);
//
//                number = number +"2";
//                edtxt.setText(number);
//                Log.e("number", number);
//
//            }
//        });
//        three.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                nos= (String) three.getText();
//                edtxt.setText(nos);
//
//                number = number +"3";
//                edtxt.setText(number);
//                Log.e("number", number);
//
//
//            }
//        });
//        four.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                nos= (String) four.getText();
//                edtxt.setText(nos);
//
//                number = number +"4";
//                edtxt.setText(number);
//                Log.e("number", number);
//
//
//            }
//        });
//        five.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                nos= (String) five.getText();
//                edtxt.setText(nos);
//
//                number = number +"5";
//                edtxt.setText(number);
//                Log.e("number", number);
//
//
//            }
//        });
//        six.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                nos= (String) six.getText();
//                edtxt.setText(nos);
//
//                number = number +"6";
//                edtxt.setText(number);
//                Log.e("number", number);
//
//
//            }
//        });
//        seven.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                nos= (String) seven.getText();
//                edtxt.setText(nos);
//
//                number = number +"7";
//                edtxt.setText(number);
//                Log.e("number", number);
//
//            }
//        });
//        eight.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                nos= (String) eight.getText();
//                edtxt.setText(nos);
//
//                number = number +"8";
//                edtxt.setText(number);
//                Log.e("number", number);
//
//
//            }
//        });
//        nine.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                nos= (String) nine.getText();
//                edtxt.setText(nos);
//
//                number = number +"9";
//                edtxt.setText(number);
//                Log.e("number", number);
//
//
//            }
//        });
//        zero.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                nos= (String) zero.getText();
//                edtxt.setText(nos);
//
//                number = number +"0";
//                edtxt.setText(number);
//                Log.e("number", number);
//
//
//            }
//        });
//        clear.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                try {
//                    number = number.substring(0, number.length() - 1);
//
//                    edtxt.setText(number);
//                }catch (Exception e){
//
//                }
//
////                if (number != null && number.length() > 0 && number.charAt(number.length() - 1) == 'x') {
////                    number = number.substring(0, number.length() - 1);
////
////                    edtxt.setText(number);
////                }
//            }
//        });

        bottom_sheet = findViewById(R.id.bottom_sheet);
        mBehavior = BottomSheetBehavior.from(bottom_sheet);

    }




//    Bottom Sheet Pay
    private void showBottomSheetDialog() {
    if (mBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
        mBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }

    final View view = getLayoutInflater().inflate(R.layout.sheet_select_card, null);

    (view.findViewById(R.id.bt_close)).setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            mBottomSheetDialog.hide();
        }
    });

        recyclerView = view.findViewById(R.id.recyclerView);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        GetListAdapter = new ArrayList<>();


        GetCards();
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new RecyclerTouchListener.ClickListener() {

            @Override
            public void onClick(View view, int position) {
                view.setSelected(true);
                int cardno = GetListAdapter.get(position).getCard_number();
                mBottomSheetDialog.hide();

                ChargeNow(cardno);
//                Toast.makeText(getApplicationContext(), "selected", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onLongClick(View view, int position) {

            }

        }));

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



    private void GetCards() {

        viewDialog.showDialog();
        GetListAdapter.clear();

        jsonArrayRequest = new JsonArrayRequest(Config.url + "cards.php?userid=" + sessionHandlerUser.getUserDetail().getUserid(), new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                viewDialog.hideDialog();
                GetCardWebCall(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                viewDialog.hideDialog();
            }
        });

        requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonArrayRequest);
    }

    private void GetCardWebCall(JSONArray array) {
        for (int i = 0; i < array.length(); i++){
            getcardsAdapter = new Cards();
            JSONObject json = null;

            try {
                json = array.getJSONObject(i);
                getcardsAdapter.setAuthorization_key(json.getString("authorization_key"));
                getcardsAdapter.setCard_number(Integer.parseInt(json.getString("card_full_number")));
                getcardsAdapter.setCard_type(json.getString("card_type"));
                getcardsAdapter.setChannel(json.getString("channel"));
                getcardsAdapter.setSet_default(json.getString("set_default"));

            } catch (JSONException e) {
                e.printStackTrace();
            }

            GetListAdapter.add(getcardsAdapter);
        }

        recylerViewAdapter = new CardPayAdapter(GetListAdapter, this);
        recyclerView.setAdapter(recylerViewAdapter);
        recylerViewAdapter.notifyDataSetChanged();


    }




    private void ChargeNow(final int cardno) {
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

            }

            @Override
            protected String doInBackground(Bitmap... params) {
                HashMap<String,String> data = new HashMap<>();

                data.put("cardno", String.valueOf(cardno));
                data.put("amount", edtxt.getText().toString());


                String result = rh.sendPostRequest(Config.url + "pay/charge_card.php",data);

                return result;
            }
        }

        verifyref ui = new verifyref();
        ui.execute();


    }
}
