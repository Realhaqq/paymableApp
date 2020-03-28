package ng.fragment;


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

import java.util.ArrayList;

import Adapters.GridAdapter;
import Adapters.ProfilePagerAdapter_walkthrough_01;
import Models.GridModel;
import me.relex.circleindicator.CircleIndicator;
import ng.paymable.ActivityAirtime;
import ng.paymable.ActivityData;
import ng.paymable.ActivityElectricity;
import ng.paymable.ActivityTv;
import ng.paymable.AddNewCardActivity;
import ng.paymable.AddWalletBalanceActivity;
import ng.paymable.HomePay2Activity;
import ng.paymable.R;
import ng.paymable.SaveCardsActivity;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    private ViewPager viewPager;
    private ProfilePagerAdapter_walkthrough_01 profilePagerAdapterWalkthrough01;

    LinearLayout card_wallet, card_airtime, card_data, card_elect, card_tv;


    private ArrayList<GridModel> homeListModelClassArrayList1;
    private RecyclerView recyclerView;
    private GridAdapter bAdapter;


    String fieldName []={"Mobile","DTH","Data Card","Landline","Broadband",
            "Gas","Electricity","Water",
            "Insurance","Fees","Credit Card","Google Play"};
    Integer fieldImage []={R.drawable.mobile,R.drawable.dth,R.drawable.data_card,R.drawable.landlne,
            R.drawable.broadband,R.drawable.gas,R.drawable.electricity,
            R.drawable.water,R.drawable.insurance,R.drawable.fee,R.drawable.creditcard,R.drawable.google_play};


    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        final View view = inflater.inflate(R.layout.fragment_home, null);

        card_wallet = view.findViewById(R.id.card_wallet);
        card_wallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), AddWalletBalanceActivity.class);
                startActivity(intent);
            }
        });
        card_airtime = view.findViewById(R.id.card_airtime);
        card_airtime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ActivityAirtime.class);
                startActivity(intent);
            }
        });
        card_data = view.findViewById(R.id.card_data);
        card_data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ActivityData.class);
                startActivity(intent);
            }
        });
        card_elect = view.findViewById(R.id.card_electricity);
        card_elect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ActivityElectricity.class);
                startActivity(intent);
            }
        });

        card_tv = view.findViewById(R.id.card_tv);
        card_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ActivityTv.class);
                startActivity(intent);
            }
        });

        viewPager = (ViewPager) view.findViewById(R.id.viewPager);

        CircleIndicator indicator = (CircleIndicator) view.findViewById(R.id.indicator);

        profilePagerAdapterWalkthrough01 = new ProfilePagerAdapter_walkthrough_01(getChildFragmentManager());
//
        viewPager.setAdapter(profilePagerAdapterWalkthrough01);
//
        indicator.setViewPager(viewPager);
//
        profilePagerAdapterWalkthrough01.registerDataSetObserver(indicator.getDataSetObserver());


        //Recycler View Code


        recyclerView = view.findViewById(R.id.recyclerView);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(),4);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        homeListModelClassArrayList1 = new ArrayList<>();

        for (int i = 0; i < fieldName.length; i++) {
            GridModel beanClassForRecyclerView_contacts = new GridModel(fieldName[i],fieldImage[i]);
            homeListModelClassArrayList1.add(beanClassForRecyclerView_contacts);
        }
        bAdapter = new GridAdapter(getActivity(),homeListModelClassArrayList1);
        recyclerView.setAdapter(bAdapter);
        return view;
    }

}