package xyz.bnayagrawal.android.bakingapp.net;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import xyz.bnayagrawal.android.bakingapp.model.Recipe;
import xyz.bnayagrawal.android.bakingapp.util.ServerUtil;

/**
 * Created by bnayagrawal on 23/3/18.
 */

public interface RecipeService {
    @GET(ServerUtil.RECIPE_PATH)
    Call<List<Recipe>> getRecipeList();
}
