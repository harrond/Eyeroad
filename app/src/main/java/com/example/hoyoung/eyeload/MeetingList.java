package com.example.hoyoung.eyeload;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

/**
 * Created by ATIV_NINE on 2016-11-08.
 */

public class MeetingList extends ListActivity {
    private static final int ACTIVITY_CREATE = 0;
    private static final int ACTIVITY_EDIT = 1;

    private static final int INSERT_ID = Menu.FIRST;
    private static final int DELETE_ID = Menu.FIRST + 1;

    private MeetingDbAdapter mDbHelper;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.meeting_list);
        mDbHelper = new MeetingDbAdapter(this);
        mDbHelper.open();
        fillData();
        registerForContextMenu(getListView());
        findViewById(R.id.addMeeting).setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v) {
                        Intent intent = new Intent(MeetingList.this, MeetingEdit.class);
                        startActivity(intent);
                    }
                }
        );
    }

    private void fillData() {
        Cursor meetingCursor = mDbHelper.fetchAllMeeting();
        startManagingCursor(meetingCursor);

        // Create an array to specify the fields we want to display in the list (only TITLE)
        String[] from = new String[]{MeetingDbAdapter.KEY_TITLE};

        // and an array of the fields we want to bind those fields to (in this case just text2)
        int[] to = new int[]{R.id.text2};

        // Now create a simple cursor adapter and set it to display
        SimpleCursorAdapter meetings =
                new SimpleCursorAdapter(this, R.layout.meeting_row, meetingCursor, from, to);
        setListAdapter(meetings);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(0, INSERT_ID, 0, R.string.menu_insert);
        return true;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch (item.getItemId()) {
            case INSERT_ID:
                createMeeting();
                return true;
        }

        return super.onMenuItemSelected(featureId, item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, DELETE_ID, 0, R.string.menu_delete);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case DELETE_ID:
                AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                mDbHelper.deleteMeeting(info.id);
                fillData();
                return true;
        }
        return super.onContextItemSelected(item);
    }

    private void createMeeting() {
        Intent i = new Intent(this, MeetingEdit.class);
        startActivityForResult(i, ACTIVITY_CREATE);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Intent i = new Intent(this, MeetingEdit.class);
        i.putExtra(MeetingDbAdapter.KEY_ROWID, id);
        startActivityForResult(i, ACTIVITY_EDIT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        fillData();
    }
}