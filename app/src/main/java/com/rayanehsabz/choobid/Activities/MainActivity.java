package com.rayanehsabz.choobid.Activities;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.rayanehsabz.choobid.Adabters.Mydatabase;
import com.rayanehsabz.choobid.BuildConfig;
import com.rayanehsabz.choobid.Fragments.NavigationDrawer;
import com.rayanehsabz.choobid.Fragments.StoreFragment;
import com.rayanehsabz.choobid.Peyment.BuyProductActivity;
import com.rayanehsabz.choobid.R;
import com.rayanehsabz.choobid.Services.NotiService;
import com.rayanehsabz.choobid.Tools.AppVariables;
import com.rayanehsabz.choobid.Tools.CalendarTool;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;


public class MainActivity extends AppCompatActivity {

    Mydatabase db;

    String email;
    String pass;

    int i = 0;

    DrawerLayout mDrawerLayout;
    Activity context;


    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            i++;

            if (i < 2) {


                Toast.makeText(this, "برای خروج دکمه بازگشت را مجددا فشار دهید.", Toast.LENGTH_SHORT).show();
                CountDownTimer cn = new CountDownTimer(2500, 100) {
                    @Override
                    public void onTick(long l) {

                    }

                    @Override
                    public void onFinish() {
                        i = 0;
                    }


                }.start();
            } else {
                super.onBackPressed();
                finish();
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent e) {
        if (keyCode == KeyEvent.KEYCODE_MENU) {

            if (!mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
                mDrawerLayout.openDrawer(GravityCompat.START);
            } else {
                mDrawerLayout.closeDrawer(GravityCompat.START);
            }
            return true;

        }
        return super.onKeyDown(keyCode, e);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = this;

        db = new Mydatabase(this);

        email = db.getSettingString(8);
        pass = db.getSettingString(9);

        new AppTypeTask().execute(

                AppVariables.getServerAddress() + "get-app-data/email/" + CalendarTool.getCoded(db.getSettingString(8)) + "/pass/" + CalendarTool.getCoded(db.getSettingString(9))
        );

        if (!isMyServiceRunning(NotiService.class)) {
            Intent in = new Intent(this, NotiService.class);
            startService(in);
        }

        // Actionbar //
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.custom_actionbar);

        View Actionview = getSupportActionBar().getCustomView();

        ((ImageView) Actionview.findViewById(R.id.search)).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Intent intent = new Intent(getApplicationContext(), SearchActivity.class);
                        startActivity(intent);


                    }
                }
        );

        ((RelativeLayout) Actionview.findViewById(R.id.backet)).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Intent intent = new Intent(getApplicationContext(), BuyProductActivity.class);
                        intent.putExtra("b", 1);
                        startActivity(intent);


                    }
                }
        );

        ((ImageView) Actionview.findViewById(R.id.search)).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Intent intent = new Intent(context, SearchActivity.class);
                        startActivity(intent);


                    }
                }
        );

        ((RelativeLayout) Actionview.findViewById(R.id.backet)).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Intent intent = new Intent(context, BuyProductActivity.class);
                        intent.putExtra("b", 1);
                        startActivity(intent);


                    }
                }
        );


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
        getSupportFragmentManager().beginTransaction().add(R.id.mainContent, new StoreFragment(), "store").commit();

    }

    @Override
    protected void onResume() {
        super.onResume();

        new BacketCount().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                AppVariables.getServerAddress() + "get-backet-count/email/" + CalendarTool.getCoded(email) + "/pass/" + CalendarTool.getCoded(pass) + "/acc-id/" + db.getSettingString(6)
        );

    }


    private boolean isMyServiceRunning(Class<?> serviceClass) {
        try {
            ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
            for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
                if (serviceClass.getName().equals(service.service.getClassName())) {
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            return false;
        }

    }


    private class AppTypeTask extends AsyncTask<String, Void, String> {
        protected String doInBackground(String... urls) {
            return AppVariables.readJSONFeed(urls[0]);
        }

        protected void onPostExecute(String result) {
            try {

                JSONObject m1 = new JSONObject(result);


                if (BuildConfig.VERSION_CODE < m1.getLong("minVersionReq")) {


                    Dialog loading = new Dialog(MainActivity.this);
                    loading.requestWindowFeature(Window.FEATURE_NO_TITLE);


                    loading.setCanceledOnTouchOutside(false);

                    loading.setContentView(R.layout.dialog_update);
                    loading.setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialogInterface) {
                            MainActivity.this.finish();
                        }
                    });


                    ((Button) loading.findViewById(R.id.buy)).setText("به روز رسانی");
                    ((Button) loading.findViewById(R.id.cancel)).setText("خروج");


                    ((Button) loading.findViewById(R.id.buy)).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://choobid.com/app"));
                            startActivity(browserIntent);

                        }
                    });

                    ((Button) loading.findViewById(R.id.cancel)).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            MainActivity.this.finish();

                        }
                    });

                    loading.show();

                } else if (!m1.getString("breakMessage").equals("")) {
                    Dialog loading = new Dialog(MainActivity.this);
                    loading.requestWindowFeature(Window.FEATURE_NO_TITLE);


                    loading.setCanceledOnTouchOutside(false);

                    loading.setContentView(R.layout.dialog_update);
                    loading.setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialogInterface) {
                            MainActivity.this.finish();
                        }
                    });

                    ((TextView) loading.findViewById(R.id.nbT)).setText(m1.getString("breakMessage"));

                    ((Button) loading.findViewById(R.id.buy)).setVisibility(View.GONE);
                    ((Button) loading.findViewById(R.id.cancel)).setText("خروج");


                    ((Button) loading.findViewById(R.id.cancel)).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            MainActivity.this.finish();

                        }
                    });

                    loading.show();

                } else {

                    if (!m1.getString("adsPicUrl").equals("")) {

                        final String url = m1.getString("adsUrl");
                        String pic = m1.getString("adsPicUrl");
                        String type = m1.getString("adsType");
                        String feId = m1.getString("adsPicFeId");


                        if (!db.getSettingString(1).equals(feId)) {


                            final Dialog loading = new Dialog(MainActivity.this);
                            loading.requestWindowFeature(Window.FEATURE_NO_TITLE);
                            loading.setCanceledOnTouchOutside(false);

                            loading.setOnCancelListener(new DialogInterface.OnCancelListener() {
                                @Override
                                public void onCancel(DialogInterface dialogInterface) {
                                    finish();
                                }
                            });

                            loading.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                            loading.setContentView(R.layout.dialog_ads);
                            ImageView adsPic = (ImageView) loading.findViewById(R.id.ads);
                            Picasso.with(MainActivity.this).load(pic).into(adsPic);
                            if (type.equals("url")) {
                                adsPic.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Intent browser = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                                        startActivity(browser);
                                        loading.dismiss();
                                    }
                                });
                            } else if (type.equals("")) {

                                adsPic.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        loading.dismiss();
                                    }
                                });

                            }

                            loading.show();
                            db.saveSetting(1, feId);
                        }

                    }

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
