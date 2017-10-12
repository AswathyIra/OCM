package com.ocm.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.ocm.R;
import com.ocm.model.UserRegisterDetails;

import java.util.ArrayList;

/**
 * Created by Aswathy_G on 10/12/2017.
 */

public class UserDetailsAdapter extends BaseAdapter {
    private Context mContext;
    ArrayList<UserRegisterDetails> userDetailsArray;
    private DatabaseReference mDatabase;

    public UserDetailsAdapter(Activity context, ArrayList<UserRegisterDetails> userDetailsArray) {
        mContext = context;
        this.userDetailsArray = userDetailsArray;
    }

    @Override
    public int getCount() {
        return userDetailsArray.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        UserDetailsAdapter.ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_user_details, parent, false);
            holder = new UserDetailsAdapter.ViewHolder();
            holder.userName = (TextView) convertView
                    .findViewById(R.id.userName);
            holder.email = (TextView) convertView
                    .findViewById(R.id.email);
            holder.ongcID = (TextView) convertView
                    .findViewById(R.id.ongcID);
            holder.approveUser = (TextView) convertView
                    .findViewById(R.id.approveUser);
            convertView.setTag(holder);

        } else {
            holder = (UserDetailsAdapter.ViewHolder) convertView.getTag();
        }
        holder.userName.setText(userDetailsArray.get(position).name);
        holder.email.setText(userDetailsArray.get(position).username);
        holder.ongcID.setText(""+userDetailsArray.get(position).ongc_id);

        if (userDetailsArray.get(position).approved) {
            holder.approveUser.setText(mContext.getResources().getString(R.string.unmark_approve));
        } else {
            holder.approveUser.setText(mContext.getResources().getString(R.string.mark_approve));
        }
        holder.approveUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDatabase = FirebaseDatabase.getInstance().getReference();
                if (userDetailsArray.get(position).approved) {
                    mDatabase.child("users").child(userDetailsArray.get(position).user_id).child("approved").setValue(false);
                } else {
                    mDatabase.child("users").child(userDetailsArray.get(position).user_id).child("approved").setValue(true);
                }
            }
        });
        return convertView;
    }

    private class ViewHolder {
        TextView userName;
        TextView email;
        TextView ongcID;
        TextView approveUser;
    }
}
