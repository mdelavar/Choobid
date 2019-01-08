package com.rayanehsabz.choobid.Fragments;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.rayanehsabz.choobid.Adabters.Mydatabase;
import com.rayanehsabz.choobid.Adabters.StoreAdapter;
import com.rayanehsabz.choobid.Classes.store;
import com.rayanehsabz.choobid.R;
import com.rayanehsabz.choobid.Tools.AppVariables;
import com.rayanehsabz.choobid.Tools.CalendarTool;
import com.rayanehsabz.choobid.Views.showLoading;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class StoreFragment extends Fragment {

    View rootView;

    String email;
    String pass;

    Mydatabase db;

    ArrayList<store> pr = new ArrayList<>();

    TextView notFound ;

    StoreAdapter adapter;
    RecyclerView recyclerView;
    RecyclerView catRecycler;

    private GridLayoutManager lLayout;
    private LinearLayoutManager catLayout;

    showLoading loading;

    String categoryId = "0";

    int visibleItemCount = 0;
    int totalItemCount = 0;
    int firstVisibleItem = 0;
    int previousTotal = 0;
    int visibleThreshold = 0;
    boolean loadingS = false;
    int fromN = 0;
    int toN = 12;


    CategoryAdapter catadapter;
    ArrayList<category> categories = new ArrayList<category>();
    ProgressBar progress;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_store, container, false);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        progress = (ProgressBar) rootView.findViewById(R.id.progress_view1);

        loading = new showLoading(getActivity(), 1);
        loading.show();


        db = new Mydatabase(getActivity());

        email = db.getSettingString(8);
        pass = db.getSettingString(9);

        notFound = (TextView) rootView.findViewById(R.id.notFound);


        // CatAdapter

        new CategoryTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR ,
                AppVariables.getServerAddress() + "/choobid-portlet/api/jsonws/account/get-store-categories/email/" + CalendarTool.getCoded(email) + "/pass/" + CalendarTool.getCoded(pass));


        catRecycler = (RecyclerView) rootView.findViewById(R.id.catRecycler);
        catLayout = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, true);

        catadapter = new CategoryAdapter();
        catRecycler.setLayoutManager(catLayout);
        catRecycler.setAdapter(catadapter);

        // Adapter //
        recyclerView = (RecyclerView) rootView.findViewById(R.id.storeRcv);
        lLayout = new GridLayoutManager(getActivity(), 2);
        adapter = new StoreAdapter(pr, false, getActivity());

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                visibleItemCount = recyclerView.getChildCount();
                totalItemCount = lLayout.getItemCount();
                firstVisibleItem = lLayout.findFirstVisibleItemPosition();

                if (loadingS) {
                    if (totalItemCount > previousTotal) {
                        loadingS = false;
                        previousTotal = totalItemCount;
                    }
                }
                if (!loadingS && (totalItemCount - visibleItemCount)
                        <= (firstVisibleItem + visibleThreshold)) {
                    // End has been reached

                    // Do something
                    fromN += 12;
                    toN += 12;
                    progress.setVisibility(View.VISIBLE);
                    refresh();

                    loadingS = true;
                }
            }
        });
        refresh();


        recyclerView.setLayoutManager(lLayout);
        recyclerView.setAdapter(adapter);


    }




    public void refresh() {

        // Get Product
        new ProductTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR ,
                AppVariables.getServerAddress() + "/choobid-portlet/api/jsonws/account/get-store/email/" + CalendarTool.getCoded(email) + "/pass/" + CalendarTool.getCoded(pass) + "/ci/" + categoryId + "/from-record/" + fromN + "/to-record/" + toN);

    }

    public void refreshCat() {
        pr.clear();
//        pr = new ArrayList<store>();
        notFound.setVisibility(View.GONE);
        visibleItemCount = 0;
        totalItemCount = 0;
        firstVisibleItem = 0;
        previousTotal = 0;
        visibleThreshold = 0;

        loadingS = false;

        fromN = 0;
        toN = 12;
        adapter.notifyDataSetChanged();
        loading.show();

        new ProductTask().execute(
                AppVariables.getServerAddress() + "/choobid-portlet/api/jsonws/account/get-store/email/" + CalendarTool.getCoded(email) + "/pass/" + CalendarTool.getCoded(pass) + "/ci/" + categoryId + "/from-record/" + fromN + "/to-record/" + toN);

    }

    private class ProductTask extends AsyncTask<String, Void, String> {
        protected String doInBackground(String... urls) {
            return AppVariables.readJSONFeed(urls[0]);
        }

        protected void onPostExecute(String result) {


            try {

                loading.dismiss();
                progress.setVisibility(View.GONE);
                JSONObject jsonObject = new JSONObject(result);

                if (jsonObject.length() > 0) {
                    JSONArray products = new JSONArray(jsonObject.getString("products"));

                    for (int i = 0; i < products.length(); i++) {

                        JSONObject js = products.getJSONObject(i);
                        pr.add(new store(js));
                        adapter.notifyDataSetChanged();


                    }
                } else if (fromN == 0 ) {
                    notFound.setVisibility(View.VISIBLE);

                }



            } catch (Exception e) {
                Log.e("ReadMahfelJSONFeedTask", "error:" + e);
            }

        }
    }

    private class CategoryTask extends AsyncTask<String, Void, String> {
        protected String doInBackground(String... urls) {
            return AppVariables.readJSONFeed(urls[0]);
        }

        protected void onPostExecute(String result) {


            try {


                JSONObject jsonObject = new JSONObject(result);

                JSONArray products = new JSONArray(jsonObject.getString("categories"));

                category all = new category();
                all.catname = "همه";
                all.catId = "0";
                categories.add(all);
                for (int i = 0; i < products.length(); i++) {

                    JSONObject js = products.getJSONObject(i);
                    if (!js.getString("id").equals("701")) {
                        categories.add(new category(js));
                        catadapter.notifyDataSetChanged();
                    }


                }


                loading.dismiss();

            } catch (Exception e) {
//          	Log.d("ReadMahfelJSONFeedTask", "error:" + e);
            }

        }
    }

    public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.MyViewHolder> {

        public CategoryAdapter() {

        }

        public int getItemCount() {
            return categories.size();
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category, parent, false);

            return new MyViewHolder(view);

        }


        @Override
        public void onBindViewHolder(final MyViewHolder holder, final int position) {

            holder.catName.setText(categories.get(position).catname);
            holder.catName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    categoryId = categories.get(position).catId;
                    refreshCat();

                }
            });
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {


            TextView catName;


            public MyViewHolder(View itemView) {
                super(itemView);

                this.catName = (TextView) itemView.findViewById(R.id.catName);


            }

        }
    }

    public class category {
        String catname;
        String catId;

        public category(JSONObject jso) {
            try {
                catname = jso.getString("name");
                catId = jso.getString("id");
            } catch (Exception e) {


            }
        }

        public category() {
        }
    }

}
