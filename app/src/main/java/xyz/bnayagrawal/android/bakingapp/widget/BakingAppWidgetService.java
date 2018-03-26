package xyz.bnayagrawal.android.bakingapp.widget;

import android.content.Intent;
import android.widget.RemoteViewsService;

/**
 * Created by bnayagrawal on 26/3/18.
 */

public class BakingAppWidgetService extends RemoteViewsService {
    public static final String EXTRA_INGREDIENT_LIST = "extra_ingredient_list";

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new ListRemoteViewsFactory(this.getApplicationContext(),intent);
    }
}
