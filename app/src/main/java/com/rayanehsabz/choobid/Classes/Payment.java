package com.rayanehsabz.choobid.Classes;

import com.rayanehsabz.choobid.Tools.AppVariables;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by ahura on 1/17/2017.
 */

public class Payment {
    public String totPrice;
    public String referenceId;

    public String status;

    public String date;

    public Payment(JSONObject jsonObject) {
        try {

            this.totPrice =  AppVariables.addCommasToNumericString(String.valueOf(Long.parseLong(jsonObject.getString("totPrice"))/10)) + "تومان" ;
            this.referenceId = jsonObject.getString("referenceId");
            this.status = (Long.parseLong( jsonObject.getString("status")) == 0) ? "پرداخت موفق" : "پرداخت ناموفق" ;
            this.date = jsonObject.getString("create-date");

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public Payment(String totPrice, String referenceId, String status , String date) {

        this.totPrice = totPrice;
        this.referenceId = referenceId;
        this.status = status;
        this.date = date;

    }

}