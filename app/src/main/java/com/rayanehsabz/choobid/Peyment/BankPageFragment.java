package com.rayanehsabz.choobid.Peyment;

import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.rayanehsabz.choobid.Tools.CalendarTool;
import com.rayanehsabz.choobid.R;

public class BankPageFragment extends Fragment {

    View rootView;

    String token = "token";
    String merchant = "";

    WebView myWebView;
    boolean isRedirectedtoBank = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_bank, container, false);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        myWebView = (WebView) rootView.findViewById(R.id.bankPage);


        token = ((BuyProductActivity) getActivity()).ShopeDetail.token;
        merchant = ((BuyProductActivity) getActivity()).ShopeDetail.merchant;


        String body = "http://choobid.com/apppay?token=" + CalendarTool.getCoded(token) + "&merchantId=" + merchant;


        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setAction("android.intent.action.VIEW");
        i.addCategory("android.intent.category.BROWSABLE");

        //String dataUri = "data:text/html," + URLEncoder.encode(body).replaceAll("\\+","%20");
        i.setData(Uri.parse(body));

        startActivity(i);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable(){
            @Override
            public void run(){
                isRedirectedtoBank = true;
            }
        }, 5);



//        WebSettings webViewSettings = myWebView.getSettings();
//        webViewSettings.setJavaScriptEnabled(true);
//        webViewSettings.setDomStorageEnabled(true);
//        webViewSettings.setLoadWithOverviewMode(true);
//        webViewSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
//        webViewSettings.setUseWideViewPort(true);
//
//        myWebView.loadData(body, "text/html; charset=utf-8", "utf-8");
//
//
//        myWebView.setWebViewClient(new WebViewClient() {
//                                       @Override
//                                       public void onPageStarted(WebView view, String url, Bitmap favicon) {
//                                           super.onPageStarted(view, url, favicon);
//
//
//                                       }
//
//                                       @Override
//                                       public void onPageFinished(WebView view, String url) {
//
//                                           view.scrollTo(0,0);
//                                           if (url.contains("http://choobid.com/ikcpaycontrol")) {
//
//                                               Intent banckResult = new Intent(getActivity() , BankResultActivity.class);
//                                               banckResult.putExtra("invoiceId" , String.valueOf(((BuyProductActivity) getActivity()).ShopeDetail.invoiceId));
//                                               getActivity().finish();
//                                               startActivity(banckResult);
//
//                                           }
//                                           super.onPageFinished(view, url);
//                                       }
//
//                                       @Override
//                                       public void onLoadResource(WebView view, String url) {
//                                           // TODO Auto-generated method stub
//                                           super.onLoadResource(view, url);
//                                       }
//
//                                       @Override
//                                       public boolean shouldOverrideUrlLoading(WebView view, String url) {
//
//                                           return super.shouldOverrideUrlLoading(view, url);
//                                       }
//                                   }
//        );


    }

    @Override
    public void onResume() {
        super.onResume();
        if (isRedirectedtoBank) {
            Intent banckResult = new Intent(getActivity() , BankResultActivity.class);
                                               banckResult.putExtra("invoiceId" , String.valueOf(((BuyProductActivity) getActivity()).ShopeDetail.invoiceId));
                                               getActivity().finish();
                                               startActivity(banckResult);

        }
    }
}
