package com.upc.inmoperu;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.upc.inmoperu.ui.Session;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.upc.inmoperu.ui.account.AccountFragment;
import com.upc.inmoperu.ui.contact.ContactFragment;
import com.upc.inmoperu.ui.home.HomeFragment;
import com.upc.inmoperu.ui.news.NewsFragment;
import com.upc.inmoperu.ui.wish.WishFragment;

public class MainActivity extends AppCompatActivity {

    public AlertDialog dialog;
    private Session session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try{
            this.getSupportActionBar().hide(); //escondemos la barrita del top
        }catch (NullPointerException e) {}
        setContentView(R.layout.activity_main);

        BottomNavigationView navigation = findViewById(R.id.nav_view);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        loadFragment(new HomeFragment());
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = (item) -> {
        switch (item.getItemId()){
            case R.id.navigation_home:
                setProgressDialog();
                loadFragment(new HomeFragment());
                new android.os.Handler().postDelayed(
                        new Runnable() {
                            public void run() {
                                dialog.dismiss();
                            }
                        },
                        3000);
                return true;
            case R.id.navigation_account:
                loadFragment(new AccountFragment());
                return true;
            case R.id.navigation_wish:
                session = new Session(getApplicationContext());
                if(session.getuserid() != "") {
                    setProgressDialog();
                    loadFragment(new WishFragment());
                    new android.os.Handler().postDelayed(
                            new Runnable() {
                                public void run() {
                                    dialog.dismiss();
                                }
                            },
                            3000);
                }else {
                    loadFragment(new WishFragment());
                }
                return true;
            case R.id.navigation_contact:
                loadFragment(new ContactFragment());
                return true;
            case R.id.navigation_new:
                loadFragment(new NewsFragment());
                return true;
        }
        return false;
    };

    private void loadFragment(Fragment fragment){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.nav_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }


    public void setProgressDialog() {

        int llPadding = 30;
        LinearLayout ll = new LinearLayout(this);
        ll.setOrientation(LinearLayout.HORIZONTAL);
        ll.setPadding(llPadding, llPadding, llPadding, llPadding);
        ll.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams llParam = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        llParam.gravity = Gravity.CENTER;
        ll.setLayoutParams(llParam);

        ProgressBar progressBar = new ProgressBar(this);
        progressBar.setIndeterminate(true);
        progressBar.setPadding(0, 0, llPadding, 0);
        progressBar.setLayoutParams(llParam);

        llParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        llParam.gravity = Gravity.CENTER;
        TextView tvText = new TextView(this);
        tvText.setText("Loading ...");
        tvText.setTextColor(Color.parseColor("#000000"));
        tvText.setTextSize(20);
        tvText.setLayoutParams(llParam);

        ll.addView(progressBar);
        ll.addView(tvText);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setView(ll);

        dialog = builder.create();
        dialog.show();
        Window window = dialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            layoutParams.copyFrom(dialog.getWindow().getAttributes());
            layoutParams.width = LinearLayout.LayoutParams.WRAP_CONTENT;
            layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;
            dialog.getWindow().setAttributes(layoutParams);
        }
    }
}