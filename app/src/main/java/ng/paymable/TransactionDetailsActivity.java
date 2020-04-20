package ng.paymable;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class TransactionDetailsActivity extends AppCompatActivity {

TextView orderid, ondate, data, type, amount, status;
ImageView back;
TextView txt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_details);
        txt= findViewById(R.id.txt);
        back= findViewById(R.id.back);
        txt.setText("My Transaction");
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        orderid = findViewById(R.id.orderid);
        ondate = findViewById(R.id.ondate);
        data = findViewById(R.id.data);
        type = findViewById(R.id.type);
        amount = findViewById(R.id.amount);
        status = findViewById(R.id.status);

        orderid.setText(getIntent().getStringExtra("orderid"));
        ondate.setText(getIntent().getStringExtra("ondate"));
        data.setText(getIntent().getStringExtra("biller"));
        type.setText(getIntent().getStringExtra("type"));
        amount.setText("₦" + getIntent().getStringExtra("amount"));


        String s;
        if (getIntent().getStringExtra("amount").contains("1")){
            s = "Successful";
        }else{
            s = "Pending";
        }
        status.setText(s);




    }
}
