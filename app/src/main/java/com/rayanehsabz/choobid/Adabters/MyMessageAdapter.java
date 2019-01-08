package com.rayanehsabz.choobid.Adabters;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;

import com.rayanehsabz.choobid.Tools.AppVariables;
import com.rayanehsabz.choobid.Classes.Message;
import com.rayanehsabz.choobid.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;

public class MyMessageAdapter extends RecyclerView.Adapter<MyMessageAdapter.MyViewHolder> {


    Activity context;
    ArrayList<Message> finish;



    Animation fadeIn;


    public MyMessageAdapter(ArrayList arrayList,  Activity context) {

        this.finish = arrayList;
        this.context = context;


    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message, parent, false);

        return new MyViewHolder(view);

    }


    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        holder.date.setText(finish.get(position).date);
        holder.content.setText(finish.get(position).content);





    }

    @Override
    public int getItemCount() {
        return finish.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {


        CardView cardView;


        TextView date;
        TextView title;
        TextView content;

        ImageView Pic;







        public MyViewHolder(View itemView) {
            super(itemView);

            this.date = (TextView) itemView.findViewById(R.id.messageDate);

            this.content = (TextView) itemView.findViewById(R.id.messageContent);

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
