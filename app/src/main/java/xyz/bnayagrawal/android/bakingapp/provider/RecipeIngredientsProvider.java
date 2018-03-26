package xyz.bnayagrawal.android.bakingapp.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import xyz.bnayagrawal.android.bakingapp.provider.RecipeIngredientsContract.RecipeIngredientsEntry;

/**
 * Created by bnayagrawal on 26/3/18.
 */

public class RecipeIngredientsProvider extends ContentProvider {

    private RecipeIngredientsDbHelper mOpenHelper;

    @Override
    public boolean onCreate() {
        mOpenHelper = new RecipeIngredientsDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] s0, @Nullable String s1) {
        Cursor cursor;
        cursor = mOpenHelper.getReadableDatabase().query(
                RecipeIngredientsEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null
        );

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        Uri returnUri;
        final SQLiteDatabase sqLiteDatabase = mOpenHelper.getWritableDatabase();
        long id = sqLiteDatabase.insert(RecipeIngredientsEntry.TABLE_NAME,null,contentValues);
        if(id > 0) {
            returnUri = ContentUris.withAppendedId(RecipeIngredientsEntry.CONTENT_URI,id);
        } else {
            throw new SQLiteException("Failed to insert ingredients!");
        }
        getContext().getContentResolver().notifyChange(uri,null);
        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
        final SQLiteDatabase sqLiteDatabase = mOpenHelper.getWritableDatabase();
        return sqLiteDatabase.delete(RecipeIngredientsEntry.TABLE_NAME,
                "1",
                null
        );
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }
}
