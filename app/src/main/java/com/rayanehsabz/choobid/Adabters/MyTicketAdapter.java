package com.rayanehsabz.choobid.Adabters;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rayanehsabz.choobid.Tools.AppVariables;
import com.rayanehsabz.choobid.Classes.MyTicket;
import com.rayanehsabz.choobid.R;
import com.rayanehsabz.choobid.Activities.ShowTicketActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;

public class MyTicketAdapter extends RecyclerView.Adapter<MyTicketAdapter.MyViewHolder> {


    Activity context;
    ArrayList<MyTicket> finish;

    boolean internal;

    Animation fadeIn;


    public MyTicketAdapter(ArrayList arrayList, boolean internal, Activity context) {

        this.finish = arrayList;
        this.context = context;
        this.internal = internal;

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_my_ticket, parent, false);

        return new MyViewHolder(view);

    }


    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        holder.urgent.setText(String.valueOf(finish.get(position).urgent));
        holder.title.setText(String.valueOf(finish.get(position).title));
        holder.status.setText(String.valueOf(finish.get(position).status));
        holder.time.setText(String.valueOf(finish.get(position).time));

        if (!finish.get(position).parentId.equals("0")) {

            holder.title.setVisibility(View.GONE);

        }

        if (!((finish.get(position).userId).equals("10158"))) {

            holder.admin.setVisibility(View.VISIBLE);
            holder.status.setVisibility(View.GONE);
            holder.statusP.setVisibility(View.GONE);

        }





        if (!internal) {

            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent show = new Intent(context, ShowTicketActivity.class);

                    show.putExtra("id", String.valueOf(finish.get(position).id));
                    show.putExtra("title", finish.get(position).title);
                    show.putExtra("urgent", finish.get(position).urgent);
                    show.putExtra("status", finish.get(position).status);
                    show.putExtra("type", finish.get(position).typeId);

                    context.startActivity(show);

                }

            });
        } else {
            holder.content.setVisibility(View.VISIBLE);
            holder.content.loadData(finish.get(position).content + "<style> * {text-align : justify; direction : rtl; color : #7c7c7c} </style>" ,"text/html; charset=utf-8", "utf-8");

            if (!finish.get(position).file1.equals("")) {
                holder.file1.setVisibility(View.VISIBLE);
                holder.file1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        String url = AppVariables.getServerAddress() + finish.get(position).file1;
                        Log.e("URL" , " -> "  + url);
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        context.startActivity(browserIntent);

                    }
                });

            }

            if (!finish.get(position).file2.equals("")) {
                holder.file2.setVisibility(View.VISIBLE);
                holder.file2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        String url = AppVariables.getServerAddress() + finish.get(position).file2;
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        context.startActivity(browserIntent);

                    }
                });

            }

        }


    }

    @Override
    public int getItemCount() {
        return finish.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {


        CardView cardView;


        TextView urgent;
        WebView content;
        TextView title;
        TextView status;
        TextView time;

        ImageView file1;
        ImageView file2;

        LinearLayout admin;


        TextView statusP;

        public MyViewHolder(View itemView) {
            super(itemView);


            this.urgent = (TextView) itemView.findViewById(R.id.urgent);

            this.content = (WebView) itemView.findViewById(R.id.content);
            this.file1 = (ImageView) itemView.findViewById(R.id.file1A);
            this.file2 = (ImageView) itemView.findViewById(R.id.file2A);


            this.title = (TextView) itemView.findViewById(R.id.title);

            this.status = (TextView) itemView.findViewById(R.id.status);
            this.statusP = (TextView) itemView.findViewById(R.id.statusp);

            this.time = (TextView) itemView.findViewById(R.id.time);
            this.admin = (LinearLayout) itemView.findViewById(R.id.admin);
            this.cardView = (CardView) itemView.findViewById(R.id.cardView);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    private class DownloadProductImage extends AsyncTask<String, Void, Bitmap> {
        long feId;
        ImageView imageicon;

        public DownloadProductImage(long feId, ImageView imageicon) {
            this.feId = feId;
            this.imageicon = imageicon;
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

            File direct = new File(Environment.getExternalStorageDirectory() + AppVariables.getProductsFolder());

            if (!direct.exists()) {
                File wallpaperDirectory = new File(Environment.getExternalStorageDirectory() + AppVariables.getProductsFolder());
                wallpaperDirectory.mkdirs();
            }

            File file = new File(new File(Environment.getExternalStorageDirectory() + AppVariables.getProductsFolder() + "/"), fileName);
            if (file.exists()) {
                file.delete();
            }
            try {
                FileOutputStream out = new FileOutputStream(file);
                imageToSave.compress(Bitmap.CompressFormat.JPEG, 100, out);
                out.flush();
                out.close();
                fadeIn = new AlphaAnimation(0, 1);
                fadeIn.setDuration(800);
                imageicon.setAnimation(fadeIn);

                Bitmap myBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());

                imageicon.setImageBitmap(myBitmap);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
