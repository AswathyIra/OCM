package com.ocm.Activity;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ocm.R;
import com.ocm.adapter.UserDetailsAdapter;
import com.ocm.model.UserRegisterDetails;

import java.util.ArrayList;

public class ViewAndApproveUserActivity extends BaseActivity {
    private ListView userDetailsList;
    private UserDetailsAdapter userDetailsAdapter;
    private ArrayList<UserRegisterDetails> userDetailsArray = new ArrayList<>();
    private ProgressDialog pd;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_and_approve_user);
        setView();
        setPageTitle(getResources().getString(R.string.approve_user));
        invokeUserDetails();
    }

    private void setView() {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        //List view intialization
        userDetailsList = (ListView) findViewById(R.id.userDetailsList);
        userDetailsAdapter = new UserDetailsAdapter(this, userDetailsArray);
        userDetailsList.setAdapter(userDetailsAdapter);
    }

    private void invokeUserDetails() {
        pd = new ProgressDialog(ViewAndApproveUserActivity.this);
        pd.setMessage("Loading...");
        pd.show();

        mDatabase.child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userDetailsArray.clear();
                if (dataSnapshot.exists()) {
                    for (DataSnapshot postSnapShot : dataSnapshot.getChildren()) {
                        UserRegisterDetails userRegisterDetails = postSnapShot.getValue(UserRegisterDetails.class);
                        if (!userRegisterDetails.admin) {
                            userDetailsArray.add(userRegisterDetails);
                        }
                    }
                }
                userDetailsAdapter.notifyDataSetChanged();
                pd.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                pd.dismiss();
            }
        });
    }

}
