package ng.fragment;


import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

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
import ng.paymable.ViewPagerAdapter;
import ng.paymable.others.MySingleton;
import ng.paymable.others.SliderUtils;
import ng.paymable.Config;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    private ViewPager viewPager;
    private ProfilePagerAdapter_walkthrough_01 profilePagerAdapterWalkthrough01;

    LinearLayout card_wallet, card_airtime, card_data, card_elect, card_tv, card_waec, card_jamb;


    private ArrayList<GridModel> homeListModelClassArrayList1;
    private RecyclerView recyclerView;
    private GridAdapter bAdapter;



    ImageView help;

    int currentPage = 0;
    Timer timer;
    final long DELAY_MS = 500;//delay in milliseconds before task is to be executed
    final long PERIOD_MS = 3000; // time in milliseconds between successive task executions.



    //    Sliders
    LinearLayout sliderDotspanel;
    private int dotscount;
    private ImageView[] dots;

    RequestQueue rq;
    List<SliderUtils> sliderImg;
    ViewPagerAdapter viewPagerAdapter;
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


        help = view.findViewById(R.id.help);
        help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://help.paymable.ng"));
                startActivity(browserIntent);
            }
        });

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


        card_waec = view.findViewById(R.id.card_waec);
        card_waec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View layout = getLayoutInflater().inflate(R.layout.toast_custom, (ViewGroup) view.findViewById(R.id.custom_toast_layout_id));
                TextView text = layout.findViewById(R.id.text);
                text.setText("Waec PIN - Coming Soon!");
                Toast toast = new Toast(getContext());
                toast.setDuration(Toast.LENGTH_LONG);
                toast.setView(layout);
                toast.show();
            }
        });
        card_jamb = view.findViewById(R.id.card_jamb);
        card_jamb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View layout = getLayoutInflater().inflate(R.layout.toast_custom, (ViewGroup) view.findViewById(R.id.custom_toast_layout_id));
                TextView text = layout.findViewById(R.id.text);
                text.setText("JAMB e-PIN - Coming Soon!");
                Toast toast = new Toast(getContext());
                toast.setDuration(Toast.LENGTH_LONG);
                toast.setView(layout);
                toast.show();
            }
        });

//        viewPager = (ViewPager) view.findViewById(R.id.viewPager);
//
//        CircleIndicator indicator = (CircleIndicator) view.findViewById(R.id.indicator);
//
//        profilePagerAdapterWalkthrough01 = new ProfilePagerAdapter_walkthrough_01(getChildFragmentManager());
////
//        viewPager.setAdapter(profilePagerAdapterWalkthrough01);
////
//        indicator.setViewPager(viewPager);
////
//        profilePagerAdapterWalkthrough01.registerDataSetObserver(indicator.getDataSetObserver());


        sliderImg = new ArrayList<>();

        viewPager = view.findViewById(R.id.viewPager);

        sliderDotspanel = view.findViewById(R.id.SliderDots);


        boolean connected = false;
        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            connected = true;
        } else {
            connected = false;
        }


        if(connected == true){
            sendRequest();
        }

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
//                for(int i = 0; i< dotscount; i++){
//                    dots[i].setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.nonactive_dot));
//                }
//                dots[position].setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.active_dot));

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        try {

            /*After setting the adapter use the timer */
            final Handler handler = new Handler();
            final Runnable Update = new Runnable() {
                public void run() {
                    if (currentPage == 4-1) {
                        currentPage = 0;
                    }
                    viewPager.setCurrentItem(currentPage++, true);
                }
            };

            timer = new Timer(); // This will create a new Thread
            timer.schedule(new TimerTask() { // task to be scheduled
                @Override
                public void run() {
                    handler.post(Update);
                }
            }, DELAY_MS, PERIOD_MS);
        }catch (Exception e){


        }


        //Recycler View Code


        recyclerView = view.findViewById(R.id.recyclerView);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(),2);
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



    public void sendRequest(){

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, Config.url + "image_slides.php", null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

                for(int i = 0; i < response.length(); i++){

                    SliderUtils sliderUtils = new SliderUtils();

                    try {
                        JSONObject jsonObject = response.getJSONObject(i);

                        sliderUtils.setSliderImageUrl(jsonObject.getString("image_url"));

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    sliderImg.add(sliderUtils);

                }

                viewPagerAdapter = new ViewPagerAdapter(sliderImg, getActivity());

                viewPager.setAdapter(viewPagerAdapter);

                dotscount = viewPagerAdapter.getCount();
                dots = new ImageView[dotscount];

                for(int i = 0; i < dotscount; i++){

                    dots[i] = new ImageView(getActivity());
                    dots[i].setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.nonactive_dot));

                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                    params.setMargins(8, 0, 8, 0);

                    sliderDotspanel.addView(dots[i], params);

                }

                dots[0].setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.active_dot));

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        MySingleton.getInstance(getActivity()).addToRequestQueue(jsonArrayRequest);

    }

}