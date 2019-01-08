package com.rayanehsabz.choobid.Activities;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import android.support.v4.app.FragmentManager.BackStackEntry;
import com.rayanehsabz.choobid.Adabters.Mydatabase;
import com.rayanehsabz.choobid.Fragments.LoginFragment;
import com.rayanehsabz.choobid.Fragments.SignUpFragment;
import com.rayanehsabz.choobid.R;

public class LoginActivity extends AppCompatActivity {

    public String email = "" ;
    public String pass = "";
    public String Repass = "";
    public String mobNum = "";
    public String username = "";

    Mydatabase db;



    @Override
    public void onBackPressed() {


        super.onBackPressed();
        if (getSupportFragmentManager().findFragmentByTag("LoginFragment").isVisible()) {

            ((Button) findViewById(R.id.login)).setBackgroundColor(getResources().getColor(R.color.gray));
            ((Button) findViewById(R.id.login)).setTextColor(getResources().getColor(R.color.black));

            ((Button) findViewById(R.id.signUp)).setBackgroundColor(getResources().getColor(R.color.darkgray));
            ((Button) findViewById(R.id.signUp)).setTextColor(getResources().getColor(R.color.white));

            ((Button) findViewById(R.id.login)).setClickable(false);
            ((Button) findViewById(R.id.signUp)).setClickable(true);

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);



        getSupportFragmentManager().beginTransaction().add(R.id.contentPanel, new LoginFragment(), "LoginFragment").commit();

        db = new Mydatabase(this);


        ((Button) findViewById(R.id.login)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter_from_right, R.anim.exit_from_left, R.anim.enter_from_left, R.anim.exit_from_right).replace(R.id.contentPanel, new LoginFragment(), "LoginFragment").commit();
                clearBackstack();
                ((Button) findViewById(R.id.login)).setBackgroundColor(getResources().getColor(R.color.gray));
                ((Button) findViewById(R.id.login)).setTextColor(getResources().getColor(R.color.black));

                ((Button) findViewById(R.id.signUp)).setBackgroundColor(getResources().getColor(R.color.darkgray));
                ((Button) findViewById(R.id.signUp)).setTextColor(getResources().getColor(R.color.white));

                view.setClickable(false);
                ((Button) findViewById(R.id.signUp)).setClickable(true);
            }
        });
        ((Button) findViewById(R.id.login)).setClickable(false);
        ((Button) findViewById(R.id.signUp)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter_from_left, R.anim.exit_from_right, R.anim.enter_from_right, R.anim.exit_from_left).replace(R.id.contentPanel, new SignUpFragment()).addToBackStack(null).commit();

                ((Button) findViewById(R.id.signUp)).setBackgroundColor(getResources().getColor(R.color.gray));
                ((Button) findViewById(R.id.signUp)).setTextColor(getResources().getColor(R.color.black));

                ((Button) findViewById(R.id.login)).setBackgroundColor(getResources().getColor(R.color.darkgray));
                ((Button) findViewById(R.id.login)).setTextColor(getResources().getColor(R.color.white));

                view.setClickable(false);
                ((Button) findViewById(R.id.login)).setClickable(true);
            }
        });




    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void clearBackstack() {

        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            BackStackEntry entry = getSupportFragmentManager().getBackStackEntryAt(
                    0);
            getSupportFragmentManager().popBackStack(entry.getId(),
                    FragmentManager.POP_BACK_STACK_INCLUSIVE);
            getSupportFragmentManager().executePendingTransactions();
        }

    }
}
