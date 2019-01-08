package com.rayanehsabz.choobid.Peyment;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.adjust.sdk.Adjust;
import com.adjust.sdk.AdjustEvent;
import com.rayanehsabz.choobid.Adabters.Mydatabase;
import com.rayanehsabz.choobid.Tools.AppVariables;
import com.rayanehsabz.choobid.Tools.CalendarTool;
import com.rayanehsabz.choobid.Activities.MainActivity;
import com.rayanehsabz.choobid.R;

import org.json.JSONObject;

import java.util.List;

public class BankResultActivity extends AppCompatActivity {

    Bundle extra;
    String invoiceId;

    String email;
    String pass;

    String accId;

    Mydatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bank_result);

        db = new Mydatabase(this);

        email = db.getSettingString(8);
        pass = db.getSettingString(9);

        accId = db.getSettingString(6);

        extra = getIntent().getExtras();

        Uri ss = getIntent().getData();
        if (ss != null) {
            List<String> patch = ss.getPathSegments();
            Toast.makeText(this, patch + "", Toast.LENGTH_SHORT).show();
        }

        if (extra != null) {

            invoiceId = extra.getString("invoiceId");

        }

        new InvoiceStatusTasck().execute(

                AppVariables.getServerAddress() + "/choobid-portlet/api/jsonws/account/get-invoice-status/email/" + CalendarTool.getCoded(email) + "/pass/" + CalendarTool.getCoded(pass) + "/acc-id/" + accId + "/invoice-no/" + invoiceId
        );
    }


    public class InvoiceStatusTasck extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            return AppVariables.readJSONFeed(strings[0]);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            try {

                JSONObject jso = new JSONObject(s);
                if (jso.length() > 0 ) {

                    final String status = jso.getString("status");
                    if (status.equals("0")) {

                        ((TextView) findViewById(R.id.invoiceStatus)).setText(getResources().getString(R.string.peymentOk));
                        ((TextView) findViewById(R.id.invoiceId)).setText(String.format(getResources().getString(R.string.invoiceId) ,jso.getString("id")));
                        String toman = String.format(getResources().getString(R.string.toman) , AppVariables.addCommasToNumericString(String.valueOf(jso.getLong("price")/10)));
                        ((TextView) findViewById(R.id.invoiceAmount)).setText(String.format(getResources().getString(R.string.invoiceAmount) , toman));

                        AdjustEvent event = new AdjustEvent("8h95k4");
                        Adjust.trackEvent(event);


                    } else if (!status.equals("-1")) {
                        ((TextView) findViewById(R.id.invoiceStatus)).setText(getResources().getString(R.string.peymentError));
                        ((TextView) findViewById(R.id.invoiceStatus)).setTextColor(getResources().getColor(R.color.red));

                        ((TextView) findViewById(R.id.invoiceId)).setText(String.format(getResources().getString(R.string.invoiceId) ,jso.getString("id")));
                        String toman = String.format(getResources().getString(R.string.toman) , AppVariables.addCommasToNumericString(String.valueOf(jso.getLong("price")/10)));
                        ((TextView) findViewById(R.id.invoiceAmount)).setText(String.format(getResources().getString(R.string.invoiceAmount) , toman));


                    }
                    ((Button) findViewById(R.id.invoiceBack)).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            Intent main  = new Intent(BankResultActivity.this , MainActivity.class);
                            main.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            finish();
                            startActivity(main);

                        }
                    });

                }

            } catch (Exception e) {

            }

        }
    }
}
