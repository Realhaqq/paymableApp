package ng.paymable;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    private static int splashTimeOut=5000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity1_splash);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                loadDashboard();
            }
        },splashTimeOut);
    }


    private void loadDashboard(){
        Intent i = new Intent(SplashActivity.this, WelcomeActivity.class);
        if(Build.VERSION.SDK_INT>20){
            ActivityOptions options =
                    ActivityOptions.makeSceneTransitionAnimation(SplashActivity.this);
            startActivity(i,options.toBundle());
        }else {
            startActivity(i);
        }
        finish();
    }
}
