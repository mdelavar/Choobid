package com.rayanehsabz.choobid.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.rayanehsabz.choobid.R;


public class TicketsActivity extends AppCompatActivity {




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tickets);





        ((ImageView) findViewById(R.id.sendM)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sendI = new Intent(TicketsActivity.this , SendTicketActivity.class);
                startActivity(sendI);
            }
        });

        ((ImageView) findViewById(R.id.myMessage)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sendI = new Intent(TicketsActivity.this , MyTicketsActivity.class);
                startActivity(sendI);
            }
        });

        ((ImageView) findViewById(R.id.notifications)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sendI = new Intent(TicketsActivity.this , MessagesActivity.class);
                startActivity(sendI);
            }
        });

    }






}
