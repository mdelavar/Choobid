package com.rayanehsabz.choobid.Adabters;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.rayanehsabz.choobid.Activities.AddAddressActivity;
import com.rayanehsabz.choobid.Tools.AppVariables;
import com.rayanehsabz.choobid.Tools.CalendarTool;
import com.rayanehsabz.choobid.Classes.Address;

import com.rayanehsabz.choobid.Peyment.ShopAddressFragment;
import com.rayanehsabz.choobid.R;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.MyViewHolder> {

    String email;
    String pass;

    Fragment context;
    ArrayList<Address> addresses;

    Mydatabase db;

    public Typeface mj_two, mj_nawal, t3;

    Animation fadeIn;


    public AddressAdapter(ArrayList arrayList, Fragment context) {
        this.addresses = arrayList;
        this.context = context;
        mj_two = Typeface.createFromAsset(context.getActivity().getAssets(),
                "fonts/Mj_Two Medium.ttf");

        mj_nawal = Typeface.createFromAsset(context.getActivity().getAssets(),
                "fonts/Mj_Nawal.ttf");
        db = new Mydatabase(context.getActivity());
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_address, parent, false);

        return new MyViewHolder(view);

    }


    @Override
    public void onBindViewHolder(final AddressAdapter.MyViewHolder holder, final int position) {

        email = db.getSettingString(8);
        pass = db.getSettingString(9);

        holder.name.setText(addresses.get(position).recepterFullName);
        holder.address.setText(
                db.getStateName(addresses.get(position).cityId)
                        + "‌ - " +
                        db.getCityName(addresses.get(position).cityId)
                        + "‌ - خیابان " +
                        addresses.get(position).street
                        + "‌ - کوچه " +
                        addresses.get(position).alley
                        + "‌ - پلاک " +
                        addresses.get(position).number
                        + "‌ - طبقه " +
                        addresses.get(position).floor
                        + "‌ - واحد " +
                        addresses.get(position).apartmentUnit
        );
        holder.number.setText(addresses.get(position).mobile);

        holder.cardView.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ((ShopAddressFragment) context).setEnable(position);
                    }
                }

        );

        holder.edit.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Intent intent = new Intent(context.getActivity() , AddAddressActivity.class);

                        intent.putExtra("id" , addresses.get(position).id + "");
                        intent.putExtra("cityId" , addresses.get(position).cityId + "");
                        intent.putExtra("postalCode" , addresses.get(position).postalCode + "");
                        intent.putExtra("street" , addresses.get(position).street + "");
                        intent.putExtra("alley" , addresses.get(position).alley + "");
                        intent.putExtra("number" , addresses.get(position).number + "");
                        intent.putExtra("floor" , addresses.get(position).floor + "");
                        intent.putExtra("apartmentUnit" , addresses.get(position).apartmentUnit + "");
                        intent.putExtra("recepterFullName" , addresses.get(position).recepterFullName + "");
                        intent.putExtra("mobile" , addresses.get(position).mobile + "");
                        intent.putExtra("phone" , addresses.get(position).phone + "");

                        context.getActivity().startActivity(intent);

                    }
                }
        );


        holder.delete.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {


                        new DeleteAddressTask().execute(

                          AppVariables.getServerAddress() + "/choobid-portlet/api/jsonws/account/delete-address" +
                                  "/email/" +
                                  CalendarTool.getCoded(email) +
                                  "/pass/" +
                                  CalendarTool.getCoded(pass) +
                                  "/add-id/" +
                                  addresses.get(position).id
                        );



                    }
                }
        );


        holder.rb.setClickable(false);
        if (addresses.get(position).selected) {
            holder.rb.setChecked(true);
        } else {
            holder.rb.setChecked(false);
        }


    }

    private class DeleteAddressTask extends AsyncTask<String, Void, String> {
        protected String doInBackground(String... urls) {
            return AppVariables.readJSONFeed(urls[0]);
        }

        protected void onPostExecute(String result) {
            try {

                JSONObject m1 = new JSONObject(result);
                if (m1.has("result") && m1.getBoolean("result")) {

                    ((ShopAddressFragment) context).refreshAddress();

                }

            } catch (Exception e) {
                Log.e("ReadMahfelJSONFeedTask", "error : " + e);

            }
        }
    }

    @Override
    public int getItemCount() {
        return addresses.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {


        CardView cardView;

        TextView name;
        TextView number;
        TextView address;


        ImageView edit;
        ImageView delete;

        RadioButton rb;


        public MyViewHolder(View itemView) {
            super(itemView);

            this.name = (TextView) itemView.findViewById(R.id.name);
            this.number = (TextView) itemView.findViewById(R.id.number);
            this.address = (TextView) itemView.findViewById(R.id.address);

            this.edit = (ImageView) itemView.findViewById(R.id.edit);
            this.delete = (ImageView) itemView.findViewById(R.id.delete);

            this.rb = (RadioButton) itemView.findViewById(R.id.radioB);
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
