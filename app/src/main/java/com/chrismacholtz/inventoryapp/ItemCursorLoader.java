package com.chrismacholtz.inventoryapp;

import android.content.Context;
import android.content.CursorLoader;
import android.net.Uri;

/**
 * Created by SWS Customer on 12/23/2016.
 */

public class ItemCursorLoader extends CursorLoader {

    public ItemCursorLoader(Context context, Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        super(context, uri, projection, selection, selectionArgs, sortOrder);
    }
}

