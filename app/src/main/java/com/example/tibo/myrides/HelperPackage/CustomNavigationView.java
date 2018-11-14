package com.example.tibo.myrides.HelperPackage;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.util.AttributeSet;
import android.view.MenuItem;

import com.example.tibo.myrides.Entities.CurrentUser;
import com.example.tibo.myrides.General.MainActivity;
import com.example.tibo.myrides.R;
import com.example.tibo.myrides.UserActivities.AddCarActivity;
import com.example.tibo.myrides.UserActivities.AddDriveActivity;
import com.example.tibo.myrides.UserActivities.MyDrivesActivity;
import com.example.tibo.myrides.UserActivities.OtherDrivesActivity;
import com.facebook.login.LoginManager;

public class CustomNavigationView extends NavigationView {
    public CustomNavigationView(Context context) {
        super(context);
    }

    public CustomNavigationView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomNavigationView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {
        super.onMeasure(widthSpec, heightSpec);
    }

    public void initSelect(Context context, DrawerLayout mDrawerLayout) {
        this.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        CurrentUser currentUser= CurrentUser.getInstance();

                        // set item as selected to persist highlight
                        menuItem.setChecked(true);
                        // close drawer when item is tapped
                        mDrawerLayout.closeDrawers();


                        // verschillende logica's / doorverwijzingen bij knopjes
                        if (menuItem.getItemId() == R.id.nav_logout) {
                            //log de user uit
                            currentUser.logout();
                            //log uit van facebook
                            LoginManager.getInstance().logOut();
                            //ga terug naar de mainActivity
                            context.startActivity(new Intent(context, MainActivity.class));
                        }

                        if (menuItem.getItemId() == R.id.nav_add_drive) {
                            context.startActivity(new Intent(context, AddDriveActivity.class));
                        }

                        if (menuItem.getItemId() == R.id.nav_add_car) {
                            context.startActivity(new Intent(context, AddCarActivity.class));
                        }

                        if (menuItem.getItemId() == R.id.nav_other_drives) {
                            context.startActivity(new Intent(context, OtherDrivesActivity.class));
                        }

                        if (menuItem.getItemId() == R.id.nav_my_drives) {
                            context.startActivity(new Intent(context, MyDrivesActivity.class));
                        }

                        return true;
                    }
                });
    }
}
