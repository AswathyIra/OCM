package com.ocm.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ocm.R;
import com.ocm.model.UserDetails;
import com.ocm.model.UserRegisterDetails;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends BaseActivity implements View.OnClickListener {
    private TextView registerTV;
    EditText username, password;
    Button loginButton;
    String user, pass;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setActivity(this);
        initView();
    }

    private void initView() {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        registerTV = (TextView) findViewById(R.id.register);
        registerTV.setOnClickListener(this);
        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
        loginButton = (Button) findViewById(R.id.loginButton);
        loginButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.register:
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                break;
            case R.id.loginButton:
                onLoginClick();
                break;
        }
    }

    private void onLoginClick() {
        user = username.getText().toString();
        pass = password.getText().toString();

        if (user.equals("")) {
            username.setError("can't be blank");
        } else if (!Patterns.EMAIL_ADDRESS.matcher(user).matches()) {
            username.setError("please enter valid email");
        } else if (pass.equals("")) {
            password.setError("can't be blank");
        } else {

            final ProgressDialog pd = new ProgressDialog(LoginActivity.this);
            pd.setMessage("Loading...");
            pd.show();

            mAuth.signInWithEmailAndPassword(user, pass)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            Log.d("MainActivity", "signIn:onComplete:" + task.isSuccessful());
                            if (task.isSuccessful()) {
                                mDatabase.child("users").child(task.getResult().getUser().getUid()).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.exists()) {
                                            UserRegisterDetails userRegisterDetails = dataSnapshot.getValue(UserRegisterDetails.class);
                                           if(userRegisterDetails.approved) {
                                               if (userRegisterDetails.admin) {
                                                   Intent intent = new Intent(LoginActivity.this, AdminHomeActivity
                                                           .class);
                                                   startActivity(intent);
                                                   finish();
                                               } else {
                                                   Intent intent = new Intent(LoginActivity.this, HomeActivity
                                                           .class);
                                                   startActivity(intent);
                                                   finish();
                                               }
                                           }
                                           else
                                           {
                                               showToast("User is not approved. Please contact the Admin");
                                           }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                    }
                                });

                            } else {
                                Toast.makeText(LoginActivity.this, "Sign In Failed",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });


        }

    }


}
