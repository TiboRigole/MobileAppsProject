package com.example.tibo.myrides.UserActivities;

import android.content.res.Resources;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.tibo.myrides.Entities.CurrentUser;
import com.example.tibo.myrides.Entities.Rit;
import com.example.tibo.myrides.HelperPackage.CustomNavigationView;
import com.example.tibo.myrides.R;
import com.example.tibo.myrides.RoomPackage.AnApplication;
import com.example.tibo.myrides.RoomPackage.AppDatabase;
import com.example.tibo.myrides.RoomPackage.RitLocal;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

//@TODO ritten van andere met jou auto weergeven
public class OtherDrivesActivity extends AppCompatActivity {

    CurrentUser currentUser;
    FirebaseFirestore db;

    // ROOM
    AnApplication application;
    static AppDatabase mDatabase;

    // layout
    private DrawerLayout mDrawerLayout;
    private TableLayout tablePrices;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_drives);

        // TODO als user offline is, vraag ritten op uit ROOM database
        // ROOM
        application = (AnApplication) getApplication();
        mDatabase = application.getDatabase();

        // DEF FIREBASE
        currentUser=CurrentUser.getInstance();
        db= FirebaseFirestore.getInstance();

        // DEF LAYOUT
        tablePrices= findViewById(R.id.tablePrices);

        mDrawerLayout=findViewById(R.id.drawer_layout_otherdrives);
        // toolbar toevoegen aan de layout (nodig om de menuknop te hebben, die het zijkantmenu opkomt)
        Toolbar toolbar = findViewById(R.id.toolbar_OtherDrivesActivity);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Other Drives");

        //menuknop toevoegen aan de toolbar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);

        //item tappen : zet het item op selected,  sluit de zijbar
        CustomNavigationView navigationView = findViewById(R.id.navigationzijkant_view);
        navigationView.setCheckedItem(R.id.nav_other_drives);
        navigationView.initSelect(this, mDrawerLayout);



        if(currentUser.isLoggedIn()) {
            makeTableOnline();
        }
        else{
            new getRittenLocal().execute();
        }



    }

    private void makeTableOffline(List<RitLocal> localRitten){
        System.out.println("Make Table");
        ArrayList<Rit> ritten= new ArrayList<>();

        HashMap<String, Double> userPrijsMap= new HashMap<>();


        for (RitLocal ritLocal : localRitten) {
            ritten.add(new Rit(ritLocal));
        }


        // zet ritten in sets, bijbehorend bij uitvoerder, bereken totale prijs
        for (Rit rit : ritten) {
            userPrijsMap.putIfAbsent(rit.getUitvoerder(), 0.0);
            userPrijsMap.put(rit.getUitvoerder(), userPrijsMap.get(rit.getUitvoerder())+rit.getTotalePrijs());
        }

        int i=0;
        // table updaten
        for (Map.Entry<String, Double> stringDoubleEntry : userPrijsMap.entrySet()) {
            System.out.println(stringDoubleEntry.getKey()+", "+stringDoubleEntry.getValue());
            /*TableRow tri= new TableRow(getApplicationContext());
            tri.setGravity(View.TEXT_ALIGNMENT_CENTER);
            tri.setBackgroundColor(getColor(R.color.colorPrimary));*/

            // waar inhoud in moet
            LinearLayout trContenti= new LinearLayout(getApplicationContext());
            if(i%2==0) {
                trContenti.setBackgroundColor(getColor(R.color.colorAccent));
            }
            else{
                trContenti.setBackgroundColor(getColor(R.color.colorTransparant));
            }
            trContenti.setGravity(View.TEXT_ALIGNMENT_CENTER);
            trContenti.setWeightSum(5f);
            trContenti.setPadding(10,10,10,10);

            // user
            TextView tvi1= new TextView(getApplicationContext());
            tvi1.setTextColor(getColor(R.color.colorText));
            tvi1.setText(stringDoubleEntry.getKey());
            tvi1.setPadding(10,10,10,10);
            tvi1.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 3f));

            // te betalen
            TextView tvi2= new TextView(getApplicationContext());
            DecimalFormat formatter= new DecimalFormat("#.##");
            tvi2.setText("€ "+formatter.format(stringDoubleEntry.getValue()));
            tvi2.setTextColor(getColor(R.color.colorText));
            tvi2.setPadding(10,10,10,10);
            tvi2.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
            trContenti.addView(tvi1);
            trContenti.addView(tvi2);

            // check if received button
            Button received= new Button(getApplicationContext());
            received.setPadding(10,10,10,10);
            float dip = 5f;
            Resources r = getResources();
            float px = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    dip,
                    r.getDisplayMetrics()
            );
            received.setMinimumHeight(0);
            received.setMinimumWidth(0);
            received.setBackgroundResource(R.drawable.ic_vinkje);
            received.setWidth((int)px);
            received.setHeight((int) px);

            received.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO verwijder alle ritten
                }
            });


            trContenti.addView(received);

            //tri.addView(trContenti);

            tablePrices.addView(trContenti);
            i++;
        }


    }

    private void makeTableOnline(){
        System.out.println("Make Table");
        ArrayList<Rit> ritten= new ArrayList<>();

        HashMap<String, Double> userPrijsMap= new HashMap<>();

        Task<QuerySnapshot> queryRitten= db.collection("ritten").whereEqualTo("eigenaarAuto", CurrentUser.getInstance().getEmail()).get();
        queryRitten.addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                for (QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots) {
                    if(!queryDocumentSnapshot.get("uitvoerder").equals(CurrentUser.getInstance().getEmail())) {
                        ritten.add(new Rit(queryDocumentSnapshot.getData()));
                    }
                }

                // zet ritten in sets, bijbehorend bij uitvoerder, bereken totale prijs
                for (Rit rit : ritten) {
                    userPrijsMap.putIfAbsent(rit.getUitvoerder(), 0.0);
                    userPrijsMap.put(rit.getUitvoerder(), userPrijsMap.get(rit.getUitvoerder())+rit.getTotalePrijs());
                }

                int i=0;
                // table updaten
                for (Map.Entry<String, Double> stringDoubleEntry : userPrijsMap.entrySet()) {
                    System.out.println(stringDoubleEntry.getKey()+", "+stringDoubleEntry.getValue());
                    /*TableRow tri= new TableRow(getApplicationContext());
                    tri.setGravity(View.TEXT_ALIGNMENT_CENTER);
                    tri.setBackgroundColor(getColor(R.color.colorPrimary));*/

                    // waar inhoud in moet
                    LinearLayout trContenti= new LinearLayout(getApplicationContext());
                    if(i%2==0) {
                        trContenti.setBackgroundColor(getColor(R.color.colorAccent));
                    }
                    else{
                        trContenti.setBackgroundColor(getColor(R.color.colorTransparant));
                    }
                    trContenti.setGravity(View.TEXT_ALIGNMENT_CENTER);
                    trContenti.setWeightSum(5f);
                    trContenti.setPadding(10,10,10,10);

                    // user
                    TextView tvi1= new TextView(getApplicationContext());
                    tvi1.setTextColor(getColor(R.color.colorText));
                    tvi1.setText(stringDoubleEntry.getKey());
                    tvi1.setPadding(10,10,10,10);
                    tvi1.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 3f));

                    // te betalen
                    TextView tvi2= new TextView(getApplicationContext());
                    DecimalFormat formatter= new DecimalFormat("#.##");
                    tvi2.setText("€ "+formatter.format(stringDoubleEntry.getValue()));
                    tvi2.setTextColor(getColor(R.color.colorText));
                    tvi2.setPadding(10,10,10,10);
                    tvi2.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
                    trContenti.addView(tvi1);
                    trContenti.addView(tvi2);

                    // check if received button
                    Button received= new Button(getApplicationContext());
                    received.setPadding(10,10,10,10);
                    float dip = 5f;
                    Resources r = getResources();
                    float px = TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_DIP,
                            dip,
                            r.getDisplayMetrics()
                    );
                    received.setMinimumHeight(0);
                    received.setMinimumWidth(0);
                    received.setBackgroundResource(R.drawable.ic_vinkje);
                    received.setWidth((int)px);
                    received.setHeight((int) px);

                    received.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // TODO verwijder alle ritten
                        }
                    });


                    trContenti.addView(received);

                    //tri.addView(trContenti);

                    tablePrices.addView(trContenti);
                    i++;
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });








    }


    private class getRittenLocal extends AsyncTask<Void, Void, List<RitLocal>>
    {

        @Override
        protected List<RitLocal> doInBackground(Void... voids) {
            List<RitLocal> localRitten= mDatabase.ritDao().getOtherRittenFromExclCarOwner(CurrentUser.getInstance().getEmail());
            return localRitten;
        }


        @Override
        protected void onPostExecute(List<RitLocal> ritLocals) {
            super.onPostExecute(ritLocals);

            makeTableOffline(ritLocals);
        }
    }


    //logica wanneer op menu knop geduwd wordt dat het sidebarmenu geopend wordt
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if(false){
            super.onBackPressed();
        }
    }
}
