package com.rayanehsabz.choobid.Fragments;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.rayanehsabz.choobid.Activities.EditProfileActivity;
import com.rayanehsabz.choobid.Activities.LoginActivity;
import com.rayanehsabz.choobid.Activities.MyAccountActivity;
import com.rayanehsabz.choobid.Activities.TicketsActivity;
import com.rayanehsabz.choobid.Adabters.Mydatabase;
import com.rayanehsabz.choobid.R;
import com.rayanehsabz.choobid.Tools.AppVariables;
import com.rayanehsabz.choobid.Tools.CalendarTool;

import org.json.JSONObject;

import java.io.File;

public class NavigationDrawer extends Fragment {


    Mydatabase db;

    View rootView;


    Button avatar;
    ImageView cleanCache;
    ImageView logout;
    ImageView terms;
    ImageView tricks;
    ImageView tickets;
    ImageView myBids;
    ImageView myAccount;
    String email = "";
    String pass = "";
    TextView name;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_drawer, container, false);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        db = new Mydatabase(getActivity());

        email = db.getSettingString(8);
        pass = db.getSettingString(9);

        avatar = (Button) rootView.findViewById(R.id.edit);
        cleanCache = (ImageView) rootView.findViewById(R.id.cleanCache);
        logout = (ImageView) rootView.findViewById(R.id.logOut);
        tickets = (ImageView) rootView.findViewById(R.id.myMessage);
        myAccount = (ImageView) rootView.findViewById(R.id.myAccount);
        name = (TextView) rootView.findViewById(R.id.screenName);

        name.setText(db.getSettingString(11));


        // clean cache
        cleanCache.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final File product = new File(Environment.getExternalStorageDirectory() + AppVariables.getProductsFolder());
                final File avatar = new File(Environment.getExternalStorageDirectory() + AppVariables.getAvatarFolder());

                final Dialog clean = new Dialog(getActivity());
                clean.requestWindowFeature(Window.FEATURE_NO_TITLE);
                clean.setContentView(R.layout.clean_cashe);

                ((TextView) clean.findViewById(R.id.productSize)).setText(humanReadableByteCount(dirSize(product), true));
                ((TextView) clean.findViewById(R.id.avatarSize)).setText(humanReadableByteCount(dirSize(avatar), true));


                ((Button) clean.findViewById(R.id.cleanB)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dirDeleteFile(product);
                        dirDeleteFile(avatar);
                        Toast.makeText(getActivity(), "حافظه پاک شد", Toast.LENGTH_LONG).show();
                        clean.dismiss();
                    }
                });

                ((Button) clean.findViewById(R.id.cancelB)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        clean.dismiss();
                    }
                });

                clean.show();
            }
        });



        // logout
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:

                                db.deleteAcc();

                                Intent login = new Intent(getActivity(), LoginActivity.class);
                                startActivity(login);

                                getActivity().finish();
                                break;


                            case DialogInterface.BUTTON_NEGATIVE:
                                //No button clicked
                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage(getActivity().getString(R.string.logoutQuestion)).setPositiveButton(getActivity().getString(R.string.logOut), dialogClickListener)
                        .setNegativeButton(getActivity().getString(R.string.cancel), dialogClickListener).show();


            }
        });


        tickets.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent tickets = new Intent(getActivity(), TicketsActivity.class);
                startActivity(tickets);
            }
        });

        myAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Intent login = new Intent(getActivity(), MyAccountActivity.class);
                startActivity(login);


            }
        });

        avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent edp = new Intent(getActivity(), EditProfileActivity.class);
                startActivity(edp);

            }
        });

    }



    private long dirDeleteFile(File dir) {

        if (dir.exists()) {
            long result = 0;
            File[] fileList = dir.listFiles();
            for (int i = 0; i < fileList.length; i++) {
                // Recursive call if it's a directory
                if (fileList[i].isDirectory()) {
                    // result += dirSize(fileList [i]);
                } else {
                    // Sum the file size in bytes

                    fileList[i].delete();
                    result += fileList[i].length();

                }
            }
            return result; // return the file size
        }
        return 0;
    }


    private long dirSize(File dir) {

        if (dir.exists()) {
            long result = 0;
            File[] fileList = dir.listFiles();
            for (int i = 0; i < fileList.length; i++) {
                // Recursive call if it's a directory
                if (fileList[i].isDirectory()) {
                    // result += dirSize(fileList [i]);
                } else {
                    // Sum the file size in bytes

                    result += fileList[i].length();

                }
            }
            return result; // return the file size
        }
        return 0;
    }


    public static String humanReadableByteCount(long bytes, boolean si) {
        int unit = si ? 1000 : 1024;
        if (bytes < unit) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp - 1) + (si ? "" : "i");
        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }


}
