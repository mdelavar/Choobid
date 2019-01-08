package com.rayanehsabz.choobid.Peyment;


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.rayanehsabz.choobid.Adabters.AddressAdapter;
import com.rayanehsabz.choobid.Adabters.Mydatabase;
import com.rayanehsabz.choobid.Activities.AddAddressActivity;
import com.rayanehsabz.choobid.Tools.AppVariables;
import com.rayanehsabz.choobid.Tools.CalendarTool;
import com.rayanehsabz.choobid.Classes.Address;
import com.rayanehsabz.choobid.R;
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
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class ShopAddressFragment extends Fragment {

    View rootView;

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
    long addressId = 0;
    long count;



    CheckBox fac;
    CheckBox gif;

    String phone = "e0";

    ArrayList<String> states = new ArrayList<String>();

    ArrayList<String> citys = new ArrayList<String>();


    RecyclerView recyclerView;
    LinearLayoutManager layoutManager;
    ArrayList<Address> list = new ArrayList<Address>();
    AddressAdapter adapter;

    showLoading loading;

    public ShopAddressFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_shop_address, container, false);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        count = ((BuyProductActivity) getActivity()).ShopeDetail.count;

        fac = (CheckBox) rootView.findViewById(R.id.factorCheck);
        gif = (CheckBox) rootView.findViewById(R.id.kaadoCheck);

        disCode = ((BuyProductActivity) getActivity()).ShopeDetail.DiscountCode;

        city = (TextView) rootView.findViewById(R.id.cityT);
        context = getActivity();
        db = new Mydatabase(getActivity());

        loading = new showLoading(context, 1);

        email = db.getSettingString(8);
        pass = db.getSettingString(9);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.addsrecycler);
        layoutManager = new LinearLayoutManager(context , LinearLayoutManager.VERTICAL, false);

        recyclerView.setLayoutManager(layoutManager);

        adapter = new AddressAdapter(list,this);
        recyclerView.setAdapter(adapter);



        recyclerView.setNestedScrollingEnabled(false);


        ((TextView) rootView.findViewById(R.id.addAddress)).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Intent intent =  new Intent(context , AddAddressActivity.class);
                        startActivity(intent);

                    }
                }
        );


        ((ImageView) rootView.findViewById(R.id.saveAndC)).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (addressId > 0 ) {
                            loading.show();
                            new SetAddressTask().execute(

                                    AppVariables.getServerAddress() +
                                            "/choobid-portlet/api/jsonws/account/set-backet-address/email/" +
                                            CalendarTool.getCoded(email) +
                                            "/pass/" +
                                            CalendarTool.getCoded(pass) +
                                            "/acc-id/" +
                                            db.getSettingString(6) +
                                            "/add-id/" +
                                            addressId +
                                            "/factor/" +
                                            (fac.isChecked() ? "1" : "0") +
                                            "/gift/" +
                                            (gif.isChecked() ? "1" : "0")

                            );

                        } else {

                            Toast.makeText(context, "ابتدا یک آدرس انتخاب نمایید.", Toast.LENGTH_SHORT).show();

                        }

                    }
                }
        );
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshAddress();
    }


    private class AddressTask extends AsyncTask<String, Void, String> {
        protected String doInBackground(String... urls) {
            return AppVariables.readJSONFeed(urls[0]);
        }

        protected void onPostExecute(String result) {


            try {
                final JSONObject m1 = new JSONObject(result);
                if (m1.length() > 0) {
                    JSONArray jsa = m1.getJSONArray("adds");

                    loading.dismiss();

                    for (int i = 0; i < jsa.length(); i++) {

                        list.add(new Address(jsa.getJSONObject(i)));
                        adapter.notifyDataSetChanged();
                    }

                }

            } catch (Exception e) {

            }

        }
    }


    private class SetAddressTask extends AsyncTask<String, Void, String> {
        protected String doInBackground(String... urls) {
            return AppVariables.readJSONFeed(urls[0]);
        }

        protected void onPostExecute(String result) {


            try {
                final JSONObject m1 = new JSONObject(result);

                   if (m1.getBoolean("result")) {
                       loading.dismiss();
                       getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter_from_left, R.anim.exit_from_right, R.anim.enter_from_right, R.anim.exit_from_left).add(R.id.mainContent, new EndBuyFragment()).addToBackStack(null).commit();

                   }



            } catch (Exception e) {

            }

        }
    }


    public void setEnable (int p) {
        try {
            for (Address a : list) {
                a.selected = false;
            }

            list.get(p).selected = true;

            addressId = list.get(p).id;

            adapter.notifyDataSetChanged();
        } catch (Exception e) {
            Log.e("Tag" , e.toString());
        }
    }
    private class InvoiceTask extends AsyncTask
            <String, Void, String> {
        protected String doInBackground(String... params) {

            StringBuilder stringBuilder = new StringBuilder();
            HttpClient httpClient = new DefaultHttpClient();

            String credentials = "json%40birib%2Eir:json2";

            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(16);

            nameValuePairs.add(new BasicNameValuePair("email", params[0]));
            nameValuePairs.add(new BasicNameValuePair("pass", params[1]));
            nameValuePairs.add(new BasicNameValuePair("accId", params[2]));
            nameValuePairs.add(new BasicNameValuePair("productId", params[3]));
            nameValuePairs.add(new BasicNameValuePair("productCount", params[4]));
            nameValuePairs.add(new BasicNameValuePair("discountCode", params[5]));
            nameValuePairs.add(new BasicNameValuePair("cityId", params[6]));
            nameValuePairs.add(new BasicNameValuePair("postalCode", params[7]));
            nameValuePairs.add(new BasicNameValuePair("street", params[8]));
            nameValuePairs.add(new BasicNameValuePair("alley", params[9]));
            nameValuePairs.add(new BasicNameValuePair("number", params[10]));
            nameValuePairs.add(new BasicNameValuePair("floor", params[11]));
            nameValuePairs.add(new BasicNameValuePair("apartmentUnit", params[12]));
            nameValuePairs.add(new BasicNameValuePair("recepterFullName", params[13]));
            nameValuePairs.add(new BasicNameValuePair("mobile", params[14]));
            nameValuePairs.add(new BasicNameValuePair("phone", params[15]));

            httpPost = new HttpPost(AppVariables.getServerAddress() + "/choobid-portlet/api/jsonws/account/create-invoice/");

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
                Log.e("invoice", result);
                if (jsonObject.has("invoiceId")) {

                    ((BuyProductActivity) getActivity()).ShopeDetail.invoiceId = jsonObject.getLong("invoiceId");
                    getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter_from_left, R.anim.exit_from_right, R.anim.enter_from_right, R.anim.exit_from_left).add(R.id.mainContent, new EndBuyFragment()).addToBackStack(null).commit();
                    loading.dismiss();

                }


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

                            } catch (Exception e) {

                            }
                            select = true;

                            Position = p;
                        } else {

                            ((BuyProductActivity) context).ShopeDetail.city = db.getCityCode(ss);
                            cityCode = ((BuyProductActivity) context).ShopeDetail.city;
                            Log.e("Tagsad", " ---> " + ((BuyProductActivity) context).ShopeDetail.city);

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

    public void refreshAddress() {

        list.clear();
        adapter.notifyDataSetChanged();

        new AddressTask().execute(
                AppVariables.getServerAddress() + "/choobid-portlet/api/jsonws/account/get-addresses/email/" + CalendarTool.getCoded(email) + "/pass/" + CalendarTool.getCoded(pass) + "/acc-id/" + db.getSettingString(6)
        );

    }
}
