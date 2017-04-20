package es.esy.shashanksingh.kmvonline;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

public class NotificationActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Check if the version of Android is Lollipop or higher
        if (Build.VERSION.SDK_INT >= 21) {
            // Set the status bar to dark-semi-transparentish
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            // Set paddingTop of toolbar to height of status bar.
            // Fixes statusbar covers toolbar issue
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        setDetailsInNavHeader();
        attachCollegeFragment();
    }


    public void setActionBarTitle(String title) {
        getSupportActionBar().setTitle(title);
    }

    private void setDetailsInNavHeader() {

        //zxcvbnm

        String[] officeDepartmentNames = new String[6];
        officeDepartmentNames[0] = "Principal";
        officeDepartmentNames[1] = "Administration Office";
        officeDepartmentNames[2] = "Accounts Office";
        officeDepartmentNames[3] = "Library";
        officeDepartmentNames[4] = "Keshav Mahavidyalaya";
        officeDepartmentNames[5]="Department of Computer Science";
        SharedPreferences preferences = getSharedPreferences(Config.SHARED_PREF_NAME_LOGIN_DETAILS, Context.MODE_PRIVATE);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View header = navigationView.getHeaderView(0);
        TextView name = (TextView) header.findViewById(R.id.nameNav);
        TextView dept = (TextView) header.findViewById(R.id.deptNav);
        if (preferences.getString(Config.NAME_SHARED_PREF, "Username").equals("student")) {
            name.setText("Student");
            dept.setText("Keshav Mahavidyalaya");
            NavigationView nav = (NavigationView) findViewById(R.id.nav_view);
            nav.setNavigationItemSelectedListener(this);
            Menu menu = nav.getMenu();
            MenuItem target = menu.findItem(R.id.main_my_account);
            target.setVisible(false);
        } else {
            name.setText(preferences.getString(Config.NAME_SHARED_PREF, "Name"));
            dept.setText(officeDepartmentNames[preferences.getInt(Config.WORK_DEPARTMENT_SHARED_PREF, 0)]);
        }
    }


    private void attachCollegeFragment() {
        collegeNotificationFragment cnf = (collegeNotificationFragment) getSupportFragmentManager().findFragmentByTag(Constants.COLLEGE_NOTIFICATION_FRAGMENT);
        if (cnf == null) {
            cnf = new collegeNotificationFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_notification, cnf, Constants.COLLEGE_NOTIFICATION_FRAGMENT);
            transaction.commit();
        } else {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_notification, cnf, Constants.COLLEGE_NOTIFICATION_FRAGMENT);
            transaction.commit();
        }
    }

    private void attachMyAccountFragment() {
        MyAccountFragment myAccountFragment = (MyAccountFragment) getSupportFragmentManager().findFragmentByTag(Constants.MY_ACCOUNT_FRAGMENT);
        if (myAccountFragment == null) {
            myAccountFragment = new MyAccountFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_notification, myAccountFragment, Constants.MY_ACCOUNT_FRAGMENT);
            transaction.commit();
        } else {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_notification, myAccountFragment, Constants.MY_ACCOUNT_FRAGMENT);
            transaction.commit();
        }
    }

    private void attachAboutUstFragment() {
        AboutFragment myAccountFragment = (AboutFragment) getSupportFragmentManager().findFragmentByTag(Constants.ABOUT_FRAGMENT);
        if (myAccountFragment == null) {
            myAccountFragment = new AboutFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_notification, myAccountFragment, Constants.ABOUT_FRAGMENT);
            transaction.commit();
        } else {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_notification, myAccountFragment, Constants.ABOUT_FRAGMENT);
            transaction.commit();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        int id = item.getItemId();
        if (id == R.id.main_home) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    attachCollegeFragment();
                }
            }, 290);
        } else if (id == R.id.main_my_account) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    attachMyAccountFragment();
                }
            }, 290);
        } else if (id == R.id.main_settings) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(NotificationActivity.this, Settings.class);
                    startActivity(intent);
                }
            }, 290);
        } else if (id == R.id.main_share) {
            shareApp();
        } else if (id == R.id.main_about_us) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    attachAboutUstFragment();
                }
            }, 290);
        } else if (id == R.id.main_logout) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    logout();
                }
            }, 290);
        } else if (id == R.id.main_feedback) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    feedbackApp();
                }
            }, 290);
        }
        return true;
    }

    private void shareApp() {
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("text/plain");
        i.putExtra(Intent.EXTRA_SUBJECT,"KmvOnline");
        String sAux = "\nKeshav Mahavidyalaya's Notification App\n\n";
        sAux =sAux +"https://drive.google.com/open?id=0B1V1lAFJ5mquVnBOZkV2ZGM4V2M\n\n";
        i.putExtra(Intent.EXTRA_TEXT,sAux);
        startActivity(Intent.createChooser(i, "Share via"));
    }
    private void feedbackApp() {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:")); // only email apps should handle this
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"shashank.singh1094@gmail.com"});
        intent.putExtra(Intent.EXTRA_SUBJECT, "KmvOnline Feedback");
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    private void logout(){
        //Creating an alert dialog to confirm logout
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Logout");
        alertDialogBuilder.setMessage("Are you sure you want to logout?");
        alertDialogBuilder.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        //Getting out sharedpreferences
                        SharedPreferences preferences = getSharedPreferences(Config.SHARED_PREF_NAME_LOGIN_DETAILS, Context.MODE_PRIVATE);
                        //Getting editor
                        SharedPreferences.Editor editor = preferences.edit();
                        //Puting the value false for loggedin
                        editor.putBoolean(Config.LOGGEDIN_SHARED_PREF, false);
                        //Putting blank value to email
                        editor.putString(Config.USERNAME_SHARED_PREF,"");
                        editor.putString(Config.NAME_SHARED_PREF,"");
                        editor.putInt(Config.WORK_DEPARTMENT_SHARED_PREF, 0);
                        //Saving the sharedpreferences
                        editor.apply();
                        //Starting login activity
                        Intent intent = new Intent(NotificationActivity.this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        //finish();
                    }
                });
        alertDialogBuilder.setNegativeButton("No",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                    }
                });
        //Showing the alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

    }

}
