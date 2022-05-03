package com.upc.inmoperu.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Session {
    private SharedPreferences prefs;

    public Session(Context cntx) {
        // TODO Auto-generated constructor stub
        prefs = PreferenceManager.getDefaultSharedPreferences(cntx);
    }

    public void setusername(String username) {
        prefs.edit().putString("username", username).commit();
    }

    public String getusername() {
        String username = prefs.getString("username","");
        return username;
    }

    public void setuserid(String userid) {
        prefs.edit().putString("userid", userid).commit();
    }

    public String getuserid() {
        String userid = prefs.getString("userid","");
        return userid;
    }

    public void settotal(String total) {
        prefs.edit().putString("total", total).commit();
    }

    public String gettotal() {
        String total = prefs.getString("total","");
        return total;
    }
}
