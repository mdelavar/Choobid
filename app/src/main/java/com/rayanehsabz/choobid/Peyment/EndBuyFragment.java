package com.rayanehsabz.choobid.Peyment;


import android.app.Activity;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.rayanehsabz.choobid.Adabters.BacketProductAdapter;
import com.rayanehsabz.choobid.Adabters.Mydatabase;
import com.rayanehsabz.choobid.Tools.AppVariables;
import com.rayanehsabz.choobid.Tools.CalendarTool;
import com.rayanehsabz.choobid.Classes.BacketProduct;
import com.rayanehsabz.choobid.R;
import com.rayanehsabz.choobid.Views.showLoading;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class EndBuyFragment extends Fragment {

    View rootView;

    String email;
    String pass;

    Mydatabase db;

    Activity context;


    long cityId = 0;

    long ship = 0;

    String Token = "";
    String merchent = "";

    RecyclerView recyclerView;
    LinearLayoutManager layoutManager;

    ArrayList<BacketProduct> products = new ArrayList<BacketProduct>();

    BacketProductAdapter adapter;


    boolean portable = false;

    showLoading loading;

    public EndBuyFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_end_buy, container, false);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        long pId = ((BuyProductActivity) getActivity()).ShopeDetail.pId;


        context = getActivity();
        db = new Mydatabase(getActivity());


        portable = ((BuyProductActivity) getActivity()).ShopeDetail.portable;

        loading = new showLoading(context, 1);
        email = db.getSettingString(8);
        pass = db.getSettingString(9);


        recyclerView = (RecyclerView) rootView.findViewById(R.id.productsR);

        layoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);

        recyclerView.setLayoutManager(layoutManager);

        adapter = new BacketProductAdapter(products, context);

        recyclerView.setAdapter(adapter);

        recyclerView.setNestedScrollingEnabled(false);

        new BacketTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                AppVariables.getServerAddress() + "get-backet/email/" + CalendarTool.getCoded(email) + "/pass/" + CalendarTool.getCoded(pass) + "/acc-id/" + db.getSettingString(6));


        ((ImageView) rootView.findViewById(R.id.pay)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                loading.show();
                new TokenTask().execute(

                        AppVariables.getServerAddress() + "get-token/email/" + CalendarTool.getCoded(email) + "/pass/" + CalendarTool.getCoded(pass) + "/acc-id/" + db.getSettingString(6) + "/invoice-no/0"
                );

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
                if (jsonObject.getLong("count") > 0) {
                    ((TextView) rootView.findViewById(R.id.tPriceb)).setText(
                            AppVariables.addCommasToNumericString("" +
                                    (jsonObject.getLong("totalPrice") + jsonObject.getLong("discount")) / 10
                            )

                    );

                    ((TextView) rootView.findViewById(R.id.discountText)).setText((jsonObject.getLong("discount") > 0 ? AppVariables.addCommasToNumericString("" + (jsonObject.getLong("discount") / 10)) : " - "));


                    if (jsonObject.has("cityId") ) {

                        cityId = jsonObject.getLong("cityId");
                        if (portable && cityId != 911) {
                            ship = 100000;
                        }

                    }

                    ((TextView) rootView.findViewById(R.id.tPriceT)).setText(
                            AppVariables.addCommasToNumericString("" +
                                    ((jsonObject.getLong("totalPrice")  + ship) / 10 )
                            )
                    );

                    ((BuyProductActivity) getActivity()).ShopeDetail.invoiceId = jsonObject.getLong("invId");

                    JSONArray jsa = new JSONArray(jsonObject.getString("products"));

                    try {

                        for (int i = 0; i < jsa.length(); i++) {
                            products.add(new BacketProduct(new JSONObject(jsa.getString(i))));
                            adapter.notifyDataSetChanged();
                        }


                    } catch (Exception e) {

                    }


                    loading.dismiss();
                } else {
                    loading.dismiss();
                    Toast.makeText(context, "خطا", Toast.LENGTH_SHORT).show();
                }


                if (jsonObject.getBoolean("hasAddress")) {

                    String s = context.getResources().getString(R.string.detail);
                    String address = db.getStateName(jsonObject.getString("cityId"))
                            + "‌ - " +
                            db.getCityName(jsonObject.getString("cityId"))
                            + "‌ - خیابان " +
                            jsonObject.getString("street")
                            + "‌ - کوچه " +
                            jsonObject.getString("alley")
                            + "‌ - پلاک " +
                            jsonObject.getString("number")
                            + "‌ - طبقه " +
                            jsonObject.getString("floor")
                            + "‌ - واحد " +
                            jsonObject.getString("apartmentUnit");

                    String name = jsonObject.getString("recepterFullName");
                    String phone = jsonObject.getString("mobile");


                    if (portable && cityId != 911) {
                        ((TextView) rootView.findViewById(R.id.sendPrice)).setText("10,000 تومان");
                    }

                    ((TextView) rootView.findViewById(R.id.detail)).setText(String.format(s, name, address, phone));

                    ((TextView) rootView.findViewById(R.id.factor)).setText((jsonObject.getInt("factor") == 1 ? "ارسال فاکتور : بله" : "ارسال فاکتور : خیر"));
                    ((TextView) rootView.findViewById(R.id.gift)).setText((jsonObject.getInt("gift") == 1 ? "کادو کردن : بله" : "کادو کردن : خیر"));

                }


            } catch (Exception e) {
            }

        }
    }


//    public void setTotalPrice() {
//
//        long price = 0;
//
//        if (products.size() > 0) {
//            for (BacketProduct a : products) {
//
//                price += (a.price * a.count);
//
//            }
//        }
//        ((TextView) rootView.findViewById(R.id.tPriceb)).setText(AppVariables.addCommasToNumericString(String.valueOf((price > 0) ? price / 10 : "0")));
//
//        ((TextView) rootView.findViewById(R.id.tPriceT)).setText(AppVariables.addCommasToNumericString(String.valueOf((price > 0) ? price / 10 : "0")));
//
//    }

    public class TokenTask extends AsyncTask<String, Void, String> {
        protected String doInBackground(String... urls) {
            return AppVariables.readJSONFeed(urls[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            try {

                JSONObject jsonObject = new JSONObject(result);

                if (jsonObject.has("token")) {

                    loading.dismiss();
                    Token = jsonObject.getString("token");
                    ((BuyProductActivity) getActivity()).ShopeDetail.token = Token;
                    merchent = "0";
                    ((BuyProductActivity) getActivity()).ShopeDetail.merchant = merchent;
                    getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter_from_left, R.anim.exit_from_right, R.anim.enter_from_right, R.anim.exit_from_left).add(R.id.mainContent, new BankPageFragment()).addToBackStack(null).commit();


                }


            } catch (Exception e) {


            }


        }
    }

}
