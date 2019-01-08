package com.rayanehsabz.choobid.Classes;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by ahura on 1/17/2017.
 */

public class MyTicket {
    public String title;
    public String status;
    public String time;
    public String urgent;
    public String content;
    public String typeId;
    public String userId ="";
    public String file1 ="";
    public String file2 ="";
    public String parentId ="";

    public long id;




    public MyTicket(JSONObject jsonObject) {
        try {

            this.title = jsonObject.getString("title");
            this.time = jsonObject.getString("time");
            this.urgent = jsonObject.getString("UrgentType");
            this.status = jsonObject.getString("status");
            this.content = jsonObject.getString("content");
            this.typeId = jsonObject.getString("typeId");
            this.id = jsonObject.getLong("id");
            this.userId = jsonObject.getString("userId");
            this.file1 = jsonObject.getString("file1");
            this.file2 = jsonObject.getString("file2");
            this.parentId = jsonObject.getString("parentId");


        } catch (JSONException e) {
            Log.e("Error" , e + "");

        }
    }


}