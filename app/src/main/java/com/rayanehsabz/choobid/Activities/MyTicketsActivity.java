package com.rayanehsabz.choobid.Activities;

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

import com.rayanehsabz.choobid.Adabters.MyTicketAdapter;
import com.rayanehsabz.choobid.Adabters.Mydatabase;
import com.rayanehsabz.choobid.Classes.MyTicket;
import com.rayanehsabz.choobid.Fragments.NavigationDrawer;
import com.rayanehsabz.choobid.Peyment.BuyProductActivity;
import com.rayanehsabz.choobid.R;
import com.rayanehsabz.choobid.Tools.AppVariables;
import com.rayanehsabz.choobid.Tools.CalendarTool;
import com.rayanehsabz.choobid.Views.showLoading;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class MyTicketsActivity extends AppCompatActivity {

    MyTicketAdapter adapter;
    ArrayList<MyTicket> tickets = new ArrayList<MyTicket>();
    LinearLayoutManager lLayout;

    DrawerLayout mDrawerLayout;

    String email;
    String pass;
    long accId;

    Mydatabase db;

    showLoading loading;

    @Override
    protected void onResume() {
        super.onResume();

        new BacketCount().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                AppVariables.getServerAddress() + "get-backet-count/email/" + CalendarTool.getCoded(email) + "/pass/" + CalendarTool.getCoded(pass) + "/acc-id/" + db.getSettingString(6)
        );

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_tickets);

        db = new Mydatabase(this);

        loading = new showLoading(this, 1);
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


                AppVariables.BackToHome(MyTicketsActivity.this);

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


        adapter = new MyTicketAdapter(tickets, false, this);
        lLayout = new LinearLayoutManager(this);
        lLayout.setOrientation(LinearLayoutManager.VERTICAL);
        RecyclerView rcl = (RecyclerView) findViewById(R.id.myT);
        rcl.setLayoutManager(lLayout);
        rcl.setAdapter(adapter);

        new TicketTask().execute(AppVariables.getServerAddress() + "get-my-tickets/email/" + CalendarTool.getCoded(email) + "/pass/" + CalendarTool.getCoded(pass) + "/acc-id/" + accId + "/from-record/0/to-record/12");


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
                        tickets.add(mt);

                        adapter.notifyDataSetChanged();
                    }

                } else {

                    ((TextView) findViewById(R.id.noTickets)).setVisibility(View.VISIBLE);

                }

            } catch (Exception e) {

            }

        }
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
