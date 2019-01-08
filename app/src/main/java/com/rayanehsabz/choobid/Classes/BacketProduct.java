package com.rayanehsabz.choobid.Classes;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by ahura on 1/17/2017.
 */

public class BacketProduct {
    public String name;
    public String englishName;

    public String pic;


    public long colorId = 0;
    public long garantyId = 0;
    public long sizeId = 0;

    public long id;
    public long feId = 1;

    public long price;
    public int count;

    public boolean portable = true;



    public BacketProduct(JSONObject jsonObject) {
        try {


            this.name = jsonObject.getString("name");

            this.count = jsonObject.getInt("c");

            if (count == 0) count = 1;

            this.id = jsonObject.getInt("id");

            this.price = jsonObject.getLong("price");

            this.pic = jsonObject.getString("pic");

            this.feId = jsonObject.getLong("feId");

            this.englishName = jsonObject.getString("englishName");

            if (jsonObject.has("portable")) {

                this.portable = jsonObject.getInt("portable") == 1;

            }

            this.colorId = jsonObject.getLong("color");
            this.garantyId = jsonObject.getLong("garanty");
            this.sizeId = jsonObject.getLong("size");

        } catch (JSONException e) {
        }
    }


}