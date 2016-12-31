package com.chrismacholtz.inventoryapp.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

import com.chrismacholtz.inventoryapp.data.ItemContract.ItemEntry;

/**
 * Created by SWS Customer on 12/23/2016.
 */

public class ItemProvider extends ContentProvider {
    public static final String LOG_TAG = ItemProvider.class.getSimpleName();
    private static final int ITEM_ALL = 100;
    private static final int ITEM_ID = 101;
    private static UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(ItemContract.CONTENT_AUTHORITY, ItemContract.PATH_INVENTORY, ITEM_ALL);
        sUriMatcher.addURI(ItemContract.CONTENT_AUTHORITY, ItemContract.PATH_INVENTORY + "/#", ITEM_ID);
    }

    private ItemDbHelper mDbHelper;

    @Override
    public boolean onCreate() {
        mDbHelper = new ItemDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Cursor cursor;

        int match = sUriMatcher.match(uri);
        switch (match) {
            case ITEM_ALL:
                cursor = db.query(ItemEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case ITEM_ID:
                selection = ItemEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = db.query(ItemEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown Uri" + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case ITEM_ALL:
                return insertItem(uri, contentValues);
            default:
                throw new IllegalArgumentException("Error reading Uri for insert");
        }

    }

    public Uri insertItem(Uri uri, ContentValues contentValues) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        long id = db.insert(ItemEntry.TABLE_NAME, null, contentValues);
        if (id == -1) {
            Log.e(LOG_TAG, "Error saving new entry");
            return null;
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case (ITEM_ALL):
                int rowsDeleted = db.delete(ItemEntry.TABLE_NAME, selection, selectionArgs);
                if (rowsDeleted > 0)
                    getContext().getContentResolver().notifyChange(uri, null);
                return rowsDeleted;
            case (ITEM_ID):
                selection = ItemEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                int rowDeleted = db.delete(ItemEntry.TABLE_NAME, selection, selectionArgs);

                getContext().getContentResolver().notifyChange(uri, null);
                return rowDeleted;
            default:
                throw new IllegalArgumentException("Error reading delete uir");
        }
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case (ITEM_ALL):
                return updateItem(uri, contentValues, selection, selectionArgs);
            case (ITEM_ID):
                selection = ItemEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateItem(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Error reading update Uri");
        }
    }

    public int updateItem(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        if (contentValues.containsKey(ItemEntry.COLUMN_ITEM_NAME)) {
            String name = contentValues.getAsString(ItemEntry.COLUMN_ITEM_NAME);
            if (name.isEmpty())
                throw new IllegalArgumentException("Error saving blank name");
        }
        if (contentValues.containsKey(ItemEntry.COLUMN_ITEM_QUANTITY)) {
            Integer quantity = contentValues.getAsInteger(ItemEntry.COLUMN_ITEM_QUANTITY);
            if (quantity < 0)
                throw new IllegalArgumentException("Error quantity less than 0");
        }
        if (contentValues.containsKey(ItemEntry.COLUMN_ITEM_PRICE)) {
            Integer price = contentValues.getAsInteger(ItemEntry.COLUMN_ITEM_PRICE);
            if (price < 0)
                throw new IllegalArgumentException("Error price less than 0");
        }
        if (contentValues.containsKey(ItemEntry.COLUMN_ITEM_ENROUTE)) {
            Integer enroute = contentValues.getAsInteger(ItemEntry.COLUMN_ITEM_ENROUTE);
            if (enroute < 0)
                throw new IllegalArgumentException("Error enroute less than 0");
        }
        if (contentValues.containsKey(ItemEntry.COLUMN_ITEM_PROVIDER_1)) {
            String provider = contentValues.getAsString(ItemEntry.COLUMN_ITEM_PROVIDER_1);
            if (provider.isEmpty())
                throw new IllegalArgumentException("Error saving blank provider(1)");
        }
        if (contentValues.containsKey(ItemEntry.COLUMN_ITEM_PROVIDER_1_PRICE)) {
            Integer price = contentValues.getAsInteger(ItemEntry.COLUMN_ITEM_PROVIDER_1_PRICE);
            if (price < 0)
                throw new IllegalArgumentException("Error price less than 0");
        }
        if (contentValues.containsKey(ItemEntry.COLUMN_ITEM_PROVIDER_2)) {
            String provider = contentValues.getAsString(ItemEntry.COLUMN_ITEM_PROVIDER_2);
            if (provider.isEmpty())
                throw new IllegalArgumentException("Error saving blank provider(1)");
        }
        if (contentValues.containsKey(ItemEntry.COLUMN_ITEM_PROVIDER_2_PRICE)) {
            Integer price = contentValues.getAsInteger(ItemEntry.COLUMN_ITEM_PROVIDER_2_PRICE);
            if (price < 0)
                throw new IllegalArgumentException("Error price less than 0");
        }
        if (contentValues.containsKey(ItemEntry.COLUMN_ITEM_PROVIDER_3)) {
            String provider = contentValues.getAsString(ItemEntry.COLUMN_ITEM_PROVIDER_3);
            if (provider.isEmpty())
                throw new IllegalArgumentException("Error saving blank provider(1)");
        }
        if (contentValues.containsKey(ItemEntry.COLUMN_ITEM_PROVIDER_3_PRICE)) {
            Integer price = contentValues.getAsInteger(ItemEntry.COLUMN_ITEM_PROVIDER_3_PRICE);
            if (price < 0)
                throw new IllegalArgumentException("Error price less than 0");
        }

        if (contentValues.size() == 0)
            return 0;

        int rowsUpdated = db.update(ItemEntry.TABLE_NAME, contentValues, selection, selectionArgs);
        if (rowsUpdated > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case (ITEM_ALL):
                return ItemEntry.CONTENT_LIST_TYPE;
            case (ITEM_ID):
                return ItemEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }
}