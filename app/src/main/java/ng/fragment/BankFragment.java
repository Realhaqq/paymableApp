package ng.fragment;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import ng.paymable.AddWalletBalanceActivity;
import ng.paymable.Config;
import ng.paymable.R;
import ng.paymable.ViewDialog;
import ng.paymable.adapters.TransactionWalletAdapter;
import ng.paymable.models.WalletTransactions;
import ng.paymable.others.MySingleton;
import ng.paymable.others.RecyclerTouchListener;
import ng.sessions.SessionHandlerUser;


/**
 * A simple {@link Fragment} subclass.
 */
public class BankFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private ViewPager viewPager;
    RecyclerView theaters_recycleview;
    RequestQueue requestQueue;
    JsonArrayRequest jsonArrayRequest;

    //    Theater Recyler
    List<WalletTransactions> GetTransactionsAdapter;
    WalletTransactions getTransactionAdapter;
    RecyclerView.Adapter recyclerViewAdapterTheater;

    SwipeRefreshLayout mSwipeRefreshLayout;

    SessionHandlerUser sessionHandlerUser;
    ViewDialog viewDialog;


    String accountno;
    TextView account_name, account_no, balance, t_account_no, t_account_name;
    ImageView wallet_plus;
    public BankFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        final View view = inflater.inflate(R.layout.bank_fragment, null);
        sessionHandlerUser = new SessionHandlerUser(getActivity());
        viewDialog = new ViewDialog(getActivity());


        account_name = view.findViewById(R.id.account_name);
        balance = view.findViewById(R.id.balance);
        t_account_no = view.findViewById(R.id.t_account_no);
        t_account_name = view.findViewById(R.id.t_account_name);
//        wallet_plus = view.findViewById(R.id.wallet_plus);
//
//        wallet_plus.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(getActivity(), AddWalletBalanceActivity.class);
//                startActivity(intent);
//            }
//        });

        CheckAccountInfo();

        mSwipeRefreshLayout = view.findViewById(R.id.swipe_container);

        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);
        //Recycleview
        theaters_recycleview =  view.findViewById(R.id.recyclerView);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        theaters_recycleview.setLayoutManager(layoutManager);
        theaters_recycleview.setItemAnimator(new DefaultItemAnimator());
        GetTransactionsAdapter = new ArrayList<>();


        theaters_recycleview.addOnItemTouchListener(new RecyclerTouchListener(getContext(), theaters_recycleview, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
//                String biller = GetTransactionsAdapter.get(position).getBiller();
//                String amount = GetTransactionsAdapter.get(position).getAmount();
//                String ondate = GetTransactionsAdapter.get(position).getOndate();
//                String ref = GetTransactionsAdapter.get(position).getRef();
//                String type = GetTransactionsAdapter.get(position).getPayment_type();
//                String orderid = GetTransactionsAdapter.get(position).getOrderid();
//                String status = GetTransactionsAdapter.get(position).getStatus();
//                String data = GetTransactionsAdapter.get(position).getTransaction_data();
////                showDialogReceipt(name, amount, ondate, ref, type);
//
//                Intent intent = new Intent(getActivity(), TransactionDetailsActivity.class);
//                intent.putExtra("biller", biller);
//                intent.putExtra("amount", amount);
//                intent.putExtra("ondate", ondate);
//                intent.putExtra("ref", ref);
//                intent.putExtra("type", type);
//                intent.putExtra("biller", biller);
//                intent.putExtra("orderid", orderid);
//                intent.putExtra("status", status);
//                intent.putExtra("data", data);
//                startActivity(intent);
            }

            @Override
            public void onLongClick(View view, int position) {

            }

        }));

        return view;
    }

    private void GetTransactions() {
        viewDialog.showDialog();
        mSwipeRefreshLayout.setRefreshing(true);

        GetTransactionsAdapter.clear();
        jsonArrayRequest = new JsonArrayRequest(Config.url + "rubies/check_transactions.php?accountno="+ accountno, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                viewDialog.hideDialog();
                mSwipeRefreshLayout.setRefreshing(false);

                GetCardWebCall(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                viewDialog.hideDialog();
                mSwipeRefreshLayout.setRefreshing(false);

            }
        });
        requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(jsonArrayRequest);
    }

    private void GetCardWebCall(JSONArray array) {
        for (int i = 0; i < array.length(); i++){
            getTransactionAdapter = new WalletTransactions();
            JSONObject json = null;



            try {
                json = array.getJSONObject(i);
                getTransactionAdapter.setAmount(json.getString("amount"));
                getTransactionAdapter.setTransactio_type(json.getString("originatorname"));
                getTransactionAdapter.setOndate(json.getString("originatoraccountnumber"));
                getTransactionAdapter.setPayment_type(json.getString("narration"));

                Toast.makeText(getContext(), json.getString("originatorname"), Toast.LENGTH_LONG).show();

            } catch (JSONException e) {
                e.printStackTrace();
            }
            GetTransactionsAdapter.add(getTransactionAdapter);
        }

        recyclerViewAdapterTheater = new TransactionWalletAdapter(GetTransactionsAdapter, getContext());
        theaters_recycleview.setAdapter(recyclerViewAdapterTheater);
        recyclerViewAdapterTheater.notifyDataSetChanged();

    }


    @Override
    public void onRefresh() {
//        GetTransactions();
        CheckAccountInfo();
    }



    private void CheckBalance(String accountno) {
        viewDialog.showDialog();
        String url_ = Config.url+"rubies/check_balance.php?accountno="+ accountno;
        JSONObject request = new JSONObject();
        JsonObjectRequest jsArrayRequest = new JsonObjectRequest
                (Request.Method.POST, url_, request, new Response.Listener<JSONObject>() {
                    @SuppressLint("ResourceAsColor")
                    @Override
                    public void onResponse(JSONObject response) {
                        viewDialog.hideDialog();

                        try {
                            if (response.getInt("status") == 0) {

                                balance.setText("₦" + response.getString("balance"));

//                                GetTransactions();

                            } else {

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        viewDialog.hideDialog();
                    }
                });
        MySingleton.getInstance(getContext()).addToRequestQueue(jsArrayRequest);
    }



    private void CheckAccountInfo() {
        viewDialog.showDialog();
        String url_ = Config.url+"rubies/account_info.php?userid="+ sessionHandlerUser.getUserDetail().getUserid();
        JSONObject request = new JSONObject();
        JsonObjectRequest jsArrayRequest = new JsonObjectRequest
                (Request.Method.POST, url_, request, new Response.Listener<JSONObject>() {
                    @SuppressLint("ResourceAsColor")
                    @Override
                    public void onResponse(JSONObject response) {
                        viewDialog.hideDialog();

                        try {
                            if (response.getInt("status") == 0) {
                                account_name.setText(response.getString("account_name"));
                                accountno = response.getString("account_no");
                                t_account_no.setText(response.getString("account_no"));
                                t_account_name.setText(response.getString("account_name"));

                                CheckBalance(accountno);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        viewDialog.hideDialog();
                    }
                });
        MySingleton.getInstance(getContext()).addToRequestQueue(jsArrayRequest);
//        jsonArrayRequest.cancel();
    }




//    @Override
//    public void onPause() {
//        super.onPause();
////        if (requestQueue != null) {
////            requestQueue.cancelAll(this);
////        }
//
//    }
}