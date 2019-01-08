package com.rayanehsabz.choobid.Services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import com.rayanehsabz.choobid.Adabters.Mydatabase;
import com.rayanehsabz.choobid.Tools.AppVariables;
import com.rayanehsabz.choobid.Activities.MessagesActivity;
import com.rayanehsabz.choobid.R;
import com.rayanehsabz.choobid.Activities.ShowContentActivity;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_17;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;

public class NotiService extends Service {

    NotificationCompat.Builder builder;


    Service context = this;
    int i = 1;

    Mydatabase db;
    WebSocketClient mWebSocketClient;
    RemoteViews BigcontentView;
    RemoteViews contentView;
    /**
     * indicates how to behave if the service is killed
     */
    int mStartMode;

    /**
     * interface for clients that bind
     */
    IBinder mBinder;

    /**
     * indicates whether onRebind should be used
     */
    boolean mAllowRebind;

    /**
     * Called when the service is being created.
     */
    @Override
    public void onCreate() {

    }

    /**
     * The service is starting, due to a call to startService()
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("close", "onStartCommand");
        db = new Mydatabase(context);
        connectWebSocket();


        return START_STICKY;

    }

    /**
     * A client is binding to the service with bindService()
     */
    @Override
    public IBinder onBind(Intent intent) {
        Log.e("close", "onBind");
        return mBinder;
    }

    /**
     * Called when all clients have unbound with unbindService()
     */
    @Override
    public boolean onUnbind(Intent intent) {
        Log.e("close", "onUnbind");
        return mAllowRebind;
    }

    /**
     * Called when a client is binding to the service with bindService()
     */
    @Override
    public void onRebind(Intent intent) {
        Log.e("close", "onRebind");
    }

    /**
     * Called when The service is no longer used and is being destroyed
     */
    @Override
    public void onDestroy() {
        doSend("close");
        Log.e("close", "onDestroy");
    }

    private void connectWebSocket() {
        URI uri;
        try {
            uri = new URI(AppVariables.getNotifWebsocketAddress());
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return;
        }

        mWebSocketClient = new WebSocketClient(uri, new Draft_17()) {

            @Override
            public void onOpen(ServerHandshake serverHandshake) {
                Log.e("WebsocketService", "Opened" + i);
                i++;
//                doSend("getBidData");
                Handler handler = new Handler(Looper.getMainLooper());

                handler.post(new Runnable() {

                    @Override
                    public void run() {
                    }
                });
            }

            @Override
            public void onMessage(String s) {
                Log.e("TagService", " M : " + s);

                try {

                    final JSONObject obj = new JSONObject(s);
                    Handler handler = new Handler(Looper.getMainLooper());

                    handler.post(new Runnable() {

                        @Override
                        public void run() {
                            try {
                                if (obj.getString("notifType").equals("bidNoti") || obj.getString("notifType").equals("subscribe")) {

                                    contentView = new RemoteViews(context.getApplicationContext().getPackageName(), R.layout.custom_noti);

                                    String startTime = getResources().getString(R.string.start);
                                    contentView.setTextViewText(R.id.Ttitle, obj.getString("name"));
                                    contentView.setTextViewText(R.id.Tdate, String.format(startTime, obj.getString("startTime")));

                                    BigcontentView = new RemoteViews(context.getApplicationContext().getPackageName(), R.layout.custom_big_noti);

                                    BigcontentView.setTextViewText(R.id.Ttitle, obj.getString("name"));
                                    BigcontentView.setTextViewText(R.id.Tdate, String.format(startTime, obj.getString("startTime")));


                                } else if (obj.getString("notifType").equals("content")) {

                                    contentView = new RemoteViews(context.getApplicationContext().getPackageName(), R.layout.custom_noti);

                                    contentView.setTextViewText(R.id.Ttitle, obj.getString("title"));
                                    contentView.setViewVisibility(R.id.Tdate, View.GONE);
                                    Intent notificationIntent = new Intent(context, ShowContentActivity.class);
                                    notificationIntent.putExtra("articleId", obj.getString("articleId"));

                                    PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent,
                                            PendingIntent.FLAG_ONE_SHOT);

                                    Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

                                    Notification noti;

                                    builder = new NotificationCompat.Builder(context);
                                    builder.setSmallIcon(R.drawable.noti_icon)
                                            .setContent(contentView)
                                            .setSound(alarmSound)
                                            .setContentIntent(contentIntent)
                                    ;

                                    noti = builder.build();

                                    SendNotification(noti, 3);
                                } else if (obj.getString("notifType").equals("costum")) {

                                    boolean insert = db.insertNotification(obj.getString("time") , "" , obj.getString("text") , "" , "" );

                                    Log.e("Insert" , insert + "");

                                    contentView = new RemoteViews(context.getApplicationContext().getPackageName(), R.layout.custom_noti);

                                    contentView.setTextViewText(R.id.Ttitle, "یک پیام جدید دریافت شد!");
                                    contentView.setViewVisibility(R.id.Tdate, View.GONE);

                                    Intent notificationIntent = new Intent(context, MessagesActivity.class);


                                    PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent,
                                            PendingIntent.FLAG_ONE_SHOT);

                                    Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

                                    Notification noti;

                                    builder = new NotificationCompat.Builder(context);
                                    builder.setSmallIcon(R.drawable.noti_icon)
                                            .setContent(contentView)
                                            .setSound(alarmSound)
                                            .setContentIntent(contentIntent)
                                    ;

                                    noti = builder.build();

                                    SendNotification(noti, 4);
                                }

                            } catch (Exception e) {

                            }


                        }

                });


            }

            catch(
            Exception e
            )

            {

                Log.e("Tag", " ---> ErrorService : " + e);

            }

        }

        @Override
        public void onClose ( int i, String s,boolean b){
            Log.e("WebsocketService", "Closed " + s);

            Handler handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    try {
                        connectWebSocket();
                    } catch (Exception ex) {
                        Log.e("Websocket-Error", "Websocket-Error---" + ex);
                    }
                }
            }, 500);



        }

        @Override
        public void onError (Exception e){
            Log.e("Websocket", "Error " + e.getMessage());
        }
    }

    ;


    mWebSocketClient.connect();

}

    public void doSend(String message) {
        JSONObject obj = new JSONObject();
        try {

        } catch (Exception e) {

        }


        mWebSocketClient.send(String.valueOf(obj));

    }

    void SendNotification(Notification noti, int id) {

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(id, noti);

    }


private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {

    long feId;
    String bidId;

    public DownloadImageTask(long feId, String bidId) {

        this.feId = feId;
        this.bidId = bidId;

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

            if (result != null) {
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

            Bitmap myBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
            BigcontentView.setImageViewBitmap(R.id.productPic, myBitmap);

            Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            Notification noti;

            builder = new NotificationCompat.Builder(context);
            builder.setSmallIcon(R.drawable.noti_icon)
                    .setContent(contentView)
                    .setCustomBigContentView(BigcontentView)
                    .setSound(alarmSound)
            ;

            noti = builder.build();

            SendNotification(noti, 2);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

}
