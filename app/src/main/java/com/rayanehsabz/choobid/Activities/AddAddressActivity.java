package com.rayanehsabz.choobid.Activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.rayanehsabz.choobid.Adabters.Mydatabase;
import com.rayanehsabz.choobid.R;
import com.rayanehsabz.choobid.Tools.AppVariables;
import com.rayanehsabz.choobid.Tools.CalendarTool;
import com.rayanehsabz.choobid.Views.showLoading;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class AddAddressActivity extends Activity {


    int Position;
    String email;
    String pass;

    Mydatabase db;

    HttpPost httpPost = null;

    TextView city;
    Activity context;
    Dialog dialog;

    boolean select = false;

    String cityCode = "";
    String disCode = "";
    EditText postCode;
    EditText street;
    EditText kooche;
    EditText pelak;
    EditText tabaghe;
    EditText vahed;

    EditText cname;
    EditText cphone;
    EditText cphone2;
    String aId = "0";

    Bundle extra;

    String phone = "e0";

    ArrayList<String> states = new ArrayList<String>();

    ArrayList<String> citys = new ArrayList<String>();

    showLoading loading;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_address);

        extra = getIntent().getExtras();

        if  (extra != null) {

            aId = extra.getString("id");

        }

        try {




            city = (TextView) findViewById(R.id.cityT);
            context = this;
            db = new Mydatabase(context);

            loading = new showLoading(context, 1);

            email = db.getSettingString(8);
            pass = db.getSettingString(9);

            states = db.getStates();

            postCode = (EditText) findViewById(R.id.postCode);
            street = (EditText) findViewById(R.id.street);
            kooche = (EditText) findViewById(R.id.kooche);
            pelak = (EditText) findViewById(R.id.pelak);
            tabaghe = (EditText) findViewById(R.id.tabaghe);
            vahed = (EditText) findViewById(R.id.vahed);

            cname = (EditText) findViewById(R.id.cName);
            cphone = (EditText) findViewById(R.id.cPhone);
            cphone2 = (EditText) findViewById(R.id.cPhone2);

            if (cphone2.length() > 0) {
                phone = cphone2.getText().toString();

            }

            if (!aId.equals("0")) {

                ((Button) findViewById(R.id.saveAndC)).setText("ویرایش");
                cityCode = extra.getString("cityId");

                select = true;

                ((TextView) findViewById(R.id.mcityT)).setText(db.getStateName(cityCode));
                ((TextView) findViewById(R.id.cityT)).setText(db.getCityName(cityCode));
                
                postCode.setText(extra.getString("postalCode"));
                street.setText(extra.getString("street"));
                kooche.setText(extra.getString("alley"));
                pelak.setText(extra.getString("number"));
                tabaghe.setText(extra.getString("floor"));
                vahed.setText(extra.getString("apartmentUnit"));
                cname.setText(extra.getString("recepterFullName"));
                cphone.setText(extra.getString("mobile"));
                cphone2.setText(extra.getString("phone"));

            }


            ((Button) findViewById(R.id.saveAndC)).setOnClickListener(new View.OnClickListener() {


                @Override
                public void onClick(View view) {

                    if (select && !postCode.getText().toString().equals("") && !street.getText().toString().equals("") && !kooche.getText().toString().equals("") && !pelak.getText().toString().equals("") && !tabaghe.getText().toString().equals("") && !vahed.getText().toString().equals("")) {

                        if (!cname.getText().toString().equals("") && !cphone.getText().toString().equals("")) {

                            phone = (cphone2.getText().toString().equals("") ? phone : cphone2.getText().toString());
                            loading.show();

                            String[] params = {CalendarTool.getCoded(email), CalendarTool.getCoded(pass), String.valueOf(db.getSettingString(6)), cityCode, CalendarTool.getCoded(postCode.getText().toString()), CalendarTool.getCoded(street.getText().toString()), CalendarTool.getCoded(kooche.getText().toString()), CalendarTool.getCoded(pelak.getText().toString()), CalendarTool.getCoded(tabaghe.getText().toString()), CalendarTool.getCoded(vahed.getText().toString()), CalendarTool.getCoded(cname.getText().toString()), CalendarTool.getCoded(cphone.getText().toString()), CalendarTool.getCoded(phone) , aId};
                            new AddAddressTask().execute(params);


                        } else {
                            Toast.makeText(context, "لطفا اطلاعات تحویل گیرنده را تکمیل کنید.", Toast.LENGTH_SHORT).show();
                        }

                    } else {

                        Toast.makeText(context, "لطفا نشانی را تکمیل کنید.", Toast.LENGTH_SHORT).show();
                    }

                }


            });

            ((LinearLayout) findViewById(R.id.Mcity)).setOnClickListener(new View.OnClickListener() {


                @Override
                public void onClick(View view) {
                    dialog = new Dialog(context);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(R.layout.dialog_cities);

                    try {

                        CitiesAdapter adapter = new CitiesAdapter(context, states, ((TextView) findViewById(R.id.mcityT)), true);
                        ((ListView) dialog.findViewById(R.id.listC)).setAdapter(adapter);
                    } catch (Exception e) {

                        Log.e("Tag1", " ---> " + e);
                    }
                    dialog.show();
                }


            });

            ((LinearLayout) findViewById(R.id.city)).setOnClickListener(new View.OnClickListener() {


                @Override
                public void onClick(View view) {
                    if (select) {
                        dialog = new Dialog(context);
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        dialog.setContentView(R.layout.dialog_cities);

                        try {


                            CitiesAdapter adapter = new CitiesAdapter(context, citys, ((TextView) findViewById(R.id.cityT)), false);
                            ((ListView) dialog.findViewById(R.id.listC)).setAdapter(adapter);
                        } catch (Exception e) {

                            Log.e("Tag2", " ---> " + e);
                        }
                        dialog.show();

                    } else {

                        Toast.makeText(context, "ابتدا استان مورد نظر را انتخاب کنید.", Toast.LENGTH_SHORT).show();

                    }
                }


            });
        } catch (Exception e) {

            Log.e("Tag" , e.toString());

        }

    }



    private class AddAddressTask extends AsyncTask<String, Void, String> {
        protected String doInBackground(String... params) {

            StringBuilder stringBuilder = new StringBuilder();
            HttpClient httpClient = new DefaultHttpClient();

            String credentials = "json%40birib%2Eir:json2";

            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(16);


            nameValuePairs.add(new BasicNameValuePair("email", params[0]));
            nameValuePairs.add(new BasicNameValuePair("pass", params[1]));
            nameValuePairs.add(new BasicNameValuePair("accId", params[2]));
            nameValuePairs.add(new BasicNameValuePair("cityId", params[3]));
            nameValuePairs.add(new BasicNameValuePair("postalCode", params[4]));
            nameValuePairs.add(new BasicNameValuePair("street", params[5]));
            nameValuePairs.add(new BasicNameValuePair("alley", params[6]));
            nameValuePairs.add(new BasicNameValuePair("number", params[7]));
            nameValuePairs.add(new BasicNameValuePair("floor", params[8]));
            nameValuePairs.add(new BasicNameValuePair("apartmentUnit", params[9]));
            nameValuePairs.add(new BasicNameValuePair("recepterFullName", params[10]));
            nameValuePairs.add(new BasicNameValuePair("mobile", params[11]));
            nameValuePairs.add(new BasicNameValuePair("phone", params[12]));
            nameValuePairs.add(new BasicNameValuePair("addId", params[13]));



            httpPost = new HttpPost(AppVariables.getServerAddress() + "/choobid-portlet/api/jsonws/account/add-addresses/");

            try {
                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            } catch (UnsupportedEncodingException e1) {

                e1.printStackTrace();
            }

            try {

                HttpResponse response = httpClient.execute(httpPost);
                StatusLine statusLine = response.getStatusLine();
                int statusCode = statusLine.getStatusCode();
//                Log.e("JSON", String.valueOf(statusCode));
                if (statusCode > -1) {
                    HttpEntity entity = response.getEntity();
                    InputStream inputStream = entity.getContent();
                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(inputStream));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        stringBuilder.append(line);
                    }
                    inputStream.close();
                } else {
//                    Log.d("JSON", "Failed to download file");
                }
            } catch (Exception e) {
                //Log.d("readJSONFeed", e.getLocalizedMessage());
            }
//        	Log.e("calback", stringBuilder.toString());
            return stringBuilder.toString();
        }


        protected void onPostExecute(String result) {
            try {


                JSONObject jsonObject = new JSONObject(result);
                Log.e("address", result);
                loading.dismiss();
                finish();

//


            } catch (Exception e) {

                Log.e("Tag", " ---> " + e);
            }


        }
    }


    public class CitiesAdapter extends BaseAdapter implements ListAdapter {
        private Context context;
        private ArrayList<String> jsa;
        private TextView textView;
        boolean setP;


        public CitiesAdapter(Context c, ArrayList<String> j, TextView t, boolean p) {
            context = c;
            jsa = j;
            textView = t;
            setP = p;

        }

        //---returns the number of images---
        public int getCount() {
            return jsa.size();
        }

        //---returns the item---
        public Object getItem(int position) {
            return position;
        }

        //---returns the ID of an item---
        public long getItemId(int position) {
            return position;
        }

        //---returns an ImageView view---
        public View getView(int position, View convertView,
                            ViewGroup parent)

        {
            View title = LayoutInflater.from(context).inflate(R.layout.item_cities,
                    null, false);
            try {
                final int p = position;


//
                final String ss = jsa.get(position);

                ((TextView) title.findViewById(R.id.name)).setText(ss);
                title.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (setP) {
                            try {

                                citys = db.getCitys(ss);
                                city.setText(citys.get(0));
                                cityCode = db.getCityCode(citys.get(0));
                                Log.e("Tagsad", " ---> " + cityCode);

                            } catch (Exception e) {

                            }
                            select = true;

                            Position = p;
                        } else {
                            cityCode = db.getCityCode(ss);
                            Log.e("Tagsad", " ---> " + cityCode);

                        }
                        textView.setText(ss);
                        dialog.dismiss();
                    }
                });
            } catch (Exception e) {

            }

            return title;
        }
    }

}
