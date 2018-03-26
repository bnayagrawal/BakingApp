package xyz.bnayagrawal.android.bakingapp.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import xyz.bnayagrawal.android.bakingapp.provider.RecipeIngredientsContract.RecipeIngredientsEntry;

/**
 * Created by bnayagrawal on 26/3/18.
 */

public class RecipeIngredientsDbHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "recipe_ingredients.db";
    private static final int DATABASE_VERSION = 1;

    public RecipeIngredientsDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_FAVORITE_MOVIES_TABLE =
                "CREATE TABLE " + RecipeIngredientsEntry.TABLE_NAME + " (" +
                        RecipeIngredientsEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        RecipeIngredientsEntry.COLUMN_QUANTITY + " REAL, " +
                        RecipeIngredientsEntry.COLUMN_MEASURE + " TEXT NOT NULL, " +
                        RecipeIngredientsEntry.COLUMN_INGREDIENT + " TEXT NOT NULL" +
                        ");";

        sqLiteDatabase.execSQL(SQL_CREATE_FAVORITE_MOVIES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + RecipeIngredientsEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
