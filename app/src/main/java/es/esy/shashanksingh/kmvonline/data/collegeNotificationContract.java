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

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public final class collegeNotificationContract {
    // To prevent someone from accidentally instantiating the contract class, give it an empty constructor.
    private collegeNotificationContract() {}
    /**
     * The "Content authority" is a name for the entire content provider, similar to the
     * relationship between a domain name and its website.  A convenient string to use for the
     * content authority is the package name for the app, which is guaranteed to be unique on the
     * device.
     */
    public static final String CONTENT_AUTHORITY = "es.esy.shashanksingh.kmvonline";
    /**
     * Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
     * the content provider.
     */
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_COLLEGE_NOTIFICATIONS = "collegeNotifications";

    public static final class collegeNotificationEntry implements BaseColumns {
        /** The content URI to access the data in the provider */
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_COLLEGE_NOTIFICATIONS);
        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of notifications.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_COLLEGE_NOTIFICATIONS;
        /**
         * The MIME type of the {@link #CONTENT_URI} for a single notification.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_COLLEGE_NOTIFICATIONS;
        /** Name of database table  */
        public final static String TABLE_NAME = "collegeNotifications";
        /** Name of database table's row */
        public final static String _ID = BaseColumns._ID;
        public final static String COLUMN_REMOTE_ID ="rid";
        public final static String COLUMN_TITLE ="title";
        public final static String COLUMN_DESCRIPTION = "description";
        public final static String COLUMN_DEPARTMENT = "department";
        public final static String COLUMN_DAY = "day";
        public final static String COLUMN_MONTH = "month";
        public final static String COLUMN_TEACHER_NAME = "teacherName";

    }

}

