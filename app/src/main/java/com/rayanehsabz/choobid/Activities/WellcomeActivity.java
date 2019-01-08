package com.rayanehsabz.choobid.Activities;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.rayanehsabz.choobid.Adabters.Mydatabase;
import com.rayanehsabz.choobid.BuildConfig;
import com.rayanehsabz.choobid.R;
import com.rayanehsabz.choobid.Tools.AppVariables;
import com.rayanehsabz.choobid.Tools.CalendarTool;
import com.rayanehsabz.choobid.Views.GifImageView;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class WellcomeActivity extends AppCompatActivity {

    Mydatabase db;

    long secondR = 3000;

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wellcome);

        ((TextView) findViewById(R.id.ver)).setText("نسخه " + BuildConfig.VERSION_NAME);

        db = new Mydatabase(this);

        try {
            GifImageView gifImageView = (GifImageView) findViewById(R.id.GifImageView);

            gifImageView.setGifImageResource(R.drawable.comp_3p);

            if (AppVariables.checkNetwoek(this)) {
                ActivityCompat.requestPermissions(WellcomeActivity.this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                        1);

            } else {

                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:
                                finish();

                                break;

                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(this.getString(R.string.connectionText)).setPositiveButton(this.getString(R.string.logOut), dialogClickListener)
                        .setCancelable(false).show();

            }


        } catch (Exception e) {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("ERROR : " + e ).show();

        }


    }



    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {



                    if (!db.getSettingString(6).equals("0")) {


                        String emailC = CalendarTool.getCoded(db.getSettingString(8));
                        String passC = CalendarTool.getCoded(db.getSettingString(9));


                        new LoginTask().execute(

                                AppVariables.getServerAddress() + "login/email/" + emailC + "/pass/" + passC + "/reg/" + BuildConfig.VERSION_CODE + "/api/" + Build.VERSION.RELEASE + "/app-type/" + AppVariables.getAppTypeId()
                        );


                    } else {

                        CountDownTimer cn = new CountDownTimer(3000, 100) {
                            @Override
                            public void onTick(long l) {

                            }

                            @Override
                            public void onFinish() {

                                Intent nIntent = new Intent(getBaseContext(), LoginActivity.class);
                                startActivity(nIntent);


                                finish();
                            }


                        }.start();

                    }

                } else {

                    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case DialogInterface.BUTTON_POSITIVE:
                                    ActivityCompat.requestPermissions(WellcomeActivity.this,
                                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                                            1);

                                    break;

                                case DialogInterface.BUTTON_NEGATIVE:
                                    finish();
                                    break;
                            }
                        }
                    };

                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage(this.getString(R.string.permissionMessage)).setPositiveButton(this.getString(R.string.config), dialogClickListener)
                            .setNegativeButton(this.getString(R.string.logOut), dialogClickListener).show();


                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    private class LoginTask extends AsyncTask<String, Void, String> {
        protected String doInBackground(String... urls) {
            return AppVariables.readJSONFeed(urls[0]);
        }

        protected void onPostExecute(String result) {

            try {


                final JSONObject m1 = new JSONObject(result);
                if (m1.getString("accountId") != null) {


                    CountDownTimer cn = new CountDownTimer(2000, 100) {
                        @Override
                        public void onTick(long l) {

                        }

                        @Override
                        public void onFinish() {
                            try {

                                if (!m1.getString("accountId").equals("-1") || !m1.getString("accountId").equals("-2")) {


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

                                            ActivityCompat.finishAffinity(WellcomeActivity.this);
                                            Intent i = new Intent(WellcomeActivity.this, MainActivity.class);
                                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            startActivity(i);

                                        }
                                    } else {

                                        ActivityCompat.finishAffinity(WellcomeActivity.this);
                                        Intent i = new Intent(WellcomeActivity.this, MainActivity.class);
                                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(i);

                                    }

                                } else {

                                    Intent nIntent = new Intent(getBaseContext(), LoginActivity.class);
                                    startActivity(nIntent);
                                    finish();

                                }


                            }catch (Exception e) {
                            }

                        }


                    }.start();

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

                ActivityCompat.finishAffinity(WellcomeActivity.this);
                Intent i= new Intent(WellcomeActivity.this, MainActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);


            } catch (Exception e) {
            }
        }
    }
}
