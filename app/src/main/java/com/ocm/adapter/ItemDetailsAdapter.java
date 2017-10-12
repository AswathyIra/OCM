package com.ocm.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.ocm.R;

import java.util.ArrayList;

/**
 * Created by Aswathy_G on 9/22/2017.
 */

public class ItemDetailsAdapter extends ArrayAdapter<String> {
    private Context mContext;
    private ArrayList<String> detailsList;
    private SparseBooleanArray mSelectedItemsIds;
    private String page;
    public ItemDetailsAdapter(Activity context, int resId, ArrayList<String> detailsList,String page) {
        super(context, resId, detailsList);
        mContext = context;
        this.page=page;
        this.detailsList = detailsList;
        mSelectedItemsIds = new SparseBooleanArray();
    }



    @Override
    public int getCount() {
        return detailsList.size();
    }



    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_details, parent, false);
            holder = new ViewHolder();
            holder.itemName = (TextView) convertView
                    .findViewById(R.id.itemName);
            convertView.setTag(holder);

        }
        else
        {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.itemName.setText(detailsList.get(position));
        convertView
                .setBackgroundColor(mSelectedItemsIds.get(position) ? 0x9934B5E4
                        : Color.TRANSPARENT);
        return convertView;
    }

    @Override
    public void add(String object) {
        detailsList.add(object);
        notifyDataSetChanged();
        Toast.makeText(mContext, object, Toast.LENGTH_LONG).show();
    }

    @Override
    public void remove(String object) {
        // super.remove(object);

        detailsList.remove(object);
        notifyDataSetChanged();
    }
    //Toggle selection methods
    public void toggleSelection(int position) {
        selectView(position, !mSelectedItemsIds.get(position));
    }


    //Remove selected selections
    public void removeSelection() {
        mSelectedItemsIds = new SparseBooleanArray();
        notifyDataSetChanged();
    }


    //Put or delete selected position into SparseBooleanArray
    public void selectView(int position, boolean value) {
        if (value)
            mSelectedItemsIds.put(position, value);
        else
            mSelectedItemsIds.delete(position);

        notifyDataSetChanged();
    }

    //Get total selected count
    public int getSelectedCount() {
        return mSelectedItemsIds.size();
    }
    private class ViewHolder {
        TextView itemName;
    }
    //Return all selected ids
    public SparseBooleanArray getSelectedIds() {
        return mSelectedItemsIds;
    }



}
