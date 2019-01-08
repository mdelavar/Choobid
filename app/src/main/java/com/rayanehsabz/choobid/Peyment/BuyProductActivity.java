package com.rayanehsabz.choobid.Peyment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.rayanehsabz.choobid.Adabters.Mydatabase;
import com.rayanehsabz.choobid.Classes.shop;
import com.rayanehsabz.choobid.Fragments.NavigationDrawer;
import com.rayanehsabz.choobid.R;
import com.rayanehsabz.choobid.Activities.SearchActivity;
import com.rayanehsabz.choobid.Views.showLoading;


public class BuyProductActivity extends AppCompatActivity {


    Mydatabase db;


    public String email;
    public String pass;

    public long pId;
    public shop ShopeDetail = new shop();

    public int backet = 0;




    showLoading loading;




    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy_product);


        Bundle extra = getIntent().getExtras();

        Log.e("id" , " ----> " + extra.getLong("pId" ));

        if (extra != null) {

            pId = extra.getLong("pId");


            try {
                backet = extra.getInt("b");
            } catch (Exception e) {
                backet = 0;
            }






        }

        if ( extra.getBoolean("bid")) {

            ShopeDetail.bid = true;
        }
        ShopeDetail.pId = pId;

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

        ((RelativeLayout) Actionview.findViewById(R.id.backet)).setVisibility(View.GONE);

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

        if (backet == 1) {
            getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter_from_left, R.anim.exit_from_right, R.anim.enter_from_right, R.anim.exit_from_left).add(R.id.mainContent, new ShopBFragment()).commit();
        } else {
            getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter_from_left, R.anim.exit_from_right, R.anim.enter_from_right, R.anim.exit_from_left).add(R.id.mainContent, new ProductDetailFragment()).commit();
        }


    }


    @Override
    protected void onResume() {
        super.onResume();

        getSupportFragmentManager().beginTransaction().replace(R.id.naviContent, new NavigationDrawer()).commit();
    }


}
