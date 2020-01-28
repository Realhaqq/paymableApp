package ng.paymable;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import ng.paymable.others.MySingleton;
import ng.sessions.SessionHandlerUser;

public class LoginActivity extends AppCompatActivity {

    ViewDialog viewDialog;
    SessionHandlerUser sessionHandlerUser;
    private static final String KEY_STATUS = "status";
    private static final String KEY_MESSAGE = "message";
    private static final String KEY_EMPTY = "";

    EditText etphone, etpassword;
    String phone, password;
    TextView txtregister;
    Button btn_login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        sessionHandlerUser = new SessionHandlerUser(getApplicationContext());
        viewDialog = new ViewDialog(this);

        if (sessionHandlerUser.isLoggedIn()){
            loadDashboard();
        }

        etphone = findViewById(R.id.phone);
        etpassword = findViewById(R.id.password);

        txtregister = findViewById(R.id.txt_signup);

        txtregister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(intent);
            }
        });

        btn_login = findViewById(R.id.btn_login);
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = etphone.getText().toString();
                String password = etpassword.getText().toString();
                if (phone.isEmpty()) {
                    etphone.setError("Enter Phone Number");
                    etphone.requestFocus();
                }else if(password.isEmpty()){
                    etpassword.setError("Enter Password");
                    etpassword.requestFocus();
                }else {
                    MakeLogin(phone, password);
                }

            }
        });




    }


    private void loadDashboard() {
        Intent i = new Intent(getApplicationContext(), HomePay2Activity.class);
        startActivity(i);
        finish();
    }

    private void MakeLogin(String phone, String password) {

        viewDialog.showDialog();
        JSONObject request = new JSONObject();
        try {
            //Populate the request parameters
            request.put("phone", phone);
            request.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsArrayRequest = new JsonObjectRequest
                (Request.Method.POST, Config.user_login, request, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        viewDialog.hideDialog();
                        try {
                            //Check if user got logged in successfully
                            if (response.getInt(KEY_STATUS) == 0) {
                                sessionHandlerUser.loginUser(response.getString("email"), response.getString("fullname"), response.getString("phone"), response.getString("userid"));

//
//                                if (response.getString("verify_status").contains("0")){
//                                    Intent intent = new Intent(LoginActivity.this, OtpActivity.class);
//                                    intent.putExtra("phone", response.getString("phone"));
//                                    startActivity(intent);
//
//                                }else{
                                    loadDashboard();
//                                }
                            } else {

                                View layout = getLayoutInflater().inflate(R.layout.toast_custom, (ViewGroup) findViewById(R.id.custom_toast_layout_id));
                                TextView text = layout.findViewById(R.id.text);
                                text.setText(response.getString(KEY_MESSAGE));
                                Toast toast = new Toast(getApplicationContext());
                                toast.setDuration(Toast.LENGTH_LONG);
                                toast.setView(layout);
                                toast.show();

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        viewDialog.hideDialog();
                        //Display error message whenever an error occurs
                        Toast.makeText(getApplicationContext(),
                                error.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });
        // Access the RequestQueue through your singleton class.
        MySingleton.getInstance(this).addToRequestQueue(jsArrayRequest);
    }


    /**
     * Validates inputs and shows error if any
     *
     * @return
     */
    private boolean validateInputs() {
        if (KEY_EMPTY.equals(phone)) {
            etphone.setError("Phone Number cannot be empty");
            etphone.requestFocus();
            return false;
        }
        if (KEY_EMPTY.equals(password)) {
            etpassword.setError("Password cannot be empty");
            etpassword.requestFocus();
            return false;
        }
        return true;
    }
}
