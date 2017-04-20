package es.esy.shashanksingh.kmvonline;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import es.esy.shashanksingh.kmvonline.sync.CollegeNotificationSyncAdapter;

/**
 * Created by shashank on 05-Apr-17.
 */

public class ShareInputCollegeNotification extends AppCompatActivity{
    private static final String INPUT_URL ="http://shashanksingh.esy.es/php/insertNotification.php";
    String title;
    String description;
    String department;
    String day;
    String month;
    String teacherName;
    boolean saveButtonDisplayed;
    EditText iTitle,iDescription;
    TextView save,cancel;
    TextInputLayout titleInputLayout,descriptionInputLayout;
    public ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.input_college_notification);
        setTitle("Compose");
        iTitle=(EditText) findViewById(R.id.titleInput);
        iDescription=(EditText) findViewById(R.id.descriptionInput);
        titleInputLayout=(TextInputLayout) findViewById(R.id.textInputLayoutTitle);
        descriptionInputLayout=(TextInputLayout) findViewById(R.id.textInputLayoutDescription);
        save=(TextView) findViewById(R.id.save_input_college_notification);
        cancel=(TextView) findViewById(R.id.cancel_input_college_notification);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent=getIntent();
        String wannaBeDescription=intent.getStringExtra(Intent.EXTRA_TEXT);
        SharedPreferences sharedPreferences = getSharedPreferences(Config.SHARED_PREF_NAME_LOGIN_DETAILS, Context.MODE_PRIVATE);
        //boolean variable to check user is logged in or not
        //initially it is false
        boolean loggedIn = false;
        //Fetching the boolean value form sharedpreferences
        loggedIn = sharedPreferences.getBoolean(Config.LOGGEDIN_SHARED_PREF, false);
        String user=sharedPreferences.getString(Config.USERNAME_SHARED_PREF, "student");
        //If we will get true
        if(loggedIn){
            if(user.equals("student")){
                Toast.makeText(ShareInputCollegeNotification.this, "You must log in as a teacher to share via KmvOnline.", Toast.LENGTH_LONG).show();
                backPressed();
            }else {
                if(wannaBeDescription.length()>0)
                    iDescription.setText(wannaBeDescription);
                else{
                    Toast.makeText(ShareInputCollegeNotification.this, "Nothing to share", Toast.LENGTH_LONG).show();
                    backPressed();
                }


            }
        }else{
            Toast.makeText(ShareInputCollegeNotification.this, "You must log in first.", Toast.LENGTH_LONG).show();
            startActivity(new Intent(ShareInputCollegeNotification.this,LoginActivity.class));
        }


        saveButtonDisplayed=true;
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                // Get details on the currently active default data network
                NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
                // If there is a network connection, fetch data
                if (networkInfo != null && networkInfo.isConnected()) {
                    saving();
                }
                else{
                    Toast.makeText(ShareInputCollegeNotification.this,"No internet. Try after some time.",Toast.LENGTH_SHORT).show();
                }
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                backPressed();
            }
        });
    }

    private void saving(){
        hideKeyboard();
        progressDialog= new ProgressDialog(ShareInputCollegeNotification.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Saving...");
        progressDialog.show();

        Calendar c = Calendar.getInstance();
        month= String.valueOf(c.get(Calendar.MONTH)+1);
        day= String.valueOf(c.get(Calendar.DAY_OF_MONTH));
        title=iTitle.getText().toString().trim();
        description=iDescription.getText().toString().trim();
        int flag=0;
        if(title.length()>100 || title.length()<1) {
            titleInputLayout.setError(getString(R.string.title_error));

            YoYo.with(Techniques.Bounce)
                    .duration(1200)
                    .repeat(1)
                    .playOn(titleInputLayout);

            flag=1;
        }else{
            titleInputLayout.setError(null);
        }
        if(description.length()>1000 || description.length()<1) {
            descriptionInputLayout.setError(getString(R.string.description_error));

            YoYo.with(Techniques.Shake)
                    .duration(1200)
                    .repeat(1)
                    .playOn(descriptionInputLayout);

            flag=1;
        }else{
            descriptionInputLayout.setError(null);
        }
        if(flag==1) {
            progressDialog.hide();
            return;
        }
        else{
            saveButtonDisplayed=false;
            invalidateOptionsMenu();
            SharedPreferences sharedPreferences = ShareInputCollegeNotification.this.getSharedPreferences(Config.SHARED_PREF_NAME_LOGIN_DETAILS, Context.MODE_PRIVATE);
            teacherName=sharedPreferences.getString("name","Anonymous");
            department=String.valueOf(sharedPreferences.getInt(Config.WORK_DEPARTMENT_SHARED_PREF,0));
            new saveClicked().execute(title, description, department, day, month, teacherName);
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id==android.R.id.home){
            backPressed();
            return true;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(ShareInputCollegeNotification.this,NotificationActivity.class));
    }

    private void backPressed(){
        hideKeyboard();
        if (iTitle.getText().toString().trim().length()>0 || iDescription.getText().toString().trim().length() > 0)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(ShareInputCollegeNotification.this);
            builder.setTitle("Discard Changes");
            builder.setMessage("Are you sure you want discard the changes?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            onBackPressed();
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
        else{
            onBackPressed();
        }
    }
    private void hideKeyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).
                    hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }


    private class saveClicked extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            URL myUrl = null;
            HttpURLConnection conn = null;
            String response = "";
            final String mTitle = params[0];
            final String mDescription = params[1];
            final String mDepartment = params[2];
            final String mDay = params[3];
            final String mMonth = params[4];
            final String mTeacherName = params[5];
            try {
                myUrl = new URL(INPUT_URL);
                conn = (HttpURLConnection) myUrl.openConnection();
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);

                HashMap<String,String> postParams=new HashMap<String,String>();
                postParams.put("title",mTitle);
                postParams.put("description",mDescription);
                postParams.put("department",mDepartment);
                postParams.put("day",mDay);
                postParams.put("month",mMonth);
                postParams.put("teacherName",mTeacherName);

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

            } catch (IOException e) {
                e.printStackTrace();
            }
            return response;
        }

        @Override
        protected void onPostExecute(String response) {
            if(response.length()<1)
                Toast.makeText(ShareInputCollegeNotification.this,"Not Saved. Try after some time.",Toast.LENGTH_SHORT).show();
            if(response.charAt(0)=='1') {
                Toast.makeText(ShareInputCollegeNotification.this, "Saved", Toast.LENGTH_SHORT).show();
                CollegeNotificationSyncAdapter.syncImmediately(ShareInputCollegeNotification.this);
                onBackPressed();
            }
            else
                Toast.makeText(ShareInputCollegeNotification.this,"Not Saved. Try after some time.",Toast.LENGTH_SHORT).show();
            progressDialog.hide();
        }

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }


}
