package ng.paymable;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import ng.paymable.others.ProjectUtils;
import ng.sessions.SessionHandlerUser;

public class ActivityPayCardWeb extends AppCompatActivity {
    SessionHandlerUser sessionHandlerUser;
    private Context mContext;
    private WebView wvPayment;
    private ImageView IVback;
    private String PAYMENT_FAIL;
    private String PAYMENT_SUCCESS;
    ViewDialog viewDialog;
    String url;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paymet_web);
        mContext = ActivityPayCardWeb.this;
        sessionHandlerUser = new SessionHandlerUser(getApplicationContext());
        viewDialog = new ViewDialog(this);
        Intent intent = getIntent();


        String orderid, ref, amount, userid;

        orderid = getIntent().getStringExtra("orderid");
        ref = getIntent().getStringExtra("ref");
        amount = getIntent().getStringExtra("myamount");
        userid = getIntent().getStringExtra("userid");
            PAYMENT_FAIL = Config.url + "credit/fail.php";
            PAYMENT_SUCCESS = Config.url + "credit/success.php";
            url = Config.url + "credit/pay_card_web.php?userid="+sessionHandlerUser.getUserDetail().getUserid() + "&orderid=" + orderid + "&ref=" + ref + "&amount=" + getIntent().getStringExtra("myamount");

//        Log.d("myuu", url);
        wvPayment = findViewById(R.id.wvPayment);
        WebSettings settings = wvPayment.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        wvPayment.loadUrl(url);
        wvPayment.setWebViewClient(new SSLTolerentWebViewClient());
        init();
    }

    private void init() {
        IVback = (ImageView) findViewById(R.id.IVback);
        IVback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.click_event));
                finish();
            }
        });
    }

    private class SSLTolerentWebViewClient extends WebViewClient {

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            super.onReceivedSslError(view, handler, error);
//            viewDialog.hideDialog();
            // this will ignore the Ssl error and will go forward to your site
            handler.proceed();
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
//            viewDialog.showDialog();
            // TODO Auto-generated method stub
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
//            viewDialog.hideDialog();
            view.loadUrl(url);
            return super.shouldOverrideUrlLoading(view, url);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
//            viewDialog.hideDialog();
            ProjectUtils.pauseProgressDialog();
            //Page load finished
            if (url.equals(PAYMENT_SUCCESS)) {
                View layout = getLayoutInflater().inflate(R.layout.toast_custom, (ViewGroup) findViewById(R.id.custom_toast_layout_id));
                TextView text = layout.findViewById(R.id.text);
                text.setText("Order Confirmed successful");
                Toast toast = new Toast(getApplicationContext());
                toast.setDuration(Toast.LENGTH_LONG);
                toast.setView(layout);
                toast.show();
                super.onPageFinished(view, PAYMENT_SUCCESS);

                finish();
                wvPayment.clearCache(true);
                wvPayment.clearHistory();
                wvPayment.destroy();
            } else if (url.equals(PAYMENT_FAIL)) {
                View layout = getLayoutInflater().inflate(R.layout.toast_custom, (ViewGroup) findViewById(R.id.custom_toast_layout_id));
                TextView text = layout.findViewById(R.id.text);
                text.setText("Order failed");
                Toast toast = new Toast(getApplicationContext());
                toast.setDuration(Toast.LENGTH_LONG);
                toast.setView(layout);
                toast.show();
                super.onPageFinished(view, PAYMENT_FAIL);

                finish();
                wvPayment.clearCache(true);
                wvPayment.clearHistory();
                wvPayment.destroy();
            } else {
//                viewDialog.hideDialog();
                super.onPageFinished(view, url);
            }

//            viewDialog.hideDialog();



        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            // TODO Auto-generated method stub
            try {
                wvPayment.stopLoading();
            } catch (Exception e) {
            }

            if (wvPayment.canGoBack()) {
                wvPayment.goBack();
            }

            wvPayment.loadUrl("about:blank");
            AlertDialog alertDialog = new AlertDialog.Builder(ActivityPayCardWeb.this).create();
            alertDialog.setTitle("Error");
            alertDialog.setMessage("Check your internet connection and try again.");
            alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Try Again", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                    startActivity(getIntent());
                }
            });
            alertDialog.show();

            super.onReceivedError(view, errorCode, description, failingUrl);
        }
    }


    @Override
    // This method is used to detect back button
    public void onBackPressed() {
        if(wvPayment.canGoBack()) {
            wvPayment.goBack();
        } else {
            // Let the system handle the back button
            setResult(RESULT_OK);
            super.onBackPressed();

        }
    }



}
