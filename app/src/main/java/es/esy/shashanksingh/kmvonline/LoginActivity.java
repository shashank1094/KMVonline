package es.esy.shashanksingh.kmvonline;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import es.esy.shashanksingh.kmvonline.sync.CollegeNotificationSyncAdapter;

public class LoginActivity extends AppCompatActivity {

    //Defining views
    private EditText editTextUsername;
    private EditText editTextPassword;
    private CoordinatorLayout coordinatorLayout;
    private TextInputLayout usernameInputLayout,passworInputLayout;
    private ProgressDialog progressDialog;
    private Button buttonLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //Initializing views
        editTextUsername = (EditText) findViewById(R.id.editTextUsername);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        usernameInputLayout=(TextInputLayout)findViewById(R.id.textInputLayoutUsername);
        passworInputLayout=(TextInputLayout)findViewById(R.id.textInputLayoutPassword);
        coordinatorLayout=(CoordinatorLayout) findViewById(R.id.coordinatorLayoutLogin);

        hideKeyboard();
        buttonLogin = (Button) findViewById(R.id.buttonLogin);
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

        TextView registrationLink=(TextView) findViewById(R.id.linkregisteration);
        registrationLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {onRegistrationLinkClicked();
            }
        });

        Button signInAsStudent=(Button) findViewById(R.id.linkStudent);
        signInAsStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {loginAsStudent();}
        });
    }

    private void onRegistrationLinkClicked(){
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse("https://goo.gl/forms/VEGUOlxYXTTlDFxo1"));
        startActivity(i);
    }

    private void loginAsStudent(){
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        // Get details on the currently active default data network
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        // If there is a network connection, fetch data
        if (networkInfo != null && networkInfo.isConnected()) {
            SharedPreferences preferences = getSharedPreferences(Config.SHARED_PREF_NAME_LOGIN_DETAILS,Context.MODE_PRIVATE);
            //Getting editor
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean(Config.LOGGEDIN_SHARED_PREF, true);
            editor.putString(Config.USERNAME_SHARED_PREF,"student");
            editor.putString(Config.NAME_SHARED_PREF,"student");
            //Saving the sharedpreferences
            editor.apply();
            CollegeNotificationSyncAdapter.syncImmediately(LoginActivity.this);
            Intent intent = new Intent(LoginActivity.this, NotificationActivity.class);
            startActivity(intent);
        }
        else{
            Snackbar.make(coordinatorLayout, R.string.no_internet, Snackbar.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //In onresume fetching value from sharedpreference
        SharedPreferences sharedPreferences = getSharedPreferences(Config.SHARED_PREF_NAME_LOGIN_DETAILS, Context.MODE_PRIVATE);
        //boolean variable to check user is logged in or not
        //initially it is false
        boolean loggedIn = false;
        //Fetching the boolean value form sharedpreferences
        loggedIn = sharedPreferences.getBoolean(Config.LOGGEDIN_SHARED_PREF, false);
        //If we will get true
        if(loggedIn){
            //We will start the Profile Activity
            CollegeNotificationSyncAdapter.syncImmediately(this);
            Intent intent = new Intent(LoginActivity.this, NotificationActivity.class);
            startActivity(intent);
        }
    }

    private void hideKeyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).
                    hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    private void showAuthDialogBox(){
        //A dialog box
        progressDialog = new ProgressDialog(LoginActivity.this, R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();
    }

    private void login(){
        hideKeyboard();

        showAuthDialogBox();

        //Checking validity of username
        final String username = editTextUsername.getText().toString().trim();
        int flag=0;
        if(username.length()<1 || username.length()>25) {
            usernameInputLayout.setError("Invalid Username");

            //Shake Animation if username is not valid
            YoYo.with(Techniques.Shake)
                    .duration(1200)
                    .repeat(1)
                    .playOn(usernameInputLayout);

            flag=1;
        }
        else {
            usernameInputLayout.setError(null);
        }


        //Checking validity of password
        final String password = editTextPassword.getText().toString().trim();
        if(password.length()<1 || password.length()>25) {
            passworInputLayout.setError("Invalid Password");
            //Bounce Animation if password is not valid
            YoYo.with(Techniques.Bounce)
                    .duration(1200)
                    .repeat(1)
                    .playOn(passworInputLayout);

            flag=1;
        }
        else {
            passworInputLayout.setError(null);
        }
        if(flag==1) {
            progressDialog.hide();
            return;
        }

        networkCall(username,password);

    }

    private void networkCall(final String username,final String password){
        //Creating a string request
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.LOGIN_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //If we are getting success from server
                        //if(response.equalsIgnoreCase(Config.LOGIN_SUCCESS)){
                        String username=null;
                        String name=null;
                        int dept=0;
                        if(response.charAt(0)=='1'){
                            StringBuilder stringBuilder=new StringBuilder(response);
                            stringBuilder.deleteCharAt(0);
                            String jsonUserDetails=stringBuilder.toString();
                            try {
                                // Create a JSONObject from the JSON response string
                                JSONObject baseJsonResponse = new JSONObject(jsonUserDetails);
                                // Extract the JSONArray associated with the key called "features",
                                // which represents a list
                                JSONArray userToBeLoggedIn = baseJsonResponse.getJSONArray("userdetails");
                                JSONObject user = userToBeLoggedIn.getJSONObject(0);
                                username=user.getString("username");
                                name=user.getString("name");
                                dept=user.getInt("workDepartment");
                            }catch (JSONException e) {
                                // If an error is thrown when executing any of the above statements in the "try" block,
                                // catch the exception here, so the app doesn't crash. Print a log message
                                // with the message from the exception.
                                Log.e("Login", "Problem parsing the user details JSON results", e);
                            }
                            //Creating a shared preference
                            SharedPreferences sharedPreferences = LoginActivity.this.getSharedPreferences(Config.SHARED_PREF_NAME_LOGIN_DETAILS, Context.MODE_PRIVATE);
                            //Creating editor to store values to shared preferences
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            //Adding values to editor
                            editor.putBoolean(Config.LOGGEDIN_SHARED_PREF, true);
                            editor.putString(Config.USERNAME_SHARED_PREF, username);
                            editor.putString(Config.NAME_SHARED_PREF,name);
                            editor.putInt(Config.WORK_DEPARTMENT_SHARED_PREF, dept);
                            //Toast.makeText(LoginActivity.this,username+name+dept,Toast.LENGTH_SHORT).show();
                            //Saving values to editor
                            editor.apply();
                            //Starting profile activity
                            //Toast.makeText(LoginActivity.this,"Welcome "+sharedPreferences.getString(Config.USERNAME_SHARED_PREF,"No Name"),Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(LoginActivity.this, NotificationActivity.class);
                            //intent.putExtra("networkCall", true);
                            startActivity(intent);
                        }else{
                            progressDialog.hide();
                            Snackbar.make(coordinatorLayout, "Invalid Credentials", Snackbar.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.hide();
                        if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                            Snackbar.make(coordinatorLayout, "No internet connection.", Snackbar.LENGTH_LONG).show();
                        } else if (error instanceof AuthFailureError) {
                            Snackbar.make(coordinatorLayout, "Auth Failure Error.", Snackbar.LENGTH_LONG).show();
                        } else if (error instanceof ServerError) {
                            Snackbar.make(coordinatorLayout, "Server Error.", Snackbar.LENGTH_LONG).show();
                        } else if (error instanceof NetworkError) {
                            Snackbar.make(coordinatorLayout, "Network Error.", Snackbar.LENGTH_LONG).show();
                        } else if (error instanceof ParseError) {
                            Snackbar.make(coordinatorLayout, "Parse Error.", Snackbar.LENGTH_LONG).show();
                        }

                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                //Adding parameters to request
                params.put(Config.KEY_USERNAME, username);
                params.put(Config.KEY_PASSWORD, password);
                //returning parameter
                return params;
            }
        };
        //Adding the string request to the queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (Integer.parseInt(android.os.Build.VERSION.SDK) > 5
                && keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0) {
            Log.d("CDA", "onKeyDown Called");
            onBackPressed();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

}
