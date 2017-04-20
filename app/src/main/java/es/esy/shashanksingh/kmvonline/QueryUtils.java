package es.esy.shashanksingh.kmvonline;

import android.content.ContentValues;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Vector;

import es.esy.shashanksingh.kmvonline.data.collegeNotificationContract.collegeNotificationEntry;

/**
 * Created by shashank on 08-Nov-16.
 */

public class QueryUtils {

    private static final String LOG_TAG = QueryUtils.class.getSimpleName();
    private static Context mContext;

    private QueryUtils() {
    }

    public static void fetchCollegeNotifications(String requestUrl,Context context) {
        mContext=context;
        Log.v(LOG_TAG, "In fetchCollegeNotifications");
        // Create URL object
        URL url = createUrl(requestUrl);
        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }
        extractFeatureFromJson(jsonResponse,mContext);
    }

    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createUrl(String stringUrl) {
        Log.v(LOG_TAG, "In createUrl");
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem building the URL ", e);
        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private static String makeHttpRequest(URL url) throws IOException {
        Log.v(LOG_TAG, "In makeHttpRequest");
        String jsonResponse = "";
        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            if (urlConnection.getResponseCode() == 200) {
            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the college notifications JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // Closing the input stream could throw an IOException, which is why
                // the makeHttpRequest(URL url) method signature specifies than an IOException
                // could be thrown.
                inputStream.close();
            }
        }
        return jsonResponse;
    }
    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        Log.v(LOG_TAG, "In readFromStream");
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        Log.e(LOG_TAG, output.toString());
        return output.toString();
    }

    public static void extractFeatureFromJson(String collegeNotificationJSON,Context context) {
        // If the JSON string is empty or null, then return early.
        Log.v(LOG_TAG, "In extractFeatureFromJson");
        if (TextUtils.isEmpty(collegeNotificationJSON)) {
            Log.e(LOG_TAG, "Empty college notification JSON string");
            return  ;
        }
        try {
            // Create a JSONObject from the JSON response string
            JSONObject baseJsonResponse = new JSONObject(collegeNotificationJSON);
            // Extract the JSONArray associated with the key called "features",
            // which represents a list
            JSONArray collegeNotificationArray = baseJsonResponse.getJSONArray("collegeNotifications");
            Vector<ContentValues> cVVector = new Vector<ContentValues>(collegeNotificationArray.length());
            // For each notification in the collegeNotificationArray, create an {@link collegeNotification} object
            for (int i = 0; i < collegeNotificationArray.length(); i++) {
                JSONObject currentCollegeNotification = collegeNotificationArray.getJSONObject(i);
                int currentId=currentCollegeNotification.getInt("id");
                String currentTitle = currentCollegeNotification.getString("title");
                String currentDescription = currentCollegeNotification.getString("description");
                // Extract the value for the key called "department"
                int currentDepartment = currentCollegeNotification.getInt("department");
                int currentDay = currentCollegeNotification.getInt("day");
                int currentMonth = currentCollegeNotification.getInt("month");
                String currentTeacherName = currentCollegeNotification.getString("teacherName");
                ContentValues collegeNotificationsValues = new ContentValues();

                collegeNotificationsValues.put(collegeNotificationEntry.COLUMN_REMOTE_ID, currentId);
                collegeNotificationsValues.put(collegeNotificationEntry.COLUMN_TITLE, currentTitle);
                collegeNotificationsValues.put(collegeNotificationEntry.COLUMN_DESCRIPTION, currentDescription);
                collegeNotificationsValues.put(collegeNotificationEntry.COLUMN_DEPARTMENT, currentDepartment);
                collegeNotificationsValues.put(collegeNotificationEntry.COLUMN_DAY, currentDay);
                collegeNotificationsValues.put(collegeNotificationEntry.COLUMN_MONTH, currentMonth);
                collegeNotificationsValues.put(collegeNotificationEntry.COLUMN_TEACHER_NAME, currentTeacherName);
                cVVector.add(collegeNotificationsValues);
            }
            // add to database
            context.getContentResolver().delete(collegeNotificationEntry.CONTENT_URI, null,null);

            if (cVVector.size() > 0) {
                ContentValues[] valuesArray = cVVector.toArray(new ContentValues[cVVector.size()]);
                context.getContentResolver().bulkInsert(collegeNotificationEntry.CONTENT_URI, valuesArray);
            }

        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("QueryUtils", "Problem parsing the notification JSON results", e);
        }
    }
}