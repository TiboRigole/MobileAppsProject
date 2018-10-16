package com.example.tibo.myrides;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class DataBaseTestActvity extends AppCompatActivity {


    private EditText usernameText;
    private EditText paswoordText;


    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_base_test_actvity);

        // FIREBASE INIT
        mAuth=FirebaseAuth.getInstance();

        //teksveldjes init
        usernameText = (EditText) findViewById(R.id.emailDBTest);
        paswoordText = (EditText) findViewById(R.id.paswoordDBTest);

        Button dbTest = (Button) findViewById(R.id.buttonDbTest);



        dbTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //LOGICA DIE IETS VERSTUURT NAAR DE DATABANK MOET HIER, MVG

                Log.d("FIREBASE","knop voordat de logica ervan wordt uitgevoerd");
                Log.d("FIREBASE",usernameText.getText().toString());
                createAccount(usernameText.getText().toString(), paswoordText.getText().toString());
            }
        });

    }




    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
//        Toast.makeText(this, currentUser.toString(), Toast.LENGTH_SHORT).show();
    }

    public void createAccount(String email, String password){
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("FIREBASE", "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(DataBaseTestActvity.this, user.toString(), Toast.LENGTH_SHORT).show();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.d("FIREBASE", "createUserWithEmail:failure", task.getException());
                            Toast.makeText(DataBaseTestActvity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            }

                        // ...
                    }
                });
    }
}
