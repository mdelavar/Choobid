package com.rayanehsabz.choobid.Fragments;


import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;

import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.rayanehsabz.choobid.Adabters.Mydatabase;
import com.rayanehsabz.choobid.Tools.AppVariables;
import com.rayanehsabz.choobid.Tools.CalendarTool;
import com.rayanehsabz.choobid.Activities.LoginActivity;
import com.rayanehsabz.choobid.R;
import com.rayanehsabz.choobid.Views.showLoading;

import org.json.JSONObject;

public class SignUpFragment extends Fragment {

    View rootView;

    Mydatabase db;
    EditText txtpassword;
    EditText txtemail;
    EditText txtscreenname;
    EditText txtmobileno;
    EditText txtrepassword;

    String email;
    String pass;

    showLoading loading;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_signup, container, false);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);




        db = new Mydatabase(getActivity());
        loading = new showLoading(getActivity(), 1) ;

        SpannableString ss = new SpannableString(((TextView) rootView.findViewById(R.id.termText)).getText().toString());
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View textView) {

                String url = "http://choobid.com/web/guest/10";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);

            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(true);
            }

        };


        ss.setSpan(clickableSpan, 0, 14, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        TextView textView = (TextView) rootView.findViewById(R.id.termText);
        textView.setText(ss);
        textView.setMovementMethod(LinkMovementMethod.getInstance());
        textView.setHighlightColor(Color.TRANSPARENT);


        email = CalendarTool.getCoded(db.getSettingString(8));
        pass = CalendarTool.getCoded(db.getSettingString(9));

        txtemail = ((EditText) rootView.findViewById(R.id.txtEmail));
        txtpassword = ((EditText) rootView.findViewById(R.id.txtPassword));
        txtrepassword = ((EditText) rootView.findViewById(R.id.txtRePassword));
        txtmobileno = ((EditText) rootView.findViewById(R.id.txtMobileNo));
        txtscreenname = ((EditText) rootView.findViewById(R.id.txtScreenName));

        txtemail.setText(((LoginActivity) getActivity()).email);
        txtpassword.setText(((LoginActivity) getActivity()).pass);
        txtrepassword.setText(((LoginActivity) getActivity()).Repass);
        txtmobileno.setText(((LoginActivity) getActivity()).mobNum);
        txtscreenname.setText(((LoginActivity) getActivity()).username);

        ((Button) rootView.findViewById(R.id.lgB)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AppVariables.hideSoftKeyboard(getActivity());



                if (txtpassword.getEditableText().toString().isEmpty() || txtemail.getEditableText().toString().isEmpty() || txtscreenname.getEditableText().toString().isEmpty() || txtmobileno.getEditableText().toString().isEmpty()) {
                    Toast.makeText(getActivity(), getString(R.string.all_fields_required), Toast.LENGTH_LONG).show();

                } else if (!txtpassword.getEditableText().toString().equals(txtrepassword.getEditableText().toString())) {
                    Toast.makeText(getActivity(), getString(R.string.password_repassword_not_match), Toast.LENGTH_LONG).show();

                } else if (txtmobileno.getEditableText().toString().length() != 11) {
                    Toast.makeText(getActivity(), getString(R.string.mobile_must_have_11_number), Toast.LENGTH_LONG).show();

                } else if (!((CheckBox) rootView.findViewById(R.id.checkTerm)).isChecked()) {

                    Toast.makeText(getActivity(), getString(R.string.agreeTerms), Toast.LENGTH_LONG).show();

                } else {
                    loading.show();
                    new ReadCheckJSONFeedTask().execute(AppVariables.getServerAddress() + "/choobid-portlet/api/jsonws/account/check-account/email/" + CalendarTool.getCoded(txtemail.getText().toString()) + "/pass/" + pass + "/scrname/" + CalendarTool.getCoded(txtscreenname.getText().toString()) + "/mobno/" + CalendarTool.getCoded(txtmobileno.getText().toString()) + "/app-type/" + AppVariables.getAppTypeId());

                }


            }
        });


    }


    private class ReadCheckJSONFeedTask extends AsyncTask<String, Void, String> {
        protected String doInBackground(String... urls) {
            return AppVariables.readJSONFeed(urls[0]);
        }

        protected void onPostExecute(String result) {
            try {


                JSONObject m1 = new JSONObject(result);
                if (m1.getString("accountId") != null) {
                    if (m1.getString("accountId").equals("-1")) {

                        loading.dismiss();
                        Toast.makeText(getActivity(),
                                getActivity().getString(R.string.loginNumError),
                                Toast.LENGTH_LONG).show();

                    } else if (m1.getString("accountId").equals("-2")) {

                        loading.dismiss();
                        Toast.makeText(getActivity(),
                                getActivity().getString(R.string.loginNameError),
                                Toast.LENGTH_LONG).show();

                    } else if (m1.getString("accountId").equals("-3")) {

                        loading.dismiss();
                        Toast.makeText(getActivity(),
                                getActivity().getString(R.string.EmailError),
                                Toast.LENGTH_LONG).show();

                    }else if (m1.getString("accountId").equals("0")) {
                        loading.dismiss();
                        ((LoginActivity) getActivity()).email = txtemail.getText().toString();
                        ((LoginActivity) getActivity()).pass = txtpassword.getText().toString();
                        ((LoginActivity) getActivity()).Repass = txtrepassword.getText().toString();
                        ((LoginActivity) getActivity()).mobNum = txtmobileno.getText().toString();
                        ((LoginActivity) getActivity()).username = txtscreenname.getText().toString();

                        getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter_from_left, R.anim.exit_from_right, R.anim.enter_from_right, R.anim.exit_from_left).replace(R.id.contentPanel, new SecurityCodeFragment()).addToBackStack(null).commit();

                    }
                } else {

                    loading.dismiss();
                    Toast.makeText(getActivity(),
                            getActivity().getString(R.string.loginServerError),
                            Toast.LENGTH_LONG).show();

                }


            } catch (Exception e) {
//            	Log.e("ReadMahfelJSONFeedTask", "error : " + e);
            }
        }
    }
}
