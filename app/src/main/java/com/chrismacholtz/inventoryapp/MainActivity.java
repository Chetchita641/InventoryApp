package com.chrismacholtz.inventoryapp;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.chrismacholtz.inventoryapp.data.ItemContract.ItemEntry;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String ADD_ITEM = "Add an Item";
    private ItemCursorAdapter mCursorAdapter;
    private ListView mListView;
    private TextView mEmptyView;
    private TextView mCategoryAll;
    private TextView mCategoryTextView1;
    private TextView mCategoryTextView2;
    private TextView mCategoryTextView3;
    private TextView mCategoryTextView4;
    private int mCurrentCategory = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);

        mCursorAdapter = new ItemCursorAdapter(this, null);
        mListView = (ListView) findViewById(R.id.list_view);
        mEmptyView = (TextView) findViewById(R.id.empty_view);
        mListView.setEmptyView(mEmptyView);

        mCategoryAll = (TextView) findViewById(R.id.menu_item0);
        mCategoryTextView1 = (TextView) findViewById(R.id.menu_item1);
        mCategoryTextView2 = (TextView) findViewById(R.id.menu_item2);
        mCategoryTextView3 = (TextView) findViewById(R.id.menu_item3);
        mCategoryTextView4 = (TextView) findViewById(R.id.menu_item4);

        mCategoryAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeCategory(0);
            }
        });

        mCategoryTextView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeCategory(1);
            }
        });
        mCategoryTextView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeCategory(2);
            }
        });
        mCategoryTextView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeCategory(3);
            }
        });
        mCategoryTextView4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeCategory(4);
            }
        });

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Uri currentItemUri = ContentUris.withAppendedId(ItemEntry.CONTENT_URI, id);
                Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                intent.setData(currentItemUri);
                startActivity(intent);
            }
        });

        changeCategory(mCurrentCategory);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case (R.id.action_add):
                Intent intent = new Intent(MainActivity.this, EditItemActivity.class);
                intent.setData(null);
                startActivity(intent);
                return true;
            case (R.id.action_insert_dummy_data):
                insertDummyData();
                return true;
            case (R.id.action_delete_all):
                showDeleteConfirmationDialog();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void insertDummyData() {
        ContentValues values = new ContentValues();
        values.put(ItemEntry.COLUMN_ITEM_NAME, "Nintendo Switch");
        values.put(ItemEntry.COLUMN_ITEM_QUANTITY, 1);
        values.put(ItemEntry.COLUMN_ITEM_PRICE, 249.99f);
        values.put(ItemEntry.COLUMN_ITEM_ENROUTE, 4);
        values.put(ItemEntry.COLUMN_ITEM_PROVIDER_1, 2);
        values.put(ItemEntry.COLUMN_ITEM_PROVIDER_1_PRICE, 205.47f);
        values.put(ItemEntry.COLUMN_ITEM_CATEGORY, 2);

        Uri newUri = getContentResolver().insert(ItemEntry.CONTENT_URI, values);
    }

    private void changeCategory(int index) {
        final int DARK = getResources().getColor(R.color.colorPrimaryDark);
        final int SELECTION = getResources().getColor(R.color.colorAccent);
        mCategoryAll.setBackgroundColor(DARK);
        mCategoryTextView1.setBackgroundColor(DARK);
        mCategoryTextView2.setBackgroundColor(DARK);
        mCategoryTextView3.setBackgroundColor(DARK);
        mCategoryTextView4.setBackgroundColor(DARK);

        switch (index) {
            case (0):
                mCategoryAll.setBackgroundColor(SELECTION);
                mCurrentCategory = 0;
                break;
            case (1):
                mCategoryTextView1.setBackgroundColor(SELECTION);
                mCurrentCategory = 1;
                break;
            case (2):
                mCategoryTextView2.setBackgroundColor(SELECTION);
                mCurrentCategory = 2;
                break;
            case (3):
                mCategoryTextView3.setBackgroundColor(SELECTION);
                mCurrentCategory = 3;
                break;
            case (4):
                mCategoryTextView4.setBackgroundColor(SELECTION);
                mCurrentCategory = 4;
        }

        getLoaderManager().initLoader(mCurrentCategory, null, this);
    }

    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_all_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the pet.
                deleteAll();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deleteAll() {
        getContentResolver().delete(ItemEntry.CONTENT_URI, null, null);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                ItemEntry._ID,
                ItemEntry.COLUMN_ITEM_NAME,
                ItemEntry.COLUMN_ITEM_QUANTITY,
                ItemEntry.COLUMN_ITEM_ENROUTE,
                ItemEntry.COLUMN_ITEM_PRICE};

        String selection;
        String[] selectionArgs;
        if (mCurrentCategory != 0) {
            selection = ItemEntry.COLUMN_ITEM_CATEGORY + "=?";
            selectionArgs = new String[]{"" + mCurrentCategory};
        } else {
            selection = null;
            selectionArgs = null;
        }
        return new ItemCursorLoader(this, ItemEntry.CONTENT_URI, projection, selection, selectionArgs, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCursorAdapter = new ItemCursorAdapter(this, data);
        mListView.setAdapter(mCursorAdapter);

        mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);
    }

}
