package com.ocm.Activity;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.ocm.R;
import com.ocm.model.UserRegisterDetails;

import org.json.JSONException;
import org.json.JSONObject;

public class RegisterActivity extends BaseActivity {
    EditText name, phone, username, password,ongcID;
    Button registerButton;
    String user, pass, nameVal, phoneVal;
    int ongcIDVal;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        setActivity(this);
        setView();
    }

    private void setView() {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        name = (EditText) findViewById(R.id.name);
        phone = (EditText) findViewById(R.id.phone);
        username = (EditText) findViewById(R.id.username);
        ongcID= (EditText) findViewById(R.id.ongcID);
        password = (EditText) findViewById(R.id.password);
        registerButton = (Button) findViewById(R.id.registerButton);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user = username.getText().toString().trim();
                pass = password.getText().toString().trim();
                nameVal = name.getText().toString().trim();
                phoneVal = phone.getText().toString().trim();
                ongcIDVal=Integer.parseInt(ongcID.getText().toString().trim());
                if (user.equals("")) {
                    username.setError("can't be blank");
                } else if (pass.equals("")) {
                    password.setError("can't be blank");
                } else if (!Patterns.EMAIL_ADDRESS.matcher(user).matches()) {
                    username.setError("please enter valid email");
                } else if (user.length() < 5) {
                    username.setError("at least 5 characters long");
                } else if (pass.length() < 5) {
                    password.setError("at least 5 characters long");
                } else {
                    final ProgressDialog pd = new ProgressDialog(RegisterActivity.this);
                    pd.setMessage("Loading...");
                    pd.show();
                    mAuth.createUserWithEmailAndPassword(user, pass)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    Log.d("MainActivity", "createUser:onComplete:" + task.isSuccessful());
                                    pd.dismiss();
                                    if (task.isSuccessful()) {
                                        UserRegisterDetails userRegDetails = new UserRegisterDetails(nameVal, phoneVal, user, pass,ongcIDVal, task.getResult().getUser().getUid());
                                        mDatabase.child("users").child(task.getResult().getUser().getUid()).setValue(userRegDetails, new DatabaseReference.CompletionListener() {
                                            @Override
                                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                                if (databaseError == null) {
//
                                                    finish();
                                                } else {
                                                    Toast.makeText(RegisterActivity.this, "Sign Up Failed " + databaseError,
                                                            Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                        Toast.makeText(RegisterActivity.this, "Successful! Please Sign In",
                                                Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(RegisterActivity.this, "Sign Up Failed " + task.getException(),
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });


                }
            }
        });
    }
}
