package es.esy.shashanksingh.kmvonline;

import android.app.NotificationManager;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.util.ArrayList;

import es.esy.shashanksingh.kmvonline.data.collegeNotificationContract;
import es.esy.shashanksingh.kmvonline.sync.CollegeNotificationSyncAdapter;


/**
 * Created by shashank on 20-Jan-17.
 */

public  class collegeNotificationFragment extends Fragment implements  LoaderManager.LoaderCallbacks<Cursor>{

    private static final String STATE_POSITION = "selected_pos_state";
    private int selectedPosition = ListView.INVALID_POSITION;
    private static final String REQUEST_URL ="http://shashanksingh.esy.es/php/showNotifications.php";
    //private static final String DELETE_URL ="http://shashanksingh.esy.es/php/deleteNotification.php";
    private static final int COLLEGE_NOTIFICATION_LOADER_ID = 1;
    private MaterialSearchView searchView;
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

    static final int COL_COLLEGE_NOTIFICATION_ID=0;
    static final int COL_COLLEGE_NOTIFICATION_REMOTE_ID=1;
    static final int COL_COLLEGE_NOTIFICATION_TITLE=2;
    static final int COL_COLLEGE_NOTIFICATION_DESCRIPTION=3;
    static final int COL_COLLEGE_NOTIFICATION_DEPARTMENT=4;
    static final int COL_COLLEGE_NOTIFICATION_DAY=5;
    static final int COL_COLLEGE_NOTIFICATION_MONTH=6;
    static final int COL_COLLEGE_NOTIFICATION_TEACHER_NAME=7;

    //Empty Constructor
    public collegeNotificationFragment() {
    }

    private String TAG=collegeNotificationFragment.class.getSimpleName();
    private CollegeNotificationAdapter mAdapter;
    private LinearLayout mEmptyStateView;
    public static SwipeRefreshLayout refreshLayout;
    private ConnectivityManager connMgr;
    private CoordinatorLayout mFabWrapper;
    private NetworkInfo networkInfo;
    private SharedPreferences preferences,sharedPreferences;
    private String user;
    View loadingIndicator;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ((NotificationActivity) getActivity()).setActionBarTitle(getString(R.string.app_name));
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        SharedPreferences.Editor editor=sharedPref.edit();
        //zxcvbnm
        editor.putBoolean("Dr. Madhu Pruthi",true);
        editor.putBoolean("College's Website",true);
        editor.apply();
        setHasOptionsMenu(true);
        connMgr = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        preferences = getActivity().getSharedPreferences(Config.SHARED_PREF_NAME_CACHE, Context.MODE_PRIVATE);
        sharedPreferences = getActivity().getSharedPreferences(Config.SHARED_PREF_NAME_LOGIN_DETAILS, Context.MODE_PRIVATE);
        final View rootView = inflater.inflate(R.layout.college_notification_fragment, container, false);
        mFabWrapper=(CoordinatorLayout) rootView.findViewById(R.id.fragmentWrapper);
        if (savedInstanceState != null && savedInstanceState.containsKey(STATE_POSITION)) {
            selectedPosition = savedInstanceState.getInt(STATE_POSITION);
        }
        mAdapter=new CollegeNotificationAdapter(getActivity(),null,0);
        loadingIndicator = rootView.findViewById(R.id.loading_indicator);
        ListView collegeNotificationListView = (ListView) rootView.findViewById(R.id.college_notification_list);
        mEmptyStateView = (LinearLayout) rootView.findViewById(R.id.empty_view_notification);
        collegeNotificationListView.setEmptyView(mEmptyStateView);
        collegeNotificationListView.setAdapter(mAdapter);


        collegeNotificationListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                //Toast.makeText(getActivity(),"CLICKED : "+position,Toast.LENGTH_SHORT).show();
                Cursor cursor = (Cursor) mAdapter.getItem(position);
                collegeNotification currentCollegeNotification =new collegeNotification(cursor.getInt(COL_COLLEGE_NOTIFICATION_REMOTE_ID),
                        cursor.getString(COL_COLLEGE_NOTIFICATION_TITLE),
                        cursor.getString(COL_COLLEGE_NOTIFICATION_DESCRIPTION),
                        cursor.getInt(COL_COLLEGE_NOTIFICATION_DEPARTMENT),
                        cursor.getInt(COL_COLLEGE_NOTIFICATION_DAY),
                        cursor.getInt(COL_COLLEGE_NOTIFICATION_MONTH),
                        cursor.getString(COL_COLLEGE_NOTIFICATION_TEACHER_NAME));
                Intent i = new Intent(getActivity(), CollegeNotificationDetail.class);
                i.putExtra("CurrentNotification", currentCollegeNotification);
                startActivity(i);
            }
        });

        refreshLayout=(SwipeRefreshLayout) rootView.findViewById(R.id.swipeRefresh);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                networkInfo = connMgr.getActiveNetworkInfo();
                // If there is a network connection,
                if (networkInfo != null && networkInfo.isConnected()) {
                    updateCollegeNotifications();
                }
                else{
                    Snackbar.make(mFabWrapper,R.string.no_internet, Snackbar.LENGTH_LONG).show();
                    refreshLayout.setRefreshing(false);
                }
            }
        });

        rootView.setFocusableInTouchMode(true);
        rootView.requestFocus();
        rootView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event)   {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    Intent homeIntent = new Intent(Intent.ACTION_MAIN);
                    homeIntent.addCategory(Intent.CATEGORY_HOME);
                    homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(homeIntent);
                    return true;
                }
                return false;
            }
        });
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
            getActivity().getMenuInflater().inflate(R.menu.search_menu, menu);
                // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.menu_search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getActivity().getComponentName()));
        EditText searchEditText = (EditText)searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        searchEditText.setTextColor(getResources().getColor(R.color.appWhite));
        searchEditText.setHintTextColor(getResources().getColor(R.color.appWhite));
        ImageView searchMagIcon = (ImageView)searchView.findViewById(android.support.v7.appcompat.R.id.search_button);
        searchMagIcon.setImageResource(R.drawable.ic_search_white_24dp);
        super.onCreateOptionsMenu(menu, inflater);

    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(COLLEGE_NOTIFICATION_LOADER_ID, null, this);
        // Clear all notification
        NotificationManager nMgr = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
        nMgr.cancelAll();
    }


private void updateCollegeNotifications() {
    CollegeNotificationSyncAdapter.syncImmediately(getActivity());
    collegeNotificationFragment.refreshLayout.setRefreshing(false);
}

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        //zxcvbnm

        Log.d(TAG, "creating new loader");
        String[] teacherNames={"Dr. Madhu Pruthi",
                "Dr. Rittu Sethi",
                "Mr. Raj Kumar",
                "Mr. P. K. Bhatia",
                "College's Website",
                "Ms. Srishti Vashishtha",
                "Mr. Rakesh Kumar"};

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
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
        selection.append(")");
        String finalSelection=selection.toString();
        String[] selectionArgs=new String[dynamicsArgs.size()];
        for(int k=0;k<dynamicsArgs.size();k++)
        {
            selectionArgs[k]=dynamicsArgs.get(k);
        }
        String sortOrder = collegeNotificationContract.collegeNotificationEntry.COLUMN_REMOTE_ID + " DESC";
        if (dynamicsArgs.size()<1) {
            finalSelection=collegeNotificationContract.collegeNotificationEntry.COLUMN_TEACHER_NAME+" IN (?)";
            return new CursorLoader(getActivity(),
                    collegeNotificationContract.collegeNotificationEntry.CONTENT_URI,
                    COLLEGE_NOTIFICATION_COLUMNS,
                    finalSelection,
                    new String[]{"Dr. Madhu Pruthi,College's Website"},
                    sortOrder);
        }
        else {
            return new CursorLoader(getActivity(),
                    collegeNotificationContract.collegeNotificationEntry.CONTENT_URI,
                    COLLEGE_NOTIFICATION_COLUMNS,
                    finalSelection,
                    selectionArgs,
                    sortOrder);
        }

        }


    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        loadingIndicator.setVisibility(View.INVISIBLE);
        mAdapter.swapCursor(data);
        Log.d(TAG, "loading finished");
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }

}

