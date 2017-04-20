package es.esy.shashanksingh.kmvonline;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import es.esy.shashanksingh.kmvonline.sync.CollegeNotificationSyncAdapter;

/**
 * Created by shashank on 21-Oct-16.
 */

public class MyAccountNotificationDetail extends AppCompatActivity  {

    private static final String DELETE_URL ="http://shashanksingh.esy.es/php/deleteNotification.php";
    private collegeNotification notification;
    private String sharedString;
    private int idToBeUsed;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.college_notification_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Check if the version of Android is Lollipop or higher
        if (Build.VERSION.SDK_INT >= 21) {
            // Set the status bar to dark-semi-transparentish
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            // Set paddingTop of toolbar to height of status bar.
            // Fixes statusbar covers toolbar issue
        }
        Intent i = getIntent();
        notification = (collegeNotification) i.getSerializableExtra("CurrentNotification");
        idToBeUsed = notification.getId();
        String[] monthsNames= new String[12];
        monthsNames[0]="January";
        monthsNames[1]="February";
        monthsNames[2]="March";
        monthsNames[3]="April";
        monthsNames[4]="May";
        monthsNames[5]="June";
        monthsNames[6]="July";
        monthsNames[7]="August";
        monthsNames[8]="September";
        monthsNames[9]="October";
        monthsNames[10]="November";
        monthsNames[11]="December";

        FloatingActionButton floatingActionButton=(FloatingActionButton)findViewById(R.id.fab_detail);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_EDIT);
                intent.setType("vnd.android.cursor.item/event");
                intent.putExtra("allDay", true);
                intent.putExtra("title", notification.getTitle());
                intent.putExtra(CalendarContract.Events.DESCRIPTION,notification.getDescription()+"\n\n"+notification.getTeacherName());
                startActivity(intent);
            }
        });
        getSupportActionBar().setTitle(monthsNames[notification.getMonth()-1]+" "+String.valueOf(notification.getDay()));
        //zxcvbnm

        String[] officeDepartmentNames=new String[6];
        officeDepartmentNames[0]="Principal";
        officeDepartmentNames[1]="Administration";
        officeDepartmentNames[2]="Accounts";
        officeDepartmentNames[3]="Library";
        officeDepartmentNames[4]="Keshav Mahavidyalaya";
        officeDepartmentNames[5]="Department of Computer Science";
        TextView headingTextView = (TextView) findViewById(R.id.detailTitle);
        headingTextView.setText(notification.getTitle());


        TextView departmentTextView = (TextView) findViewById(R.id.detailDepartment);
        departmentTextView.setText(officeDepartmentNames[notification.getDepartment()]);

        TextView descriptionTextView = (TextView) findViewById(R.id.detailDescription);
        descriptionTextView.setText(notification.getDescription());
        Linkify.addLinks(descriptionTextView, Linkify.WEB_URLS);
        descriptionTextView.setMovementMethod(LinkMovementMethod.getInstance());

        TextView teacherNameTextView = (TextView) findViewById(R.id.detailTeacherName);
        teacherNameTextView.setText(notification.getTeacherName());

        sharedString=notification.getTitle()+
                "\n\n"+notification.getDescription()+
                "\n\n"+notification.getTeacherName()+
                "\n"+officeDepartmentNames[notification.getDepartment()]+
                "\n"+String.valueOf(notification.getDay())+" "+
                monthsNames[notification.getMonth()-1]+
                "\n\n#KMV notification app";

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.college_notification_detail_setting_menu, menu);
//        Menu Resource, Menu
        SharedPreferences sharedPreferences = getSharedPreferences(Config.SHARED_PREF_NAME_LOGIN_DETAILS, Context.MODE_PRIVATE);
        String user = sharedPreferences.getString(Config.NAME_SHARED_PREF,"student");
        if( notification.getTeacherName().equals(user) )  {
            getMenuInflater().inflate(R.menu.admin_delete, menu);//Menu Resource, Menu
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


    private void deleteTheNotification(){
        AlertDialog.Builder builder = new AlertDialog.Builder(MyAccountNotificationDetail.this);
        builder.setTitle("Delete");
        builder.setMessage("Are you sure you want to delete this notification?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                        // Get details on the currently active default data network
                        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
                        // If there is a network connection, fetch data
                        if (networkInfo != null && networkInfo.isConnected()) {
                            new deleteNow().execute(String.valueOf(idToBeUsed));
                        }
                        else{
                            Toast.makeText(MyAccountNotificationDetail.this,"No internet. Try after some time.",Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //  Action for 'NO' Button
                        dialog.cancel();
                    }
                });

        //Creating dialog box
        AlertDialog alert = builder.create();
        alert.show();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.detail_delete:
                ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                // Get details on the currently active default data network
                NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
                // If there is a network connection, fetch data
                if (networkInfo != null && networkInfo.isConnected()) {
                    deleteTheNotification();
                } else {
                    Toast.makeText(MyAccountNotificationDetail.this,"No internet. Try after some time.",Toast.LENGTH_SHORT).show();
                }
                return true;
            case R.id.detail_share:
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_TEXT, sharedString);
                startActivity(Intent.createChooser(i, "Share via"));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private class deleteNow extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            URL myUrl = null;
            HttpURLConnection conn = null;
            String response = "";
            final String mID = params[0];
            try {
                myUrl = new URL(DELETE_URL);
                conn = (HttpURLConnection) myUrl.openConnection();
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);

                HashMap<String,String> postParams=new HashMap<String,String>();
                postParams.put("id",mID);
                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));

                StringBuilder result = new StringBuilder();
                boolean first = true;
                for(Map.Entry<String, String> entry : postParams.entrySet()){
                    if (first)
                        first = false;
                    else
                        result.append("&");
                    result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
                    result.append("=");
                    result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
                }
                writer.write(result.toString());
                writer.flush();
                writer.close();

                InputStream inputStream = conn.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
                String line = "";
                while ((line = bufferedReader.readLine()) != null) {
                    response += line;
                }
                bufferedReader.close();
                inputStream.close();
                conn.disconnect();
                os.close();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return response;

        }
        @Override
        protected void onPostExecute(String response) {
            if (response.length()<1)
                Toast.makeText(MyAccountNotificationDetail.this,"Try after some time.",Toast.LENGTH_SHORT).show();
            if(response.charAt(0)=='1')
                Toast.makeText(MyAccountNotificationDetail.this,"Deleted",Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(MyAccountNotificationDetail.this,"Try after some time.",Toast.LENGTH_SHORT).show();
            CollegeNotificationSyncAdapter.syncImmediately(MyAccountNotificationDetail.this);
            onBackPressed();
        }

        @Override
        protected void onPreExecute() {}
        @Override
        protected void onProgressUpdate(Void... values) {}

    }

}