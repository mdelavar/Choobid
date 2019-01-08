package com.rayanehsabz.choobid.Fragments;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.adjust.sdk.Adjust;
import com.adjust.sdk.AdjustEvent;
import com.rayanehsabz.choobid.Adabters.Mydatabase;
import com.rayanehsabz.choobid.Tools.AppVariables;
import com.rayanehsabz.choobid.BuildConfig;
import com.rayanehsabz.choobid.Tools.CalendarTool;
import com.rayanehsabz.choobid.Activities.MainActivity;
import com.rayanehsabz.choobid.R;

import com.rayanehsabz.choobid.Views.showLoading;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class LoginFragment extends Fragment {

    View rootView;

    Mydatabase db;

    EditText email;
    EditText pass;

    long feId;
    String avatar;

    showLoading loading;

    private RelativeLayout rl;

    public LoginFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_login, container, false);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        db = new Mydatabase(getActivity());
        loading = new showLoading(getActivity(), 1);

        email = (EditText) rootView.findViewById(R.id.txtEmail);
        pass = (EditText) rootView.findViewById(R.id.txtPassword);

        rl = (RelativeLayout) rootView.findViewById(R.id.activity_main);

        ((TextView) rootView.findViewById(R.id.lg)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AppVariables.hideSoftKeyboard(getActivity());

                String emailC = CalendarTool.getCoded(email.getText().toString());
                String passC = CalendarTool.getCoded(pass.getText().toString());

                if (emailC.equals("") || passC.equals("")) {
                    Toast.makeText(getActivity(), "لطفا تمام فیلد ها را پر کنید", Toast.LENGTH_LONG).show();
                } else {
                    loading.show();
                    new LoginTask().execute(

                            AppVariables.getServerAddress() + "login/email/" + emailC + "/pass/" + passC + "/reg/" + BuildConfig.VERSION_CODE + "/api/" + Build.VERSION.RELEASE + "/app-type/" + AppVariables.getAppTypeId()
                    );
                }

            }
        });


        ((TextView) rootView.findViewById(R.id.forgetPass)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                if (email.getText().toString().equals("")) {
                                    Toast.makeText(getActivity(), "ایمیل را وارد نمایید.", Toast.LENGTH_SHORT).show();
                                } else {
                                    TextView tx = (TextView) rootView.findViewById(R.id.forgetPass);
                                    tx.setClickable(false);
                                    tx.setText(" ");
                                    tx.setBackgroundResource(R.drawable.wait_icon);
                                    new forgetJSONFeedTask().execute(

                                            AppVariables.getServerAddress() + "send-pass/email/"
                                                    + CalendarTool.getCoded(email.getText().toString())) ;
                                    break;
                                }


                            case DialogInterface.BUTTON_NEGATIVE:
                                //No button clicked
                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage(getActivity().getString(R.string.forgetPassText)).setPositiveButton(getActivity().getString(R.string.send), dialogClickListener)
                        .setNegativeButton(getActivity().getString(R.string.cancel), dialogClickListener).show();
            }
        });

    }

    private class forgetJSONFeedTask extends AsyncTask
            <String, Void, String> {
        protected String doInBackground(String... urls) {
            return AppVariables.readJSONFeed(urls[0]);
        }
        String result1;
        protected void onPostExecute(String result) {
            List<String> scrnames = new ArrayList<String>();
            try {

                JSONObject jsonObject = new JSONObject(result);
                if (jsonObject.has("code") && !jsonObject.getString("code").equals("0") ) {
                    TextView tx = (TextView) rootView.findViewById(R.id.forgetPass);
                    tx.setText(R.string.PassSended);
                    tx.setBackgroundResource(0);
                } else if (jsonObject.has("code") && jsonObject.getString("code").equals("-1")) {
                    TextView tx = (TextView) rootView.findViewById(R.id.forgetPass);
                    tx.setText(R.string.PassNotSended);
                    tx.setBackgroundResource(0);
                }




            } catch (Exception e) {
            }




        }
    }

    private class LoginTask extends AsyncTask<String, Void, String> {
        protected String doInBackground(String... urls) {
            return AppVariables.readJSONFeed(urls[0]);
        }

        protected void onPostExecute(String result) {

            try {

                JSONObject m1 = new JSONObject(result);
                if (m1.getString("accountId") != null) {
                    if (m1.getString("accountId").equals("-1")) {


                        loading.dismiss();
                        Toast.makeText(getActivity(),
                                getActivity().getString(R.string.loginPassError),
                                Toast.LENGTH_LONG).show();

                    } else if (m1.getString("accountId").equals("-2")) {


                        loading.dismiss();
                        Toast.makeText(getActivity(),
                                getActivity().getString(R.string.loginEmailError),
                                Toast.LENGTH_LONG).show();

                    } else {


                        db.saveSetting(6, m1.getString("accountId"));
                        db.saveSetting(8, m1.getString("email"));
                        db.saveSetting(9, pass.getText().toString());
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

                        AdjustEvent event = new AdjustEvent("h6fujq");
                        Adjust.trackEvent(event);

                        if (!m1.getString("avatarFeId").equals("0")) {

                            if (!db.getSettingString(110).equals(m1.getString("avatarFeId"))) {
                                new DownloadAvatar(m1.getLong("avatarFeId")).execute("http://choobid.com" + m1.getString("avatar"));
                            } else {

                                ActivityCompat.finishAffinity(getActivity());
                                Intent i = new Intent(getActivity(), MainActivity.class);
                                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(i);

                            }
                        } else {

                            ActivityCompat.finishAffinity(getActivity());
                            Intent i = new Intent(getActivity(), MainActivity.class);
                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(i);

                        }




                    }

                } else {


                    Toast.makeText(getActivity(),
                            getActivity().getString(R.string.loginServerError),
                            Toast.LENGTH_LONG).show();
                    loading.dismiss();

                }
            } catch (Exception e) {
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
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            try {

                if (result != null) {
                    createDirectoryAndSaveFile(result,String.valueOf(feId) + ".jpg");
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

            File file = new File(new File(Environment.getExternalStorageDirectory() + AppVariables.getAvatarFolder()+ "/" ), fileName);
            if (file.exists()) {
                file.delete();
            }
            try {
                FileOutputStream out = new FileOutputStream(file);
                imageToSave.compress(Bitmap.CompressFormat.JPEG, 100, out);
                out.flush();
                out.close();

                ActivityCompat.finishAffinity(getActivity());
                Intent i= new Intent(getActivity(), MainActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);


            } catch (Exception e) {
            }
        }
    }



}
