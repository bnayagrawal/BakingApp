package xyz.bnayagrawal.android.bakingapp.provider;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by bnayagrawal on 26/3/18.
 */

//A temporary database
public class RecipeIngredientsContract {

    public static final String CONTENT_AUTHORITY = "xyz.bnayagrawal.android.bakingapp";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_INGREDIENTS = "ingredients";

    public static final class RecipeIngredientsEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_INGREDIENTS)
                .build();

        public static final String TABLE_NAME = "recipeIngredients";

        public static final String _ID = "movie_id";
        public static final String COLUMN_QUANTITY = "quantity";
        public static final String COLUMN_MEASURE = "measure";
        public static final String COLUMN_INGREDIENT = "ingredient";
    }
}
