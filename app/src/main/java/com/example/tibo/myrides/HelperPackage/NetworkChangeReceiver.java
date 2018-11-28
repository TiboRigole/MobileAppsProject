package com.example.tibo.myrides.HelperPackage;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tibo.myrides.Entities.CurrentUser;
import com.example.tibo.myrides.R;
import com.example.tibo.myrides.UserActivities.HomeActivity;

import org.json.JSONException;
import org.json.JSONObject;

public class NetworkChangeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context,final Intent intent){
        final ConnectivityManager connMgr = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        final android.net.NetworkInfo wifi = connMgr
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        final android.net.NetworkInfo mobile = connMgr
                .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);


            // Do something
            checkConnection(context);

    }

    protected boolean isOnline(final Context context) {

        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo netInfo = cm.getActiveNetworkInfo();

        if (netInfo != null && netInfo.isConnectedOrConnecting()) {

            return true;

        } else {

            return false;

        }

    }

    public void checkConnection(final Context context){

        if(isOnline(context)){
            Toast.makeText(context, "You are connected to Internet", Toast.LENGTH_SHORT).show();
            if(!CurrentUser.getInstance().getEmail().equals("") && !CurrentUser.getInstance().getDisplayName().equals("")){
                CurrentUser.getInstance().setLoggedIn(true);
            }
        }
        else {

            SharedPreferences myPref= PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
            String userString= myPref.getString("user", "");
            Dialog myDialog= new Dialog(context);
            myDialog.setContentView(R.layout.offlinepopupwindow);
            TextView txtclose;
            Button button;
            txtclose= myDialog.findViewById(R.id.txtclose);
            button= myDialog.findViewById(R.id.goOffline);
            TextView uitlegText= myDialog.findViewById(R.id.uitlegOffline);
            if(userString.equals("")) {
                uitlegText.setText("Offline mode niet beschikbaar, log eerst in");
            }
            else{
                try {
                    JSONObject laatsteUser= new JSONObject(userString);
                    uitlegText.setText("Laatst ingelogde user\n" + laatsteUser.getString("displayName"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
            txtclose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    myDialog.dismiss();
                }
            });


            button.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    if(!userString.equals("")){

                        try {
                            JSONObject userJson= new JSONObject(userString);
                            CurrentUser.getInstance().setDisplayName(userJson.getString("displayName"));
                            CurrentUser.getInstance().setEmail(userJson.getString("email"));
                            CurrentUser.getInstance().logout();

                            context.startActivity(new Intent(context, HomeActivity.class));

                            Toast toast= Toast.makeText(context, "Offline Mode\n"+CurrentUser.getInstance().getDisplayName(), Toast.LENGTH_SHORT);
                            TextView toastView =(TextView) toast.getView().findViewById(android.R.id.message);
                            if(toastView!=null) toastView.setGravity(Gravity.CENTER);
                            toast.show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }



                    }



                    myDialog.dismiss();
                }

            });
            myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            myDialog.show();

        }

    }
}
