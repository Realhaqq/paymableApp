package ng;

import java.util.ArrayList;
import android.app.Activity;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import ng.paymable.R;

public class CustomSpinner extends Activity {

    /**************  Intialize Variables *************/
    public  ArrayList<SpinnerModel> CustomListViewValuesArr = new ArrayList<SpinnerModel>();
    TextView output = null;
    CustomAdapter adapter;
    CustomSpinner activity = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_spinner);

        activity  = this;

        Spinner  SpinnerExample = (Spinner)findViewById(R.id.spinner);
        output                  = (TextView)findViewById(R.id.output);

        // Set data in arraylist
        setListData();

        // Resources passed to adapter to get image
        Resources res = getResources();

        // Create custom adapter object ( see below CustomAdapter.java )
        adapter = new CustomAdapter(activity, R.layout.spinner_rows, CustomListViewValuesArr,res);

        // Set adapter to spinner
        SpinnerExample.setAdapter(adapter);

        // Listener called when spinner item selected
        SpinnerExample.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View v, int position, long id) {
                // your code here

                // Get selected row data to show on screen
                String Company    = ((TextView) v.findViewById(R.id.company)).getText().toString();
//                String CompanyUrl = ((TextView) v.findViewById(R.id.sub)).getText().toString();

                String OutputMsg = "Selected Company : \n\n"+Company+"\n";
                output.setText(OutputMsg);

                Toast.makeText(
                        getApplicationContext(),OutputMsg, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });
    }

    /****** Function to set data in ArrayList *************/
    public void setListData()
    {

        // Now i have taken static values by loop.
        // For further inhancement we can take data by webservice / json / xml;

        for (int i = 0; i < 11; i++) {

            final SpinnerModel sched = new SpinnerModel();

            /******* Firstly take data in model object ******/
            sched.setCompanyName("Company "+i);
            sched.setImage("image"+i);
//            sched.setUrl("http:\\www."+i+".com");

            /******** Take Model Object in ArrayList **********/
            CustomListViewValuesArr.add(sched);
        }

    }

}
