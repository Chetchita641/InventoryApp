package com.chrismacholtz.inventoryapp;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.chrismacholtz.inventoryapp.data.ItemContract.ItemEntry;

/**
 * Created by SWS Customer on 12/23/2016.
 */

public class ItemCursorAdapter extends CursorAdapter {

    public ItemCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView productNameTextView = (TextView) view.findViewById(R.id.product_name_list);
        TextView quantityTextView = (TextView) view.findViewById(R.id.quantity_list);
        TextView enrouteTextView = (TextView) view.findViewById(R.id.enroute_list);
        TextView priceTextView = (TextView) view.findViewById(R.id.product_price_list);

        int nameColumnIndex = cursor.getColumnIndex(ItemEntry.COLUMN_ITEM_NAME);
        int quantityColumnIndex = cursor.getColumnIndex(ItemEntry.COLUMN_ITEM_QUANTITY);
        int enrouteColumnIndex = cursor.getColumnIndex(ItemEntry.COLUMN_ITEM_ENROUTE);
        int priceColumnIndex = cursor.getColumnIndex(ItemEntry.COLUMN_ITEM_PRICE);
        float price = (cursor.getFloat(priceColumnIndex));

        productNameTextView.setText(cursor.getString(nameColumnIndex));
        quantityTextView.setText("Qty: " + cursor.getInt(quantityColumnIndex));
        enrouteTextView.setText("Enroute:  " + cursor.getInt(enrouteColumnIndex));
        priceTextView.setText("$" + String.format("%.2f", price));
    }
}
