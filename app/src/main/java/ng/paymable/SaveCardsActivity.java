package ng.paymable;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import Adapters.SaveCardAdapter;
import Models.SaveCardsModel;
import ng.paymable.adapters.CardAdapter;
import ng.paymable.models.Cards;
import ng.paymable.others.RecyclerTouchListener;
import ng.sessions.SessionHandlerUser;

public class SaveCardsActivity extends AppCompatActivity {
    TextView txt;

    List<Cards> GetListAdapter;
    Cards getcardsAdapter;
    JsonArrayRequest jsonArrayRequest;
    RequestQueue requestQueue;
    private RecyclerView recyclerView;
    RecyclerView.Adapter recylerViewAdapter;
    private SaveCardAdapter bAdapter;

    TextView add_new_card;
    ViewDialog viewDialog;
    SessionHandlerUser sessionHandlerUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_cards);

        sessionHandlerUser = new SessionHandlerUser(this);
        viewDialog = new ViewDialog(this);

        txt=findViewById(R.id.txt);
        txt.setText("Save Cards");

        add_new_card = findViewById(R.id.add_new_card);

        add_new_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SaveCardsActivity.this, AddNewCardActivity.class);
                startActivity(intent);
            }
        });

        recyclerView = findViewById(R.id.recyclerView);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(SaveCardsActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        GetListAdapter = new ArrayList<>();


        GetCards();
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new RecyclerTouchListener.ClickListener() {

            @Override
            public void onClick(View view, int position) {

            }

            @Override
            public void onLongClick(View view, int position) {

            }

        }));
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

        recylerViewAdapter = new CardAdapter(GetListAdapter, this);
        recyclerView.setAdapter(recylerViewAdapter);
        recylerViewAdapter.notifyDataSetChanged();


    }
}
