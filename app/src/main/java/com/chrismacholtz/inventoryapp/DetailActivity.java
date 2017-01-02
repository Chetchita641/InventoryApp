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
import java.io.InputStream;

/**
 * Created by SWS Customer on 12/23/2016.
 */

public class DetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String LOG_TAG = DetailActivity.class.getSimpleName();
    private static final int LOADER_ID = 5;
    private TextView mProductNameTextView;
    private ImageView mImageView;
    private TextView mCategoryTextView;
    private TextView mPriceTextView;
    private TextView mQuantityTextView;
    private TextView mEnrouteTextView;
    private LinearLayout mSupplierLayout1;
    private LinearLayout mSupplierLayout2;
    private LinearLayout mSupplierLayout3;
    private TextView mSupplierNameTextView1;
    private TextView mSupplierNameTextView2;
    private TextView mSupplierNameTextView3;
    private TextView mSupplierPriceTextView1;
    private TextView mSupplierPriceTextView2;
    private TextView mSupplierPriceTextView3;

    private Button mSupplierMinus1;
    private Button mSupplierMinus2;
    private Button mSupplierMinus3;
    private Button mSupplierPlus1;
    private Button mSupplierPlus2;
    private Button mSupplierPlus3;
    private TextView mNumOrderTextView1;
    private TextView mNumOrderTextView2;
    private TextView mNumOrderTextView3;
    private int mNumOrder1 = 1;
    private int mNumOrder2 = 1;
    private int mNumOrder3 = 1;
    private float fSupplierPrice1 = 0;
    private float fSupplierPrice2 = 0;
    private float fSupplierPrice3 = 0;
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

        mSupplierLayout1 = (LinearLayout) findViewById(R.id.supplier_layout1);
        mSupplierLayout2 = (LinearLayout) findViewById(R.id.supplier_layout2);
        mSupplierLayout3 = (LinearLayout) findViewById(R.id.supplier_layout3);
        mSupplierNameTextView1 = (TextView) findViewById(R.id.supplier_name1);
        mSupplierNameTextView2 = (TextView) findViewById(R.id.supplier_name2);
        mSupplierNameTextView3 = (TextView) findViewById(R.id.supplier_name3);
        mSupplierPriceTextView1 = (TextView) findViewById(R.id.supplier_price1);
        mSupplierPriceTextView2 = (TextView) findViewById(R.id.supplier_price2);
        mSupplierPriceTextView3 = (TextView) findViewById(R.id.supplier_price3);

        mCurrentItemUri = getIntent().getData();

        getLoaderManager().initLoader(LOADER_ID, null, this);

        //Individual clickListeners for the plus and minus buttons for the ordering. In reality, there are probably only a large variety of single suppliers.
        mSupplierMinus1 = (Button) findViewById(R.id.supplier_minus1);
        mSupplierMinus2 = (Button) findViewById(R.id.supplier_minus2);
        mSupplierMinus3 = (Button) findViewById(R.id.supplier_minus3);
        mSupplierPlus1 = (Button) findViewById(R.id.supplier_plus1);
        mSupplierPlus2 = (Button) findViewById(R.id.supplier_plus2);
        mSupplierPlus3 = (Button) findViewById(R.id.supplier_plus3);
        mNumOrderTextView1 = (TextView) findViewById(R.id.num_order1);
        mNumOrderTextView2 = (TextView) findViewById(R.id.num_order2);
        mNumOrderTextView3 = (TextView) findViewById(R.id.num_order3);

        mSupplierMinus1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                decrement(1);
            }
        });
        mSupplierMinus2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                decrement(2);
            }
        });
        mSupplierMinus3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                decrement(3);
            }
        });
        mSupplierPlus1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                increment(1);
            }
        });
        mSupplierPlus2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                increment(2);
            }
        });
        mSupplierPlus3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                increment(3);
            }
        });

        Button supplierButton1 = (Button) findViewById(R.id.supplier_order1);
        Button supplierButton2 = (Button) findViewById(R.id.supplier_order2);
        Button supplierButton3 = (Button) findViewById(R.id.supplier_order3);
        supplierButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendEmailOrder(1);

            }
        });
        supplierButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendEmailOrder(2);
            }
        });
        supplierButton3.setOnClickListener(new View.OnClickListener() {
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

    /**
     * Edit Item: Edit the details
     * Delete: Delete the item
     */
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

    //Copied from Udacity's Pets project
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

    //Copied from Udacity's Pets project
    private void deleteItem() {
        if (mCurrentItemUri != null) {
            int rowDeleted = getContentResolver().delete(mCurrentItemUri, null, null);
            if (rowDeleted != 0) {
                Toast toast = Toast.makeText(getApplicationContext(), getString(R.string.delete_item_sucess), Toast.LENGTH_SHORT);
                toast.show();
            } else {
                Toast toast = Toast.makeText(getApplicationContext(), getString(R.string.delete_item_error), Toast.LENGTH_SHORT);
                toast.show();
            }
        } else {
            throw new IllegalArgumentException("Error in deleting Item");
        }
        finish();
    }

    //Convert an integer to a matching supplier name
    private String getSupplierName(int index) {
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
                return getString(R.string.unknown_supplier);
        }
    }

    //Convert an integer to a matching category name for the spinner
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

    /**Borrowed and slightly modified from Carlos Andres Jimenez's MyShareImageExample, obtained through the Udacity forums.
     * There's was a bug in which if the user switched too fast, the input streams wouldn't properly close. However, in this state, there may be memory leaks.
     * Hopefully, nothing too big. If your phone blows up from an excessive amount of bitmap data, my bad.
     **/
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
        }
//        finally {
//            try {
//                input.close();
//            } catch (IOException ioe) {
//
//            }
//        }
    }

    //Separate method to lower the desired order and update the textView. Also includes checks against negative numbers and zero.
    private void decrement(int index) {
        switch (index) {
            case (1):
                mNumOrder1--;
                if (mNumOrder1 < 1) {
                    mNumOrder1 = 1;
                }
                mNumOrderTextView1.setText("" + mNumOrder1);
                mSupplierPriceTextView1.setText("$" + String.format("%.2f", (mNumOrder1 * fSupplierPrice1)));
                break;
            case (2):
                mNumOrder2--;
                if (mNumOrder2 < 1) {
                    mNumOrder2 = 1;
                }
                mNumOrderTextView2.setText("" + mNumOrder2);
                mSupplierPriceTextView2.setText("$" + String.format("%.2f", (mNumOrder2 * fSupplierPrice2)));
                break;
            case (3):
                mNumOrder3--;
                if (mNumOrder3 < 1) {
                    mNumOrder3 = 1;
                }
                mNumOrderTextView3.setText("" + mNumOrder3);
                mSupplierPriceTextView3.setText("$" + String.format("%.2f", (mNumOrder3 * fSupplierPrice3)));
        }
    }

    //Separate method to increase the desired order and update the textView. Also includes checks against higher than 2-digit numbers.
    private void increment(int index) {
        switch (index) {
            case (1):
                mNumOrder1++;
                if (mNumOrder1 > 99) {
                    mNumOrder1 = 99;
                }
                mNumOrderTextView1.setText("" + mNumOrder1);
                mSupplierPriceTextView1.setText("$" + String.format("%.2f", (mNumOrder1 * fSupplierPrice1)));
                break;
            case (2):
                mNumOrder2++;
                if (mNumOrder2 > 99) {
                    mNumOrder2 = 99;
                }
                mNumOrderTextView2.setText("" + mNumOrder2);
                mSupplierPriceTextView2.setText("$" + String.format("%.2f", (mNumOrder2 * fSupplierPrice2)));
                break;
            case (3):
                mNumOrder3++;
                if (mNumOrder3 > 99) {
                    mNumOrder3 = 99;
                }
                mNumOrderTextView3.setText("" + mNumOrder3);
                mSupplierPriceTextView3.setText("$" + String.format("%.2f", (mNumOrder3 * fSupplierPrice3)));
        }
    }

    //Pulls out the information and fills it all into an email via an implicit intent
    private void sendEmailOrder(int index) {
        String productName = mProductNameTextView.getText().toString();
        String supplierName = "";
        int quantity = 0;
        float supplierPrice = 0;
        switch (index) {
            case (1):
                supplierName = mSupplierNameTextView1.getText().toString();
                quantity = mNumOrder1;
                supplierPrice = fSupplierPrice1;
                break;
            case (2):
                supplierName = mSupplierNameTextView2.getText().toString();
                quantity = mNumOrder2;
                supplierPrice = fSupplierPrice2;
                break;
            case (3):
                supplierName = mSupplierNameTextView3.getText().toString();
                quantity = mNumOrder3;
                supplierPrice = fSupplierPrice3;
                break;
        }

        addEnroute(quantity);

        String totalPriceString = "$" + String.format("%.2f", (supplierPrice * quantity));
        //Sample output: Please order 5 more of Pokemon Y from LexCorp, at a cost of $60.45. Thank you
        String orderSummary = "Please order " + quantity + " more of " + productName
                + " from " + supplierName + ", at a cost of " + totalPriceString + "\nThank you";

        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:")); // only email apps should handle this
        intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.order_summary_email_subject) + supplierName);
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
            Toast toast = Toast.makeText(this, getString(R.string.enroute_update_success), Toast.LENGTH_SHORT);
            toast.show();
        } else {
            Toast toast = Toast.makeText(this, getString(R.string.enroute_update_error), Toast.LENGTH_SHORT);
            toast.show();
        }
        mEnrouteTextView.setText(getString(R.string.enroute) + " " + mEnroute);
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
                ItemEntry.COLUMN_ITEM_SUPPLIER_1,
                ItemEntry.COLUMN_ITEM_SUPPLIER_1_PRICE,
                ItemEntry.COLUMN_ITEM_SUPPLIER_2,
                ItemEntry.COLUMN_ITEM_SUPPLIER_2_PRICE,
                ItemEntry.COLUMN_ITEM_SUPPLIER_3,
                ItemEntry.COLUMN_ITEM_SUPPLIER_3_PRICE
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
            int supplierName1ColumnIndex = data.getColumnIndex(ItemEntry.COLUMN_ITEM_SUPPLIER_1);
            int supplierName2ColumnIndex = data.getColumnIndex(ItemEntry.COLUMN_ITEM_SUPPLIER_2);
            int supplierName3ColumnIndex = data.getColumnIndex(ItemEntry.COLUMN_ITEM_SUPPLIER_3);
            int supplierPrice1ColumnIndex = data.getColumnIndex(ItemEntry.COLUMN_ITEM_SUPPLIER_1_PRICE);
            int supplierPrice2ColumnIndex = data.getColumnIndex(ItemEntry.COLUMN_ITEM_SUPPLIER_2_PRICE);
            int supplierPrice3ColumnIndex = data.getColumnIndex(ItemEntry.COLUMN_ITEM_SUPPLIER_3_PRICE);

            String productName = data.getString(productNameColumnIndex);
            int categoryInt = data.getInt(categoryColumnIndex);
            float fProductPrice = data.getFloat(priceColumnIndex);
            String priceString = getString(R.string.sale_price) + String.format("%.2f", fProductPrice);
            String quantityString = getString(R.string.qty) + "  " + data.getInt(quantityColumnIndex);
            mEnroute = data.getInt(enrouteColumnIndex);
            String enrouteString = getString(R.string.enroute) + " " + mEnroute;

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

            //If a supplier is not provided, then don't display
            if (data.getString(supplierName1ColumnIndex) != null) {
                String supplierName1 = getSupplierName(data.getInt(supplierName1ColumnIndex));
                fSupplierPrice1 = data.getFloat(supplierPrice1ColumnIndex);
                String supplierPriceString1 = "$" + String.format("%.2f", fSupplierPrice1);
                mSupplierNameTextView1.setText(supplierName1);
                mSupplierPriceTextView1.setText(supplierPriceString1);
            } else {
                mSupplierLayout1.setVisibility(View.GONE);
            }

            if (data.getString(supplierName2ColumnIndex) != null) {
                String supplierName2 = getSupplierName(data.getInt(supplierName2ColumnIndex));
                fSupplierPrice2 = data.getFloat(supplierPrice2ColumnIndex);
                String supplierPriceString2 = "$" + String.format("%.2f", fSupplierPrice2);
                mSupplierNameTextView2.setText(supplierName2);
                mSupplierPriceTextView2.setText(supplierPriceString2);
            } else {
                mSupplierLayout2.setVisibility(View.GONE);
            }

            if (data.getString(supplierName3ColumnIndex) != null) {
                String supplierName3 = getSupplierName(data.getInt(supplierName3ColumnIndex));
                fSupplierPrice3 = data.getFloat(supplierPrice3ColumnIndex);
                String supplierPriceString3 = "$" + String.format("%.2f", fSupplierPrice3);
                mSupplierNameTextView3.setText(supplierName3);
                mSupplierPriceTextView3.setText(supplierPriceString3);
            } else {
                mSupplierLayout3.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        loader.reset();
    }
}
