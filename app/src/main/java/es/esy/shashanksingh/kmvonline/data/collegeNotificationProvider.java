package es.esy.shashanksingh.kmvonline.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import es.esy.shashanksingh.kmvonline.data.collegeNotificationContract.collegeNotificationEntry;

import static es.esy.shashanksingh.kmvonline.data.collegeNotificationContract.CONTENT_AUTHORITY;


public class collegeNotificationProvider extends ContentProvider {

    public static final String LOG_TAG = collegeNotificationProvider.class.getSimpleName();
    /** URI matcher code for the content URI for the collegeNotifications table */
    private static final int COLLEGE_NOTIFICATIONS = 100;
    /** URI matcher code for the content URI for a single college notification in the collegeNotification table */
    private static final int COLLEGE_NOTIFICATION_ID = 101;
    /**
     * UriMatcher object to match a content URI to a corresponding code.
     * The input passed into the constructor represents the code to return for the root URI.
     * It's common to use NO_MATCH as the input for this case.
     */
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    // Static initializer. This is run the first time anything is called from this class.
    static {
        sUriMatcher.addURI(CONTENT_AUTHORITY, collegeNotificationContract.PATH_COLLEGE_NOTIFICATIONS, COLLEGE_NOTIFICATIONS);
        //In an URI * is used for string and # for number.
        sUriMatcher.addURI(CONTENT_AUTHORITY, collegeNotificationContract.PATH_COLLEGE_NOTIFICATIONS + "/#", COLLEGE_NOTIFICATION_ID);
    }
    /** Database helper object */
    private collegeNotificationDBhelper mDbHelper;

    @Override
    public boolean onCreate() {
        mDbHelper = new collegeNotificationDBhelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        // Get readable database
        SQLiteDatabase database = mDbHelper.getReadableDatabase();
        // This cursor will hold the result of the query
        Cursor cursor;
        // Figure out if the URI matcher can match the URI to a specific code
        int match = sUriMatcher.match(uri);
        switch (match) {
            case COLLEGE_NOTIFICATIONS:
                cursor = database.query(collegeNotificationEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case COLLEGE_NOTIFICATION_ID:
                selection = collegeNotificationEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                cursor = database.query(collegeNotificationEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        // Return the cursor
        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case COLLEGE_NOTIFICATIONS:
                return insertCollegeNotification(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }


    private Uri insertCollegeNotification(Uri uri, ContentValues values) {
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        // Insert the notification with the given values
        long id = database.insert(collegeNotificationEntry.TABLE_NAME, null, values);
        // If the ID is -1, then the insertion failed. Log an error and return null.
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }
        // Notify all listeners that the data has changed for the notification content URI
        getContext().getContentResolver().notifyChange(uri, null);
        // Return the new URI with the ID (of the newly inserted row) appended at the end
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection,
                      String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case COLLEGE_NOTIFICATIONS:
                return updateCollegeNotifications(uri, contentValues, selection, selectionArgs);
            case COLLEGE_NOTIFICATION_ID:
                selection = collegeNotificationEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                return updateCollegeNotifications(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    private int updateCollegeNotifications(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        // Otherwise, get writeable database to update the data
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        // Perform the update on the database and get the number of rows affected
        int rowsUpdated = database.update(collegeNotificationEntry.TABLE_NAME, values, selection, selectionArgs);
        // If 1 or more rows were updated, then notify all listeners that the data at the
        // given URI has changed
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        // Return the number of rows updated
        return rowsUpdated;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Get writeable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        // Track the number of rows that were deleted
        int rowsDeleted;
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case COLLEGE_NOTIFICATIONS:
                // Delete all rows that match the selection and selection args
                rowsDeleted = database.delete(collegeNotificationEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case COLLEGE_NOTIFICATION_ID:
                // Delete a single row given by the ID in the URI
                selection = collegeNotificationEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                rowsDeleted = database.delete(collegeNotificationEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
        // If 1 or more rows were deleted, then notify all listeners that the data at the
        // given URI has changed
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        // Return the number of rows deleted
        return rowsDeleted;
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case COLLEGE_NOTIFICATIONS:
                return collegeNotificationEntry.CONTENT_LIST_TYPE;
            case COLLEGE_NOTIFICATION_ID:
                return collegeNotificationEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case COLLEGE_NOTIFICATIONS:
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        //normalizeDate(value);
                        long _id = db.insert(collegeNotificationEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            default:
                return super.bulkInsert(uri, values);
        }
    }
}
