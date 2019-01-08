package com.rayanehsabz.choobid.Classes;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by ahura on 1/17/2017.
 */

public class store {
    public String name;
    public String englishName;

    public String pic;


    public long id;
    public long feId = 1;

    public long price;
    public long offPrice;

    public long inventory = 1;



    public store(JSONObject jsonObject) {
        try {

            this.pic = jsonObject.getString("pic");
            this.name = jsonObject.getString("name");
            this.englishName = jsonObject.getString("Ename");
            this.id = jsonObject.getInt("id");

            this.price = jsonObject.getLong("price");
            this.offPrice = jsonObject.getLong("offPrice");


            this.feId = jsonObject.getLong("feId");
            this.inventory = jsonObject.getLong("inventory");



        } catch (JSONException e) {
            Log.e(" e -- > "  , e.toString());
        }
    }


}