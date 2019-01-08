package com.rayanehsabz.choobid.Activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rayanehsabz.choobid.Adabters.MyPaymentAdapter;
import com.rayanehsabz.choobid.Adabters.Mydatabase;
import com.rayanehsabz.choobid.Classes.Payment;
import com.rayanehsabz.choobid.Fragments.NavigationDrawer;
import com.rayanehsabz.choobid.Peyment.BuyProductActivity;
import com.rayanehsabz.choobid.R;
import com.rayanehsabz.choobid.Tools.AppVariables;
import com.rayanehsabz.choobid.Tools.CalendarTool;
import com.rayanehsabz.choobid.Views.showLoading;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class MyAccountActivity extends AppCompatActivity {

    String email = "";
    String pass = "";
    String accountId = "";

    String defaul_feId = "0";
    final int PIC_CROP = 5;
    final int PICK_IMAGE = 1;

    List<Long> feIds = new ArrayList<Long>();

    ArrayList<Payment> payments = new ArrayList<>();
    MyPaymentAdapter padapter;

    Mydatabase db;

    ImageView myAvatar;

    RecyclerView recyclerView;
    RecyclerView precyclerView;

    private LinearLayoutManager lLayout;

    private LinearLayoutManager plLayout;

    showLoading loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_account);

        loading = new showLoading(this, 1);
        loading.show();


        AppVariables.hideSoftKeyboard(this);
        db = new Mydatabase(this);

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


        Toolbar parent = (Toolbar) Actionview.getParent();
        parent.setPadding(0, 0, 0, 0);//for tab otherwise give space in tab
        parent.setContentInsetsAbsolute(0, 0);


        // Drawer //
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




        TextView scrName = (TextView) findViewById(R.id.txtScrName1);
        scrName.setText(db.getSettingString(11));

        accountId = db.getSettingString(6);
        email = db.getSettingString(8);
        pass = db.getSettingString(9);

        // Get Product

        // Get Product
        new MyPaymentsTask().execute(
                AppVariables.getServerAddress() + "/choobid-portlet/api/jsonws/account/get-peyments/email/" + CalendarTool.getCoded(email) + "/pass/" + CalendarTool.getCoded(pass) + "/from-record/0/to-record/48");



        // Adapter //
        precyclerView = (RecyclerView) findViewById(R.id.payments);
        padapter = new MyPaymentAdapter(payments, this);
        plLayout = new LinearLayoutManager(this , LinearLayoutManager.VERTICAL , false );
        precyclerView.setLayoutManager(plLayout);
        precyclerView.setAdapter(padapter);

    }





    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {

        long feId;
        int a;

        public DownloadImageTask(long feId, int a) {

            this.feId = feId;
            this.a = a;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                if (!urldisplay.trim().isEmpty()) {
                    InputStream in = new java.net.URL(urldisplay).openStream();
                    mIcon11 = BitmapFactory.decodeStream(in);
                }
            } catch (Exception e) {
//                Log.e("Error", "error" + e);
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            try {

                createDirectoryAndSaveFile(result, String.valueOf(feId) + ".jpg");


            } catch (Exception ex) {

            }

        }

        private void createDirectoryAndSaveFile(Bitmap imageToSave, String fileName) {

            File direct = new File(Environment.getExternalStorageDirectory() + AppVariables.getAvatarFolder());

            if (!direct.exists()) {

                direct.mkdirs();
            }

            File file = new File(new File(Environment.getExternalStorageDirectory() + AppVariables.getAvatarFolder() + "/"), fileName);
            if (file.exists()) {
                file.delete();
            }
            try {
                FileOutputStream out = new FileOutputStream(file);
                imageToSave.compress(Bitmap.CompressFormat.JPEG, 100, out);
                out.flush();
                out.close();

                if (a == 1) {

                    Uri uri = Uri.fromFile(file);
                    Picasso.with(MyAccountActivity.this).load(uri).into(myAvatar);

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }


    private class MyPaymentsTask extends AsyncTask<String, Void, String> {
        protected String doInBackground(String... urls) {
            return AppVariables.readJSONFeed(urls[0]);
        }

        protected void onPostExecute(String result) {


            try {


                JSONObject jsonObject = new JSONObject(result);

                loading.dismiss();

                JSONArray products = new JSONArray(jsonObject.getString("result"));

                if (products.length() > 0) {

                    if (products.length() > 0) {
                        Payment pa1 = new Payment("مبلغ", "کد پیگیری","وضعیت" , "تاریخ");
                        payments.add(pa1);
                    }

                    for (int i = 0; i < products.length(); i++) {

                        JSONObject js = products.getJSONObject(i);
                        payments.add(new Payment(js));
                        padapter.notifyDataSetChanged();


                    }

                }


            } catch (Exception e) {
//          	Log.d("ReadMahfelJSONFeedTask", "error:" + e);
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
