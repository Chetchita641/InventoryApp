package com.chrismacholtz.inventoryapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.chrismacholtz.inventoryapp.data.ItemContract.ItemEntry;

/**
 * Created by SWS Customer on 12/23/2016.
 */

public class ItemDbHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "inventory.db";

    public static final String SQL_CREATE_ENTRIES = "CREATE TABLE " + ItemEntry.TABLE_NAME + " ("
            + ItemEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + ItemEntry.COLUMN_ITEM_NAME + " TEXT NOT NULL, "
            + ItemEntry.COLUMN_ITEM_IMAGE_URI + " TEXT,"
            + ItemEntry.COLUMN_ITEM_QUANTITY + " INTEGER NOT NULL DEFAULT 0, "
            + ItemEntry.COLUMN_ITEM_PRICE + " FLOAT NOT NULL, "
            + ItemEntry.COLUMN_ITEM_CATEGORY + " INTEGER NOT NULL DEFAULT 0, "
            + ItemEntry.COLUMN_ITEM_ENROUTE + " INTEGER NOT NULL DEFAULT 0, "
            + ItemEntry.COLUMN_ITEM_PROVIDER_1 + " INTEGER, "
            + ItemEntry.COLUMN_ITEM_PROVIDER_1_PRICE + " FLOAT, "
            + ItemEntry.COLUMN_ITEM_PROVIDER_2 + " INTEGER, "
            + ItemEntry.COLUMN_ITEM_PROVIDER_2_PRICE + " FLOAT, "
            + ItemEntry.COLUMN_ITEM_PROVIDER_3 + " INTEGER, "
            + ItemEntry.COLUMN_ITEM_PROVIDER_3_PRICE + " FLOAT);";

    public static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + ItemEntry.TABLE_NAME;

    public ItemDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(SQL_DELETE_ENTRIES);
        onCreate(sqLiteDatabase);
    }
}
