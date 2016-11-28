package com.example.hoyoung.eyeload;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by ATIV_NINE on 2016-11-08.
 */
public class MeetingEdit extends Activity {
    private EditText mTitleText;
    private EditText mBodyText;
    private Long mRowId;
    private MeetingDbAdapter mDbHelper;
    final static int ACT_EDIT = 0;

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDbHelper = new MeetingDbAdapter(this);
        mDbHelper.open();

        setContentView(R.layout.meeting_edit);
        setTitle(R.string.edit_meeting);

        mTitleText = (EditText) findViewById(R.id.meetingname);
        mBodyText = (EditText) findViewById(R.id.meetingcontent);

        Button confirmButton = (Button) findViewById(R.id.register);

        mRowId = (savedInstanceState == null) ? null :
                (Long) savedInstanceState.getSerializable(MeetingDbAdapter.KEY_ROWID);
        if (mRowId == null) {
            Bundle extras = getIntent().getExtras();
            mRowId = extras != null ? extras.getLong(MeetingDbAdapter.KEY_ROWID)
                    : null;
        }
        populateFields();
        confirmButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                setResult(RESULT_OK);
                finish();
            }
        });
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
    }

    private void populateFields() {
        if (mRowId != null) {
            Cursor meeting = mDbHelper.fetchMeeting(mRowId);
            startManagingCursor(meeting);
            mTitleText.setText(meeting.getString(
                    meeting.getColumnIndexOrThrow(MeetingDbAdapter.KEY_TITLE)));
            mBodyText.setText(meeting.getString(
                    meeting.getColumnIndexOrThrow(MeetingDbAdapter.KEY_BODY)));

        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        saveState();
        outState.putSerializable(MeetingDbAdapter.KEY_ROWID, mRowId);
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveState();
    }

    @Override
    protected void onResume() {
        super.onResume();
        populateFields();
    }

    private void saveState() {
        String title = mTitleText.getText().toString();
        String body = mBodyText.getText().toString();

        if (mRowId == null) {
            long id = mDbHelper.createMeeting(title, body);
            if (id > 0) {
                mRowId = id;
            }
        } else {
            mDbHelper.updateMeeting(mRowId, title, body);
        }
    }
}