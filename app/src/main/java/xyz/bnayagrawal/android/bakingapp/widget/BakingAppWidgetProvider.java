package xyz.bnayagrawal.android.bakingapp.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import xyz.bnayagrawal.android.bakingapp.R;
import xyz.bnayagrawal.android.bakingapp.RecipeDetailsActivity;
import xyz.bnayagrawal.android.bakingapp.model.Ingredient;

/**
 * Created by bnayagrawal on 26/3/18.
 */

public class BakingAppWidgetProvider extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            Intent intent = new Intent(context, BakingAppWidgetService.class);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            intent.putExtra("random", ThreadLocalRandom.current().nextInt(0, 99999 + 1));
            intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
            //RemoteView
            RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.widget_recipe);
            rv.setRemoteAdapter(appWidgetId, R.id.list_widget_recipe_steps, intent);
            //If no recipe selected
            rv.setEmptyView(R.id.list_widget_recipe_steps, R.id.text_widget_empty);

            //Start RecipeDetailsActivity When clicked
            Intent startRecipeDetailsActivityIntent = new Intent(context, RecipeDetailsActivity.class);
            PendingIntent startRecipeDetailsActivityPendingIntent = PendingIntent
                    .getActivity(context, 0, startRecipeDetailsActivityIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            rv.setOnClickPendingIntent(R.id.list_widget_recipe_steps, startRecipeDetailsActivityPendingIntent);

            appWidgetManager.updateAppWidget(appWidgetId, rv);
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    public static void updateWidgets(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds, ArrayList<Ingredient> ingredientList) {
        for (int appWidgetId : appWidgetIds) {
            Intent intent = new Intent(context, BakingAppWidgetService.class);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            intent.putParcelableArrayListExtra(BakingAppWidgetService.EXTRA_INGREDIENT_LIST,ingredientList);
            intent.putExtra("random", ThreadLocalRandom.current().nextInt(0, 99999 + 1));
            intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
            //RemoteView
            RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.widget_recipe);
            rv.setRemoteAdapter(appWidgetId, R.id.list_widget_recipe_steps, intent);
            //If no recipe selected
            rv.setEmptyView(R.id.list_widget_recipe_steps, R.id.text_widget_empty);

            //Start RecipeDetailsActivity When clicked
            Intent startRecipeDetailsActivityIntent = new Intent(context, RecipeDetailsActivity.class);
            PendingIntent startRecipeDetailsActivityPendingIntent = PendingIntent
                    .getActivity(context, 0, startRecipeDetailsActivityIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            rv.setOnClickPendingIntent(R.id.list_widget_recipe_steps, startRecipeDetailsActivityPendingIntent);

            appWidgetManager.updateAppWidget(appWidgetId, rv);
        }
    }
}
