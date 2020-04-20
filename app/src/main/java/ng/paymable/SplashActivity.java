package ng.paymable;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import ng.paymable.others.MySingleton;

public class SplashActivity extends AppCompatActivity {

    ViewDialog viewDialog;
    private static int splashTimeOut=1000;
    private String verison = "1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity1_splash);

        viewDialog = new ViewDialog(this);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                checkUpdates();
//                loadDashboard();
            }
        },splashTimeOut);
    }


    private void loadDashboard(){
        Intent i = new Intent(SplashActivity.this, LoginActivity.class);
        if(Build.VERSION.SDK_INT>20){
            ActivityOptions options =
                    ActivityOptions.makeSceneTransitionAnimation(SplashActivity.this);
            startActivity(i,options.toBundle());
        }else {
            startActivity(i);
        }
        finish();
    }



    private void checkUpdates() {
        viewDialog.showDialog();
        String url_ = Config.url + "update.php";
        JSONObject request = new JSONObject();
        JsonObjectRequest jsArrayRequest = new JsonObjectRequest
                (Request.Method.POST, url_, request, new Response.Listener<JSONObject>() {
                    @SuppressLint("ResourceAsColor")
                    @Override
                    public void onResponse(JSONObject response) {
                        viewDialog.hideDialog();
                        try {
                            if (response.getInt("status") == 0) {
                                if(response.getString("updates").contains("1")){
                                    if(response.getString("version").contains(verison)){
                                        AlertDialog alertDialog = new AlertDialog.Builder(SplashActivity.this).create();
                                        alertDialog.setTitle("Attention!");
                                        alertDialog.setMessage("New Update Available! Update Paymable App now");
                                        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "UPDATE PAYMABLE", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
//                                        finish();
//                                        startActivity(getIntent());
                                                final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                                                try {
                                                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                                                } catch (android.content.ActivityNotFoundException anfe) {
                                                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                                                }


                                            }
                                        });
                                        alertDialog.show();
                                    }

                                }else{
                                    loadDashboard();

                                }

//                                txtbalance.setText("₦" + response.getString("balance"));

                            } else {

                                loadDashboard();
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
        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsArrayRequest);
    }
}
