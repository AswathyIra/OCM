package com.ocm.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.ocm.R;
import com.ocm.conf.IntentConstants;

public class AdminHomeActivity extends BaseActivity implements View.OnClickListener {

    Button addRigButton,addEquipSystem,addEquipSubButton,viewApproveUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);
        setPageTitle("Admin Home");
        initView();
    }
    private void initView()
    {
        addRigButton= (Button) findViewById(R.id.addRigButton);
        addRigButton.setOnClickListener(this);
        addEquipSystem= (Button) findViewById(R.id.addEquipSystem);
        addEquipSystem.setOnClickListener(this);
        addEquipSubButton= (Button) findViewById(R.id.addEquipSubButton);
        addEquipSubButton.setOnClickListener(this);
        viewApproveUser= (Button) findViewById(R.id.viewApproveUser);
        viewApproveUser.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.addRigButton:
                startActivity(new Intent(AdminHomeActivity.this, AddAndViewActivity.class).putExtra(IntentConstants.PAGE,IntentConstants.PAGE_RIG));
                break;
            case R.id.addEquipSystem:
                startActivity(new Intent(AdminHomeActivity.this, AddAndViewActivity.class).putExtra(IntentConstants.PAGE,IntentConstants.PAGE_EQUIP));
                break;
            case R.id.addEquipSubButton:
                startActivity(new Intent(AdminHomeActivity.this, AddSubDataActivity.class).putExtra(IntentConstants.PAGE,IntentConstants.PAGE_EQUIP));
                break;
            case R.id.viewApproveUser:
                startActivity(new Intent(AdminHomeActivity.this, ViewAndApproveUserActivity.class).putExtra(IntentConstants.PAGE,IntentConstants.PAGE_EQUIP));
                break;
        }
    }
}
