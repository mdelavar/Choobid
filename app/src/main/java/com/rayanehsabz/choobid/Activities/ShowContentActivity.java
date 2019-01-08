package com.rayanehsabz.choobid.Activities;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rayanehsabz.choobid.Adabters.Mydatabase;
import com.rayanehsabz.choobid.Fragments.NavigationDrawer;
import com.rayanehsabz.choobid.Peyment.BuyProductActivity;
import com.rayanehsabz.choobid.R;
import com.rayanehsabz.choobid.Tools.AppVariables;
import com.rayanehsabz.choobid.Tools.CalendarTool;
import com.rayanehsabz.choobid.Views.showLoading;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

public class ShowContentActivity extends AppCompatActivity {

    Bundle extera ;


    String email;
    String pass;



    Mydatabase db;

    showLoading loading;

    String articleId ="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_content);

        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(3);

        loading = new showLoading(this , 1);
        loading.show();

        extera = getIntent().getExtras();

        db = new Mydatabase(this);

        email = db.getSettingString(8);
        pass = db.getSettingString(9);


        // Actionbar //
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.custom_actionbar);

        View Actionview = getSupportActionBar().getCustomView();


        ((ImageView) Actionview.findViewById(R.id.search)).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Intent intent = new Intent(getApplicationContext() , SearchActivity.class);
                        startActivity(intent);


                    }
                }
        );

        ((RelativeLayout) Actionview.findViewById(R.id.backet)).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Intent intent = new Intent(getApplicationContext() , BuyProductActivity.class);
                        intent.putExtra("b" , 1);
                        startActivity(intent);


                    }
                }
        );

        ((ImageView) Actionview.findViewById(R.id.backToHome)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                AppVariables.BackToHome(ShowContentActivity.this);

            }
        });

        Toolbar parent = (Toolbar) Actionview.getParent();
        parent.setPadding(0, 0, 0, 0);//for tab otherwise give space in tab
        parent.setContentInsetsAbsolute(0, 0);


        // Drawer
        final DrawerLayout mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
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
        // Drawer //



        if (extera!=null) {
            articleId = extera.getString("articleId");

        }

        new GetContentTask().execute(

                AppVariables.getServerAddress() + "/choobid-portlet/api/jsonws/account/get-content/email/" + CalendarTool.getCoded(email) + "/pass/" + CalendarTool.getCoded(pass) + "/article-id/" + articleId
        );


    }



    public class GetContentTask extends AsyncTask<String,Void,String> {

        @Override
        protected String doInBackground(String... strings) {
            return  AppVariables.readJSONFeed(strings[0]);
        }


        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);



            try {

                JSONObject jsonObject = new JSONObject(s);
                JSONArray jsa = jsonObject.getJSONArray("articles");

                JSONObject jso = jsa.getJSONObject(0);
                if (jso.length() > 0) {

                    if(!jso.getString("pic").equals("")) {
                        Picasso.with(ShowContentActivity.this).load(AppVariables.getServerAddress() + jso.getString("pic")).into((ImageView) findViewById(R.id.newsPic));
                    }
                    if(!jso.getString("title").equals("")) {
                        ((TextView) findViewById(R.id.newsTitle)).setText(jso.getString("title"));
                    }

                    if(!jso.getString("content").equals("")) {
                        WebView myWebview = (WebView) findViewById(R.id.newsContent);
                        WebSettings webViewSettings = myWebview.getSettings();
                        webViewSettings.setJavaScriptEnabled(true);
                        webViewSettings.setDomStorageEnabled(true);
                        webViewSettings.setLoadWithOverviewMode(true);
                        webViewSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
                        webViewSettings.setUseWideViewPort(true);

                        String style = "<style> *{ text-align:justify !important;padding : 10px 14px ; font-size:50px !important; line-height : 80px !important; direction : rtl !important;} img {padding : 12px 20px !important;}</style>";
                        myWebview.loadData(style + jso.getString("content"), "text/html; charset=utf-8", "utf-8");

                    }
                    loading.dismiss();
                }

            } catch (Exception e) {


            }
        }
    }


    @Override
    protected void onResume() {
        super.onResume();

        new BacketCount().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                AppVariables.getServerAddress() + "/choobid-portlet/api/jsonws/account/get-backet-count/email/" + CalendarTool.getCoded(email) + "/pass/" + CalendarTool.getCoded(pass) + "/acc-id/" + db.getSettingString(6)
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
                Log.e("ReadMahfelJSONFeedTask", "error : " + e);

            }
        }
    }
}
