package com.rayanehsabz.choobid.Fragments;


import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.adjust.sdk.Adjust;
import com.adjust.sdk.AdjustEvent;
import com.rayanehsabz.choobid.Adabters.Mydatabase;
import com.rayanehsabz.choobid.Tools.AppVariables;
import com.rayanehsabz.choobid.Tools.CalendarTool;
import com.rayanehsabz.choobid.Activities.LoginActivity;
import com.rayanehsabz.choobid.Activities.MainActivity;
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
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class SecurityCodeFragment extends Fragment {

    View rootView;
    public String secCode;

    Mydatabase db;

    showLoading loading;

    CountDownTimer ts;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_security, container, false);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        db = new Mydatabase(getActivity());
        loading = new showLoading(getActivity(), 1);

        ts = new CountDownTimer(60000 , 1000) {
            @Override
            public void onTick(long l) {
                ((TextView) rootView.findViewById(R.id.timeS)).setClickable(false);
                 String hms = String.format("%02d:%02d",
                        TimeUnit.MILLISECONDS.toMinutes(l) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(l)),
                        TimeUnit.MILLISECONDS.toSeconds(l) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(l)));
                ((TextView) rootView.findViewById(R.id.timeS)).setText(hms);

            }

            @Override
            public void onFinish() {

                ((TextView) rootView.findViewById(R.id.timeS)).setClickable(true);
                ((TextView) rootView.findViewById(R.id.timeS)).setText("درخواست مجدد کد فعالسازی");
                ((TextView) rootView.findViewById(R.id.timeS)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        final Dialog buy = new Dialog(getActivity());
                        buy.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        buy.setContentView(R.layout.buy_bid_dialog);


                        ((TextView) buy.findViewById(R.id.nbT)).setText(String.format(getActivity().getResources().getString(R.string.sendSec), ((LoginActivity) getActivity()).mobNum));
                       Log.e("Mobno" , ((LoginActivity) getActivity()).mobNum + "");
                        ((Button) buy.findViewById(R.id.buy)).setText("ارسال");
                        ((Button) buy.findViewById(R.id.buy)).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                ((TextView) rootView.findViewById(R.id.timeS)).setText(getActivity().getResources().getString(R.string.wait));
                                new ReadSeccCodeJSONFeedTask().execute(

                                        AppVariables.getServerAddress() + "/choobid-portlet/api/jsonws/account/re-send-seccode/mobno/" + CalendarTool.getCoded(((LoginActivity) getActivity()).mobNum)
                                );

                                buy.dismiss();
                            }
                        });

                        ((Button) buy.findViewById(R.id.cancel)).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                buy.dismiss();
                            }
                        });

                        buy.show();

                    }
                });
            }
        };
        new ReadSeccCodeJSONFeedTask().execute(

                AppVariables.getServerAddress() + "/choobid-portlet/api/jsonws/account/send-seccode/mobno/" + CalendarTool.getCoded(((LoginActivity) getActivity()).mobNum)
        );
        Log.e("Tag" ," -----> " +  ((LoginActivity) getActivity()).mobNum);

        ((Button) rootView.findViewById(R.id.lg)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                EditText txtsecuritycode = (EditText) rootView.findViewById(R.id.txtScurity);


                //Log.d("verifyFromMobile",verifyFromMobile);

                if (!txtsecuritycode.getEditableText().toString().equals(secCode)) {
                    Toast.makeText(getActivity(),getString(R.string.SecurityCode_not_match),Toast.LENGTH_LONG).show();

                } else {


                    loading.show();

                    String cemail = CalendarTool.getCoded(((LoginActivity) getActivity()).email);
                    String cscreenname = CalendarTool.getCoded(((LoginActivity) getActivity()).username);
                    String cpassword = CalendarTool.getCoded(((LoginActivity) getActivity()).pass);
                    String cmobileno = CalendarTool.getCoded(((LoginActivity) getActivity()).mobNum);

                    //Toast.makeText(getBaseContext(),Charset.forName("UTF-8").encode(txtscreenname.getText().toString()).toString(),Toast.LENGTH_LONG);
                    String[] params = {cemail , cpassword , cscreenname, cmobileno , AppVariables.getAppTypeId()};
                    new ReadMahfelJSONFeedTask().execute(params);
                }
            }


        });


    }



    private class ReadMahfelJSONFeedTask extends AsyncTask <String, Void, String> {
        protected String doInBackground(String... params) {
            StringBuilder stringBuilder = new StringBuilder();
            HttpClient httpClient = new DefaultHttpClient();

            String credentials = "json%40birib%2Eir:json2";

            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(6);
            nameValuePairs.add(new BasicNameValuePair("email", params[0]));
            nameValuePairs.add(new BasicNameValuePair("pass", params[1]));
            nameValuePairs.add(new BasicNameValuePair("scrname", params[2]));
            nameValuePairs.add(new BasicNameValuePair("mobno", params[3]));
            nameValuePairs.add(new BasicNameValuePair("appType", params[4]));

            HttpPost httpPost = new HttpPost(AppVariables.getServerAddress() + "/choobid-portlet/api/jsonws/account/create-account/");
            try {
                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            } catch (UnsupportedEncodingException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }

            try {
                HttpResponse response = httpClient.execute(httpPost);
                StatusLine statusLine = response.getStatusLine();
                int statusCode = statusLine.getStatusCode();
                //Log.e("JSON", String.valueOf(statusCode));
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
//        	Log.d("result", stringBuilder.toString());
            return stringBuilder.toString();
        }

        protected void onPostExecute(String result) {
            try {

                JSONObject m1 = new JSONObject(result);
                if (m1.getString("accountId") != null) {
                    loading.dismiss();
                    db.saveSetting(6, m1.getString("accountId"));
                    db.saveSetting(8, m1.getString("email"));
                    db.saveSetting(9, ((LoginActivity) getActivity()).pass);
                    db.saveSetting(11, m1.getString("sreenName"));

                    db.saveSetting(110, m1.getString("avatarFeId"));
                    db.saveSetting(111, m1.getString("avatar"));
                    db.saveSetting(112, m1.getString("sologan"));
                    db.saveSetting(113, m1.getString("mobile"));
                    db.saveSetting(114, m1.getString("name"));
                    db.saveSetting(115, m1.getString("kodemeli"));

                    db.saveSetting(116, m1.getString("cityCode") , "CityCode");

                    db.saveSetting(117, m1.getString("day") , "Day");
                    db.saveSetting(118, m1.getString("month") , "Month");
                    db.saveSetting(119, m1.getString("year") , "Year");

                    AdjustEvent event = new AdjustEvent("6ugzc7");
                    Adjust.trackEvent(event);

                    Toast.makeText(getActivity(), getActivity().getString(R.string.loginOk), Toast.LENGTH_LONG).show();
                    ActivityCompat.finishAffinity(getActivity());
                    Intent i = new Intent(getActivity(), MainActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);

                    clearBackstack();

                } else {
                    loading.dismiss();
                    Toast.makeText(getActivity(),
                            getActivity().getString(R.string.loginServerError),
                            Toast.LENGTH_LONG).show();

                }

            } catch (Exception e) {
        	   Log.e("ReadMahfelJSONFeedTask", "error:" + e);
            }
        }

    }
    private class ReadSeccCodeJSONFeedTask extends AsyncTask<String, Void, String> {
        protected String doInBackground(String... urls) {
            return AppVariables.readJSONFeed(urls[0]);
        }

        protected void onPostExecute(String result) {
            try {

                JSONObject jsonObject = new JSONObject(result);
                if (jsonObject.has("code")) {
                     secCode =  jsonObject.getString("code");

                    ts.start();
                    Log.e("Tag" , " -----> " + secCode);
                }

            } catch (Exception e) {
//            	Log.e("ReadMahfelJSONFeedTask", "error : " + e);
            }
        }
    }





    public void clearBackstack() {

        FragmentManager.BackStackEntry entry = getActivity().getSupportFragmentManager().getBackStackEntryAt(
                0);
        getActivity().getSupportFragmentManager().popBackStack(entry.getId(),
                android.app.FragmentManager.POP_BACK_STACK_INCLUSIVE);
        getActivity().getSupportFragmentManager().executePendingTransactions();


    }
}
