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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rayanehsabz.choobid.Adabters.MyMessageAdapter;
import com.rayanehsabz.choobid.Adabters.Mydatabase;
import com.rayanehsabz.choobid.Classes.Message;
import com.rayanehsabz.choobid.Fragments.NavigationDrawer;
import com.rayanehsabz.choobid.Peyment.BuyProductActivity;
import com.rayanehsabz.choobid.R;
import com.rayanehsabz.choobid.Tools.AppVariables;
import com.rayanehsabz.choobid.Tools.CalendarTool;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class MessagesActivity extends AppCompatActivity {

    Mydatabase db;
    RecyclerView recyclerView;
    LinearLayoutManager layoutManager;
    MyMessageAdapter myMessageAdapter;
    String email;
    String pass;
    ArrayList<Message> messages = new ArrayList<Message>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);

        db = new Mydatabase(this);

        email = db.getSettingString(8);
        pass = db.getSettingString(9);

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


                AppVariables.BackToHome(MessagesActivity.this);

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



        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(4);

        JSONArray notifs = db.getNotifications();
        for (int i = 0 ; i < notifs.length() ; i++) {
            try {
                messages.add(new Message(notifs.getJSONObject(i)));
            } catch (Exception e) {

            }

        }

        recyclerView = (RecyclerView) findViewById(R.id.myNotiRecycler);
        layoutManager = new LinearLayoutManager(this);
        myMessageAdapter = new MyMessageAdapter(messages , this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(myMessageAdapter);


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
