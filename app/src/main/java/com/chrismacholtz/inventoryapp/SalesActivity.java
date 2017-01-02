package com.chrismacholtz.inventoryapp;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.chrismacholtz.inventoryapp.data.ItemContract.ItemEntry;

import java.util.ArrayList;

public class SalesActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    //The tax rate in Tucson, AZ
    private static final float TAX = 0.081f;

    private ItemCursorAdapter mCursorAdapter;
    private ListView mListView;
    private TextView mEmptyView;
    private TextView mCategoryAll;
    private TextView mCategoryTextView1;
    private TextView mCategoryTextView2;
    private TextView mCategoryTextView3;
    private TextView mCategoryTextView4;
    private int mCurrentCategory = 0;

    private float mTaxTotal = 0f;
    private float mGrandTotal = 0f;

    //Both ArrayLists have corresponding indexes. mUpdateIds keeps a log of the products choosen, while mQuantityArray logs in the quantities
    private ArrayList<Long> mUpdateIds;
    private ArrayList<Integer> mQuantityArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sales_layout);

        mCursorAdapter = new ItemCursorAdapter(this, null);
        mListView = (ListView) findViewById(R.id.list_view_sales);
        mEmptyView = (TextView) findViewById(R.id.empty_view_sales);
        mListView.setEmptyView(mEmptyView);

        mCategoryAll = (TextView) findViewById(R.id.menu_item0_sales);
        mCategoryTextView1 = (TextView) findViewById(R.id.menu_item1_sales);
        mCategoryTextView2 = (TextView) findViewById(R.id.menu_item2_sales);
        mCategoryTextView3 = (TextView) findViewById(R.id.menu_item3_sales);
        mCategoryTextView4 = (TextView) findViewById(R.id.menu_item4_sales);

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

        mUpdateIds = new ArrayList<>();
        mQuantityArray = new ArrayList<>();

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String productName = mCursorAdapter.getProductName(position);
                int quantity = mCursorAdapter.getQuantity(position);
                float price = mCursorAdapter.getPrice(position);
                addToSale(productName, quantity, price, id);
            }
        });

        Button buyButton = (Button) findViewById(R.id.buy_button);
        buyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buyProducts();
            }
        });

        changeCategory(mCurrentCategory);
    }

    //A homemade tab layout
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

    //Finds the information on the clicked item and creates an invoice. Also updates the Grand Total with tax included.
    private void addToSale(String productName, int quantity, float price, long id) {
        TextView productNameSalesTextView = (TextView) findViewById(R.id.sales_product_name);
        TextView totalSalesTextView = (TextView) findViewById(R.id.sales_total);
        TextView taxTextView = (TextView) findViewById(R.id.sales_tax);
        TextView grandTotalTextView = (TextView) findViewById(R.id.sales_grand_total);

        //Calculate the hypothetical quantity after purchasing. If a user does choose to purchase, adjust the real quantities.
        int afterQuantity;
        int index;

        if (!mUpdateIds.contains(id)) {
            mUpdateIds.add(id);
            index = mUpdateIds.indexOf(id);
            afterQuantity = quantity - 1;
            mQuantityArray.add(afterQuantity);
        } else {
            index = mUpdateIds.indexOf(id);
            afterQuantity = mQuantityArray.get(index) - 1;
            mQuantityArray.set(index, afterQuantity);
        }

        if (afterQuantity > -1) {
            productNameSalesTextView.append(productName + "\n");
            totalSalesTextView.append("$" + String.format("%.2f", price) + "\n");

            mTaxTotal += price * TAX;
            taxTextView.setText("Tax " + (TAX * 100) + "%: " + String.format("%.2f", mTaxTotal));
            mGrandTotal += price + mTaxTotal;
            grandTotalTextView.setText("Total: $" + String.format("%.2f", mGrandTotal));
        } else {
            mQuantityArray.set(index, 0);
            Toast toast = Toast.makeText(this, "Out of Stock", Toast.LENGTH_SHORT);
            toast.show();
        }

    }

    //Once the user presses the buy button, this method is called. mUpdateIds is cycled through to
    //find the products choosen and the new quantities are stored in the database
    private void buyProducts() {
        ContentValues values = new ContentValues();
        Uri currentItemUri;
        for (int i = 0; i < mUpdateIds.size(); i++) {
            values.put(ItemEntry.COLUMN_ITEM_QUANTITY, mQuantityArray.get(i));
            currentItemUri = ContentUris.withAppendedId(ItemEntry.CONTENT_URI, mUpdateIds.get(i));
            int rowUpdated = getContentResolver().update(currentItemUri, values, null, null);
            if (rowUpdated != 0) {
                Toast toast = Toast.makeText(this, "Quantities updated", Toast.LENGTH_SHORT);
                toast.show();
            } else {
                Toast toast = Toast.makeText(this, "Error updating quantities", Toast.LENGTH_SHORT);
                toast.show();
            }
        }

        finish();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                ItemEntry._ID,
                ItemEntry.COLUMN_ITEM_NAME,
                ItemEntry.COLUMN_ITEM_QUANTITY,
                ItemEntry.COLUMN_ITEM_ENROUTE,
                ItemEntry.COLUMN_ITEM_PRICE};

        //Find if a category tab is selected. If so, only load that category.
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
