package ng.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import ng.paymable.Config;

import Adapters.GridAdapter;
import Adapters.ProfilePagerAdapter_walkthrough_01;
import Models.GridModel;
import me.relex.circleindicator.CircleIndicator;
import ng.paymable.ActivityAirtime;
import ng.paymable.ActivityData;
import ng.paymable.ActivityElectricity;
import ng.paymable.ActivityTv;
import ng.paymable.AddWalletBalanceActivity;
import ng.paymable.R;
import ng.paymable.TransactionDetailsActivity;
import ng.paymable.ViewDialog;
import ng.paymable.adapters.TransactionAdapter;
import ng.paymable.models.Transactions;
import ng.paymable.others.RecyclerTouchListener;
import ng.sessions.SessionHandlerUser;


/**
 * A simple {@link Fragment} subclass.
 */
public class TransactionFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private ViewPager viewPager;
    RecyclerView theaters_recycleview;
    RequestQueue requestQueue;
    JsonArrayRequest jsonArrayRequest;

    //    Theater Recyler
    List<Transactions> GetTransactionsAdapter;
    Transactions getTransactionAdapter;
    RecyclerView.Adapter recyclerViewAdapterTheater;

    SwipeRefreshLayout mSwipeRefreshLayout;

    SessionHandlerUser sessionHandlerUser;
    ViewDialog viewDialog;
    public TransactionFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        final View view = inflater.inflate(R.layout.fragment_transaction, null);
        sessionHandlerUser = new SessionHandlerUser(getActivity());
        viewDialog = new ViewDialog(getActivity());


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
                String biller = GetTransactionsAdapter.get(position).getBiller();
                String amount = GetTransactionsAdapter.get(position).getAmount();
                String ondate = GetTransactionsAdapter.get(position).getOndate();
                String ref = GetTransactionsAdapter.get(position).getRef();
                String type = GetTransactionsAdapter.get(position).getPayment_type();
                String orderid = GetTransactionsAdapter.get(position).getOrderid();
                String status = GetTransactionsAdapter.get(position).getStatus();
                String data = GetTransactionsAdapter.get(position).getTransaction_data();
//                showDialogReceipt(name, amount, ondate, ref, type);

                Intent intent = new Intent(getActivity(), TransactionDetailsActivity.class);
                intent.putExtra("biller", biller);
                intent.putExtra("amount", amount);
                intent.putExtra("ondate", ondate);
                intent.putExtra("ref", ref);
                intent.putExtra("type", type);
                intent.putExtra("biller", biller);
                intent.putExtra("orderid", orderid);
                intent.putExtra("status", status);
                intent.putExtra("data", data);
                startActivity(intent);
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
        jsonArrayRequest = new JsonArrayRequest(Config.url + "transactions.php?userid=" + sessionHandlerUser.getUserDetail().getEmail(), new Response.Listener<JSONArray>() {
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
            getTransactionAdapter = new Transactions();
            JSONObject json = null;

            try {
                json = array.getJSONObject(i);
                getTransactionAdapter.setAmount(json.getString("amount"));
                getTransactionAdapter.setBiller(json.getString("service"));
                getTransactionAdapter.setOndate(json.getString("ondate"));
                getTransactionAdapter.setRef(json.getString("ref"));
                getTransactionAdapter.setPayment_type(json.getString("payment_type"));
                getTransactionAdapter.setStatus(json.getString("status"));
                getTransactionAdapter.setOrderid(json.getString("orderid"));

            } catch (JSONException e) {
                e.printStackTrace();
            }
            GetTransactionsAdapter.add(getTransactionAdapter);
        }

        recyclerViewAdapterTheater = new TransactionAdapter(GetTransactionsAdapter, getContext());
        theaters_recycleview.setAdapter(recyclerViewAdapterTheater);
        recyclerViewAdapterTheater.notifyDataSetChanged();

    }


    @Override
    public void onRefresh() {
        GetTransactions();
    }
}