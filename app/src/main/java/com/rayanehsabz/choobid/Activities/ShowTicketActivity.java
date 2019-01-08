package com.rayanehsabz.choobid.Activities;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.rayanehsabz.choobid.Adabters.MyTicketAdapter;
import com.rayanehsabz.choobid.Adabters.Mydatabase;
import com.rayanehsabz.choobid.Classes.MyTicket;
import com.rayanehsabz.choobid.R;
import com.rayanehsabz.choobid.Tools.AppVariables;
import com.rayanehsabz.choobid.Tools.CalendarTool;
import com.rayanehsabz.choobid.Views.showLoading;

import org.apache.commons.io.FileUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class ShowTicketActivity extends AppCompatActivity {

    Activity context = this;
    Mydatabase db;
    RecyclerView rcycler;
    LinearLayoutManager layoutManager;
    MyTicketAdapter adapter;

    ArrayList<MyTicket> ticketList = new ArrayList<MyTicket>();

    Bundle extra;

    String urgent;
    String title;
    String type;
    String status;
    String id;

    File pickFile1 = null;
    File pickFile2 = null;

    String file1Type = "";
    String file2Type = "";

    String email;
    String pass;

    String accId;
    showLoading loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_ticket);

        db = new Mydatabase(context);


        loading = new showLoading(this,1);
        loading.show();

        email = db.getSettingString(8);
        pass = db.getSettingString(9);

        accId = db.getSettingString(6);

        extra = getIntent().getExtras();

        if (extra != null) {
            id =  extra.getString("id");
            urgent = extra.getString("urgent");
            title = extra.getString("title");
            type = extra.getString("type");
            status = extra.getString("status");
        }

        rcycler = (RecyclerView) findViewById(R.id.myT);
        layoutManager = new LinearLayoutManager(context);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        adapter = new MyTicketAdapter(ticketList , true ,context);
        rcycler.setLayoutManager(layoutManager);
        rcycler.setAdapter(adapter);

        new TicketTask().execute(AppVariables.getServerAddress() + "/choobid-portlet/api/jsonws/account/get-ticket-answers/email/" + CalendarTool.getCoded(email) + "/pass/" +  CalendarTool.getCoded(pass) + "/ticket-id/" + id + "/from-record/0/to-record/12") ;

        final EditText editText = (EditText) findViewById(R.id.contentT) ;
        ((ImageView) findViewById(R.id.sendM)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (editText.getText().toString().equals("")) {
                    Toast.makeText(context, "متن پیام را وارد کنید.", Toast.LENGTH_SHORT).show();
                } else {

                    String[] params = {CalendarTool.getCoded(email), CalendarTool.getCoded(pass), String.valueOf(accId), id , CalendarTool.getCoded(""), CalendarTool.getCoded(editText.getText().toString()), String.valueOf(type), CalendarTool.getCoded(urgent), null, null, CalendarTool.getCoded("ارسال شده") , file1Type , file2Type };
                    loading.show();
                    new SendMessageWithFileTask().execute(params);
                    view.setClickable(false);
                }

            }
        });
    }



    private class SendMessageWithFileTask extends AsyncTask
            <String, Void, String> {
        protected String doInBackground(String... params) {
            StringBuilder stringBuilder = new StringBuilder();
            HttpClient httpClient = new DefaultHttpClient();

            String credentials = "json%40birib%2Eir:json2";

            MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
            entityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);


            entityBuilder.addTextBody("email", params[0]);
            entityBuilder.addTextBody("pass", params[1]);
            entityBuilder.addTextBody("accId", params[2]);
            entityBuilder.addTextBody("pid", params[3]);
            entityBuilder.addTextBody("title", params[4]);
            entityBuilder.addTextBody("content", params[5]);
            entityBuilder.addTextBody("typeId", params[6]);
            entityBuilder.addTextBody("urgentT", params[7]);


            ProgressHttpEntityWrapper.ProgressCallback progressCallback = new ProgressHttpEntityWrapper.ProgressCallback() {

                public void progress(float progress) {


                }

            };

            if (pickFile1 != null) {
                try {
                    byte[] data = FileUtils.readFileToByteArray(pickFile1);
                    ByteArrayBody byteArrayBody = new ByteArrayBody(data, "file1." + file1Type);
                    entityBuilder.addPart("file1", byteArrayBody);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            } else {
                try {
                    File temp = new File(Environment.getExternalStorageDirectory() + "/test.txt");
                    temp.createNewFile();

                    byte[] data = FileUtils.readFileToByteArray(temp);
                    ByteArrayBody byteArrayBody = new ByteArrayBody(data, "file1." + file1Type);
                    entityBuilder.addPart("file1", byteArrayBody);
                    temp.delete();
                }catch (Exception e) {
                    Log.e("f" , "" + e);
                }
            }

            if (pickFile2 != null) {
                try {
                    byte[] data = FileUtils.readFileToByteArray(pickFile2);
                    ByteArrayBody byteArrayBody = new ByteArrayBody(data, "file2." + file1Type);
                    entityBuilder.addPart("file2", byteArrayBody);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            } else {
                try {
                    File temp = new File(Environment.getExternalStorageDirectory() + "/test.txt");
                    temp.createNewFile();
                    byte[] data = FileUtils.readFileToByteArray(temp);
                    ByteArrayBody byteArrayBody = new ByteArrayBody(data, "file2." + file1Type);
                    entityBuilder.addPart("file2", byteArrayBody);
                    temp.delete();
                }catch (Exception e) {
                    Log.e("f" , "" + e);
                }
            }

            entityBuilder.addTextBody("status", params[10]);
            entityBuilder.addTextBody("fileType1", params[11]);
            entityBuilder.addTextBody("fileType2", params[12]);

            HttpEntity reqentity = entityBuilder.build();

            HttpPost httpPost = new HttpPost(AppVariables.getServerAddress() + "/choobid-portlet/api/jsonws/account/send-ticket/");
            httpPost.setEntity(new ProgressHttpEntityWrapper(reqentity, progressCallback));






            try {

                HttpResponse response = httpClient.execute(httpPost);
                StatusLine statusLine = response.getStatusLine();
                int statusCode = statusLine.getStatusCode();
//                Log.e("JSON", String.valueOf(statusCode));
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
//        	Log.e("calback", stringBuilder.toString());
            return stringBuilder.toString();
        }


        protected void onPostExecute(String result) {
            try {
                Log.e("Tag" , result + "");
                JSONObject jsonObject = new JSONObject(result);

                if (result != null) {

                    Toast.makeText(context, "پیام با موفقیت ارسال شد", Toast.LENGTH_SHORT).show();
                    finish();

                }



            } catch (Exception e) {
                Log.e("ReadMahfelJSONFeedTask", e + "");
            }


        }
    }






    private class TicketTask extends AsyncTask<String, Void, String> {
        protected String doInBackground(String... urls) {
            return AppVariables.readJSONFeed(urls[0]);
        }

        protected void onPostExecute(String result) {


            try {
                final JSONObject m1 = new JSONObject(result);
                if (m1.length() > 0) {
                    JSONArray jsa = m1.getJSONArray("tickets");

                    loading.dismiss();

                    for (int i = 0; i < jsa.length(); i++) {
                        MyTicket mt = new MyTicket(jsa.getJSONObject(i));
                        ticketList.add(mt);

                        adapter.notifyDataSetChanged();
                    }

                } else {

                    ((TextView) findViewById(R.id.noTickets)).setVisibility(View.VISIBLE);

                }

            } catch (Exception e) {

            }

        }
    }
}
