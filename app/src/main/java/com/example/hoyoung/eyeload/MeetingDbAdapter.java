/*
 * Copyright (C) 2008 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.example.hoyoung.eyeload;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Simple meetings database access helper class. Defines the basic CRUD operations
 * for the meetings example, and gives the ability to list all meetings as well as
 * retrieve or modify a specific meetings.
 * <p>
 * This has been improved from the first version of this tutorial through the
 * addition of better error handling and also using returning a Cursor instead
 * of using a collection of inner classes (which is less scalable and not
 * recommended).
 */
public class MeetingDbAdapter {

    public static final String KEY_TITLE = "title";
    public static final String KEY_BODY = "body";
    public static final String KEY_ROWID = "_id";

    private static final String TAG = "MeetingDbAdapter";
    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;

    /**
     * Database creation sql statement
     */
    private static final String DATABASE_CREATE =
            "create table meetings (_id integer primary key autoincrement, "
                    + "title text not null, body text not null);";

    private static final String DATABASE_NAME = "data";
    private static final String DATABASE_TABLE = "meetings";
    private static final int DATABASE_VERSION = 2;

    private final Context mCtx;

    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {

            db.execSQL(DATABASE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS meetings");
            onCreate(db);
        }
    }

    /**
     * Constructor - takes the context to allow the database to be
     * opened/created
     *
     * @param ctx the Context within which to work
     */
    public MeetingDbAdapter(Context ctx) {
        this.mCtx = ctx;
    }

    /**
     * Open the meetings database. If it cannot be opened, try to create a new
     * instance of the database. If it cannot be created, throw an exception to
     * signal the failure
     *
     * @return this (self reference, allowing this to be chained in an
     * initialization call)
     * @throws SQLException if the database could be neither opened or created
     */
    public MeetingDbAdapter open() throws SQLException {
        mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        mDbHelper.close();
    }


    /**
     * Create a new meeting using the title and body provided. If the meeting is
     * successfully created return the new rowId for that meeting, otherwise return
     * a -1 to indicate failure.
     *
     * @param title the title of the meeting
     * @param body  the body of the meeting
     * @return rowId or -1 if failed
     */


    public long createMeeting(String title, String body) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_TITLE, title);
        initialValues.put(KEY_BODY, body);
        //initialValues.put(KEY_ICON_ID, img);

        return mDb.insert(DATABASE_TABLE, null, initialValues);
    }

    /**
     * Delete the meeting with the given rowId
     *
     * @param rowId id of meeting to delete
     * @return true if deleted, false otherwise
     */
    public boolean deleteMeeting(long rowId) {

        return mDb.delete(DATABASE_TABLE, KEY_ROWID + "=" + rowId, null) > 0;
    }

    /**
     * Return a Cursor over the list of all meeting in the database
     *
     * @return Cursor over all meeting
     */
    public Cursor fetchAllMeeting() {

        return mDb.query(DATABASE_TABLE, new String[]{KEY_ROWID, KEY_TITLE,
                KEY_BODY}, null, null, null, null, null);
    }

    /**
     * Return a Cursor positioned at the meeting that matches the given rowId
     *
     * @param rowId id of meeting to retrieve
     * @return Cursor positioned to matching meeting, if found
     * @throws SQLException if meeting could not be found/retrieved
     */
    public Cursor fetchMeeting(long rowId) throws SQLException {

        Cursor mCursor =

                mDb.query(true, DATABASE_TABLE, new String[]{KEY_ROWID,
                                KEY_TITLE, KEY_BODY}, KEY_ROWID + "=" + rowId, null,
                        null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;

    }

    /**
     * Update the meeting using the details provided. The meeting to be updated is
     * specified using the rowId, and it is altered to use the title and body
     * values passed in
     *
     * @param rowId id of meeting to update
     * @param title value to set meeting title to
     * @param body  value to set meeting body to
     * @return true if the meeting was successfully updated, false otherwise
     */
    public boolean updateMeeting(long rowId, String title, String body) {
        ContentValues args = new ContentValues();
        args.put(KEY_TITLE, title);
        args.put(KEY_BODY, body);

        return mDb.update(DATABASE_TABLE, args, KEY_ROWID + "=" + rowId, null) > 0;
    }

}
