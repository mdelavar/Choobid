package com.rayanehsabz.choobid.Tools;

//import android.app.Activity;
//
//import android.content.Context;
//import android.content.Intent;
//import android.net.ConnectivityManager;
//import android.net.NetworkInfo;
//
//
//import android.util.Log;
//import android.view.WindowManager;
//
//import org.apache.http.HttpEntity;
//import org.apache.http.HttpResponse;
//import org.apache.http.StatusLine;
//import org.apache.http.client.HttpClient;
//import org.apache.http.client.methods.HttpGet;
//import org.apache.http.impl.client.DefaultHttpClient;
//
//import java.io.BufferedReader;
//import java.io.InputStream;
//import java.io.InputStreamReader;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.WindowManager;

import com.rayanehsabz.choobid.Activities.MainActivity;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by sabz3 on 03/13/2017.
 */

public class AppVariables {

    private static String siteAddress = "http://choobid.com/";
    private static String socketAddress = "ws://yourDomain.etc";
    private static String appTypeId = "1";
    private static String serverAddress = "choobid-portlet/api/jsonws/account/";
    private static String notifWebsocketAddress = socketAddress + "/choobid-portlet/websocket/notif";
    private static String avatarFolder = "/choobid/.avatar";
    private static String productsFolder = "/choobid/.products";

    static public boolean checkNetwoek(Activity context) {

        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }

    public static String getAppTypeId() {
        return appTypeId;
    }

    public static String getServerAddress() {
        return siteAddress + serverAddress;
    }

    public static String getAvatarFolder() {
        return avatarFolder;
    }

    public static String getProductsFolder() {
        return productsFolder;
    }

    public static String getNotifWebsocketAddress() {
        return notifWebsocketAddress;
    }

    public static String addCommasToNumericString(String digits) {
        String result = "";
        int len = digits.length();
        int nDigits = 0;
        for (int i = len - 1; i >= 0; i--) {
            result = digits.charAt(i) + result;
            nDigits++;
            if (((nDigits % 3) == 0) && (i > 0)) {
                result = "," + result;
            }
        }
        return (result);
    }

    public static String readJSONFeed(String URL) {
        StringBuilder stringBuilder = new StringBuilder();
        HttpClient httpClient = new DefaultHttpClient();
        String credentials = "json%40birib%2Eir:json2";

        Log.e("aa" , URL);
        HttpGet httpGet = new HttpGet(URL);
        //httpGet.addHeader("Authorization","Basic "+credentials);

        try {
            HttpResponse response = httpClient.execute(httpGet);
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

    public static void hideSoftKeyboard(Activity a) {

        a.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

    }

    public static void BackToHome(FragmentActivity context) {

        if (context.getSupportFragmentManager().getBackStackEntryCount() > 0) {
            FragmentManager.BackStackEntry entry = context.getSupportFragmentManager().getBackStackEntryAt(
                    0);
            context.getSupportFragmentManager().popBackStack(entry.getId(),
                    android.app.FragmentManager.POP_BACK_STACK_INCLUSIVE);
            context.getSupportFragmentManager().executePendingTransactions();
        }

        Intent main = new Intent(context , MainActivity.class);
        main.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(main);
        context.finishAffinity();
    }


}
