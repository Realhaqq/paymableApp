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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

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
import ng.paymable.TransactionDetailsActivity;
import ng.paymable.ViewDialog;
import ng.paymable.adapters.TransactionAdapter;
import ng.paymable.adapters.TransactionWalletAdapter;
import ng.paymable.models.Transactions;
import ng.paymable.models.WalletTransactions;
import ng.paymable.others.MySingleton;
import ng.paymable.others.RecyclerTouchListener;
import ng.sessions.SessionHandlerUser;


/**
 * A simple {@link Fragment} subclass.
 */
public class WalletTransactionFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

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

    TextView wallet_balance;
    ImageView wallet_plus;
    public WalletTransactionFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        final View view = inflater.inflate(R.layout.fragment_wallet, null);
        sessionHandlerUser = new SessionHandlerUser(getActivity());
        viewDialog = new ViewDialog(getActivity());


        wallet_balance = view.findViewById(R.id.wallet_balance);
        wallet_plus = view.findViewById(R.id.wallet_plus);

        wallet_plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), AddWalletBalanceActivity.class);
                startActivity(intent);
            }
        });

        CheckBalance();

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

        GetTransactions();
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
        jsonArrayRequest = new JsonArrayRequest(Config.url + "wallet_transactions.php?userid=" + sessionHandlerUser.getUserDetail().getUserid(), new Response.Listener<JSONArray>() {
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
                getTransactionAdapter.setTransactio_type(json.getString("transaction_type"));
                getTransactionAdapter.setOndate(json.getString("ondate"));
                getTransactionAdapter.setPayment_type(json.getString("payment_type"));

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
        GetTransactions();
    }



    private void CheckBalance() {
        String url_ = Config.user_wallet+"?userid="+ sessionHandlerUser.getUserDetail().getUserid();
        JSONObject request = new JSONObject();
        JsonObjectRequest jsArrayRequest = new JsonObjectRequest
                (Request.Method.POST, url_, request, new Response.Listener<JSONObject>() {
                    @SuppressLint("ResourceAsColor")
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            if (response.getInt("status") == 0) {

                                wallet_balance.setText("₦" + response.getString("balance"));

                            } else {

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                });
        MySingleton.getInstance(getContext()).addToRequestQueue(jsArrayRequest);
    }




}