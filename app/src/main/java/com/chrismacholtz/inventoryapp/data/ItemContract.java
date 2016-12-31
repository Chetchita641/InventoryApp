package com.chrismacholtz.inventoryapp.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by SWS Customer on 12/23/2016.
 */

public class ItemContract {
    public static final String CONTENT_AUTHORITY = "com.chrismacholtz.inventoryapp";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_INVENTORY = "inventory";

    private ItemContract() {
    }

    public static final class ItemEntry implements BaseColumns {
        public static final String TABLE_NAME = "inventory";
        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_ITEM_NAME = "name";
        public static final String COLUMN_ITEM_QUANTITY = "quantity";
        public static final String COLUMN_ITEM_PRICE = "price";
        public static final String COLUMN_ITEM_CATEGORY = "category";
        public static final String COLUMN_ITEM_ENROUTE = "enroute";
        public static final String COLUMN_ITEM_PROVIDER_1 = "provider1";
        public static final String COLUMN_ITEM_PROVIDER_2 = "provider2";
        public static final String COLUMN_ITEM_PROVIDER_3 = "provider3";
        public static final String COLUMN_ITEM_PROVIDER_1_PRICE = "provider1price";
        public static final String COLUMN_ITEM_PROVIDER_2_PRICE = "provider2price";
        public static final String COLUMN_ITEM_PROVIDER_3_PRICE = "provider3price";

        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of pets.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_INVENTORY;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single pet.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_INVENTORY;

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_INVENTORY);
    }
}



