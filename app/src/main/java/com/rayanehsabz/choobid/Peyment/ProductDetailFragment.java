package com.rayanehsabz.choobid.Peyment;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.DefaultSliderView;
import com.rayanehsabz.choobid.Adabters.Mydatabase;
import com.rayanehsabz.choobid.Tools.AppVariables;
import com.rayanehsabz.choobid.Tools.CalendarTool;
import com.rayanehsabz.choobid.R;
import com.rayanehsabz.choobid.Views.showLoading;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class ProductDetailFragment extends Fragment {

    View rootView;

    String email;
    String pass;

    Mydatabase db;

    FragmentActivity context;
    showLoading loading;


    private LinearLayoutManager lLayout;


    ArrayList<String> wnames = new ArrayList<>();
    ArrayList<String> wfeIds = new ArrayList<>();
    ArrayList<String> wprices = new ArrayList<>();
    ArrayList<String> wcodes = new ArrayList<>();
    ArrayList<String> wdates = new ArrayList<>();
    ArrayList<String> wavatars = new ArrayList<>();

    long colorId = 0;
    long sizeId = 0;
    long garantyId = 0;


    public ProductDetailFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_product_detail, container, false);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        long pId = ((BuyProductActivity) getActivity()).ShopeDetail.pId;
        loading = new showLoading(getActivity(), 1);
        loading.show();

        context = getActivity();


        db = new Mydatabase(getActivity());

        email = db.getSettingString(8);
        pass = db.getSettingString(9);

        new ProductTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR ,
                AppVariables.getServerAddress() + "/choobid-portlet/api/jsonws/account/get-store-product/email/" + CalendarTool.getCoded(email) + "/pass/" + CalendarTool.getCoded(pass) + "/product-id/" + pId);


    }


    public void setLastWinner(JSONArray lastOffer) {
        try {


            for (int i = 0; i < lastOffer.length(); i++) {
                JSONObject os = new JSONObject(lastOffer.getString(i));
                wnames.add(os.getString("name"));
                wfeIds.add(os.getString("avatarFeId"));
                wavatars.add(os.getString("avatar"));
                wprices.add(os.getString("proposedPrice"));
                wdates.add(os.getString("winnerDate"));
                wcodes.add(os.getString("bidCode"));

            }




        } catch (Exception e) {

        }

    }

    private class ProductTask extends AsyncTask<String, Void, String> {
        protected String doInBackground(String... urls) {
            return AppVariables.readJSONFeed(urls[0]);
        }

        protected void onPostExecute(String result) {


            try {


                JSONObject jsonObject = new JSONObject(result);
                JSONArray jsonArray = new JSONArray(jsonObject.getString("product"));

                JSONObject jso = new JSONObject(jsonArray.getString(0));

                ((ScrollView) rootView.findViewById(R.id.myScroll)).fullScroll(ScrollView.FOCUS_UP);
                if (jso.has("winners")) {
                    wnames.clear();
                    wfeIds.clear();
                    wavatars.clear();
                    wprices.clear();
                    wcodes.clear();
                    wdates.clear();

                    JSONArray lastOffer = new JSONArray(jso.getString("winners"));

                    if (lastOffer.length() > 0) {
                        setLastWinner(lastOffer);
                    } else {

                        ((LinearLayout) rootView.findViewById(R.id.winnerP)).setVisibility(View.GONE);
                    }

                }

                ((TextView) rootView.findViewById(R.id.Pname)).setText(jso.getString("name"));
                ((TextView) rootView.findViewById(R.id.ename)).setText(jso.getString("englishName"));

                String p = AppVariables.addCommasToNumericString(String.valueOf(jso.getLong("price") / 10));

                ((BuyProductActivity) context).ShopeDetail.price = jso.getLong("price");
                String toman = getResources().getString(R.string.toman);
                ((TextView) rootView.findViewById(R.id.mainPrice)).setText(String.format(toman, p));


                HashMap<String, String> sss = new HashMap<String, String>();

                ((BuyProductActivity) context).ShopeDetail.portable = jso.getLong("portable") == 1 ;

                ((BuyProductActivity) context).ShopeDetail.PfeId = jso.getString("feId");

                ((BuyProductActivity) context).ShopeDetail.Pname = jso.getString("name");
                ((BuyProductActivity) context).ShopeDetail.PEname = jso.getString("englishName");

                JSONArray news = jso.getJSONArray("pics");

                if (news.length() > 0) {
                    for (int i = 0; i < news.length(); i++) {
                        JSONObject m1 = news.getJSONObject(i);
                        sss.put("pic-" + i, "http://choobid.com" + m1.getString("mediaPic"));

                    }
                } else  {
                    sss.put("pic-0" , "http://choobid.com" + jso.getString("pic"));
                }

                setSlider(sss);


                JSONArray ja = new JSONArray(jso.getString("technicals"));
                if (ja.length() > 0) {
                    setTechnical(ja);
                } else {
                    ((LinearLayout) rootView.findViewById(R.id.technicalP)).setVisibility(View.GONE);
                }


                if (jso.getString("Pinfo").equals("")) {
                    ((LinearLayout) rootView.findViewById(R.id.infoP)).setVisibility(View.GONE);
                } else {
                    WebView browser = ((WebView) rootView.findViewById(R.id.info));

                    String style = "<style> p , span , h1 ,h2,h3,h4,h5{ text-align:justify !important;padding : 10px 14px ; font-size:50px !important; line-height : 80px !important; direction : rtl !important;} img {padding : 12px 20px !important;}</style>";
                    browser.loadData(jso.getString("Pinfo") + style, "text/html; charset=utf-8", "utf-8");
                    browser.getSettings().setLoadWithOverviewMode(true);
                    browser.getSettings().setUseWideViewPort(true);
                }

                ((ImageView) rootView.findViewById(R.id.shopB)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {


                        long pId = ((BuyProductActivity) getActivity()).ShopeDetail.pId;


                        new AddToBacketTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR ,
                                AppVariables.getServerAddress() + "/choobid-portlet/api/jsonws/account/add-to-backet/email/" + CalendarTool.getCoded(email) + "/pass/" + CalendarTool.getCoded(pass) + "/acc-id/" + db.getSettingString(6) + "/product-id/" + pId + "/product-count/1/"
                                + "/color-id/" + colorId + "/garanty-id/" + garantyId + "/size-id/" + sizeId
                        );

                        loading.show();


                    }
                });


                loading.dismiss();
            } catch (Exception e) {

                Log.e("Tag", " ---> " + e);
            }

        }
    }

    //


    private class AddToBacketTask extends AsyncTask<String, Void, String> {
        protected String doInBackground(String... urls) {
            return AppVariables.readJSONFeed(urls[0]);
        }

        protected void onPostExecute(String result) {


            try {


                JSONObject jsonObject = new JSONObject(result);


                if (jsonObject.has("invoiceId")) {

                    loading.dismiss();

                    context.getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter_from_left, R.anim.exit_from_right, R.anim.enter_from_right, R.anim.exit_from_left).add(R.id.mainContent, new ShopBFragment()).addToBackStack(null).commit();

                }

            } catch (Exception e) {



            }

        }
    }




    public void setTechnical(JSONArray jsa) {

        for (int i = 0; i < jsa.length(); i++) {
            try {


                JSONObject jsb = new JSONObject(jsa.getString(i));
                View title = LayoutInflater.from(context).inflate(R.layout.technical_title,
                        ((LinearLayout) rootView.findViewById(R.id.technical)), false);
                ((TextView) title.findViewById(R.id.Ttitle)).setText(jsb.getString("title"));
                ((LinearLayout) rootView.findViewById(R.id.technical)).addView(title);

                JSONArray content = new JSONArray(jsb.getString("content"));
                for (int j = 0; j < content.length(); j++) {
                    JSONObject Kv = new JSONObject(content.getString(j));
                    View item = LayoutInflater.from(context).inflate(R.layout.technical_item,
                            ((LinearLayout) rootView.findViewById(R.id.technical)), false);
                    ((TextView) item.findViewById(R.id.key)).setText(Kv.getString("name"));
                    ((TextView) item.findViewById(R.id.value)).setText(Kv.getString("value"));
                    ((LinearLayout) rootView.findViewById(R.id.technical)).addView(item);

                }
            } catch (Exception e) {

                Log.e("Tag", " ---> " + e);

            }

        }
//

    }

    private void setSlider(HashMap<String, String> ss) {
        SliderLayout mDemoSlider = (SliderLayout) rootView.findViewById(R.id.slider);
        DisplayMetrics ds = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(ds);
        LinearLayout.LayoutParams rel_btn = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ds.widthPixels - 47);

        mDemoSlider.setLayoutParams(rel_btn);

        for (String name : ss.keySet()) {

            DefaultSliderView textSliderView = new DefaultSliderView(context);
            // initialize a SliderLayout
            textSliderView
                    .description(name)
                    .image(ss.get(name))
                    .setScaleType(BaseSliderView.ScaleType.Fit);

            //add your extra information
            textSliderView.bundle(new Bundle());
            textSliderView.getBundle()
                    .putString("extra", name);

            mDemoSlider.addSlider(textSliderView);
        }
        mDemoSlider.setPresetTransformer(SliderLayout.Transformer.Default);
        mDemoSlider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);

        mDemoSlider.setCustomAnimation(new DescriptionAnimation());
        mDemoSlider.setDuration(4000);
    }


}
