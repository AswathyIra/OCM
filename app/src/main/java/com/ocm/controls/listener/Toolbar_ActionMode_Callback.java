package com.ocm.controls.listener;


import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.view.ActionMode;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.ocm.Activity.AddAndViewActivity;
import com.ocm.R;
import com.ocm.adapter.ItemDetailsAdapter;
import com.ocm.controls.AddItemDialogBuilder;

import java.util.ArrayList;

/**
 * Created by SONU on 22/03/16.
 */
public class Toolbar_ActionMode_Callback implements ActionMode.Callback {

    private Context context;
    private ItemDetailsAdapter itemDetailsAdapter;
    ArrayList<String> detailsListArray;
    private boolean isListViewFragment;
    private DeleteItemEventListener deleteItemEventListener;
    public Toolbar_ActionMode_Callback(Context context, DeleteItemEventListener deleteItemEventListener,ItemDetailsAdapter itemDetailsAdapter, ArrayList<String> detailsListArray) {
        this.context = context;
        this.deleteItemEventListener=deleteItemEventListener;
        this.itemDetailsAdapter = itemDetailsAdapter;
        this.detailsListArray = detailsListArray;
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        mode.getMenuInflater().inflate(R.menu.toolbar_cab, menu);//Inflate the menu over action mode
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {

        //Sometimes the meu will not be visible so for that we need to set their visibility manually in this method
        //So here show action menu according to SDK Levels
        if (Build.VERSION.SDK_INT < 11) {
            MenuItemCompat.setShowAsAction(menu.findItem(R.id.action_delete), MenuItemCompat.SHOW_AS_ACTION_NEVER);
//            MenuItemCompat.setShowAsAction(menu.findItem(R.id.action_edit), MenuItemCompat.SHOW_AS_ACTION_NEVER);
        } else {
            menu.findItem(R.id.action_delete).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
//            menu.findItem(R.id.action_edit).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        }

        return true;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete:
//                activity.deleteRows();//delete selected rows
                deleteItemEventListener.onItemDelete();
                break;

//            case R.id.action_edit:
//                Toast.makeText(context, "You selected Forward menu.", Toast.LENGTH_SHORT).show();//Show toast
//                mode.finish();//Finish action mode
//                break;


        }
        return false;
    }


    @Override
    public void onDestroyActionMode(ActionMode mode) {

        //When action mode destroyed remove selected selections and set action mode to null
        //First check current fragment action mode
        deleteItemEventListener.onActionModeNull();

    }

    public interface DeleteItemEventListener {
        void onItemDelete();
        void onActionModeNull();
    }
}