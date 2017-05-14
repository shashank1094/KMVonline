package es.esy.shashanksingh.kmvonline;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import es.esy.shashanksingh.kmvonline.data.collegeNotificationContract;

import static es.esy.shashanksingh.kmvonline.collegeNotificationFragment.COL_COLLEGE_NOTIFICATION_DAY;
import static es.esy.shashanksingh.kmvonline.collegeNotificationFragment.COL_COLLEGE_NOTIFICATION_DEPARTMENT;
import static es.esy.shashanksingh.kmvonline.collegeNotificationFragment.COL_COLLEGE_NOTIFICATION_DESCRIPTION;
import static es.esy.shashanksingh.kmvonline.collegeNotificationFragment.COL_COLLEGE_NOTIFICATION_MONTH;
import static es.esy.shashanksingh.kmvonline.collegeNotificationFragment.COL_COLLEGE_NOTIFICATION_REMOTE_ID;
import static es.esy.shashanksingh.kmvonline.collegeNotificationFragment.COL_COLLEGE_NOTIFICATION_TEACHER_NAME;
import static es.esy.shashanksingh.kmvonline.collegeNotificationFragment.COL_COLLEGE_NOTIFICATION_TITLE;


/**
 * Created by shashank on 11-Mar-17.
 */

public class SearchResultsActivity extends AppCompatActivity implements  LoaderManager.LoaderCallbacks<Cursor>{
    private ListView t;
    private String TAG=SearchResultsActivity.class.getSimpleName();
    private String query;
    private CollegeNotificationAdapter mAdapter;
    private TextView mEmptyStateView;

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
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);
        t=(ListView) findViewById(R.id.searchResult);
        mAdapter=new CollegeNotificationAdapter(this,null,0);
        mEmptyStateView=(TextView) findViewById(R.id.empty_view_search_results);
        t.setEmptyView(mEmptyStateView);
        t.setAdapter(mAdapter);
        t.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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
                //Creating a shared preference
                SharedPreferences sharedPreferences = SearchResultsActivity.this.getSharedPreferences(Config.SHARED_PREF_LAST_SEARCHED_QUERY, Context.MODE_PRIVATE);
                //Creating editor to store values to shared preferences
                SharedPreferences.Editor editor = sharedPreferences.edit();
                //Adding values to editor
                editor.putString(Config.SEARCHED_QUERY,query);
                //Saving values to editor
                editor.apply();
                Intent i = new Intent(SearchResultsActivity.this, CollegeNotificationSearchDetail.class);
                i.putExtra("CurrentNotification", currentCollegeNotification);
                startActivity(i);
            }
        });
        handleIntent(getIntent());
        if(query==null) {
            SharedPreferences sharedPreferences = SearchResultsActivity.this.getSharedPreferences(Config.SHARED_PREF_LAST_SEARCHED_QUERY, Context.MODE_PRIVATE);
            query = sharedPreferences.getString(Config.SEARCHED_QUERY, "notice");
        }
        mEmptyStateView.setText("No results found for '" + query.replace("''","'") + "'.");

        getSupportActionBar().setTitle("Search result '"+query.replace("''","'")+"'");
        getSupportLoaderManager().initLoader(100, null, this);
    }
    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            query= intent.getStringExtra(SearchManager.QUERY);
            query=query.replace("'","''");
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        //zxcvbnm

        Log.d(TAG, "creating new loader");
        String[] teacherNames={"Dr. Madhu Pruthi",
                "Dr. Rittu Sethi",
                "Mr. Raj Kumar",
                "Mr. P. K. Bhatia",
                "College's Website",
                "Ms. Srishti Vashishtha",
                "Mr. Rakesh Kumar"};
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        StringBuilder selection=new StringBuilder(collegeNotificationContract.collegeNotificationEntry.COLUMN_TEACHER_NAME+" IN (");

        ArrayList<String> dynamicsArgs=new ArrayList<String>();
        for(int j=0;j<teacherNames.length;j++){
            Boolean shouldShow=sharedPref.getBoolean(teacherNames[j],false);
            if(shouldShow){
                selection.append("?,");
                dynamicsArgs.add(teacherNames[j]);
            }
        }

        selection.deleteCharAt(selection.length()-1);
        selection.append(") AND ( "+
                collegeNotificationContract.collegeNotificationEntry.COLUMN_TITLE+" LIKE '%"+query+"%' OR "+
                collegeNotificationContract.collegeNotificationEntry.COLUMN_TEACHER_NAME+" LIKE '%"+query+"%' OR "+
                collegeNotificationContract.collegeNotificationEntry.COLUMN_DESCRIPTION+" LIKE '%"+query+"%' )");
        String finalSelection=selection.toString();
        String[] selectionArgs=new String[dynamicsArgs.size()];
        for(int k=0;k<dynamicsArgs.size();k++)
        {
            selectionArgs[k]=dynamicsArgs.get(k);
        }
        String sortOrder = collegeNotificationContract.collegeNotificationEntry.COLUMN_REMOTE_ID + " DESC";
        //Uri weatherForLocationUri = collegeNotificationContract.collegeNotificationEntry.buildWeatherLocationWithStartDate(
        //        locationSetting, System.currentTimeMillis());
//        if (dynamicsArgs.size()<1) {
//            finalSelection=collegeNotificationContract.collegeNotificationEntry.COLUMN_TEACHER_NAME+" IN (?) AND (  "+
//                    collegeNotificationContract.collegeNotificationEntry.COLUMN_TITLE+" LIKE '%"+query+"%' OR "+
//                    collegeNotificationContract.collegeNotificationEntry.COLUMN_TEACHER_NAME+" LIKE '%"+query+"%' OR "+
//                    collegeNotificationContract.collegeNotificationEntry.COLUMN_DESCRIPTION+" LIKE '%"+query+"%' )";
//            return new CursorLoader(this,
//                    collegeNotificationContract.collegeNotificationEntry.CONTENT_URI,
//                    COLLEGE_NOTIFICATION_COLUMNS,
//                    finalSelection,
//                    //zxcvbnm
//                    new String[]{"Dr. Madhu Pruthi,College's Website"},
//                    sortOrder);
//        }
//        else {
        return new CursorLoader(this,
                collegeNotificationContract.collegeNotificationEntry.CONTENT_URI,
                COLLEGE_NOTIFICATION_COLUMNS,
                finalSelection,
                selectionArgs,
                sortOrder);
    }
//    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);
        Log.d(TAG, "loading finished");
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }
}
