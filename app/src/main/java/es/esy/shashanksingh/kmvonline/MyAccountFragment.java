package es.esy.shashanksingh.kmvonline;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

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

import es.esy.shashanksingh.kmvonline.data.collegeNotificationContract;
import es.esy.shashanksingh.kmvonline.sync.CollegeNotificationSyncAdapter;

import static es.esy.shashanksingh.kmvonline.collegeNotificationFragment.COL_COLLEGE_NOTIFICATION_DAY;
import static es.esy.shashanksingh.kmvonline.collegeNotificationFragment.COL_COLLEGE_NOTIFICATION_DEPARTMENT;
import static es.esy.shashanksingh.kmvonline.collegeNotificationFragment.COL_COLLEGE_NOTIFICATION_DESCRIPTION;
import static es.esy.shashanksingh.kmvonline.collegeNotificationFragment.COL_COLLEGE_NOTIFICATION_MONTH;
import static es.esy.shashanksingh.kmvonline.collegeNotificationFragment.COL_COLLEGE_NOTIFICATION_REMOTE_ID;
import static es.esy.shashanksingh.kmvonline.collegeNotificationFragment.COL_COLLEGE_NOTIFICATION_TEACHER_NAME;
import static es.esy.shashanksingh.kmvonline.collegeNotificationFragment.COL_COLLEGE_NOTIFICATION_TITLE;

/**
 * Created by shashank on 17-Mar-17.
 */

public class MyAccountFragment extends Fragment implements  LoaderManager.LoaderCallbacks<Cursor> {

    private String TAG=MyAccountFragment.class.getSimpleName();
    private CollegeNotificationAdapter mAdapter;
    private CoordinatorLayout mFabWrapper;
    private NetworkInfo networkInfo;
    private ConnectivityManager connMgr;
    private MenuItem deleteAllMenuItem;
    public MyAccountFragment(){
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        // Retain this fragment across configuration changes.
        ((NotificationActivity) getActivity()).setActionBarTitle("My Account");
        final View rootView = inflater.inflate(R.layout.my_account_fragment, container, false);
        setHasOptionsMenu(true);
        mFabWrapper=(CoordinatorLayout) rootView.findViewById(R.id.fabWrapper);
        connMgr = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fabMain);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                networkInfo = connMgr.getActiveNetworkInfo();
                // If there is a network connection, fetch data
                if (networkInfo != null && networkInfo.isConnected()) {
                    startActivity(new Intent(getActivity(), InputCollegeNotification.class));
                }
                else{
                    Snackbar.make(mFabWrapper, R.string.no_internet, Snackbar.LENGTH_LONG).show();
                }
            }
        });
        ListView listView=(ListView) rootView.findViewById(R.id.my_account_college_notification_list);
        mAdapter=new CollegeNotificationAdapter(getActivity(),null,0);
        TextView mEmptyStateView = (TextView) rootView.findViewById(R.id.my_account_empty_view);
        listView.setEmptyView(mEmptyStateView);
        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Cursor cursor = (Cursor) mAdapter.getItem(position);
                collegeNotification currentCollegeNotification =new collegeNotification(cursor.getInt(COL_COLLEGE_NOTIFICATION_REMOTE_ID),
                        cursor.getString(COL_COLLEGE_NOTIFICATION_TITLE),
                        cursor.getString(COL_COLLEGE_NOTIFICATION_DESCRIPTION),
                        cursor.getInt(COL_COLLEGE_NOTIFICATION_DEPARTMENT),
                        cursor.getInt(COL_COLLEGE_NOTIFICATION_DAY),
                        cursor.getInt(COL_COLLEGE_NOTIFICATION_MONTH),
                        cursor.getString(COL_COLLEGE_NOTIFICATION_TEACHER_NAME));
                Intent i = new Intent(getActivity(), MyAccountNotificationDetail.class);
                i.putExtra("CurrentNotification", currentCollegeNotification);
                startActivity(i);
            }
        });
        getActivity().getSupportLoaderManager().initLoader(100, null, this);
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        getActivity().getMenuInflater().inflate(R.menu.activity_my_account_delete_all, menu);
        deleteAllMenuItem = menu.findItem(R.id.main_delete_all);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.main_delete_all:
                if(mAdapter.getCount()<1)
                    Snackbar.make(mFabWrapper,"You have no notifications until now.", Snackbar.LENGTH_LONG).show();
                else {
                    ConnectivityManager connMgr = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
                    // Get details on the currently active default data network
                    NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
                    // If there is a network connection, fetch data
                    if (networkInfo != null && networkInfo.isConnected()) {
                        showDeleteAllDialogBox();
                    } else {
                        //Toast.makeText(getActivity(),"No internet. Try after some time.",Toast.LENGTH_SHORT).show();
                        Snackbar.make(mFabWrapper, "No internet connection. Try after some time.", Snackbar.LENGTH_LONG).show();
                    }

                    return true;
                }
        }
        return false;
    }
    private void showDeleteAllDialogBox(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Delete All");
        builder.setMessage("Are you sure you want to delete all your notifications?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        ConnectivityManager connMgr = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
                        // Get details on the currently active default data network
                        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
                        // If there is a network connection, fetch data
                        if (networkInfo != null && networkInfo.isConnected()) {
                            SharedPreferences sharedPreferences = getActivity().getSharedPreferences(Config.SHARED_PREF_NAME_LOGIN_DETAILS, Context.MODE_PRIVATE);
                            String user = sharedPreferences.getString(Config.NAME_SHARED_PREF,"student");
                                new MyAccountFragment.deleteAllNow().execute(user);
                        }
                        else{
                            //Toast.makeText(getActivity(),"No internet. Try after some time.",Toast.LENGTH_SHORT).show();
                            Snackbar.make(mFabWrapper,"No internet connection. Try after some time.", Snackbar.LENGTH_LONG).show();
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

    private static final String[] COLLEGE_NOTIFICATION_COLUMNS = {
            collegeNotificationContract.collegeNotificationEntry.TABLE_NAME + "." + collegeNotificationContract.collegeNotificationEntry._ID,
            collegeNotificationContract.collegeNotificationEntry.COLUMN_REMOTE_ID,
            collegeNotificationContract.collegeNotificationEntry.COLUMN_TITLE,
            collegeNotificationContract.collegeNotificationEntry.COLUMN_DESCRIPTION,
            collegeNotificationContract.collegeNotificationEntry.COLUMN_DEPARTMENT,
            collegeNotificationContract.collegeNotificationEntry.COLUMN_DAY,
            collegeNotificationContract.collegeNotificationEntry.COLUMN_MONTH,
            collegeNotificationContract.collegeNotificationEntry.COLUMN_TEACHER_NAME
    };
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        SharedPreferences preferences = getActivity().getSharedPreferences(Config.SHARED_PREF_NAME_LOGIN_DETAILS, Context.MODE_PRIVATE);
        String name=preferences.getString(Config.NAME_SHARED_PREF, "Name");
        String sortOrder = collegeNotificationContract.collegeNotificationEntry.COLUMN_REMOTE_ID + " DESC";
        String finalSelection=collegeNotificationContract.collegeNotificationEntry.COLUMN_TEACHER_NAME+" IN (?)";
            return new CursorLoader(getActivity(),
                    collegeNotificationContract.collegeNotificationEntry.CONTENT_URI,
                    COLLEGE_NOTIFICATION_COLUMNS,
                    finalSelection,
                    new String[]{name},
                    sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);
        Log.d(TAG, "loading finished");
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }


    private static final String DELETE_ALL_URL ="http://shashanksingh.esy.es/php/deleteAllNotifications.php";
    private class deleteAllNow extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            URL myUrl = null;
            HttpURLConnection conn = null;
            String response = "";
            final String user = params[0];
            try {
                myUrl = new URL(DELETE_ALL_URL);
                conn = (HttpURLConnection) myUrl.openConnection();
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);

                HashMap<String,String> postParams=new HashMap<String,String>();
                Log.d("ooooooooooooooooooo",user);
                postParams.put("teacherName",user);
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
                Snackbar.make(mFabWrapper,"Try after some time.", Snackbar.LENGTH_LONG).show();
            if(response.charAt(0)=='1') {
                Snackbar.make(mFabWrapper,"Deleted all your Notifications.", Snackbar.LENGTH_LONG).show();
                CollegeNotificationSyncAdapter.syncImmediately(getActivity());
            }
            else
                Snackbar.make(mFabWrapper,"Try after some time.", Snackbar.LENGTH_LONG).show();
        }
        @Override
        protected void onPreExecute() {}
        @Override
        protected void onProgressUpdate(Void... values) {}

    }
}
