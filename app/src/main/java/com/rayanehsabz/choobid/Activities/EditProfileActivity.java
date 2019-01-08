package com.rayanehsabz.choobid.Activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.rayanehsabz.choobid.Adabters.Mydatabase;
import com.rayanehsabz.choobid.R;
import com.rayanehsabz.choobid.Tools.AppVariables;
import com.rayanehsabz.choobid.Tools.CalendarTool;
import com.rayanehsabz.choobid.Views.showLoading;
import com.squareup.picasso.Picasso;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class EditProfileActivity extends AppCompatActivity {

    String email = "";
    String pass = "";
    String accountId = "";

    String defaul_feId = "0";
    Bitmap selectedBitmap = null;
    String imageDataString = "0";

    HttpPost httpPost = null;

    showLoading loading;

    List<Long> feIds = new ArrayList<Long>();

    int Position;

    Dialog changePass;
    EditText passE;
    EditText npassE;
    EditText reNpassE;

    Activity context = this;
    Mydatabase db;
    ImageView myAvatar;

    TextView city;
    Dialog dialog;

    String cityCode = "0";

    boolean select = false;


    String year = "0";
    String month = "0";
    String day = "0";

    ArrayList<String> states = new ArrayList<String>();

    ArrayList<String> citys = new ArrayList<String>();

    ArrayList<String> months = new ArrayList<String>();


    Dialog date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        db = new Mydatabase(this);
        accountId = db.getSettingString(6);
        email = db.getSettingString(8);
        pass = db.getSettingString(9);

        city = (TextView) findViewById(R.id.cTypes);

        states = db.getStates();

        loading = new showLoading(this, 1);

        if (!db.getSettingString(117).equals("")) {
            day = db.getSettingString(117);
        }
        if (!db.getSettingString(118).equals("")) {
            month = db.getSettingString(118);

        }
        if (!db.getSettingString(119).equals("")) {
            year = db.getSettingString(119);

        }
        months.add("فروردین");
        months.add("اردیبهشت");
        months.add("خرداد");
        months.add("تیر");
        months.add("مرداد");
        months.add("شهریور");
        months.add("مهر");
        months.add("آبان");
        months.add("آذر");
        months.add("دی");
        months.add("بهمن");
        months.add("اسفند");


        changePass = new Dialog(context);
        changePass.requestWindowFeature(Window.FEATURE_NO_TITLE);
        changePass.setContentView(R.layout.dialog_change_pass);

        passE = (EditText) changePass.findViewById(R.id.cPass);
        npassE = (EditText) changePass.findViewById(R.id.npass);
        reNpassE = (EditText) changePass.findViewById(R.id.reNPass);

        ((Button) changePass.findViewById(R.id.changeP)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (passE.getText().toString().equals("") || npassE.getText().toString().equals("") || reNpassE.getText().toString().equals("")) {
                    Toast.makeText(context, getResources().getString(R.string.all_fields_required), Toast.LENGTH_SHORT).show();
                } else if (!passE.getText().toString().equals(db.getSettingString(9))) {
                    Toast.makeText(context, "رمز عبور وارد شده صحیح نمی باشد.", Toast.LENGTH_SHORT).show();
                } else if (!npassE.getText().toString().equals(reNpassE.getText().toString())) {
                    Toast.makeText(context, "رمز عبور جدید با تکرار آن برابر نیست.", Toast.LENGTH_SHORT).show();
                } else {
                    ((Button) changePass.findViewById(R.id.changeP)).setClickable(false);
                    ((TextView) changePass.findViewById(R.id.waitP)).setVisibility(View.VISIBLE);
                    new ReadPassJSONFeedTask().execute(
                            AppVariables.getServerAddress() + "/choobid-portlet/api/jsonws/account/update-pass/email/"
                                    + CalendarTool.getCoded(email) + "/pass/" + CalendarTool.getCoded(pass) + "/scrname/" + CalendarTool.getCoded(db.getSettingString(11)) + "/newpass/" + CalendarTool.getCoded(npassE.getText().toString()));
                }
            }
        });

        ((Button) changePass.findViewById(R.id.cancel)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changePass.dismiss();
            }
        });

        AppVariables.hideSoftKeyboard(this);


        myAvatar = (ImageView) findViewById(R.id.myAvatar);


        TextView Txtemail = (TextView) findViewById(R.id.txtEmail1);
        TextView accId = (TextView) findViewById(R.id.accId);
        EditText sologan = (EditText) findViewById(R.id.sologan);
        TextView mob = (TextView) findViewById(R.id.mobileNo);
        EditText name = (EditText) findViewById(R.id.name);
        EditText nationalCode = (EditText) findViewById(R.id.nationalCode);

        TextView stated = (TextView) findViewById(R.id.sTypes);
        TextView cityd = (TextView) findViewById(R.id.cTypes);
        TextView dayd = (TextView) findViewById(R.id.day);
        TextView monthd = (TextView) findViewById(R.id.month);
        TextView yeard = (TextView) findViewById(R.id.year);


        Log.e("TAg" , " --> " + db.getSettingString(116));
        if (!db.getSettingString(116).equals("0")) {
            stated.setText(db.getStateName(db.getSettingString(116)));
            cityd.setText(db.getCityName(db.getSettingString(116)));
            select = true;
            citys = db.getCitys(db.getStateName(db.getSettingString(116)));
        }
        if (!db.getSettingString(117).equals("0")) {
            dayd.setText(db.getSettingString(117));
            monthd.setText(months.get(Integer.parseInt(db.getSettingString(118)) - 1));
            yeard.setText(db.getSettingString(119));
        }

        TextView scrName = (TextView) findViewById(R.id.scrName);

        sologan.setText(db.getSettingString(112));
        sologan.setSelection(sologan.length());

        mob.setText(db.getSettingString(113));

        name.setText(db.getSettingString(114));
        name.setSelection(name.length());

        nationalCode.setText(db.getSettingString(115));
        nationalCode.setSelection(nationalCode.length());

        scrName.setText(db.getSettingString(11));
        accId.setText(db.getSettingString(6));
        Txtemail.setText(db.getSettingString(8));


        new ReadPicsJSONFeedTask().execute(
                AppVariables.getServerAddress() + "/choobid-portlet/api/jsonws/account/get-avatars/email/"
                        + CalendarTool.getCoded(email) + "/pass/" + CalendarTool.getCoded(pass));
        new CheckPresenterTask().execute(
                AppVariables.getServerAddress() + "/choobid-portlet/api/jsonws/account/check-presenter/email/"
                        + CalendarTool.getCoded(email) + "/pass/" + CalendarTool.getCoded(pass) + "/acc-id/" + db.getSettingString(6));




        ((ImageView) findViewById(R.id.changePass)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changePass.show();
            }
        });


        ((RelativeLayout) findViewById(R.id.Mstate)).setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View view) {
                dialog = new Dialog(context);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.dialog_cities);

                try {

                    CitiesAdapter adapter = new CitiesAdapter(context, states, ((TextView) findViewById(R.id.sTypes)), true);
                    ((ListView) dialog.findViewById(R.id.listC)).setAdapter(adapter);
                } catch (Exception e) {

                    Log.e("Tag1", " ---> " + e);
                }
                dialog.show();
            }


        });

        ((RelativeLayout) findViewById(R.id.Mcity)).setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View view) {
                if (select) {
                    dialog = new Dialog(context);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(R.layout.dialog_cities);

                    try {


                        CitiesAdapter adapter = new CitiesAdapter(context, citys, ((TextView) findViewById(R.id.cTypes)), false);
                        ((ListView) dialog.findViewById(R.id.listC)).setAdapter(adapter);
                    } catch (Exception e) {

                        Log.e("Tag2", " ---> " + e);
                    }
                    dialog.show();

                } else {
                    Toast.makeText(context, "ابتدا استان مورد نظر را انتخاب کنید.", Toast.LENGTH_SHORT).show();

                }
            }


        });

        ((RelativeLayout) findViewById(R.id.dayP)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                date = new Dialog(context);
                date.requestWindowFeature(Window.FEATURE_NO_TITLE);
                date.setContentView(R.layout.dialog_cities);
                DateAdapter adapter = new DateAdapter(context, 0, ((TextView) findViewById(R.id.day)));
                ((ListView) date.findViewById(R.id.listC)).setAdapter(adapter);
                date.show();
            }
        });

        ((RelativeLayout) findViewById(R.id.monthP)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                date = new Dialog(context);
                date.requestWindowFeature(Window.FEATURE_NO_TITLE);
                date.setContentView(R.layout.dialog_cities);
                DateAdapter adapter = new DateAdapter(context, 1, ((TextView) findViewById(R.id.month)));
                ((ListView) date.findViewById(R.id.listC)).setAdapter(adapter);
                date.show();
            }
        });

        ((RelativeLayout) findViewById(R.id.yearP)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                date = new Dialog(context);
                date.requestWindowFeature(Window.FEATURE_NO_TITLE);
                date.setContentView(R.layout.dialog_cities);
                DateAdapter adapter = new DateAdapter(context, 2, ((TextView) findViewById(R.id.year)));
                ((ListView) date.findViewById(R.id.listC)).setAdapter(adapter);
                date.show();
            }
        });

        final Dialog presenter = new Dialog(context);
        presenter.requestWindowFeature(Window.FEATURE_NO_TITLE);
        presenter.setContentView(R.layout.dialog_set_presenter);

        ((Button) presenter.findViewById(R.id.changeP)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               EditText prId = (EditText) presenter.findViewById(R.id.presenterCode);
                if (prId.getText().toString().equals("")) {
                    Toast.makeText(context, "کد معرف را وارد کنید.", Toast.LENGTH_SHORT).show();
                } else {
                    new SetPresenterTask().execute(
                            AppVariables.getServerAddress() + "/choobid-portlet/api/jsonws/account/set-presenter/email/"
                                    + CalendarTool.getCoded(email) + "/pass/" + CalendarTool.getCoded(pass) + "/acc-id/" + db.getSettingString(6) + "/presenter-account-id/"  + prId.getText().toString()

                    );


                }


            }
        });

        ((Button) presenter.findViewById(R.id.cancel)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.dismiss();
            }
        });

        ((RelativeLayout) findViewById(R.id.presenterP)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.show();
            }
        });

    }

    public class CitiesAdapter extends BaseAdapter implements ListAdapter {
        private Context context;
        private ArrayList<String> jsa;
        private TextView textView;
        boolean setP;


        public CitiesAdapter(Context c, ArrayList<String> j, TextView t, boolean p) {
            context = c;
            jsa = j;
            textView = t;
            setP = p;

        }

        //---returns the number of images---
        public int getCount() {
            return jsa.size();
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


//
                final String ss = jsa.get(position);

                ((TextView) title.findViewById(R.id.name)).setText(ss);
                title.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (setP) {
                            try {
                                citys = db.getCitys(ss);
                                city.setText(citys.get(0));
                                cityCode = db.getCityCode(citys.get(0));

                            } catch (Exception e) {

                            }
                            select = true;

                            Position = p;
                        } else {

                            cityCode = db.getCityCode(ss);
                            Log.e("Tag", " ---> " + cityCode);

                        }
                        textView.setText(ss);
                        dialog.dismiss();
                    }
                });
            } catch (Exception e) {

            }

            return title;
        }
    }


    public class DateAdapter extends BaseAdapter implements ListAdapter {
        private Context context;
        private int type;
        private TextView value;


        public DateAdapter(Context c, int type, TextView value) {
            context = c;
            this.type = type;
            this.value = value;


        }

        //---returns the number of images---
        public int getCount() {
            if (type == 0) {
                return 31;
            } else if (type == 1) {
                return 12;
            } else {
                return 101;
            }
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
        public View getView(final int position, View convertView,
                            ViewGroup parent)

        {
            final View title = LayoutInflater.from(context).inflate(R.layout.item_cities,
                    null, false);
            try {


                if (type == 0) {


                    ((TextView) title.findViewById(R.id.name)).setText("" + (position + 1));
                    title.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            day = ((TextView) title.findViewById(R.id.name)).getText().toString();
                            value.setText(day);
                            date.dismiss();
                        }
                    });


                } else if (type == 1) {

                    ((TextView) title.findViewById(R.id.name)).setText(months.get(position));
                    title.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            month = String.valueOf(position + 1);
                            value.setText(((TextView) title.findViewById(R.id.name)).getText().toString());
                            date.dismiss();
                        }
                    });


                } else {


                    ((TextView) title.findViewById(R.id.name)).setText((position + 1300) + "");
                    title.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            year = ((TextView) title.findViewById(R.id.name)).getText().toString();
                            value.setText(year);
                            date.dismiss();

                        }
                    });


                }

            } catch (Exception e) {

            }

            return title;
        }
    }


    private class ReadPassJSONFeedTask extends AsyncTask
            <String, Void, String> {
        protected String doInBackground(String... urls) {
            return AppVariables.readJSONFeed(urls[0]);
        }

        protected void onPostExecute(String result) {
            try {

                JSONObject m1 = new JSONObject(result);
                if (m1.getString("accountId") != null) {
                    db.saveSetting(9, m1.getString("pass"));
                    ((TextView) changePass.findViewById(R.id.waitP)).setVisibility(View.GONE);
                    reNpassE.setText("");
                    npassE.setText("");
                    passE.setText("");
                    ((Button) changePass.findViewById(R.id.changeP)).setClickable(true);
                    Toast.makeText(context, getResources().getString(R.string.PassChanged), Toast.LENGTH_SHORT).show();
                    changePass.dismiss();
                }
//


            } catch (Exception e) {
//            	Log.d("ReadMahfelJSONFeedTask", e.getLocalizedMessage());
            }
        }
    }

    private class ReadPicsJSONFeedTask extends AsyncTask<String, Void, String> {
        protected String doInBackground(String... urls) {
            return AppVariables.readJSONFeed(urls[0]);
        }

        protected void onPostExecute(String result) {
            List<String> scrnames = new ArrayList<String>();
            try {

                JSONObject jsonObject = new JSONObject(result);
                JSONArray newsItems = new JSONArray(jsonObject.getString("avatars"));
                String att;
                for (int i = 0; i < newsItems.length(); i++) {
                    JSONObject m1 = newsItems.getJSONObject(i);
                    if (m1.getString("picFeId") != null) {
                        att = m1.getString("downloadUrl");

                        if ((att.substring(0, 7)).equals("http://")) {
                            String attS = att.substring(7);
                            attS = attS.substring(attS.indexOf("/"));
                            attS = attS.substring(0, (attS.indexOf("?")));
                            att = attS;
                        }

                        new DownloadImageTask(m1.getLong("picFeId"), 0).execute(AppVariables.getServerAddress() + att);

                        feIds.add(m1.getLong("picFeId"));


                    }
                }

            } catch (Exception e) {
//            	Log.e("ReadMahfelJSONFeedTask", "errora:" + e);
            }

        }
    }


    private class CheckPresenterTask extends AsyncTask<String, Void, String> {
        protected String doInBackground(String... urls) {
            return AppVariables.readJSONFeed(urls[0]);
        }

        protected void onPostExecute(String result) {
            List<String> scrnames = new ArrayList<String>();
            try {

                JSONObject jsonObject = new JSONObject(result);

                if (jsonObject.getBoolean("result")) {

                    ((RelativeLayout) findViewById(R.id.presenterP)).setVisibility(View.VISIBLE);
                }


            } catch (Exception e) {
//            	Log.e("ReadMahfelJSONFeedTask", "errora:" + e);
            }

        }
    }
    private class SetPresenterTask extends AsyncTask<String, Void, String> {
        protected String doInBackground(String... urls) {
            return AppVariables.readJSONFeed(urls[0]);
        }

        protected void onPostExecute(String result) {
            List<String> scrnames = new ArrayList<String>();
            try {

                JSONObject jsonObject = new JSONObject(result);

                if (jsonObject.getBoolean("result")) {

                    Toast.makeText(context, "کد معرف با موفقیت ثبت شد.", Toast.LENGTH_SHORT).show();
                    finish();
                } else {

                    Toast.makeText(context, "کد معرف یافت نشد.", Toast.LENGTH_SHORT).show();
                }


            } catch (Exception e) {
//            	Log.e("ReadMahfelJSONFeedTask", "errora:" + e);
            }

        }
    }

    public void edit(View v) {



        String sologan = (((EditText) findViewById(R.id.sologan)).getText().toString().equals("")) ? "e0" : ((EditText) findViewById(R.id.sologan)).getText().toString();
        String name = (((EditText) findViewById(R.id.name)).getText().toString().equals("")) ? "e0" : ((EditText) findViewById(R.id.name)).getText().toString();
        String national =( ((EditText) findViewById(R.id.nationalCode)).getText().toString().equals("")) ? "e0" : ((EditText) findViewById(R.id.nationalCode)).getText().toString();

        EditText nationalCode = (EditText) findViewById(R.id.nationalCode);


        imageDataString = "0";
        if (selectedBitmap != null) {
            try {


                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                if (selectedBitmap.getWidth() > 800) {
                    selectedBitmap = Bitmap.createScaledBitmap(selectedBitmap, 800, (int) (selectedBitmap.getHeight() * 800 / selectedBitmap.getWidth()), true);
                }

                selectedBitmap.compress(Bitmap.CompressFormat.JPEG, 99, baos); //bm is the bitmap object

                byte[] b = baos.toByteArray();

                imageDataString = Base64.encodeToString(b, 0);
                selectedBitmap = null;
                baos.flush();
                baos.close();

            } catch (Exception e) {

            }
        }



        if (nationalCode.length() == 0) {
            loading = new showLoading(this, 1);
            loading.show();
            String[] params = {CalendarTool.getCoded(email), CalendarTool.getCoded(pass), imageDataString, defaul_feId, CalendarTool.getCoded(sologan), CalendarTool.getCoded(name), CalendarTool.getCoded(national), year, month, day, cityCode};
            new SendMessageWithFileTask().execute(params);

        } else {

            if (nationalCode.length() != 10) {
                Toast.makeText(context,"کد ملی باید ده رقمی باشد" , Toast.LENGTH_LONG).show();

            } else {
                String temp = nationalCode.getText().toString();
                boolean again = false;
                for (int i = 1 ; i < 10 ; i++) {
                    if (temp.charAt(i) != temp.charAt(i - 1)) {
                        again = true;
                        break;
                    }

                }
                if (!again) {
                    Toast.makeText(context, "کد ملی معتبر نمی باشد", Toast.LENGTH_SHORT).show();
                } else {
                    int n_letter1 = Integer.parseInt(Character.toString(nationalCode.getText().toString().charAt(9)));
                    int n_letter2 = Integer.parseInt(Character.toString(nationalCode.getText().toString().charAt(8)));
                    int n_letter3 = Integer.parseInt(Character.toString(nationalCode.getText().toString().charAt(7)));
                    int n_letter4 = Integer.parseInt(Character.toString(nationalCode.getText().toString().charAt(6)));
                    int n_letter5 = Integer.parseInt(Character.toString(nationalCode.getText().toString().charAt(5)));
                    int n_letter6 = Integer.parseInt(Character.toString(nationalCode.getText().toString().charAt(4)));
                    int n_letter7 = Integer.parseInt(Character.toString(nationalCode.getText().toString().charAt(3)));
                    int n_letter8 = Integer.parseInt(Character.toString(nationalCode.getText().toString().charAt(2)));
                    int n_letter9 = Integer.parseInt(Character.toString(nationalCode.getText().toString().charAt(1)));
                    int n_letter10 = Integer.parseInt(Character.toString(nationalCode.getText().toString().charAt(0)));

                    int n_sum = n_letter2 * 2 + n_letter3 * 3 + n_letter4 * 4 + n_letter5 * 5 + n_letter6 * 6 + n_letter7 * 7 + n_letter8 * 8 + n_letter9 * 9 + n_letter10 * 10;
                    int n_remain = n_sum % 11;

                    if (n_remain < 2) {
                        if (n_remain != n_letter1) {
                            Toast.makeText(context, "کد ملی معتبر نمی باشد", Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        if ((11 - n_remain) != n_letter1) {
                            Toast.makeText(context, "کد ملی معتبر نمی باشد", Toast.LENGTH_SHORT).show();
                        } else {
                            loading = new showLoading(this, 1);
                            loading.show();
                            String[] params = {CalendarTool.getCoded(email), CalendarTool.getCoded(pass), imageDataString, defaul_feId, CalendarTool.getCoded(sologan), CalendarTool.getCoded(name), CalendarTool.getCoded(national), year, month, day, cityCode};
                            new SendMessageWithFileTask().execute(params);

                        }
                    }
                }
            }

        }

    }


    public void openMyAccount(View arg0) {

        Intent webS = new Intent(this, MyAccountActivity.class);
        //webS.putExtra("bidId" , String.valueOf(b.get(position).id));

        this.startActivity(webS);

    }

    public void defaultPictures(View arg0) {

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.default_pics);


        dialog.show();

        GridView gridView = (GridView) dialog.findViewById(R.id.cartoonsgv);
        gridView.setAdapter(new ImageAdapter(this));
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent,
                                    View v, int position, long id) {

                defaul_feId = String.valueOf(feIds.get(position));
                selectedBitmap = null;
                File avatar = new File(Environment.getExternalStorageDirectory() + AppVariables.getAvatarFolder() + "/" + defaul_feId + ".jpg");


                ImageView im = (ImageView) findViewById(R.id.myAvatar);
                Uri uri = Uri.fromFile(avatar);
                Picasso.with(EditProfileActivity.this).load(uri).into(im);
                dialog.dismiss();

            }
        });


    }

    public class ImageAdapter extends BaseAdapter {
        private Context context;

        public ImageAdapter(Context c) {
            context = c;
        }

        //---returns the number of images---
        public int getCount() {
            return feIds.size();
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
            View v;
            ImageView postimg = null;


            LinearLayout.LayoutParams paramsLO = new LinearLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
            final float scale = getResources().getDisplayMetrics().density;
            int padding_in_px = (int) (3 * scale + 0.5f);

            v = LayoutInflater.from(EditProfileActivity.this).inflate(R.layout.default_pic_grid, null);

            postimg = (ImageView) v.findViewById(R.id.default_img);

            File imgFile = new File("/sdcard" + AppVariables.getAvatarFolder() + "/" + feIds.get(position) + ".jpg");

            if (imgFile.exists()) {


                Uri uri = Uri.fromFile(imgFile);
                Picasso.with(EditProfileActivity.this).load(uri).into(postimg);

                postimg.setTag(feIds.get(position));
            }


            return v;
        }

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

                if (result != null ) {
                    createDirectoryAndSaveFile(result, String.valueOf(feId) + ".jpg");
                }

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
                    Picasso.with(EditProfileActivity.this).load(uri).into(myAvatar);

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }


    private class SendMessageWithFileTask extends AsyncTask
            <String, Void, String> {
        protected String doInBackground(String... params) {
            StringBuilder stringBuilder = new StringBuilder();
            HttpClient httpClient = new DefaultHttpClient();


            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(11);


            nameValuePairs.add(new BasicNameValuePair("email", params[0]));
            nameValuePairs.add(new BasicNameValuePair("pass", params[1]));
            nameValuePairs.add(new BasicNameValuePair("avatar", params[2]));
            nameValuePairs.add(new BasicNameValuePair("feid", params[3]));
            nameValuePairs.add(new BasicNameValuePair("sologan", params[4]));
            nameValuePairs.add(new BasicNameValuePair("namesurename", params[5]));
            nameValuePairs.add(new BasicNameValuePair("natinalCode", params[6]));
            nameValuePairs.add(new BasicNameValuePair("year", params[7]));
            nameValuePairs.add(new BasicNameValuePair("month", params[8]));
            nameValuePairs.add(new BasicNameValuePair("day", params[9]));
            nameValuePairs.add(new BasicNameValuePair("cityCode", params[10]));

            httpPost = new HttpPost(AppVariables.getServerAddress() + "/choobid-portlet/api/jsonws/account/update-account/");

            try {
                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            } catch (UnsupportedEncodingException e1) {

                e1.printStackTrace();
            }

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
                }

            } catch (Exception e) {

            }
//
            return stringBuilder.toString();
        }


        protected void onPostExecute(String result) {
            try {

                JSONObject m1 = new JSONObject(result);

                if (m1.getString("accountId") != null) {


                    db.saveSetting(6, m1.getString("accountId"));

                    db.saveSetting(8, m1.getString("email"));

                    db.saveSetting(11, m1.getString("sreenName"));


                    db.saveSetting(110, m1.getString("avatarFeId"));
                    db.saveSetting(111, m1.getString("avatar"));

                    db.saveSetting(112, m1.getString("sologan"));
                    db.saveSetting(113, m1.getString("mobile"));
                    db.saveSetting(114, m1.getString("name"));
                    db.saveSetting(115, m1.getString("kodemeli"));

                    db.saveSetting(116, m1.getString("cityCode") , "CityCode");

                    db.saveSetting(117, m1.getString("day") , "Day");
                    db.saveSetting(118, m1.getString("month") , "Month");
                    db.saveSetting(119, m1.getString("year") , "Year");


                    if (!m1.getString("avatarFeId").equals("0")) {

                        if (!db.getSettingString(110).equals(m1.getString("avatarFeId"))) {

                            new DownloadAvatar(m1.getLong("avatarFeId")).execute("http://choobid.com" + m1.getString("avatar"));

                        } else {
                            Toast.makeText(context, "ویرایش با موفقیت انجام شد.", Toast.LENGTH_LONG).show();
                            finish();
                        }

                    } else {
                        Toast.makeText(context, "ویرایش با موفقیت انجام شد.", Toast.LENGTH_LONG).show();
                        finish();
                    }
                }


            } catch (Exception e) {
                Log.e("ReadMahfelJSONFeedTask", e + "");
            }
        }
    }


    private class DownloadAvatar extends AsyncTask<String, Void, Bitmap> {
        long feId;
        ImageView imageicon;

        public DownloadAvatar(long feId) {
            this.feId = feId;
        }

        protected void onPreExecute() {


        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
//                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            try {

                if (result != null) {
                    createDirectoryAndSaveFile(result, String.valueOf(feId) + ".jpg");
                }


            } catch (Exception ex) {

            }

        }

        private void createDirectoryAndSaveFile(Bitmap imageToSave, String fileName) {

            File direct = new File(Environment.getExternalStorageDirectory() + AppVariables.getAvatarFolder());

            if (!direct.exists()) {
                File wallpaperDirectory = new File(Environment.getExternalStorageDirectory() + AppVariables.getAvatarFolder());
                wallpaperDirectory.mkdirs();
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


                finish();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


}
