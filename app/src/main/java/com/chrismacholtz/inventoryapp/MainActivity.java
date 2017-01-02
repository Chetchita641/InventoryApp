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
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.chrismacholtz.inventoryapp.data.ItemContract.ItemEntry;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private ItemCursorAdapter mCursorAdapter;
    private ListView mListView;
    private TextView mEmptyView;
    private TextView mCategoryAll;
    private TextView mCategoryTextView1;
    private TextView mCategoryTextView2;
    private TextView mCategoryTextView3;
    private TextView mCategoryTextView4;
    private int mCurrentCategory = 0;

    //Each of these arrays are matched by index
    private ArrayList<Integer> mQuantityArray;
    private ArrayList<Integer> mEnrouteArray;
    private ArrayList<Long> mUpdateIds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);

        mCursorAdapter = new ItemCursorAdapter(this, null);
        mListView = (ListView) findViewById(R.id.list_view_main);
        mEmptyView = (TextView) findViewById(R.id.empty_view_main);
        mListView.setEmptyView(mEmptyView);

        mCategoryAll = (TextView) findViewById(R.id.menu_item0_main);
        mCategoryTextView1 = (TextView) findViewById(R.id.menu_item1_main);
        mCategoryTextView2 = (TextView) findViewById(R.id.menu_item2_main);
        mCategoryTextView3 = (TextView) findViewById(R.id.menu_item3_main);
        mCategoryTextView4 = (TextView) findViewById(R.id.menu_item4_main);

        //Arrays allowing to shift enroute numbers to quantity numbers via "Shipment Arrival" in the menu bar
        mQuantityArray = new ArrayList<>();
        mEnrouteArray = new ArrayList<>();
        mUpdateIds = new ArrayList<>();

        //A homemade Tab layout
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

        //Click = detailed view pops up
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Uri currentItemUri = ContentUris.withAppendedId(ItemEntry.CONTENT_URI, id);
                Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                intent.setData(currentItemUri);
                startActivity(intent);
            }
        });

        //Click = sales view pops up
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.sale_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SalesActivity.class);
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

    /**
     * Add Item: Adds item via EditItemActivity
     * Shipment Arrival: Transfers enroute numbers to current quantity numbers. Used to simulate a delivery truck coming in.
     * Insert Dummy Data: Inserts a selection of fake data in a variety of categories
     * Delete All: Y'know, if you want to watch the world burn, go ahead
     **/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case (R.id.action_add):
                Intent intent = new Intent(MainActivity.this, EditItemActivity.class);
                intent.setData(null);
                startActivity(intent);
                return true;
            case (R.id.action_shipment_arrival):
                shipmentArrival();
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

    //Takes the products' enroute numbers and transfers them into the current quantity numbers. Used to simulate a delivery truck coming in.
    private void shipmentArrival() {
        ContentValues values = new ContentValues();
        Uri currentItemUri;
        for (int i = 0; i < mUpdateIds.size(); i++) {
            int newQuantity = mQuantityArray.get(i) + mEnrouteArray.get(i);

            values.put(ItemEntry.COLUMN_ITEM_QUANTITY, newQuantity);
            values.put(ItemEntry.COLUMN_ITEM_ENROUTE, 0);
            currentItemUri = ContentUris.withAppendedId(ItemEntry.CONTENT_URI, mUpdateIds.get(i));
            int rowUpdated = getContentResolver().update(currentItemUri, values, null, null);
            if (rowUpdated != 0) {
                Toast toast = Toast.makeText(this, getString(R.string.shipment_arrival_success), Toast.LENGTH_SHORT);
                toast.show();
            } else {
                Toast toast = Toast.makeText(this, getString(R.string.shipment_arrival_error), Toast.LENGTH_SHORT);
                toast.show();
            }
        }
    }

    //Long list of dummy data. Sorry, video games was all I could think of at the time. Also, a bit of a Nintendo fan here.
    private void insertDummyData() {
        ContentValues values = new ContentValues();
        values.put(ItemEntry.COLUMN_ITEM_NAME, "Nintendo Switch");
        values.put(ItemEntry.COLUMN_ITEM_QUANTITY, 1);
        values.put(ItemEntry.COLUMN_ITEM_PRICE, 249.99f);
        values.put(ItemEntry.COLUMN_ITEM_ENROUTE, 4);
        values.put(ItemEntry.COLUMN_ITEM_SUPPLIER_1, 2);
        values.put(ItemEntry.COLUMN_ITEM_SUPPLIER_1_PRICE, 205.47f);
        values.put(ItemEntry.COLUMN_ITEM_CATEGORY, 2);
        getContentResolver().insert(ItemEntry.CONTENT_URI, values);
        values.clear();

        values.put(ItemEntry.COLUMN_ITEM_NAME, "Metroid Federation Force");
        values.put(ItemEntry.COLUMN_ITEM_QUANTITY, 24);
        values.put(ItemEntry.COLUMN_ITEM_PRICE, 39.99f);
        values.put(ItemEntry.COLUMN_ITEM_ENROUTE, 4);
        values.put(ItemEntry.COLUMN_ITEM_SUPPLIER_1, 4);
        values.put(ItemEntry.COLUMN_ITEM_SUPPLIER_1_PRICE, 32.76f);
        values.put(ItemEntry.COLUMN_ITEM_CATEGORY, 1);
        getContentResolver().insert(ItemEntry.CONTENT_URI, values);
        values.clear();

        values.put(ItemEntry.COLUMN_ITEM_NAME, "Pokemon Omega Ruby");
        values.put(ItemEntry.COLUMN_ITEM_QUANTITY, 65);
        values.put(ItemEntry.COLUMN_ITEM_PRICE, 39.99f);
        values.put(ItemEntry.COLUMN_ITEM_ENROUTE, 10);
        values.put(ItemEntry.COLUMN_ITEM_SUPPLIER_1, 3);
        values.put(ItemEntry.COLUMN_ITEM_SUPPLIER_1_PRICE, 32.76f);
        values.put(ItemEntry.COLUMN_ITEM_CATEGORY, 1);
        getContentResolver().insert(ItemEntry.CONTENT_URI, values);
        values.clear();

        values.put(ItemEntry.COLUMN_ITEM_NAME, "Nintendo 3DS Carrying Case");
        values.put(ItemEntry.COLUMN_ITEM_QUANTITY, 5);
        values.put(ItemEntry.COLUMN_ITEM_PRICE, 19.99f);
        values.put(ItemEntry.COLUMN_ITEM_ENROUTE, 10);
        values.put(ItemEntry.COLUMN_ITEM_SUPPLIER_1, 6);
        values.put(ItemEntry.COLUMN_ITEM_SUPPLIER_1_PRICE, 32.76f);
        values.put(ItemEntry.COLUMN_ITEM_CATEGORY, 3);
        getContentResolver().insert(ItemEntry.CONTENT_URI, values);
        values.clear();

        values.put(ItemEntry.COLUMN_ITEM_NAME, "Yo Kai Watch Backpack");
        values.put(ItemEntry.COLUMN_ITEM_QUANTITY, 6);
        values.put(ItemEntry.COLUMN_ITEM_PRICE, 14.99f);
        values.put(ItemEntry.COLUMN_ITEM_ENROUTE, 2);
        values.put(ItemEntry.COLUMN_ITEM_SUPPLIER_1, 1);
        values.put(ItemEntry.COLUMN_ITEM_SUPPLIER_1_PRICE, 4.76f);
        values.put(ItemEntry.COLUMN_ITEM_CATEGORY, 4);
        getContentResolver().insert(ItemEntry.CONTENT_URI, values);
        values.clear();
    }

    //A homemade tab layout. Contains the LoaderManager call.
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

    //Copied from Udacity's Pets project
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

    //Sssssssssssssssss.... KABOOM!
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

        //Check if a category has been clicked. If so, only load that category.
        String selection = null;
        String[] selectionArgs = null;
        if (mCurrentCategory != 0) {
            selection = ItemEntry.COLUMN_ITEM_CATEGORY + "=?";
            selectionArgs = new String[]{"" + mCurrentCategory};
        }
        return new ItemCursorLoader(this, ItemEntry.CONTENT_URI, projection, selection, selectionArgs, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        int quantityColumnIndex = data.getColumnIndex(ItemEntry.COLUMN_ITEM_QUANTITY);
        int enrouteColumnIndex = data.getColumnIndex(ItemEntry.COLUMN_ITEM_ENROUTE);
        int idColumnIndex = data.getColumnIndex(ItemEntry._ID);

        //Go through each of the cursor entries. If an entry is a repeat, then find the index and put its information in the corresponding mQuantity
        // and mEnroute ArrayLists
        while (data.moveToNext()) {
            long id = data.getLong(idColumnIndex);
            if (!mUpdateIds.contains(id)) {
                mUpdateIds.add(id);
                mQuantityArray.add(data.getInt(quantityColumnIndex));
                mEnrouteArray.add(data.getInt(enrouteColumnIndex));
            }
        }

        mCursorAdapter = new ItemCursorAdapter(this, data);
        mListView.setAdapter(mCursorAdapter);

        mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);
    }

}
