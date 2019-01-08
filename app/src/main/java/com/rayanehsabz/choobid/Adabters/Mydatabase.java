package com.rayanehsabz.choobid.Adabters;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class Mydatabase extends SQLiteAssetHelper {

    private static final String DATABASE_NAME = "choobid.db";
    private static final int DATABASE_VERSION = 1;

    public Mydatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    public void saveSetting(int id , String value) {

        SQLiteDatabase db=getWritableDatabase();

        String query;

        query="Update setting set value = '" + value + "' where Id =" + id ;
//        System.out.println("query:" + query);
        db.execSQL(query);

    }

    public void saveSetting(int id , String value, String description) {

        SQLiteDatabase db=getWritableDatabase();

        Cursor cursor;
        String query;

        query="SELECT value FROM setting Where id = " + id ;
        cursor=db.rawQuery(query,null);
        if (cursor.getCount() == 0) {
            query="Insert Into setting (id, description, value) Values (" + id + ",'" + description + "','" + value + "')";
//          System.out.println("query:" + query);
            db.execSQL(query);

        } else {
            query="Update setting set value = '" + value + "' where Id =" + id ;
//          System.out.println("query:" + query);
            db.execSQL(query);

        }


    }


    public boolean insertNotification( String time,  String title , String content , String pic , String feId) {

        SQLiteDatabase db=getWritableDatabase();

        Cursor cursor;
        String query;

        query="Insert Into notifications ( time, content, pic, feId, title) Values ('" + time + "','" + content + "','" + pic + "','" + feId + "','" + title + "')";
//          System.out.println("query:" + query);
        db.execSQL(query);

        return true;


    }

    public JSONArray getNotifications () {

        SQLiteDatabase db=getWritableDatabase();

        Cursor cursor;
        String query;

        JSONArray ss = new JSONArray();
        query="SELECT * FROM notifications" ;
        cursor=db.rawQuery(query,null);
        for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            try {
                JSONObject jso = new JSONObject();
                jso.put("time" , cursor.getString(0) );
                jso.put("content" , cursor.getString(1) );
                ss.put(jso);
            } catch (Exception e) {

            }


        }

        return ss;


    }

    public String getSettingString(int id)
    {

        SQLiteDatabase db=getReadableDatabase();

        Cursor cursor;
        String query;
        String settingValue = "";
        query="SELECT value FROM setting Where id = " + id ;
        cursor=db.rawQuery(query,null);
        for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            settingValue =cursor.getString(0);
            //System.out.println("roomDraft:" + roomDraft);
        }
        cursor.close();

        return settingValue;
    }

    public ArrayList getStates()
    {

        SQLiteDatabase db=getReadableDatabase();

        Cursor cursor;
        String query;
        ArrayList<String> ss = new ArrayList<String>();
        query="SELECT stateName FROM city GROUP BY stateName" ;
        cursor=db.rawQuery(query,null);
        for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            ss.add(cursor.getString(0));

        }
        cursor.close();

        return ss;
    }

    public ArrayList<String> getCitys(String stateName)
    {

        SQLiteDatabase db=getReadableDatabase();

        Cursor cursor;
        String query;
        ArrayList<String> ss = new ArrayList<String>();
        query="SELECT cityName FROM city Where stateName = " + '"' + stateName + '"' ;
        cursor=db.rawQuery(query,null);
        for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            ss.add(cursor.getString(0));
            //System.out.println("roomDraft:" + roomDraft);
        }
        cursor.close();

        return ss;
    }

    public String getCityCode(String cityName)
    {

        SQLiteDatabase db=getReadableDatabase();

        Cursor cursor;
        String query;
        String settingValue = "";
        query="SELECT cityId FROM city Where cityName = " + '"' + cityName + '"';
        cursor=db.rawQuery(query,null);
        for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            settingValue =cursor.getString(0);
            //System.out.println("roomDraft:" + roomDraft);
        }
        cursor.close();

        return settingValue;
    }


    public String getCityName(String cityCode)
    {

        SQLiteDatabase db=getReadableDatabase();

        Cursor cursor;
        String query;
        String settingValue = "";
        query="SELECT cityName FROM city Where cityId = "  + cityCode ;
        cursor=db.rawQuery(query,null);
        for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            settingValue =cursor.getString(0);
            //System.out.println("roomDraft:" + roomDraft);
        }
        cursor.close();

        return settingValue;
    }


    public String getStateName(String cityCode)
    {

        SQLiteDatabase db=getReadableDatabase();

        Cursor cursor;
        String query;
        String settingValue = "";
        query="SELECT stateName FROM city Where cityId ="  + Long.parseLong(cityCode) ;
        cursor=db.rawQuery(query,null);
        for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            settingValue =cursor.getString(0);
            //System.out.println("roomDraft:" + roomDraft);
        }
        cursor.close();

        return settingValue;
    }


    public void deleteAcc() {

        SQLiteDatabase db=getWritableDatabase();

        String query;

        query="Update setting set value = '0' where Id = 6" ;
        //System.out.println("query:" + query);
        db.execSQL(query);

    }





}