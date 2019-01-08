package com.rayanehsabz.choobid.Classes;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by ahura on 1/17/2017.
 */

public class Message {

    public String date;
    public String title;

    public String pic;
    public String content;
    public long feId;





    public Message(JSONObject jsonObject) {
        try {

            this.content = jsonObject.getString("content");
            this.date = jsonObject.getString("time");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}