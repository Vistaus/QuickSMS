package com.hexrain.design.quicksms.helpers;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

public class QuickAdapter extends CursorAdapter{

    private LayoutInflater inflater;
    private Context cContext;
    private Cursor c;

    @SuppressWarnings("deprecation")
    public QuickAdapter(Context context, Cursor c) {
        super(context, c);
        this.cContext = context;
        inflater = LayoutInflater.from(context);
        this.c = c;
        c.moveToFirst();
    }

    @Override
    public int getCount() {
        return c.getCount();
    }

    @Override
    public Object getItem(int position) {
        return super.getItem(position);
    }

    @Override
    public long getItemId(int position) {
        Cursor cursor = getCursor();
        cursor.moveToPosition(position);
        return cursor.getLong(cursor.getColumnIndex("_id"));
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        c.moveToPosition(position);

        if (convertView == null) {
            inflater = (LayoutInflater) cContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(android.R.layout.simple_list_item_single_choice, null);
        }

        TextView eventType = convertView.findViewById(android.R.id.text1);

        String message = c.getString(c.getColumnIndex(Constants.COLUMN_TEXT));
        eventType.setText(new Crypter().decrypt(message));

        return convertView;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return inflater.inflate(android.R.layout.simple_list_item_single_choice, null);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

    }
}