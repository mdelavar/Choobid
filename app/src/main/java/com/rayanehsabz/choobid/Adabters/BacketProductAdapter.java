package com.rayanehsabz.choobid.Adabters;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
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
import com.rayanehsabz.choobid.Classes.BacketProduct;
import com.rayanehsabz.choobid.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;

public class BacketProductAdapter extends RecyclerView.Adapter<BacketProductAdapter.MyViewHolder> {


    Activity context;
    ArrayList<BacketProduct> products;

    public Typeface mj_two, mj_nawal, t3;

    Animation fadeIn;


    public BacketProductAdapter(ArrayList arrayList, Activity context ) {
        this.products = arrayList;
        this.context = context;
        mj_two = Typeface.createFromAsset(context.getAssets(),
                "fonts/Mj_Two Medium.ttf");

        mj_nawal = Typeface.createFromAsset(context.getAssets(),
                "fonts/Mj_Nawal.ttf");
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bproduct_abs, parent, false);

        return new MyViewHolder(view);

    }


    @Override
    public void onBindViewHolder(final BacketProductAdapter.MyViewHolder holder, final int position) {

        File f = new File(Environment.getExternalStorageDirectory() + AppVariables.getProductsFolder() + "/" + products.get(position).feId + ".jpg");

        Bitmap mb = BitmapFactory.decodeFile(f.getAbsolutePath());
        holder.pic.setImageBitmap(mb);

        holder.name.setText(products.get(position).name);
        holder.Ename.setText(products.get(position).englishName);

        holder.count.setText(products.get(position).count + "");

        holder.basePrice.setText(AppVariables.addCommasToNumericString(String.valueOf(products.get(position).price / 10)));
        holder.price.setText(AppVariables.addCommasToNumericString(String.valueOf((products.get(position).price / 10)  * products.get(position).count)));


    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {


        CardView cardView;
        ImageView pic;
        TextView Ename;
        TextView name;


        TextView basePrice;
        TextView price;
        TextView count;


        public MyViewHolder(View itemView) {
            super(itemView);


            this.pic = (ImageView) itemView.findViewById(R.id.pImg);
            this.Ename = (TextView) itemView.findViewById(R.id.pename);
            this.name = (TextView) itemView.findViewById(R.id.pname);

            this.count = (TextView) itemView.findViewById(R.id.tcount);
            this.basePrice = (TextView) itemView.findViewById(R.id.oPrice);

            this.price = (TextView) itemView.findViewById(R.id.tPrice);


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
