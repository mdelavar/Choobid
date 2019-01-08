package com.rayanehsabz.choobid.Adabters;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;

import com.rayanehsabz.choobid.Tools.AppVariables;
import com.rayanehsabz.choobid.Peyment.BuyProductActivity;
import com.rayanehsabz.choobid.Classes.store;
import com.rayanehsabz.choobid.R;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;

public class StoreAdapter extends RecyclerView.Adapter<StoreAdapter.MyViewHolder> {


    Activity context;
    ArrayList<store> products;

    boolean bid;
    public Typeface mj_two, mj_nawal, t3;

    Animation fadeIn;


    public StoreAdapter(ArrayList arrayList, Boolean bid, Activity context) {
        this.products = arrayList;
        this.context = context;
        this.bid = bid;

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_store, parent, false);

        return new MyViewHolder(view);

    }


    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        String productDIC = AppVariables.getProductsFolder();


        holder.name.setText(products.get(position).name);
        holder.ename.setText(products.get(position).englishName);

        Picasso.with(context).load(R.drawable.default_pic).into(holder.pic);
        final File file = new File(Environment.getExternalStorageDirectory() + productDIC + "/" + products.get(position).feId + ".jpg");
        if (file.exists()) {

            Picasso.with(context).load(file).placeholder(R.drawable.default_pic).into(holder.pic);

        } else if (products.get(position).feId > 0) {
            new DownloadProductImage(products.get(position).feId, holder.pic).execute(AppVariables.getServerAddress() + products.get(position).pic + "?t=342342323&imageThumbnail=2");

        }


        if (products.get(position).inventory > 0 && products.get(position).price > 0 && products.get(position).offPrice > -1) {
            String p = AppVariables.addCommasToNumericString(String.valueOf(products.get(position).price / 10));
            String op = AppVariables.addCommasToNumericString(String.valueOf(products.get(position).offPrice / 10));
            String sp = context.getResources().getString(R.string.toman);

            if (products.get(position).price == products.get(position).offPrice) {
                holder.price.setTextColor(Color.parseColor("#f15a22"));
                holder.price.setPaintFlags(holder.price.getPaintFlags() | Paint.LINEAR_TEXT_FLAG);
                holder.price.setText(String.format(sp, p));
                holder.offPrice.setText("");
            } else {
                holder.price.setTextColor(Color.parseColor("#b40000"));
                holder.price.setPaintFlags(holder.price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                holder.offPrice.setTextColor(Color.parseColor("#f15a22"));
                holder.price.setText(String.format(sp, p));
                holder.offPrice.setText(String.format(sp, op));
            }

            holder.enter.setImageResource(R.drawable.add_to_b);
            holder.enter.setClickable(true);
            holder.enter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent webS = new Intent(context, BuyProductActivity.class);

                    webS.putExtra("pId", products.get(position).id);

                    if (bid) {
                        webS.putExtra("bid", true);
                    } else {
                        webS.putExtra("bid", false);
                    }

                    context.startActivity(webS);

                }
            });
        } else {

            holder.price.setText(context.getResources().getString(R.string.unavailable));
            holder.enter.setImageResource(R.drawable.add_to_b_off);
            holder.enter.setClickable(false);

        }


    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {


        CardView cardView;
        ImageView pic;

        TextView name;
        TextView ename;


        TextView price;
        TextView offPrice;
        ImageView enter;

        public MyViewHolder(View itemView) {
            super(itemView);


            this.pic = (ImageView) itemView.findViewById(R.id.productImage);

            this.name = (TextView) itemView.findViewById(R.id.productName);
            this.ename = (TextView) itemView.findViewById(R.id.productEName);


            this.price = (TextView) itemView.findViewById(R.id.price);
            this.offPrice = (TextView) itemView.findViewById(R.id.offprice);

            this.enter = (ImageView) itemView.findViewById(R.id.addToB);

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
                Picasso.with(context).load(file).placeholder(R.drawable.default_pic).into(imageicon);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
