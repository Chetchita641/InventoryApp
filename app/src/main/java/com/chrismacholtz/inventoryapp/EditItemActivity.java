package com.chrismacholtz.inventoryapp;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
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
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.chrismacholtz.inventoryapp.data.ItemContract.ItemEntry;

/**
 * Created by SWS Customer on 12/23/2016.
 */

public class EditItemActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    //TODO: Set up adding fields
    //TODO: Store information in database
    private static final int LOADER_ID = 6;

    private EditText mProductNameEditText;
    private Spinner mCategorySpinner;
    private int mCategory = 0;
    private EditText mPriceEditText;
    private EditText mQuantityEditText;
    private EditText mEnrouteEditText;

    private LinearLayout mProviderLayout1;
    private LinearLayout mProviderLayout2;
    private LinearLayout mProviderLayout3;
    private Spinner mProviderSpinner1;
    private Spinner mProviderSpinner2;
    private Spinner mProviderSpinner3;
    private int mProvider1 = 0;
    private int mProvider2 = 0;
    private int mProvider3 = 0;
    private EditText mProviderPriceEditText1;
    private EditText mProviderPriceEditText2;
    private EditText mProviderPriceEditText3;

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

        mProviderSpinner1 = (Spinner) findViewById(R.id.provider_spinner1);
        mProviderSpinner2 = (Spinner) findViewById(R.id.provider_spinner2);
        mProviderSpinner3 = (Spinner) findViewById(R.id.provider_spinner3);
        mProviderPriceEditText1 = (EditText) findViewById(R.id.provider_edit_price1);
        mProviderPriceEditText2 = (EditText) findViewById(R.id.provider_edit_price2);
        mProviderPriceEditText3 = (EditText) findViewById(R.id.provider_edit_price3);

        mProductNameEditText.setOnTouchListener(mTouchListener);
        mPriceEditText.setOnTouchListener(mTouchListener);
        mQuantityEditText.setOnTouchListener(mTouchListener);
        mEnrouteEditText.setOnTouchListener(mTouchListener);
        mProviderSpinner1.setOnTouchListener(mTouchListener);
        mProviderSpinner2.setOnTouchListener(mTouchListener);
        mProviderSpinner3.setOnTouchListener(mTouchListener);
        mProviderPriceEditText1.setOnTouchListener(mTouchListener);
        mProviderPriceEditText2.setOnTouchListener(mTouchListener);
        mProviderPriceEditText3.setOnTouchListener(mTouchListener);

        mCurrentItemUri = getIntent().getData();
        if (mCurrentItemUri == null) {
            setTitle("Add Item");
        } else {
            setTitle("Edit Item");
            getLoaderManager().initLoader(LOADER_ID, null, this);
        }

        setupSpinner();
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
        ArrayAdapter providerSpinnerAdapter = ArrayAdapter.createFromResource(this, R.array.array_suppliers, android.R.layout.simple_spinner_item);
        providerSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        mProviderSpinner1.setAdapter(providerSpinnerAdapter);
        mProviderSpinner2.setAdapter(providerSpinnerAdapter);
        mProviderSpinner3.setAdapter(providerSpinnerAdapter);

        ArrayAdapter categorySpinnerAdapter = ArrayAdapter.createFromResource(this, R.array.array_categories, android.R.layout.simple_spinner_item);
        categorySpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        mCategorySpinner.setAdapter(categorySpinnerAdapter);

        mProviderSpinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    mProvider1 = getProviderInt(selection);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //Is there supposed to be something here?
            }
        });
        mProviderSpinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    mProvider2 = getProviderInt(selection);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //Is there supposed to be something here?
            }
        });
        mProviderSpinner3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    mProvider3 = getProviderInt(selection);
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
        String providerPriceString1 = mProviderPriceEditText1.getText().toString().trim();
        String providerPriceString2 = mProviderPriceEditText2.getText().toString().trim();
        String providerPriceString3 = mProviderPriceEditText3.getText().toString().trim();

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

        float providerPrice1 = 0;
        float providerPrice2 = 0;
        float providerPrice3 = 0;

        if (!mProviderPriceEditText1.getText().toString().isEmpty())
            providerPrice1 = Float.parseFloat(providerPriceString1);

        if (!mProviderPriceEditText2.getText().toString().isEmpty())
            providerPrice2 = Float.parseFloat(providerPriceString2);

        if (!mProviderPriceEditText3.getText().toString().isEmpty())
            providerPrice3 = Float.parseFloat(providerPriceString3);

        if (mProvider1 > 0 && providerPrice1 > 0f) {
            if (mProvider1 > 0 ^ providerPrice1 > 0f) {
                Toast toast = Toast.makeText(this, "Error saving Provider #1", Toast.LENGTH_SHORT);
                toast.show();
                return;
            } else {
                values.put(ItemEntry.COLUMN_ITEM_PROVIDER_1, mProvider1);
                values.put(ItemEntry.COLUMN_ITEM_PROVIDER_1_PRICE, providerPrice1);
            }
        }
        if (mProvider2 > 0 && providerPrice2 > 0f) {
            if (mProvider2 > 0 ^ providerPrice2 > 0f) {
                Toast toast = Toast.makeText(this, "Error saving Provider #2", Toast.LENGTH_SHORT);
                toast.show();
                return;
            } else {
                values.put(ItemEntry.COLUMN_ITEM_PROVIDER_2, mProvider2);
                values.put(ItemEntry.COLUMN_ITEM_PROVIDER_2_PRICE, providerPrice2);
            }
        }
        if (mProvider3 > 0 && providerPrice3 > 0f) {
            if (mProvider3 > 0 ^ providerPrice3 > 0f) {
                Toast toast = Toast.makeText(this, "Error saving Provider price", Toast.LENGTH_SHORT);
                toast.show();
                return;
            } else {
                values.put(ItemEntry.COLUMN_ITEM_PROVIDER_3, mProvider3);
                values.put(ItemEntry.COLUMN_ITEM_PROVIDER_3_PRICE, providerPrice3);
            }
        }

        values.put(ItemEntry.COLUMN_ITEM_NAME, productName);
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

    private int getProviderInt(String selection) {
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

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                ItemEntry._ID,
                ItemEntry.COLUMN_ITEM_NAME,
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
            int priceColumnIndex = data.getColumnIndex(ItemEntry.COLUMN_ITEM_PRICE);
            int quantityColumnIndex = data.getColumnIndex(ItemEntry.COLUMN_ITEM_QUANTITY);
            int enrouteColumnIndex = data.getColumnIndex(ItemEntry.COLUMN_ITEM_ENROUTE);
            int providerName1ColumnIndex = data.getColumnIndex(ItemEntry.COLUMN_ITEM_PROVIDER_1);
            int providerName2ColumnIndex = data.getColumnIndex(ItemEntry.COLUMN_ITEM_PROVIDER_2);
            int providerName3ColumnIndex = data.getColumnIndex(ItemEntry.COLUMN_ITEM_PROVIDER_3);
            int providerPrice1ColumnIndex = data.getColumnIndex(ItemEntry.COLUMN_ITEM_PROVIDER_1_PRICE);
            int providerPrice2ColumnIndex = data.getColumnIndex(ItemEntry.COLUMN_ITEM_PROVIDER_2_PRICE);
            int providerPrice3ColumnIndex = data.getColumnIndex(ItemEntry.COLUMN_ITEM_PROVIDER_3_PRICE);

            mProductNameEditText.setText(data.getString(productNameColumnIndex));
            mPriceEditText.setText(data.getString(priceColumnIndex));
            mQuantityEditText.setText(data.getString(quantityColumnIndex));
            mEnrouteEditText.setText(data.getString(enrouteColumnIndex));

            if (data.getInt(providerName1ColumnIndex) != 0) {
                mProviderSpinner1.setSelection(data.getInt(providerName1ColumnIndex));
                mProviderPriceEditText1.setText(data.getString(providerPrice1ColumnIndex));
            }
            if (data.getInt(providerName2ColumnIndex) != 0) {
                mProviderSpinner2.setSelection(data.getInt(providerName2ColumnIndex));
                mProviderPriceEditText2.setText(data.getString(providerPrice2ColumnIndex));
            }
            if (data.getInt(providerName3ColumnIndex) != 0) {
                mProviderSpinner3.setSelection(data.getInt(providerName3ColumnIndex));
                mProviderPriceEditText3.setText(data.getString(providerPrice3ColumnIndex));
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        loader.reset();
    }
}
