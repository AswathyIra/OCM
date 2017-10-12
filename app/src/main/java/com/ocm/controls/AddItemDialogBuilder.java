package com.ocm.controls;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.ocm.R;
import com.ocm.framework.Utility;




public class AddItemDialogBuilder extends OCMAlertDialogBuilder {

    private Activity mActivity;
    private AlertDialog mDialog;
    private AddItemDialogEventListener addItemAlertDialogEventListener;
    private DatabaseReference mDatabase;
    private String page;
    private String mainItemName=null;

    public AddItemDialogBuilder(Activity activity,String page, AddItemDialogEventListener addItemAlertDialogEventListener) {
        super(activity);
        this.mActivity = activity;
        this.page=page;
        this.addItemAlertDialogEventListener = addItemAlertDialogEventListener;
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }
    public AddItemDialogBuilder(Activity activity,String page,String mainItemName, AddItemDialogEventListener addItemAlertDialogEventListener) {
        super(activity);
        this.mActivity = activity;
        this.page=page;
        this.mainItemName=mainItemName;
        this.addItemAlertDialogEventListener = addItemAlertDialogEventListener;
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }
    @Override
    public AlertDialog create() {
        this.setTitle(mActivity.getResources().getString(R.string.add)+" "+page)
                .setView(mActivity.getLayoutInflater().inflate(R.layout.dialog_add_item, null))
                .setCancelable(false)
                .setPositiveButton(R.string.add, null)
                .setNegativeButton(R.string.cancel, null);

        mDialog = super.create();

        this.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                setButtonsCallback(mDialog);
            }
        });
        return mDialog;
    }

    public void setButtonsCallback(final AlertDialog dialog) {
        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSave();
                dialog.dismiss();
            }
        });

        dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }

    private EditText getItemView() {
        return (EditText) mDialog.findViewById(R.id.itemName);
    }

    private void onSave() {
        String itemName = getItemView().getText().toString();

        if (Utility.isNullOrEmpty(itemName)) {
            showValidationWarning(mActivity.getString(R.string.validation_enter_item));
            return;
        }

        new AddItemAsyncTask(itemName).execute();
    }

    private void showValidationWarning(String message) {
        Toast.makeText(mDialog.getContext(), message, Toast.LENGTH_LONG).show();
    }


    class AddItemAsyncTask extends AsyncTask<Void, Void, Void> {
        private String itemName;

        public AddItemAsyncTask(String itemName) {
            this.itemName = itemName;
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected Void doInBackground(Void... voids) {
            if (!Utility.canConnectToInternet())
                return null;
            if(null!=mainItemName)
            {
                addSubItemService(itemName);
            }
            else {
                addItemService(itemName);
            }
            return  null;
        }


        @Override
        protected void onPostExecute(Void aVoid) {
            addItemAlertDialogEventListener.onItemAdd();
            mDialog.dismiss();
        }

    }
    private void addItemService(final String itemName)
    {

        mDatabase.child("data").child(page).child(itemName).push().setValue(1, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError == null) {
                    Toast.makeText(mActivity, "Added Successfully ",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(mActivity, "Error while adding: " + databaseError,
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    private void addSubItemService(final String itemName)
    {

        mDatabase.child("data").child(page).child(mainItemName).child("Categories").child(itemName).push().setValue(1, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError == null) {
                    Toast.makeText(mActivity, "Added Successfully ",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(mActivity, "Error while adding: " + databaseError,
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public interface AddItemDialogEventListener {
        void onItemAdd();
    }
}
