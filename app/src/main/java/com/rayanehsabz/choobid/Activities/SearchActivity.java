package com.rayanehsabz.choobid.Activities;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.AsyncTask;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.rayanehsabz.choobid.Adabters.Mydatabase;
import com.rayanehsabz.choobid.Adabters.StoreAdapter;
import com.rayanehsabz.choobid.Classes.store;
import com.rayanehsabz.choobid.R;
import com.rayanehsabz.choobid.Tools.AppVariables;
import com.rayanehsabz.choobid.Tools.CalendarTool;
import com.rayanehsabz.choobid.Views.showLoading;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {

    Activity context;


    EditText value;
    ImageView bSearch;

    String email;
    String pass;

    Mydatabase db;

    ArrayList<store> pr = new ArrayList<>();

    TextView notFound;

    StoreAdapter adapter;
    RecyclerView recyclerView;


    private GridLayoutManager lLayout;

    showLoading loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        context = this;


        loading = new showLoading(context, 1);


        db = new Mydatabase(context);

        email = db.getSettingString(8);
        pass = db.getSettingString(9);

        notFound = (TextView) findViewById(R.id.notFound);


        recyclerView = (RecyclerView) findViewById(R.id.storeRcv);
        lLayout = new GridLayoutManager(context, 2) {
            @Override
            protected boolean isLayoutRTL() {
                return true;
            }
        };

        adapter = new StoreAdapter(pr, false, context);
        recyclerView.setLayoutManager(lLayout);
        recyclerView.setAdapter(adapter);


        value = (EditText) findViewById(R.id.searchText);
        bSearch = (ImageView) findViewById(R.id.searchB);

        bSearch.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        search(value.getText().toString());

                    }
                }
        );

        ((ImageView) findViewById(R.id.voice)).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {


                        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

                        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "fa_IR");
                        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                                getString(R.string.speech_prompt));

                        try {
                            startActivityForResult(intent, 1);
                        } catch (ActivityNotFoundException a) {
                            Toast.makeText(context, "تلفن همراه شما از جستجوی صوتی پشتیبانی نمی کند!", Toast.LENGTH_SHORT).show();
                        }

                    }

                }
        );

        value.setOnEditorActionListener(
                new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                        search(value.getText().toString());
                        return false;
                    }

                }


        );


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case 1: {
                if (resultCode == Activity.RESULT_OK && null != data) {
                    String yourResult = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS).get(0);

                    value.setText(yourResult);
                    value.setSelection(yourResult.length());
                    search(value.getText().toString());
                }
                break;
            }
        }
    }

    public void search(String val) {

        if (val.length() > 2) {
            pr.clear();
            adapter.notifyDataSetChanged();
            loading.show();

            new ProductTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                    AppVariables.getServerAddress() + "/choobid-portlet/api/jsonws/account/search/email/" + CalendarTool.getCoded(email) + "/pass/" + CalendarTool.getCoded(pass) + "/value/" + CalendarTool.getCoded(val));
        } else {

            Toast.makeText(context , "متن وارد شده باید بیشتر از سه حرف باشد!" , Toast.LENGTH_SHORT).show();

        }
    }

    private class ProductTask extends AsyncTask<String, Void, String> {
        protected String doInBackground(String... urls) {
            return AppVariables.readJSONFeed(urls[0]);
        }

        protected void onPostExecute(String result) {


            try {
                notFound.setVisibility(View.GONE);
                loading.dismiss();
                JSONObject jsonObject = new JSONObject(result);

                if (jsonObject.length() > 0) {
                    JSONArray products = new JSONArray(jsonObject.getString("products"));

                    for (int i = 0; i < products.length(); i++) {

                        JSONObject js = products.getJSONObject(i);
                        pr.add(new store(js));
                        adapter.notifyDataSetChanged();


                    }
                } else {

                    notFound.setVisibility(View.VISIBLE);

                }


            } catch (Exception e) {
                Log.e("ReadMahfelJSONFeedTask", "error:" + e);
            }

        }
    }
}
