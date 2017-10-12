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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
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

public class AddAndViewActivity extends BaseActivity implements View.OnClickListener, ActionMenuView.OnMenuItemClickListener {
    String page;
    private ListView detailsList;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    ProgressDialog pd;
    private ArrayList<String> detailsListArray = new ArrayList<>();
    private ItemDetailsAdapter itemDetailsAdapter;
    private FloatingActionButton fab;
    private ActionMode mActionMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_and_view);
        initView();
        setData();
    }

    @Override
    protected void onResume() {
        getAllItemDetails();
        super.onResume();
    }

    private void initView() {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        detailsList = (ListView) findViewById(R.id.detailsList);
        itemDetailsAdapter = new ItemDetailsAdapter(this, R.layout.list_item_details, detailsListArray, page);
        detailsList.setAdapter(itemDetailsAdapter);
        implementListViewClickListeners();

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(this);
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
            mActionMode = startSupportActionMode(new Toolbar_ActionMode_Callback(this, mItemDeleteListener, itemDetailsAdapter, detailsListArray));
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

    private void setData() {
        if (null != getIntent() && null != getIntent().getStringExtra(IntentConstants.PAGE)) {
            page = getIntent().getStringExtra(IntentConstants.PAGE);
        }
        if (page.equalsIgnoreCase(IntentConstants.PAGE_RIG)) {
            setPageTitle(getResources().getString(R.string.add_view_rig));
        } else {
            setPageTitle(getResources().getString(R.string.add_view_equipment_system));
        }
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
        new AddItemDialogBuilder(this, page, mItemAddedListener).create().show();

    }

    private void getAllItemDetails() {
        pd = new ProgressDialog(AddAndViewActivity.this);
        pd.setMessage("Loading...");
        pd.show();

        mDatabase.child("data").child(page).addValueEventListener(new ValueEventListener() {
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

    private void removeFromFireBase(String object) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        Query applesQuery = ref.child("data").child(page).child(object);

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

    private final AddItemDialogBuilder.AddItemDialogEventListener mItemAddedListener = new AddItemDialogBuilder.AddItemDialogEventListener() {
        @Override
        public void onItemAdd() {

        }
    };

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        return false;
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
