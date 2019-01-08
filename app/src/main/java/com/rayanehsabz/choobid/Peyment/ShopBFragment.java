package com.rayanehsabz.choobid.Peyment;


import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.util.LongSparseArray;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.rayanehsabz.choobid.Adabters.Mydatabase;
import com.rayanehsabz.choobid.Tools.AppVariables;
import com.rayanehsabz.choobid.Tools.CalendarTool;
import com.rayanehsabz.choobid.Classes.BacketProduct;
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
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import static com.rayanehsabz.choobid.Tools.AppVariables.hideSoftKeyboard;

public class ShopBFragment extends Fragment {

    View rootView;

    String email;
    String pass;

    String code = "";
    Mydatabase db;

    Activity context;

    boolean portable = false;

    List<Integer> counts = new ArrayList<Integer>();
    LongSparseArray<ArrayAdapter> adapters = new LongSparseArray<ArrayAdapter>();


    ArrayList<BacketProduct> products = new ArrayList<BacketProduct>();
    ArrayList<MyViewHolder> viewHolders = new ArrayList<MyViewHolder>();


    long discont = 0;

    showLoading loading;

    public ShopBFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_shop_b, container, false);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        context = getActivity();

        hideSoftKeyboard(context);
        for (int i = 1; i < 20; i++) counts.add(i);

        loading = new showLoading(context, 1);
        db = new Mydatabase(getActivity());

        email = db.getSettingString(8);
        pass = db.getSettingString(9);


        loading.show();
        new BacketTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                AppVariables.getServerAddress() + "get-backet/email/" + CalendarTool.getCoded(email) + "/pass/" + CalendarTool.getCoded(pass) + "/acc-id/" + db.getSettingString(6));


        ((ImageView) rootView.findViewById(R.id.next_detail)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int finish = 0;
                for (int i = 0; i < products.size() ;i++) {
                    BacketProduct b = products.get(i);
                    if (i == products.size() - 1) finish = 1;
                    new EditTask().execute(

                            AppVariables.getServerAddress() +
                                    "/choobid-portlet/api/jsonws/account/edit-backet-item/email/"
                                    + CalendarTool.getCoded(email) +
                                    "/pass/" +
                                    CalendarTool.getCoded(pass) +
                                    "/inpr-id/" +
                                    b.id +
                                    "/product-id/0/product-count/" +
                                    b.count +
                                    "/color-id/" +
                                    b.colorId +
                                    "/garanty-id/" +
                                    b.garantyId +
                                    "/size-id/" +
                                    b.sizeId +
                                    "/finish/" + finish


                    );
                }


                ((BuyProductActivity) getActivity()).ShopeDetail.portable = portable;
                if (portable) {
                    getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter_from_left, R.anim.exit_from_right, R.anim.enter_from_right, R.anim.exit_from_left).add(R.id.mainContent, new ShopAddressFragment()).addToBackStack(null).commit();
                } else {


                    getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter_from_left, R.anim.exit_from_right, R.anim.enter_from_right, R.anim.exit_from_left).add(R.id.mainContent, new EndBuyFragment()).addToBackStack(null).commit();


                }
            }
        });

        ((ImageView) rootView.findViewById(R.id.checkD)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                EditText dis = (EditText) rootView.findViewById(R.id.discountCode);
                code = dis.getText().toString();
                String pId = String.valueOf(((BuyProductActivity) getActivity()).ShopeDetail.pId);
                String accId = String.valueOf(db.getSettingString(6));
                new CheckCode().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                        AppVariables.getServerAddress() + "get-check-discount-code/email/" + CalendarTool.getCoded(email) + "/pass/" + CalendarTool.getCoded(pass) + "/acc-id/" + accId + "/product-id/" + pId + "/discount-code/" + code);


            }
        });


    }


    private class BacketTask extends AsyncTask<String, Void, String> {
        protected String doInBackground(String... urls) {
            return AppVariables.readJSONFeed(urls[0]);
        }

        protected void onPostExecute(String result) {


            try {
                JSONObject jsonObject = new JSONObject(result);

                products.clear();
                products = new ArrayList<BacketProduct>();


                if (jsonObject.getLong("count") > 0) {

                    JSONArray jsa = new JSONArray(jsonObject.getString("products"));

                    discont = jsonObject.getLong("discount");

                    ((EditText) rootView.findViewById(R.id.discountCode)).setText((jsonObject.getString("discountCode").equals("0")) ? "" : jsonObject.getString("discountCode") );
                    ((TextView) rootView.findViewById(R.id.discountText)).setText((discont > 0) ? AppVariables.addCommasToNumericString("" + ( discont / 10)) : " - ");
                    LinearLayout parent = (LinearLayout) rootView.findViewById(R.id.products);
                    parent.removeAllViews();

                    for (int i = 0; i < jsa.length(); i++) {

                        try {

                            BacketProduct bs = new BacketProduct(new JSONObject(jsa.getString(i)));
                            products.add(bs);


                            if (bs.portable) portable = true;

                            View item = LayoutInflater.from(context).inflate(R.layout.item_bproduct, parent, false);

                            MyViewHolder holder = new MyViewHolder(item);
                            viewHolders.add(holder);

                            File f = new File(Environment.getExternalStorageDirectory() + AppVariables.getProductsFolder() + "/" + bs.feId + ".jpg");

                            Bitmap mb = BitmapFactory.decodeFile(f.getAbsolutePath());

                            holder.pic.setImageBitmap(mb);
                            holder.name.setText(bs.name);
                            holder.Ename.setText(bs.englishName);
                            holder.basePrice.setText(AppVariables.addCommasToNumericString(String.valueOf(bs.price / 10)));
                            holder.price.setText(
                                    AppVariables.addCommasToNumericString(
                                            String.valueOf(
                                                    (bs.price / 10) * bs.count
                                            )
                                    )
                            );

                            ArrayAdapter<Integer> adapter = new ArrayAdapter<Integer>(context, android.R.layout.simple_spinner_item, counts);
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                            final int finalI = i;
                            holder.count.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> adapterView, View view, int j, long l) {

                                    try {
                                        products.get(finalI).count = j + 1;
                                        viewHolders.get(finalI).price.setText(
                                                AppVariables.addCommasToNumericString(
                                                        String.valueOf(
                                                                (products.get(finalI).price / 10) * products.get(finalI).count
                                                        )
                                                )
                                        );
                                        setTotalPrice();
                                    } catch (Exception e) {
                                    }

                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> adapterView) {

                                }
                            });

                            adapters.append(bs.id, adapter);
                            holder.count.setAdapter(adapter);
                            holder.count.setSelection(bs.count - 1);

                            final long idp = bs.id;
                            holder.del.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    loading.show();
                                    portable = false;
                                    new BacketTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                                            AppVariables.getServerAddress() + "delete-backet-item/email/" + CalendarTool.getCoded(email) + "/pass/" + CalendarTool.getCoded(pass) + "/acc-id/" + db.getSettingString(6) + "/inpr-id/" + idp
                                    );

                                }
                            });


                            parent.addView(item);

                        } catch (Exception e) {
                        }

                    }

                    setTotalPrice();
                    loading.dismiss();

                } else {

                    LinearLayout parent = (LinearLayout) rootView.findViewById(R.id.products);
                    parent.removeAllViews();
                    ((TextView) rootView.findViewById(R.id.noProd)).setVisibility(View.VISIBLE);
                    setTotalPrice();
                    loading.dismiss();


                }




            } catch (Exception e) {
            }

        }
    }


    private class EditTask extends AsyncTask<String, Void, String> {
        protected String doInBackground(String... urls) {
            return AppVariables.readJSONFeed(urls[0]);
        }

        protected void onPostExecute(String result) {


            try {
                JSONObject jsonObject = new JSONObject(result);

            } catch (Exception e) {
            }

        }
    }

    public void setTotalPrice() {

        long price = 0;

        if (products.size() > 0) {
            for (BacketProduct a : products) {

                price += (a.price * a.count);

            }
        }

        ((TextView) rootView.findViewById(R.id.tPriceb)).setText(AppVariables.addCommasToNumericString(String.valueOf((price > 0) ? price / 10 : "0")));

        ((TextView) rootView.findViewById(R.id.tPriceT)).setText(AppVariables.addCommasToNumericString(String.valueOf((price > 0) ? (price - discont) / 10 : "0")));

    }

    public class MyViewHolder extends RecyclerView.ViewHolder {


        CardView cardView;
        ImageView pic;
        TextView Ename;
        TextView name;


        ImageView del;

        TextView basePrice;
        TextView price;
        Spinner count;


        public MyViewHolder(View itemView) {
            super(itemView);


            this.del = (ImageView) itemView.findViewById(R.id.delete);
            this.pic = (ImageView) itemView.findViewById(R.id.pImg);
            this.Ename = (TextView) itemView.findViewById(R.id.pename);
            this.name = (TextView) itemView.findViewById(R.id.pname);

            this.count = (Spinner) itemView.findViewById(R.id.tcount);


            this.basePrice = (TextView) itemView.findViewById(R.id.oPrice);

            this.price = (TextView) itemView.findViewById(R.id.tPrice);


            this.cardView = (CardView) itemView.findViewById(R.id.cardView);
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


            HttpPost httpPost = new HttpPost(AppVariables.getServerAddress() + "create-invoice/");

            try {
                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            } catch (UnsupportedEncodingException e1) {

                e1.printStackTrace();
            }

            try {

                HttpResponse response = httpClient.execute(httpPost);
                StatusLine statusLine = response.getStatusLine();
                int statusCode = statusLine.getStatusCode();
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
                }
            } catch (Exception e) {
            }
            return stringBuilder.toString();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        protected void onPostExecute(String result) {
            try {


                JSONObject jsonObject = new JSONObject(result);
                if (jsonObject.has("invoiceId")) {



                }


            } catch (Exception e) {

            }


        }
    }


    private class CheckCode extends AsyncTask<String, Void, String> {
        protected String doInBackground(String... urls) {
            return AppVariables.readJSONFeed(urls[0]);
        }

        protected void onPostExecute(String result) {
            List<String> scrnames = new ArrayList<String>();
            try {

                JSONObject jsonObject = new JSONObject(result);
                if (jsonObject.length() > 0) {

                    long price = ((BuyProductActivity) getActivity()).ShopeDetail.price;
                    if (!jsonObject.getBoolean("error")) {
                        Toast.makeText(context, "کد تخفیف با موفقیت اعمال شد.", Toast.LENGTH_SHORT).show();

                        discont = jsonObject.getLong("discont");
                        ((TextView) rootView.findViewById(R.id.discountText)).setText(AppVariables.addCommasToNumericString("" + ( discont / 10)));
                        setTotalPrice();


                    } else {

                        Toast.makeText(context, "کد تخفیف معتبر نمی باشد.", Toast.LENGTH_SHORT).show();
                    }

                }

            } catch (Exception e) {
            }

        }
    }


}
