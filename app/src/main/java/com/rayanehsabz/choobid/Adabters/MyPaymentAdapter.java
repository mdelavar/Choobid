package com.rayanehsabz.choobid.Adabters;

import android.app.Activity;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rayanehsabz.choobid.Tools.AppVariables;
import com.rayanehsabz.choobid.Classes.Payment;
import com.rayanehsabz.choobid.R;

import java.util.ArrayList;

public class MyPaymentAdapter extends RecyclerView.Adapter<MyPaymentAdapter.MyViewHolder> {


    Activity context;
    ArrayList<Payment> payments;

    public Typeface mj_two,mj_nawal,t3;

    Animation fadeIn;


    public MyPaymentAdapter(ArrayList arrayList, Activity context) {
        this.payments = arrayList;
        this.context = context;
        mj_two = Typeface.createFromAsset(context.getAssets(),
                "fonts/Mj_Two Medium.ttf");

        mj_nawal = Typeface.createFromAsset(context.getAssets(),
                "fonts/Mj_Nawal.ttf");
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_my_payment, parent, false);

        return new MyViewHolder(view);

    }



    @Override
    public void onBindViewHolder(final MyPaymentAdapter.MyViewHolder holder, final int position) {
        String productDIC = AppVariables.getProductsFolder();

        if (position == 0) {

            holder.status.setTypeface(mj_two,1);
            holder.price.setTypeface(mj_two,1);
            holder.date.setTypeface(mj_two,1);
            holder.refrence.setTypeface(mj_two,1);

            holder.border.setVisibility(View.VISIBLE);

        } else if (position%2 == 0) {

            holder.parent.setBackgroundColor(context.getResources().getColor(R.color.white));
        }

        String toman = context.getResources().getString(R.string.toman);

        holder.price.setText(payments.get(position).totPrice);



        holder.status.setText(payments.get(position).status);
        holder.date.setText(payments.get(position).date);
        holder.refrence.setText(payments.get(position).referenceId);

    }

    @Override
    public int getItemCount() {
        return payments.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {



        TextView price ;
        TextView refrence;
        TextView status;
        TextView date;

        View border;
        LinearLayout parent;


        public MyViewHolder(View itemView) {
            super(itemView);


            this.date = (TextView) itemView.findViewById(R.id.payment_date);
            this.status = (TextView) itemView.findViewById(R.id.peyment_status);
            this.refrence = (TextView) itemView.findViewById(R.id.peyment_refrence);
            this.price = (TextView) itemView.findViewById(R.id.peyment_price);

            this.border = (View) itemView.findViewById(R.id.borderV);
            this.parent = (LinearLayout) itemView.findViewById(R.id.earnedP);


        }
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

}
