package com.chrismacholtz.inventoryapp;

import android.app.LoaderManager;
import android.content.ContentValues;
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
import android.widget.Button;
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

    private Button mProviderMinus1;
    private Button mProviderMinus2;
    private Button mProviderMinus3;
    private Button mProviderPlus1;
    private Button mProviderPlus2;
    private Button mProviderPlus3;
    private TextView mNumOrderTextView1;
    private TextView mNumOrderTextView2;
    private TextView mNumOrderTextView3;
    private int mNumOrder1 = 1;
    private int mNumOrder2 = 1;
    private int mNumOrder3 = 1;
    private float fProviderPrice1 = 0;
    private float fProviderPrice2 = 0;
    private float fProviderPrice3 = 0;
    private int mEnroute;

    private Uri mCurrentItemUri;

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

        mProviderMinus1 = (Button) findViewById(R.id.provider_minus1);
        mProviderMinus2 = (Button) findViewById(R.id.provider_minus2);
        mProviderMinus3 = (Button) findViewById(R.id.provider_minus3);
        mProviderPlus1 = (Button) findViewById(R.id.provider_plus1);
        mProviderPlus2 = (Button) findViewById(R.id.provider_plus2);
        mProviderPlus3 = (Button) findViewById(R.id.provider_plus3);

        mNumOrderTextView1 = (TextView) findViewById(R.id.num_order1);
        mNumOrderTextView2 = (TextView) findViewById(R.id.num_order2);
        mNumOrderTextView3 = (TextView) findViewById(R.id.num_order3);

        mProviderMinus1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                decrement(1);
            }
        });
        mProviderMinus2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                decrement(2);
            }
        });
        mProviderMinus3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                decrement(3);
            }
        });

        mProviderPlus1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                increment(1);
            }
        });
        mProviderPlus2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                increment(2);
            }
        });
        mProviderPlus3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                increment(3);
            }
        });

        Button providerButton1 = (Button) findViewById(R.id.provider_order1);
        Button providerButton2 = (Button) findViewById(R.id.provider_order2);
        Button providerButton3 = (Button) findViewById(R.id.provider_order3);

        providerButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendEmailOrder(1);

            }
        });

        providerButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendEmailOrder(2);
            }
        });

        providerButton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendEmailOrder(3);
            }
        });

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

    private void decrement(int index) {
        switch (index) {
            case (1):
                mNumOrder1--;
                if (mNumOrder1 < 1) {
                    mNumOrder1 = 1;
                }
                mNumOrderTextView1.setText("" + mNumOrder1);
                mProviderPriceTextView1.setText("$" + String.format("%.2f", (mNumOrder1 * fProviderPrice1)));
                break;
            case (2):
                mNumOrder2--;
                if (mNumOrder2 < 1) {
                    mNumOrder2 = 1;
                }
                mNumOrderTextView2.setText("" + mNumOrder2);
                mProviderPriceTextView2.setText("$" + String.format("%.2f", (mNumOrder2 * fProviderPrice2)));
                break;
            case (3):
                mNumOrder3--;
                if (mNumOrder3 < 1) {
                    mNumOrder3 = 1;
                }
                mNumOrderTextView3.setText("" + mNumOrder3);
                mProviderPriceTextView3.setText("$" + String.format("%.2f", (mNumOrder3 * fProviderPrice3)));
        }
    }

    private void increment(int index) {
        switch (index) {
            case (1):
                mNumOrder1++;
                if (mNumOrder1 > 99) {
                    mNumOrder1 = 99;
                }
                mNumOrderTextView1.setText("" + mNumOrder1);
                mProviderPriceTextView1.setText("$" + String.format("%.2f", (mNumOrder1 * fProviderPrice1)));
                break;
            case (2):
                mNumOrder2++;
                if (mNumOrder2 > 99) {
                    mNumOrder2 = 99;
                }
                mNumOrderTextView2.setText("" + mNumOrder2);
                mProviderPriceTextView2.setText("$" + String.format("%.2f", (mNumOrder2 * fProviderPrice2)));
                break;
            case (3):
                mNumOrder3++;
                if (mNumOrder3 > 99) {
                    mNumOrder3 = 99;
                }
                mNumOrderTextView3.setText("" + mNumOrder3);
                mProviderPriceTextView3.setText("$" + String.format("%.2f", (mNumOrder3 * fProviderPrice3)));
        }
    }

    private void sendEmailOrder(int index) {
        String productName = mProductNameTextView.getText().toString();
        String providerName = "";
        int quantity = 0;
        float providerPrice = 0;
        switch (index) {
            case (1):
                providerName = mProviderNameTextView1.getText().toString();
                quantity = mNumOrder1;
                providerPrice = fProviderPrice1;
                break;
            case (2):
                providerName = mProviderNameTextView2.getText().toString();
                quantity = mNumOrder2;
                providerPrice = fProviderPrice2;
                break;
            case (3):
                providerName = mProviderNameTextView3.getText().toString();
                quantity = mNumOrder3;
                providerPrice = fProviderPrice3;
                break;
        }

        addEnroute(quantity);

        String totalPriceString = "$" + String.format("%.2f", (providerPrice * quantity));
        String orderSummary = "Please order " + quantity + " more of " + productName
                + " from " + providerName + ", at a cost of " + totalPriceString + "\nThank you";

        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:")); // only email apps should handle this
        intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.order_summary_email_subject) + providerName);
        intent.putExtra(Intent.EXTRA_TEXT, orderSummary);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    private void addEnroute(int addOrder) {
        mEnroute += addOrder;

        ContentValues values = new ContentValues();
        values.put(ItemEntry.COLUMN_ITEM_ENROUTE, mEnroute);

        int rowUpdated = getContentResolver().update(mCurrentItemUri, values, null, null);
        if (rowUpdated != 0) {
            Toast toast = Toast.makeText(this, "Enroute updated", Toast.LENGTH_SHORT);
            toast.show();
        } else {
            Toast toast = Toast.makeText(this, "Error updating Enroute", Toast.LENGTH_SHORT);
            toast.show();
        }
        mEnrouteTextView.setText("Enroute:  " + mEnroute);
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
            int categoryInt = data.getInt(categoryColumnIndex);
            float fProductPrice = data.getFloat(priceColumnIndex);
            String priceString = "Sale Price: $" + String.format("%.2f", fProductPrice);
            String quantityString = "Qty:  " + data.getInt(quantityColumnIndex);
            mEnroute = data.getInt(enrouteColumnIndex);
            String enrouteString = "Enroute:  " + mEnroute;

            Uri productImageUri = null;
            if (data.getString(productImageColumnIndex) != null) {
                productImageUri = Uri.parse(data.getString(productImageColumnIndex));
                mImageView.setImageBitmap(getBitmapFromUri(productImageUri));
            }

            mProductNameTextView.setText(productName);
            mCategoryTextView.setText(getCategoryName(categoryInt));
            mPriceTextView.setText(priceString);
            mQuantityTextView.setText(quantityString);
            mEnrouteTextView.setText(enrouteString);

            if (data.getString(providerName1ColumnIndex) != null) {
                String providerName1 = getProviderName(data.getInt(providerName1ColumnIndex));
                fProviderPrice1 = data.getFloat(providerPrice1ColumnIndex);
                String providerPriceString1 = "$" + String.format("%.2f", fProviderPrice1);
                mProviderNameTextView1.setText(providerName1);
                mProviderPriceTextView1.setText(providerPriceString1);
            } else {
                mProviderLayout1.setVisibility(View.GONE);
            }

            if (data.getString(providerName2ColumnIndex) != null) {
                String providerName2 = getProviderName(data.getInt(providerName2ColumnIndex));
                fProviderPrice2 = data.getFloat(providerPrice2ColumnIndex);
                String providerPriceString2 = "$" + String.format("%.2f", fProviderPrice2);
                mProviderNameTextView2.setText(providerName2);
                mProviderPriceTextView2.setText(providerPriceString2);
            } else {
                mProviderLayout2.setVisibility(View.GONE);
            }

            if (data.getString(providerName3ColumnIndex) != null) {
                String providerName3 = getProviderName(data.getInt(providerName3ColumnIndex));
                fProviderPrice3 = data.getFloat(providerPrice3ColumnIndex);
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
