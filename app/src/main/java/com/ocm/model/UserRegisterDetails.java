package com.ocm.model;

/**
 * Created by Aswathy_G on 6/15/2017.
 */

public class UserRegisterDetails {
    public String username;
    public String password;
    public String name;
    public String phone;
    public String user_id;
    public int ongc_id;
    public boolean admin=false;
    public boolean approved=false;
    public UserRegisterDetails() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public UserRegisterDetails(String name, String phone, String username, String password,int ongc_id,String user_id) {
        this.user_id=user_id;
        this.ongc_id=ongc_id;
        this.name = name;
        this.phone = phone;
        this.username = username;
        this.password = password;
    }
}
