package com.rayanehsabz.choobid.Activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.rayanehsabz.choobid.Adabters.Mydatabase;
import com.rayanehsabz.choobid.Fragments.NavigationDrawer;
import com.rayanehsabz.choobid.Peyment.BuyProductActivity;
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


public class SendTicketActivity extends AppCompatActivity {

    Mydatabase db;
    String email;
    String pass;
    long accId;
    long typeId = 0;
    String urgent = "";

    File pickFile1 = null;
    File pickFile2 = null;

    String file1Type = "";
    String file2Type = "";

    int fileN;

    Context context = this;
    DrawerLayout mDrawerLayout;
    Dialog dialogT;
    Dialog dialogU;
    HttpPost httpPost = null;

    showLoading loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_ticket);

        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );
        db = new Mydatabase(context);

        loading = new showLoading(this,1);
        loading.show();

        email = db.getSettingString(8);
        pass = db.getSettingString(9);

        accId = Long.parseLong(db.getSettingString(6));

        // Actionbar //
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.custom_actionbar);

        View Actionview = getSupportActionBar().getCustomView();

        ((ImageView) Actionview.findViewById(R.id.search)).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Intent intent = new Intent(context , SearchActivity.class);
                        startActivity(intent);


                    }
                }
        );

        ((RelativeLayout) Actionview.findViewById(R.id.backet)).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Intent intent = new Intent(context , BuyProductActivity.class);
                        intent.putExtra("b" , 1);
                        startActivity(intent);


                    }
                }
        );

        ((ImageView) Actionview.findViewById(R.id.backToHome)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                AppVariables.BackToHome(SendTicketActivity.this);

            }
        });

        Toolbar parent = (Toolbar) Actionview.getParent();
        parent.setPadding(0, 0, 0, 0);//for tab otherwise give space in tab
        parent.setContentInsetsAbsolute(0, 0);


        // Drawer //
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        final LinearLayout mDrawer = (LinearLayout) findViewById(R.id.left_drawer);
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);


        ImageView img = (ImageView) Actionview.findViewById(R.id.tuggleB);

        img.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (!mDrawerLayout.isDrawerOpen(mDrawer)) {
                    mDrawerLayout.openDrawer(mDrawer);
                } else {
                    mDrawerLayout.closeDrawer(mDrawer);
                }

            }
        });

        getSupportFragmentManager().beginTransaction().add(R.id.naviContent, new NavigationDrawer()).commit();


        dialogT = new Dialog(context);
        dialogT.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogT.setContentView(R.layout.dialog_cities);

        dialogU = new Dialog(context);
        dialogU.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogU.setContentView(R.layout.dialog_urgent);

        ((TextView) dialogU.findViewById(R.id.fori)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                urgent = ((TextView) dialogU.findViewById(R.id.fori)).getText().toString();
                ((TextView) findViewById(R.id.uTypes)).setText(urgent);
                dialogU.dismiss();
            }
        });


        ((TextView) dialogU.findViewById(R.id.normal)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                urgent = ((TextView) dialogU.findViewById(R.id.normal)).getText().toString();
                ((TextView) findViewById(R.id.uTypes)).setText(urgent);
                dialogU.dismiss();
            }
        });


        new TypesTask().execute(AppVariables.getServerAddress() + "get-ticket-types/email/" + CalendarTool.getCoded(email) + "/pass/" + CalendarTool.getCoded(pass));
        ((RelativeLayout) findViewById(R.id.Mcity)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogT.show();
            }
        });

        ((RelativeLayout) findViewById(R.id.ucity)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogU.show();
            }
        });




        ((ImageView) findViewById(R.id.del1)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ((ImageView) findViewById(R.id.pickF1B)).setImageResource(R.drawable.file_1);
                ((ImageView)findViewById(R.id.del1)).setVisibility(View.GONE);
                ((TextView) findViewById(R.id.file1Name)).setText("");
                pickFile1 = null;

            }
        });

        ((ImageView) findViewById(R.id.del2)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ((ImageView) findViewById(R.id.pickF2B)).setImageResource(R.drawable.file_2);
                ((ImageView)findViewById(R.id.del2)).setVisibility(View.GONE);
                ((TextView) findViewById(R.id.file2Name)).setText("");
                pickFile2 = null;

            }
        });



    }

    private class TypesTask extends AsyncTask<String, Void, String> {
        protected String doInBackground(String... urls) {
            return AppVariables.readJSONFeed(urls[0]);
        }

        protected void onPostExecute(String result) {


            try {
                final JSONObject m1 = new JSONObject(result);
                JSONArray jsa = m1.getJSONArray("types");
                if (jsa.length() > 0) {
                    loading.dismiss();
                    TypesAdapter adapter = new TypesAdapter(context, jsa, ((TextView) findViewById(R.id.tTypes)));
                    ((ListView) dialogT.findViewById(R.id.listC)).setAdapter(adapter);
                }
            } catch (Exception e) {

            }

        }
    }


    public class TypesAdapter extends BaseAdapter implements ListAdapter {
        private Context context;
        private JSONArray jsa;
        private TextView textView;


        public TypesAdapter(Context c, JSONArray j, TextView t) {
            context = c;
            jsa = j;
            textView = t;

        }

        //---returns the number of images---
        public int getCount() {
            return jsa.length();
        }

        //---returns the item---
        public Object getItem(int position) {
            return position;
        }

        //---returns the ID of an item---
        public long getItemId(int position) {
            return position;
        }

        //---returns an ImageView view---
        public View getView(int position, View convertView,
                            ViewGroup parent)

        {
            View title = LayoutInflater.from(context).inflate(R.layout.item_cities,
                    null, false);
            try {
                final int p = position;
                final JSONObject jso = new JSONObject(jsa.getString(position));

//
                final String ss = jso.getString("name");
                final long sss = jso.getLong("id");
                ((TextView) title.findViewById(R.id.name)).setText(ss);
                title.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        typeId = sss;
                        textView.setText(ss);
                        dialogT.dismiss();

                    }
                });
            } catch (Exception e) {

            }

            return title;
        }
    }


    public void sendPost(View arg0) throws IOException {

        EditText title = (EditText) findViewById(R.id.titleT);
        EditText content = (EditText) findViewById(R.id.contentT);


        if (typeId == 0) {
            Toast.makeText(context, "موضوع را مشخص کنید", Toast.LENGTH_LONG).show();
        } else if (urgent.equals("")) {
            Toast.makeText(context, "اولویت را مشخص کنید", Toast.LENGTH_LONG).show();
        } else if (title.getText().toString().equals("")) {
            Toast.makeText(context, "عنوان را وارد کنید", Toast.LENGTH_LONG).show();
        } else if (content.getText().toString().equals("")) {
            Toast.makeText(context, "متن پیام را وارد کنید", Toast.LENGTH_LONG).show();
        } else {

            String[] params = {CalendarTool.getCoded(email), CalendarTool.getCoded(pass), String.valueOf(accId), "0", CalendarTool.getCoded(title.getText().toString()), CalendarTool.getCoded(content.getText().toString()), String.valueOf(typeId), CalendarTool.getCoded(urgent), null, null, CalendarTool.getCoded("ارسال شده") , file1Type , file2Type };
            loading.show();
            new SendMessageWithFileTask().execute(params);
            arg0.setClickable(false);

        }


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
                }
            }

            entityBuilder.addTextBody("status", params[10]);
            entityBuilder.addTextBody("fileType1", params[11]);
            entityBuilder.addTextBody("fileType2", params[12]);

            HttpEntity reqentity = entityBuilder.build();

            httpPost = new HttpPost(AppVariables.getServerAddress() + "send-ticket/");
            httpPost.setEntity(new ProgressHttpEntityWrapper(reqentity, progressCallback));






            try {

                HttpResponse response = httpClient.execute(httpPost);
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


        protected void onPostExecute(String result) {
            try {
                JSONObject jsonObject = new JSONObject(result);

                if (result != null) {

                    Toast.makeText(context, "پیام با موفقیت ارسال شد", Toast.LENGTH_SHORT).show();
                    finish();

                }



            } catch (Exception e) {
            }


        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        new BacketCount().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                AppVariables.getServerAddress() + "get-backet-count/email/" + CalendarTool.getCoded(email) + "/pass/" + CalendarTool.getCoded(pass) + "/acc-id/" + db.getSettingString(6)
        );
    }


    private class BacketCount extends AsyncTask<String, Void, String> {
        protected String doInBackground(String... urls) {
            return AppVariables.readJSONFeed(urls[0]);
        }

        protected void onPostExecute(String result) {
            try {

                JSONObject m1 = new JSONObject(result);

                if (m1.getInt("count") > 0) {

                    View view = getSupportActionBar().getCustomView();
                    ((TextView) view.findViewById(R.id.bc)).setVisibility(View.VISIBLE);
                    ((TextView) view.findViewById(R.id.bc)).setText(m1.getString("count"));

                } else {
                    View view = getSupportActionBar().getCustomView();
                    ((TextView) view.findViewById(R.id.bc)).setVisibility(View.GONE);
                }

            } catch (Exception e) {

            }
        }
    }

}
