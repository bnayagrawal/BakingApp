package xyz.bnayagrawal.android.bakingapp.widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.util.ArrayList;
import java.util.List;

import xyz.bnayagrawal.android.bakingapp.R;
import xyz.bnayagrawal.android.bakingapp.model.Ingredient;

/**
 * Created by bnayagrawal on 26/3/18.
 */

public class ListRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

    private Context mContext;
    private int mAppWidgetId;
    private List<Ingredient> mIngredientList;

    public ListRemoteViewsFactory(Context context, Intent intent) {
        this.mContext = context;
        mAppWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);
        if(intent.hasExtra(BakingAppWidgetService.EXTRA_INGREDIENT_LIST))
            mIngredientList = intent.getParcelableArrayListExtra(BakingAppWidgetService.EXTRA_INGREDIENT_LIST);
        else
            mIngredientList = new ArrayList<>();
    }

    @Override
    public void onCreate() {
    }

    @Override
    public void onDataSetChanged() {

    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        return mIngredientList.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        RemoteViews rv = new RemoteViews(mContext.getPackageName(), R.id.text_widget_recipe_step);
        Ingredient ingredient = mIngredientList.get(position);
        String ingredientText = ingredient.getQuantity() + " " +
                ingredient.getMeasure() + " " + ingredient.getIngredient();
        rv.setTextViewText(R.id.text_widget_recipe_step, ingredientText);
        return rv;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 0;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }
}
