package com.ocm.Activity;

import android.app.ProgressDialog;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.ActionMenuView;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.ocm.R;
import com.ocm.adapter.ItemDetailsAdapter;
import com.ocm.conf.IntentConstants;
import com.ocm.controls.AddItemDialogBuilder;
import com.ocm.controls.listener.Toolbar_ActionMode_Callback;

import java.util.ArrayList;

public class AddSubDataActivity extends BaseActivity implements View.OnClickListener, ActionMenuView.OnMenuItemClickListener {
    private Spinner mainMenuSpinner;
    String page = "EQUIP";
    private ArrayAdapter<String> mainMenuSpinnerAdapter;
    private ArrayList<String> mainMenuSpinnerArray = new ArrayList<>();
    private String mainMenuSelected = null;
    private ProgressDialog pd;
    private DatabaseReference mDatabase;
    private ListView detailsList;
    private ArrayList<String> detailsListArray = new ArrayList<>();
    private ItemDetailsAdapter itemDetailsAdapter;
    private FloatingActionButton fab;
    private ActionMode mActionMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_sub_data);
        setView();
        setPageTitle(getResources().getString(R.string.equip_categories));
        invokeMainMenuDetails();

    }

    @Override
    protected void onResume() {
        if (null != mainMenuSelected) {
            getAllSubItemDetails();
        }
        super.onResume();
    }

    private void setView() {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mainMenuSpinner = (Spinner) findViewById(R.id.mainMenuSpinner);
        // Creating adapter for spinner
        mainMenuSpinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, mainMenuSpinnerArray);
        // Drop down layout style - list view with radio button
        mainMenuSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // attaching data adapter to spinner
        mainMenuSpinner.setAdapter(mainMenuSpinnerAdapter);
        mainMenuSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mainMenuSelected = mainMenuSpinnerArray.get(position);
                getAllSubItemDetails();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //List view intialization
        detailsList = (ListView) findViewById(R.id.detailsList);
        itemDetailsAdapter = new ItemDetailsAdapter(this, R.layout.list_item_details, detailsListArray, page);
        detailsList.setAdapter(itemDetailsAdapter);

        implementListViewClickListeners();

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(this);
    }

    private void invokeMainMenuDetails() {
        pd = new ProgressDialog(AddSubDataActivity.this);
        pd.setMessage("Loading...");
        pd.show();

        mDatabase.child("data").child(page).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mainMenuSpinnerArray.clear();
                if (dataSnapshot.exists()) {
                    for (DataSnapshot postSnapShot : dataSnapshot.getChildren()) {
                        mainMenuSpinnerArray.add(postSnapShot.getKey());
                    }
                }
                mainMenuSpinnerAdapter.notifyDataSetChanged();
                pd.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                pd.dismiss();
            }
        });
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab:
                addButtonClick();
                break;

        }
    }

    private void addButtonClick() {
        new AddItemDialogBuilder(this, page, mainMenuSelected, mItemAddedListener).create().show();

    }

    private final AddItemDialogBuilder.AddItemDialogEventListener mItemAddedListener = new AddItemDialogBuilder.AddItemDialogEventListener() {
        @Override
        public void onItemAdd() {

        }
    };

    private void getAllSubItemDetails() {
        pd = new ProgressDialog(AddSubDataActivity.this);
        pd.setMessage("Loading...");
        pd.show();

        mDatabase.child("data").child(page).child(mainMenuSelected).child("Categories").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                detailsListArray.clear();
                if (dataSnapshot.exists()) {
                    for (DataSnapshot postSnapShot : dataSnapshot.getChildren()) {
                        detailsListArray.add(postSnapShot.getKey());
                    }
                }
                itemDetailsAdapter.notifyDataSetChanged();
                pd.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                pd.dismiss();
            }
        });
    }

    private void implementListViewClickListeners() {

        detailsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //If ActionMode not null select item
                if (mActionMode == null) {
            /*no items selected, so perform item click actions
             * like moving to next activity */
                    Toast toast = Toast.makeText(getApplicationContext(), "Item "
                                    + (position + 1) + ": " + detailsListArray.get(position),
                            Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
                    toast.show();

                } else
                    // add or remove selection for current list item
                    onListItemSelect(position);
            }
        });
        detailsList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                //Select item on long click
                onListItemSelect(position);
                return true;
            }
        });
    }

    //List item select method
    private void onListItemSelect(int position) {
        itemDetailsAdapter.toggleSelection(position);//Toggle the selection
        boolean hasCheckedItems = itemDetailsAdapter.getSelectedCount() > 0;//Check if any items are already selected or not
        if (hasCheckedItems && mActionMode == null)
            // there are some selected items, start the actionMode
            mActionMode = startSupportActionMode(new Toolbar_ActionMode_Callback(AddSubDataActivity.this, mItemDeleteListener, itemDetailsAdapter, detailsListArray));
        else if (!hasCheckedItems && mActionMode != null)
            // there no selected items, finish the actionMode
            mActionMode.finish();
        if (mActionMode != null)
            //set action mode title on item selection
            mActionMode.setTitle(String.valueOf(itemDetailsAdapter
                    .getSelectedCount()) + " selected");
    }

    //Delete selected rows
    public void deleteRows() {
        SparseBooleanArray selected = itemDetailsAdapter
                .getSelectedIds();//Get selected ids
        //Loop all selected ids
        for (int i = (selected.size() - 1); i >= 0; i--) {
            if (selected.valueAt(i)) {
                //If current id is selected remove the item via key
                removeFromFireBase(detailsListArray.get(selected.keyAt(i)));
                detailsListArray.remove(selected.keyAt(i));
                itemDetailsAdapter.notifyDataSetChanged();//notify adapter

            }
        }
        Toast.makeText(this, selected.size() + " item deleted.", Toast.LENGTH_SHORT).show();//Show Toast
        mActionMode.finish();//Finish action mode after use
    }

    private void removeFromFireBase(String object) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        Query applesQuery = ref.child("data").child(page).child(mainMenuSelected).child("Categories").child(object);

        applesQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot appleSnapshot : dataSnapshot.getChildren()) {
                    appleSnapshot.getRef().removeValue();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("ERROR", "onCancelled", databaseError.toException());
            }
        });

    }

    private void setData() {
        if (null != getIntent() && null != getIntent().getStringExtra(IntentConstants.PAGE)) {
            page = getIntent().getStringExtra(IntentConstants.PAGE);
        }
        if (page.equalsIgnoreCase(IntentConstants.PAGE_RIG)) {
            setTitle(getResources().getString(R.string.add_view_rig));
        } else {
            setTitle(getResources().getString(R.string.add_view_equipment_system));
        }
    }

    private final Toolbar_ActionMode_Callback.DeleteItemEventListener mItemDeleteListener = new Toolbar_ActionMode_Callback.DeleteItemEventListener() {
        @Override
        public void onItemDelete() {
            deleteRows();
        }

        @Override
        public void onActionModeNull() {
            itemDetailsAdapter.removeSelection();
            if (mActionMode != null)
                mActionMode = null;
        }
    };
}
