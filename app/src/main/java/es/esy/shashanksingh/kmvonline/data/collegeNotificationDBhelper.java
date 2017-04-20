/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package es.esy.shashanksingh.kmvonline.data;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import es.esy.shashanksingh.kmvonline.data.collegeNotificationContract.collegeNotificationEntry;

/**
 * Database helper for College Notification app. Manages database creation and version management.
 */
public class collegeNotificationDBhelper extends SQLiteOpenHelper {
    /**
     * Name of the database file
     */
    private static final String DATABASE_NAME = "notifications.db";
    /**
     * Database version. If you change the database schema, you must increment the database version.
     */
    private static final int DATABASE_VERSION = 1;

    public collegeNotificationDBhelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * This is called when the database is created for the first time.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create a String that contains the SQL statement to create the  table
        String SQL_CREATE_PETS_TABLE = "CREATE TABLE " + collegeNotificationEntry.TABLE_NAME + " ("
                + collegeNotificationEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + collegeNotificationEntry.COLUMN_REMOTE_ID + " INTEGER NOT NULL, "
                + collegeNotificationEntry.COLUMN_TITLE + " TEXT NOT NULL, "
                + collegeNotificationEntry.COLUMN_DESCRIPTION + " TEXT NOT NULL, "
                + collegeNotificationEntry.COLUMN_DEPARTMENT + " INTEGER NOT NULL, "
                + collegeNotificationEntry.COLUMN_DAY + " INTEGER NOT NULL ,"
                + collegeNotificationEntry.COLUMN_MONTH + " INTEGER NOT NULL ,"
                +collegeNotificationEntry.COLUMN_TEACHER_NAME + " TEXT NOT NULL );";
        // Execute the SQL statement
        db.execSQL(SQL_CREATE_PETS_TABLE);
    }
    /**
     * This is called when the database needs to be upgraded.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + collegeNotificationEntry.TABLE_NAME);
        onCreate(db);
    }
}