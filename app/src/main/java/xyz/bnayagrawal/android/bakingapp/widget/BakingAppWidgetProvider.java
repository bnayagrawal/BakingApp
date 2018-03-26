package xyz.bnayagrawal.android.bakingapp.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.TaskStackBuilder;
import android.widget.RemoteViews;

import xyz.bnayagrawal.android.bakingapp.R;
import xyz.bnayagrawal.android.bakingapp.RecipeDetailsActivity;

/**
 * Created by bnayagrawal on 26/3/18.
 */

public class BakingAppWidgetProvider extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            //RemoteView
            RemoteViews rv = new RemoteViews(
                    context.getPackageName(),
                    R.layout.widget_recipe
            );

            //Launch activity when widget clicked
            Intent startActivityIntent = new Intent(context, RecipeDetailsActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, startActivityIntent, 0);
            rv.setOnClickPendingIntent(R.id.layout_widget_list_container, pendingIntent);

            //Set adapter for widget listView
            Intent intent = new Intent(context, BakingAppWidgetService.class);
            rv.setRemoteAdapter(R.id.list_widget_recipe_steps, intent);

            //Handle each listView item click
            Intent clickIntentTemplate = new Intent(context, RecipeDetailsActivity.class);
            PendingIntent clickPendingIntentTemplate = TaskStackBuilder.create(context)
                    .addNextIntentWithParentStack(clickIntentTemplate)
                    .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
            rv.setPendingIntentTemplate(R.id.list_widget_recipe_steps, clickPendingIntentTemplate);

            appWidgetManager.updateAppWidget(appWidgetId, rv);
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    @Override
    public void onReceive(final Context context, Intent intent) {
        final String action = intent.getAction();
        if (action.equals(AppWidgetManager.ACTION_APPWIDGET_UPDATE)) {
            AppWidgetManager mgr = AppWidgetManager.getInstance(context);
            ComponentName cn = new ComponentName(context, BakingAppWidgetProvider.class);
            mgr.notifyAppWidgetViewDataChanged(mgr.getAppWidgetIds(cn), R.id.list_widget_recipe_steps);
        }
        super.onReceive(context, intent);
    }

    public static void sendRefreshBroadcast(Context context) {
        Intent intent = new Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        intent.setComponent(new ComponentName(context, BakingAppWidgetProvider.class));
        context.sendBroadcast(intent);
    }
}
