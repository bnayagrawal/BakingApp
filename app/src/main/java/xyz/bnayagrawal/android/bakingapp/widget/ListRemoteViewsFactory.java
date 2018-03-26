package xyz.bnayagrawal.android.bakingapp.widget;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Binder;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import xyz.bnayagrawal.android.bakingapp.R;
import xyz.bnayagrawal.android.bakingapp.provider.RecipeIngredientsContract;
import xyz.bnayagrawal.android.bakingapp.provider.RecipeIngredientsContract.RecipeIngredientsEntry;

/**
 * Created by bnayagrawal on 26/3/18.
 */

public class ListRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

    private Context mContext;
    private Cursor mCursor;

    public ListRemoteViewsFactory(Context context, Intent intent) {
        this.mContext = context;
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDataSetChanged() {
        if (mCursor != null) {
            mCursor.close();
        }

        final long identityToken = Binder.clearCallingIdentity();
        mCursor = mContext.getContentResolver().query(
                RecipeIngredientsContract.RecipeIngredientsEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        Binder.restoreCallingIdentity(identityToken);
    }

    @Override
    public void onDestroy() {
        if (mCursor != null) {
            mCursor.close();
        }
    }

    @Override
    public int getCount() {
        if (mCursor != null)
            return mCursor.getCount();
        else
            return 0;
    }

    @Override
    public RemoteViews getViewAt(int position) {
        String ingredient;
        if (null == mCursor || position == AdapterView.INVALID_POSITION || !mCursor.moveToPosition(position))
            return null;

        //Add quantity
        ingredient = mCursor.getString(
                mCursor.getColumnIndex(
                        RecipeIngredientsEntry.COLUMN_QUANTITY
                )
        );

        //Add measure
        ingredient += " " + mCursor.getString(
                mCursor.getColumnIndex(
                        RecipeIngredientsEntry.COLUMN_MEASURE
                )
        );

        //Add ingredient name
        ingredient += " " + mCursor.getString(
                mCursor.getColumnIndex(
                        RecipeIngredientsEntry.COLUMN_INGREDIENT
                )
        );

        RemoteViews rv = new RemoteViews(mContext.getPackageName(), R.layout.widget_step_list_item);
        rv.setTextViewText(R.id.text_widget_recipe_step, ingredient);

        Intent fillInIntent = new Intent();
        rv.setOnClickFillInIntent(R.id.text_widget_recipe_step, fillInIntent);

        return rv;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        int id = position;
        if (mCursor.moveToPosition(position))
            id = mCursor.getInt(0);

        return id;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
