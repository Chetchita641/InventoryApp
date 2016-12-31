package com.chrismacholtz.inventoryapp;

import android.app.LoaderManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chrismacholtz.inventoryapp.data.ItemContract.ItemEntry;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by SWS Customer on 12/23/2016.
 */

public class DetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String LOG_TAG = DetailActivity.class.getSimpleName();
    private static final int LOADER_ID = 5;
    private static final String EDIT_ITEM = "Edit Item";
    private TextView mProductNameTextView;
    private ImageView mImageView;
    private TextView mCategoryTextView;
    private TextView mPriceTextView;
    private TextView mQuantityTextView;
    private TextView mEnrouteTextView;
    private LinearLayout mProviderLayout1;
    private LinearLayout mProviderLayout2;
    private LinearLayout mProviderLayout3;
    private TextView mProviderNameTextView1;
    private TextView mProviderNameTextView2;
    private TextView mProviderNameTextView3;
    private TextView mProviderPriceTextView1;
    private TextView mProviderPriceTextView2;
    private TextView mProviderPriceTextView3;
    private Uri mCurrentItemUri;

    //TODO: Add ordering intent (email)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_view);

        mProductNameTextView = (TextView) findViewById(R.id.product_name_detail);
        mImageView = (ImageView) findViewById(R.id.image_detail);
        mCategoryTextView = (TextView) findViewById(R.id.category_detail);
        mPriceTextView = (TextView) findViewById(R.id.price_detail);
        mQuantityTextView = (TextView) findViewById(R.id.quantity_detail);
        mEnrouteTextView = (TextView) findViewById(R.id.enroute_detail);

        mProviderLayout1 = (LinearLayout) findViewById(R.id.provider_layout1);
        mProviderLayout2 = (LinearLayout) findViewById(R.id.provider_layout2);
        mProviderLayout3 = (LinearLayout) findViewById(R.id.provider_layout3);
        mProviderNameTextView1 = (TextView) findViewById(R.id.provider_name1);
        mProviderNameTextView2 = (TextView) findViewById(R.id.provider_name2);
        mProviderNameTextView3 = (TextView) findViewById(R.id.provider_name3);
        mProviderPriceTextView1 = (TextView) findViewById(R.id.provider_price1);
        mProviderPriceTextView2 = (TextView) findViewById(R.id.provider_price2);
        mProviderPriceTextView3 = (TextView) findViewById(R.id.provider_price3);

        mCurrentItemUri = getIntent().getData();

        getLoaderManager().initLoader(LOADER_ID, null, this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_edit:
                Intent intent = new Intent(DetailActivity.this, EditItemActivity.class);
                intent.setData(mCurrentItemUri);
                startActivity(intent);
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                showDeleteConfirmationDialog();
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the pet.
                deleteItem();
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

    private void deleteItem() {
        if (mCurrentItemUri != null) {
            int rowDeleted = getContentResolver().delete(mCurrentItemUri, null, null);
            if (rowDeleted != 0) {
                Toast toast = Toast.makeText(getApplicationContext(), "Item Deleted", Toast.LENGTH_SHORT);
                toast.show();
            } else {
                Toast toast = Toast.makeText(getApplicationContext(), "Error in deleting Item", Toast.LENGTH_SHORT);
                toast.show();
            }
        } else {
            throw new IllegalArgumentException("Error in deleting Item");
        }
        finish();
    }

    private String getProviderName(int index) {
        switch (index) {
            case (1):
                return getString(R.string.supplier1);
            case (2):
                return getString(R.string.supplier2);
            case (3):
                return getString(R.string.supplier3);
            case (4):
                return getString(R.string.supplier4);
            case (5):
                return getString(R.string.supplier5);
            case (6):
                return getString(R.string.supplier6);
            default:
                return "Unknown";
        }
    }

    private String getCategoryName(int index) {
        switch (index) {
            case (1):
                return getString(R.string.category1);
            case (2):
                return getString(R.string.category2);
            case (3):
                return getString(R.string.category3);
            case (4):
                return getString(R.string.category4);
            default:
                return getString(R.string.category_all);
        }
    }

    public Bitmap getBitmapFromUri(Uri uri) {
        if (uri == null || uri.toString().isEmpty())
            return null;

        // Get the dimensions of the View
        int targetW = mImageView.getWidth();
        int targetH = mImageView.getHeight();

        InputStream input = null;
        try {
            input = this.getContentResolver().openInputStream(uri);

            // Get the dimensions of the bitmap
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(input, null, bmOptions);
            input.close();

            int photoW = bmOptions.outWidth;
            int photoH = bmOptions.outHeight;

            // Determine how much to scale down the image
            int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

            // Decode the image file into a Bitmap sized to fill the View
            bmOptions.inJustDecodeBounds = false;
            bmOptions.inSampleSize = scaleFactor;
            bmOptions.inPurgeable = true;

            input = getContentResolver().openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(input, null, bmOptions);
            input.close();
            return bitmap;

        } catch (FileNotFoundException fne) {
            Log.e(LOG_TAG, "Failed to load image.", fne);
            return null;
        } catch (Exception e) {
            Log.e(LOG_TAG, "Failed to load image.", e);
            return null;
        } finally {
            try {
                input.close();
            } catch (IOException ioe) {

            }
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                ItemEntry._ID,
                ItemEntry.COLUMN_ITEM_NAME,
                ItemEntry.COLUMN_ITEM_IMAGE_URI,
                ItemEntry.COLUMN_ITEM_CATEGORY,
                ItemEntry.COLUMN_ITEM_PRICE,
                ItemEntry.COLUMN_ITEM_QUANTITY,
                ItemEntry.COLUMN_ITEM_ENROUTE,
                ItemEntry.COLUMN_ITEM_PROVIDER_1,
                ItemEntry.COLUMN_ITEM_PROVIDER_1_PRICE,
                ItemEntry.COLUMN_ITEM_PROVIDER_2,
                ItemEntry.COLUMN_ITEM_PROVIDER_2_PRICE,
                ItemEntry.COLUMN_ITEM_PROVIDER_3,
                ItemEntry.COLUMN_ITEM_PROVIDER_3_PRICE
        };

        return new ItemCursorLoader(this, mCurrentItemUri, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data.moveToFirst()) {
            int productNameColumnIndex = data.getColumnIndex(ItemEntry.COLUMN_ITEM_NAME);
            int productImageColumnIndex = data.getColumnIndex(ItemEntry.COLUMN_ITEM_IMAGE_URI);
            int categoryColumnIndex = data.getColumnIndex(ItemEntry.COLUMN_ITEM_CATEGORY);
            int priceColumnIndex = data.getColumnIndex(ItemEntry.COLUMN_ITEM_PRICE);
            int quantityColumnIndex = data.getColumnIndex(ItemEntry.COLUMN_ITEM_QUANTITY);
            int enrouteColumnIndex = data.getColumnIndex(ItemEntry.COLUMN_ITEM_ENROUTE);
            int providerName1ColumnIndex = data.getColumnIndex(ItemEntry.COLUMN_ITEM_PROVIDER_1);
            int providerName2ColumnIndex = data.getColumnIndex(ItemEntry.COLUMN_ITEM_PROVIDER_2);
            int providerName3ColumnIndex = data.getColumnIndex(ItemEntry.COLUMN_ITEM_PROVIDER_3);
            int providerPrice1ColumnIndex = data.getColumnIndex(ItemEntry.COLUMN_ITEM_PROVIDER_1_PRICE);
            int providerPrice2ColumnIndex = data.getColumnIndex(ItemEntry.COLUMN_ITEM_PROVIDER_2_PRICE);
            int providerPrice3ColumnIndex = data.getColumnIndex(ItemEntry.COLUMN_ITEM_PROVIDER_3_PRICE);

            String productName = data.getString(productNameColumnIndex);
            Uri productImageUri = Uri.parse(data.getString(productImageColumnIndex));
            int categoryInt = data.getInt(categoryColumnIndex);
            float fProductPrice = data.getFloat(priceColumnIndex);
            String priceString = "Sale Price: $" + String.format("%.2f", fProductPrice);
            String quantityString = "Qty:  " + data.getInt(quantityColumnIndex);
            String enrouteString = "Enroute:  " + data.getInt(enrouteColumnIndex);

            mProductNameTextView.setText(productName);
            mCategoryTextView.setText(getCategoryName(categoryInt));
            mPriceTextView.setText(priceString);
            mQuantityTextView.setText(quantityString);
            mEnrouteTextView.setText(enrouteString);

            mImageView.setImageBitmap(getBitmapFromUri(productImageUri));

            if (data.getString(providerName1ColumnIndex) != null) {
                String providerName1 = getProviderName(data.getInt(providerName1ColumnIndex));
                float fProviderPrice1 = data.getFloat(providerPrice1ColumnIndex);
                String providerPriceString1 = "$" + String.format("%.2f", fProviderPrice1);
                mProviderNameTextView1.setText(providerName1);
                mProviderPriceTextView1.setText(providerPriceString1);
            } else {
                mProviderLayout1.setVisibility(View.GONE);
            }

            if (data.getString(providerName2ColumnIndex) != null) {
                String providerName2 = getProviderName(data.getInt(providerName2ColumnIndex));
                float fProviderPrice2 = data.getFloat(providerPrice2ColumnIndex);
                String providerPriceString2 = "$" + String.format("%.2f", fProviderPrice2);
                mProviderNameTextView2.setText(providerName2);
                mProviderPriceTextView2.setText(providerPriceString2);
            } else {
                mProviderLayout2.setVisibility(View.GONE);
            }

            if (data.getString(providerName3ColumnIndex) != null) {
                String providerName3 = getProviderName(data.getInt(providerName3ColumnIndex));
                float fProviderPrice3 = data.getFloat(providerPrice3ColumnIndex);
                String providerPriceString3 = "$" + String.format("%.2f", fProviderPrice3);
                mProviderNameTextView3.setText(providerName3);
                mProviderPriceTextView3.setText(providerPriceString3);
            } else {
                mProviderLayout3.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        loader.reset();
    }
}
