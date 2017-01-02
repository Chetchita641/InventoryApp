package com.chrismacholtz.inventoryapp;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.chrismacholtz.inventoryapp.data.ItemContract.ItemEntry;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import static com.chrismacholtz.inventoryapp.data.ItemProvider.LOG_TAG;

/**
 * Created by SWS Customer on 12/23/2016.
 */

public class EditItemActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int LOADER_ID = 6;
    private static final int PICK_IMAGE_REQUEST = 0;
    private EditText mProductNameEditText;
    private Spinner mCategorySpinner;
    private int mCategory = 0;
    private EditText mPriceEditText;
    private EditText mQuantityEditText;
    private EditText mEnrouteEditText;
    private Spinner mSupplierSpinner1;
    private Spinner mSupplierSpinner2;
    private Spinner mSupplierSpinner3;
    private int mSupplier1 = 0;
    private int mSupplier2 = 0;
    private int mSupplier3 = 0;
    private EditText mSupplierPriceEditText1;
    private EditText mSupplierPriceEditText2;
    private EditText mSupplierPriceEditText3;
    private ImageView mImageView;
    private Uri mImageUri;
    private Uri mCurrentItemUri;

    private boolean mItemHasChanged = false;
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mItemHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_edit);

        mProductNameEditText = (EditText) findViewById(R.id.product_name_edit);
        mCategorySpinner = (Spinner) findViewById(R.id.category_spinner);
        mPriceEditText = (EditText) findViewById(R.id.price_edit);
        mQuantityEditText = (EditText) findViewById(R.id.quantity_edit);
        mEnrouteEditText = (EditText) findViewById(R.id.enroute_edit);

        mSupplierSpinner1 = (Spinner) findViewById(R.id.supplier_spinner1);
        mSupplierSpinner2 = (Spinner) findViewById(R.id.supplier_spinner2);
        mSupplierSpinner3 = (Spinner) findViewById(R.id.supplier_spinner3);
        mSupplierPriceEditText1 = (EditText) findViewById(R.id.supplier_edit_price1);
        mSupplierPriceEditText2 = (EditText) findViewById(R.id.supplier_edit_price2);
        mSupplierPriceEditText3 = (EditText) findViewById(R.id.supplier_edit_price3);

        mProductNameEditText.setOnTouchListener(mTouchListener);
        mPriceEditText.setOnTouchListener(mTouchListener);
        mQuantityEditText.setOnTouchListener(mTouchListener);
        mEnrouteEditText.setOnTouchListener(mTouchListener);
        mSupplierSpinner1.setOnTouchListener(mTouchListener);
        mSupplierSpinner2.setOnTouchListener(mTouchListener);
        mSupplierSpinner3.setOnTouchListener(mTouchListener);
        mSupplierPriceEditText1.setOnTouchListener(mTouchListener);
        mSupplierPriceEditText2.setOnTouchListener(mTouchListener);
        mSupplierPriceEditText3.setOnTouchListener(mTouchListener);

        mCurrentItemUri = getIntent().getData();
        if (mCurrentItemUri == null) {
            setTitle("Add Item");
        } else {
            setTitle("Edit Item");
            getLoaderManager().initLoader(LOADER_ID, null, this);
        }

        setupSpinner();

        mImageView = (ImageView) findViewById(R.id.image_add);
        if (mImageUri != null) {
            mImageView.setImageBitmap(getBitmapFromUri(mImageUri));
        } else {
            mImageView.setImageResource(R.drawable.ic_insert_photo_black_48dp);
        }

//        ViewTreeObserver viewTreeObserver = mImageView.getViewTreeObserver();
//        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//            @Override
//            public void onGlobalLayout() {
//                mImageView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
//                mImageView.setImageBitmap(getBitmapFromUri(mImageUri));
//            }
//        });

        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImageSelector();
                mImageView.setImageBitmap(getBitmapFromUri(mImageUri));
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                saveItem();
                finish();
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // If the pet hasn't changed, continue with navigating up to parent activity
                // which is the {@link CatalogActivity}.
                if (!mItemHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditItemActivity.this);
                    return true;
                }

                // Otherwise if there are unsaved changes, setup a dialog to warn the user.
                // Create a click listener to handle the user confirming that
                // changes should be discarded.
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent activity.
                                NavUtils.navigateUpFromSameTask(EditItemActivity.this);
                            }
                        };

                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
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

    @Override
    public void onBackPressed() {
        // If the pet hasn't changed, continue with handling back button press
        if (!mItemHasChanged) {
            super.onBackPressed();
            return;
        }

        // Otherwise if there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that changes should be discarded.
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };

        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    private void setupSpinner() {
        ArrayAdapter supplierSpinnerAdapter = ArrayAdapter.createFromResource(this, R.array.array_suppliers, android.R.layout.simple_spinner_item);
        supplierSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        mSupplierSpinner1.setAdapter(supplierSpinnerAdapter);
        mSupplierSpinner2.setAdapter(supplierSpinnerAdapter);
        mSupplierSpinner3.setAdapter(supplierSpinnerAdapter);

        ArrayAdapter categorySpinnerAdapter = ArrayAdapter.createFromResource(this, R.array.array_categories, android.R.layout.simple_spinner_item);
        categorySpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        mCategorySpinner.setAdapter(categorySpinnerAdapter);

        mSupplierSpinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    mSupplier1 = getSupplierInt(selection);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //Is there supposed to be something here?
            }
        });
        mSupplierSpinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    mSupplier2 = getSupplierInt(selection);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //Is there supposed to be something here?
            }
        });
        mSupplierSpinner3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    mSupplier3 = getSupplierInt(selection);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //Is there supposed to be something here?
            }
        });

        mCategorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    Log.v("mCategory", "" + mCategory);
                    mCategory = getCategoryInt(selection);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void saveItem() {
        String productName = mProductNameEditText.getText().toString().trim();
        String priceString = mPriceEditText.getText().toString().trim();
        String quantityString = mQuantityEditText.getText().toString().trim();
        String enrouteString = mEnrouteEditText.getText().toString().trim();
        String supplierPriceString1 = mSupplierPriceEditText1.getText().toString().trim();
        String supplierPriceString2 = mSupplierPriceEditText2.getText().toString().trim();
        String supplierPriceString3 = mSupplierPriceEditText3.getText().toString().trim();

        if (productName.isEmpty()) {
            Toast toast = Toast.makeText(this, "Product Name Required", Toast.LENGTH_SHORT);
            toast.show();
            return;
        }
        if (priceString.isEmpty() || Float.parseFloat(priceString) < 0) {
            Toast toast = Toast.makeText(this, "Price Required", Toast.LENGTH_SHORT);
            toast.show();
            return;
        }
        if (quantityString.isEmpty() || Integer.parseInt(quantityString) < 0) {
            Toast toast = Toast.makeText(this, "Quantity Required", Toast.LENGTH_SHORT);
            toast.show();
            return;
        }
        if (enrouteString.isEmpty() || Integer.parseInt(enrouteString) < 0) {
            Toast toast = Toast.makeText(this, "Enroute Required", Toast.LENGTH_SHORT);
            toast.show();
            return;
        }

        ContentValues values = new ContentValues();

        float supplierPrice1 = 0;
        float supplierPrice2 = 0;
        float supplierPrice3 = 0;

        if (!mSupplierPriceEditText1.getText().toString().isEmpty())
            supplierPrice1 = Float.parseFloat(supplierPriceString1);

        if (!mSupplierPriceEditText2.getText().toString().isEmpty())
            supplierPrice2 = Float.parseFloat(supplierPriceString2);

        if (!mSupplierPriceEditText3.getText().toString().isEmpty())
            supplierPrice3 = Float.parseFloat(supplierPriceString3);

        if (mSupplier1 > 0 && supplierPrice1 > 0f) {
            if (mSupplier1 > 0 ^ supplierPrice1 > 0f) {
                Toast toast = Toast.makeText(this, getString(R.string.saving_supplier_error) + 1, Toast.LENGTH_SHORT);
                toast.show();
                return;
            } else {
                values.put(ItemEntry.COLUMN_ITEM_SUPPLIER_1, mSupplier1);
                values.put(ItemEntry.COLUMN_ITEM_SUPPLIER_1_PRICE, supplierPrice1);
            }
        }
        if (mSupplier2 > 0 && supplierPrice2 > 0f) {
            if (mSupplier2 > 0 ^ supplierPrice2 > 0f) {
                Toast toast = Toast.makeText(this, getString(R.string.saving_supplier_error) + 2, Toast.LENGTH_SHORT);
                toast.show();
                return;
            } else {
                values.put(ItemEntry.COLUMN_ITEM_SUPPLIER_2, mSupplier2);
                values.put(ItemEntry.COLUMN_ITEM_SUPPLIER_2_PRICE, supplierPrice2);
            }
        }
        if (mSupplier3 > 0 && supplierPrice3 > 0f) {
            if (mSupplier3 > 0 ^ supplierPrice3 > 0f) {
                Toast toast = Toast.makeText(this, getString(R.string.saving_supplier_error) + 3, Toast.LENGTH_SHORT);
                toast.show();
                return;
            } else {
                values.put(ItemEntry.COLUMN_ITEM_SUPPLIER_3, mSupplier3);
                values.put(ItemEntry.COLUMN_ITEM_SUPPLIER_3_PRICE, supplierPrice3);
            }
        }

        values.put(ItemEntry.COLUMN_ITEM_NAME, productName);
        values.put(ItemEntry.COLUMN_ITEM_IMAGE_URI, String.valueOf(mImageUri));
        values.put(ItemEntry.COLUMN_ITEM_CATEGORY, mCategory);
        values.put(ItemEntry.COLUMN_ITEM_PRICE, Float.parseFloat(priceString));
        values.put(ItemEntry.COLUMN_ITEM_QUANTITY, Integer.parseInt(quantityString));
        values.put(ItemEntry.COLUMN_ITEM_ENROUTE, Integer.parseInt(enrouteString));

        Uri newUri;

        if (mCurrentItemUri == null) {
            newUri = getContentResolver().insert(ItemEntry.CONTENT_URI, values);

            if (newUri != null) {
                Toast toast = Toast.makeText(this, "Item Saved", Toast.LENGTH_SHORT);
                toast.show();
            } else {
                Toast toast = Toast.makeText(this, "Error saving Item", Toast.LENGTH_SHORT);
                toast.show();
            }
        } else {
            int rowUpdated = getContentResolver().update(mCurrentItemUri, values, null, null);
            if (rowUpdated != 0) {
                Toast toast = Toast.makeText(this, "Item Updated", Toast.LENGTH_SHORT);
                toast.show();
            } else {
                Toast toast = Toast.makeText(this, "Error updating Item", Toast.LENGTH_SHORT);
                toast.show();
            }
        }
    }

    private int getSupplierInt(String selection) {
        if (selection.equals(getString(R.string.supplier1))) {
            return 1;
        } else if (selection.equals(getString(R.string.supplier2))) {
            return 2;
        } else if (selection.equals(getString(R.string.supplier3))) {
            return 3;
        } else if (selection.equals(getString(R.string.supplier4))) {
            return 4;
        } else if (selection.equals(getString(R.string.supplier5))) {
            return 5;
        } else if (selection.equals(getString(R.string.supplier6))) {
            return 6;
        } else {
            return 0;
        }
    }

    private int getCategoryInt(String selection) {
        if (selection.equals(getString(R.string.category1))) {
            return 1;
        } else if (selection.equals(getString(R.string.category2))) {
            return 2;
        } else if (selection.equals(getString(R.string.category3))) {
            return 3;
        } else if (selection.equals(getString(R.string.category4))) {
            return 4;
        } else {
            return 0;
        }
    }

    public void openImageSelector() {
        Intent intent;

        if (Build.VERSION.SDK_INT < 19) {
            intent = new Intent(Intent.ACTION_GET_CONTENT);
        } else {
            intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
        }

        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        // The ACTION_OPEN_DOCUMENT intent was sent with the request code READ_REQUEST_CODE.
        // If the request code seen here doesn't match, it's the response to some other intent,
        // and the below code shouldn't run at all.

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            // The document selected by the user won't be returned in the intent.
            // Instead, a URI to that document will be contained in the return intent
            // provided to this method as a parameter.  Pull that uri using "resultData.getData()"

            if (resultData != null) {
                mImageUri = resultData.getData();
                Log.i(LOG_TAG, "Uri: " + mImageUri.toString());

                mImageView.setImageBitmap(getBitmapFromUri(mImageUri));
            }
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
            int productImageUriColumnIndex = data.getColumnIndex(ItemEntry.COLUMN_ITEM_IMAGE_URI);
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

            mProductNameEditText.setText(data.getString(productNameColumnIndex));
            mPriceEditText.setText(data.getString(priceColumnIndex));
            mQuantityEditText.setText(data.getString(quantityColumnIndex));
            mEnrouteEditText.setText(data.getString(enrouteColumnIndex));
            mCategorySpinner.setSelection(data.getInt(categoryColumnIndex));

            if (data.getInt(supplierName1ColumnIndex) != 0) {
                mSupplierSpinner1.setSelection(data.getInt(supplierName1ColumnIndex));
                mSupplierPriceEditText1.setText(data.getString(supplierPrice1ColumnIndex));
            }
            if (data.getInt(supplierName2ColumnIndex) != 0) {
                mSupplierSpinner2.setSelection(data.getInt(supplierName2ColumnIndex));
                mSupplierPriceEditText2.setText(data.getString(supplierPrice2ColumnIndex));
            }
            if (data.getInt(supplierName3ColumnIndex) != 0) {
                mSupplierSpinner3.setSelection(data.getInt(supplierName3ColumnIndex));
                mSupplierPriceEditText3.setText(data.getString(supplierPrice3ColumnIndex));
            }

            if (data.getString(productImageUriColumnIndex) != null) {
                mImageUri = Uri.parse(data.getString(productImageUriColumnIndex));
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        loader.reset();
    }
}
