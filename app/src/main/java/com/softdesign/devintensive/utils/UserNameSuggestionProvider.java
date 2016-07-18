package com.softdesign.devintensive.utils;

import android.app.SearchManager;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.support.annotation.Nullable;

import com.softdesign.devintensive.data.manager.DataManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by qwsa on 15.07.2016.
 */
public class UserNameSuggestionProvider extends ContentProvider {

    List<String> users;

    @Override
    public boolean onCreate() {
        return false;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] strings, String s, String[] strings1, String s1) {
        if (users == null || users.isEmpty()){
            users = new ArrayList<>();
            users = (List<String>) DataManager.getInstance().getUserList();
        }

        MatrixCursor cursor = new MatrixCursor(
                new String[] {
                        BaseColumns._ID,
                        SearchManager.SUGGEST_COLUMN_TEXT_1,
                        SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID
                }
        );

        if (users != null) {
            String query = uri.getLastPathSegment().toUpperCase();
            int limit = Integer.parseInt(uri.getQueryParameter(SearchManager.SUGGEST_PARAMETER_LIMIT));

            int lenght = users.size();
            for (int i = 0; i < lenght && cursor.getCount() < limit; i++) {
                String city = users.get(i);
                if (city.toUpperCase().contains(query)){
                    cursor.addRow(new Object[]{ i, city, i });
                }
            }
        }
            return cursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        return null;
    }

    @Override
    public int delete(Uri uri, String s, String[] strings) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {
        return 0;
    }
}
