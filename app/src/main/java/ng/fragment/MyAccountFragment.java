package ng.fragment;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import Adapters.GridAdapter;
import Adapters.ProfilePagerAdapter_walkthrough_01;
import Models.GridModel;
import me.relex.circleindicator.CircleIndicator;
import ng.paymable.LoginActivity;
import ng.paymable.R;
import ng.paymable.SaveCardsActivity;
import ng.paymable.ViewDialog;
import ng.paymable.others.MySingleton;
import ng.sessions.SessionHandlerUser;
import ng.paymable.Config;


/**
 * A simple {@link Fragment} subclass.
 */
public class MyAccountFragment extends Fragment {




    public MyAccountFragment() {
        // Required empty public constructor
    }


    private TextView txtname, txtemail, txtphone, txtbalance, txtlogoname;
    LinearLayout logout, card_save_card;
    SessionHandlerUser sessionHandlerUser;
    ViewDialog viewDialog;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        final View view = inflater.inflate(R.layout.fragment_my_account, null);

        viewDialog = new ViewDialog(getActivity());
        sessionHandlerUser = new SessionHandlerUser(getContext());
        txtname = view.findViewById(R.id.txtname);
        txtemail = view.findViewById(R.id.txtemail);
        txtphone = view.findViewById(R.id.txtphone);
        txtlogoname = view.findViewById(R.id.txtlogoname);

        txtbalance = view.findViewById(R.id.wallet_balance);

        CheckBalance();


        card_save_card = view.findViewById(R.id.card_save_cards);
        card_save_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), SaveCardsActivity.class);
                startActivity(intent);
            }
        });
        logout = view.findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sessionHandlerUser.logoutUser();
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });

        txtname.setText(sessionHandlerUser.getUserDetail().getFullname());
        txtphone.setText(sessionHandlerUser.getUserDetail().getPhone());
        txtemail.setText(sessionHandlerUser.getUserDetail().getEmail());
        char first = sessionHandlerUser.getUserDetail().getFullname().charAt(0);
        txtlogoname.setText(""+first);



        return view;
    }

    private void CheckBalance() {
        viewDialog.showDialog();
        String url_ = Config.user_wallet+"?userid="+ sessionHandlerUser.getUserDetail().getUserid();
        JSONObject request = new JSONObject();
        JsonObjectRequest jsArrayRequest = new JsonObjectRequest
                (Request.Method.POST, url_, request, new Response.Listener<JSONObject>() {
                    @SuppressLint("ResourceAsColor")
                    @Override
                    public void onResponse(JSONObject response) {
                        viewDialog.hideDialog();
                        try {
                            if (response.getInt("status") == 0) {

                               txtbalance.setText("₦" + response.getString("balance"));

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


}