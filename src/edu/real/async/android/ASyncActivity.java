package edu.real.async.android;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Contacts;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

public class ASyncActivity extends FragmentActivity
        implements AdapterView.OnItemClickListener,
        LoaderManager.LoaderCallbacks<Cursor> {
    /*
     * Defines an array that contains column names to move from the
     * Cursor to the ListView.
     */
    @SuppressLint("InlinedApi")
    private final static String[] FROM_COLUMNS = {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB
                    ? ContactsContract.Contacts.DISPLAY_NAME_PRIMARY
                    : ContactsContract.Contacts.DISPLAY_NAME };

    // Define global mutable variables
    /*
     * Defines an array that contains resource ids for the layout views
     * that get the Cursor column contents. The id is pre-defined in
     * the Android framework, so it is prefaced with "android.R.id"
     */
    private final static int[] TO_IDS = { android.R.id.text1 };

    // Define a ListView object
    ListView mContactsList;
    // Define variables for the contact the user selects
    // The contact's _ID value
    long mContactId;
    // The contact's LOOKUP_KEY
    String mContactKey;
    // A content URI for the selected contact
    Uri mContactUri;
    // An adapter that binds the result Cursor to the ListView
    private SimpleCursorAdapter mCursorAdapter;

    @SuppressLint("InlinedApi")
    private static final String[] PROJECTION = { Contacts._ID,
            Contacts.LOOKUP_KEY,
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB
                    ? ContactsContract.Contacts.DISPLAY_NAME_PRIMARY
                    : ContactsContract.Contacts.DISPLAY_NAME

    };

    // The column index for the _ID column
    private static final int CONTACT_ID_INDEX = 0;
    // The column index for the CONTACT_KEY column
    private static final int CONTACT_KEY_INDEX = 1;

    // Defines the text expression
    @SuppressLint("InlinedApi")
    private static final String SELECTION = Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB
            ? Contacts.DISPLAY_NAME_PRIMARY + " LIKE ?"
            : Contacts.DISPLAY_NAME + " LIKE ?";

    // Defines a variable for the search string
    private String mSearchString;
    // Defines the array to hold values that replace the ?
    private String[] mSelectionArgs = { mSearchString };

    private static final String[] PROJECTION_RAW = {
            ContactsContract.Data._ID,
            ContactsContract.Data.MIMETYPE, ContactsContract.Data.DATA1,
            ContactsContract.Data.DATA2, ContactsContract.Data.DATA3,
            ContactsContract.Data.DATA4, ContactsContract.Data.DATA5,
            ContactsContract.Data.DATA6, ContactsContract.Data.DATA7,
            ContactsContract.Data.DATA8, ContactsContract.Data.DATA9,
            ContactsContract.Data.DATA10, ContactsContract.Data.DATA11,
            ContactsContract.Data.DATA12, ContactsContract.Data.DATA13,
            ContactsContract.Data.DATA14, ContactsContract.Data.DATA15
    };

    // Defines the selection clause
    private static final String SELECTION_RAW = ContactsContract.Data.LOOKUP_KEY
            + " = ?";

    /*
     * Defines a string that specifies a sort order of MIME type
     */
    private static final String SORT_ORDER = ContactsContract.Data.MIMETYPE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_async);

        mContactsList = (ListView) findViewById(R.id.listView1);

        // Gets a CursorAdapter
        mCursorAdapter = new SimpleCursorAdapter(this,
                R.layout.contacts_list_item, null, FROM_COLUMNS, TO_IDS, 0);
        // Sets the adapter for the ListView
        mContactsList.setAdapter(mCursorAdapter);

        // Set the item click listener to be the current fragment.
        mContactsList.setOnItemClickListener(this);

        mSearchString = "";

        // Initializes the loader
        getSupportLoaderManager().initLoader(0, null, this);

        if (false) {
            // Put the result Cursor in the adapter for the ListView
            // mCursorAdapter.swapCursor(contacts_list_loader);

            // Defines the array to hold the search criteria
            String[] mSelectionArgs = { "" };

            /*
             * Defines a variable to contain the selection value. Once
             * you have the Cursor from the Contacts table, and you've
             * selected the desired row, move the row's LOOKUP_KEY
             * value into this variable.
             */
            String mLookupKey = "%";

            // Assigns the selection parameter
            mSelectionArgs[0] = mLookupKey;
            // Starts the query
            CursorLoader mLoader = new CursorLoader(this,
                    ContactsContract.Data.CONTENT_URI, PROJECTION_RAW,
                    SELECTION_RAW, mSelectionArgs, SORT_ORDER);
        }

    }

    @Override
    public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
        /*
         * Makes search string into pattern and stores it in the
         * selection array
         */
        mSelectionArgs[0] = "%"; // + mSearchString + "%";
        // Starts the query
        Loader<Cursor> ret = new CursorLoader(this,
                ContactsContract.Contacts.CONTENT_URI, PROJECTION, SELECTION,
                mSelectionArgs, null);
        return ret;

    }

    @Override
    public void onLoadFinished(Loader<Cursor> arg0, Cursor cursor) {
        // Put the result Cursor in the adapter for the ListView
        mCursorAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> arg0) {
        // Delete the reference to the existing Cursor
        mCursorAdapter.swapCursor(null);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View item, int position,
            long rowID) {
        // Get the Cursor
        Cursor cursor = ((CursorAdapter) parent.getAdapter()).getCursor();
        // Move to the selected contact
        cursor.moveToPosition(position);
        // Get the _ID value
        mContactId = cursor.getLong(CONTACT_ID_INDEX);
        // Get the selected LOOKUP KEY
        mContactKey = cursor.getString(CONTACT_KEY_INDEX);
        // Create the contact's content Uri
        mContactUri = Contacts.getLookupUri(mContactId, mContactKey);
        // You can use mContactUri as the content URI for retrieving
        // the
        // details for a contact.

    }
}
