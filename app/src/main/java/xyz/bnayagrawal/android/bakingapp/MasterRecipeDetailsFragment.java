package xyz.bnayagrawal.android.bakingapp;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import xyz.bnayagrawal.android.bakingapp.adapter.RecipeStepsListAdapter;
import xyz.bnayagrawal.android.bakingapp.model.Ingredient;
import xyz.bnayagrawal.android.bakingapp.model.Recipe;
import xyz.bnayagrawal.android.bakingapp.provider.RecipeIngredientsContract;
import xyz.bnayagrawal.android.bakingapp.widget.BakingAppWidgetProvider;

import xyz.bnayagrawal.android.bakingapp.provider.RecipeIngredientsContract.RecipeIngredientsEntry;
/**
 * Created by bnayagrawal on 23/3/18.
 */

public class MasterRecipeDetailsFragment extends Fragment {
    public static final String ARGUMENT_RECIPE = "recipe";
    private static final String EXTRA_SELECTED_STEP_ITEM_POSITION = "extra_selected_step_item_position";
    private static final String EXTRA_RECIPE = "recipe";

    @Nullable
    @BindView(R.id.layout_split_recipe_details_container)
    LinearLayout mLayoutSplitRecipeDetailsContainer;

    @Nullable
    @BindView(R.id.layout_ingredients_container)
    LinearLayout mLayoutIngredients;

    @BindView(R.id.list_recipe_steps)
    ListView mListRecipeSteps;

    private Context mContext;
    private Recipe mRecipe;
    private boolean mIsSplitRecipeDetails = false;
    private int mSelectedStepItemPosition = -1;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        if (null != savedInstanceState) {
            mRecipe = savedInstanceState.getParcelable(EXTRA_RECIPE);
            mSelectedStepItemPosition = savedInstanceState.getInt(EXTRA_SELECTED_STEP_ITEM_POSITION);
        }

        View view = inflater.inflate(R.layout.fragment_recipe_details, container, false);
        ButterKnife.bind(this, view);

        Bundle bundle = getArguments();
        if (bundle != null && bundle.containsKey(ARGUMENT_RECIPE))
            mRecipe = bundle.getParcelable(ARGUMENT_RECIPE);

        if (null != mLayoutSplitRecipeDetailsContainer)
            mIsSplitRecipeDetails = true;

        if (!mIsSplitRecipeDetails) {
            //inflate ingredients list
            mLayoutIngredients = (LinearLayout) getLayoutInflater().inflate(R.layout.partial_recipe_ingredients, null);
        }

        RecipeStepsListAdapter adapter = new RecipeStepsListAdapter(
                mContext,
                mRecipe.getSteps(),
                (mSelectedStepItemPosition == -1) ? null : mSelectedStepItemPosition
        );

        //Inflate ingredients
        inflateIngredientList();

        //Add ingredients list as header item to listView if not in split mode
        if (!mIsSplitRecipeDetails)
            mListRecipeSteps.addHeaderView(mLayoutIngredients, null, false);

        //Add steps header view
        mListRecipeSteps.addHeaderView(
                getLayoutInflater().inflate(R.layout.partial_steps_header, null),
                null,
                false
        );

        mListRecipeSteps.setAdapter(adapter);

        //update widgets
        updateDatabase();
        BakingAppWidgetProvider.sendRefreshBroadcast(mContext);

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putParcelable(EXTRA_RECIPE, mRecipe);
        outState.putInt(EXTRA_SELECTED_STEP_ITEM_POSITION, mSelectedStepItemPosition);
        super.onSaveInstanceState(outState);
    }

    private void inflateIngredientList() {
        LayoutInflater inflater = getLayoutInflater();
        List<Ingredient> ingredients = mRecipe.getIngredients();
        View view;
        TextView textView;

        for (Ingredient ingredient : ingredients) {
            view = inflater.inflate(R.layout.item_ingredient, mLayoutIngredients, false);
            textView = view.findViewById(R.id.text_ingredient_name);
            textView.setText(ingredient.getIngredient());
            String quantity = ingredient.getQuantity() + " " + ingredient.getMeasure();
            textView = view.findViewById(R.id.text_ingredient_quantity);
            textView.setText(quantity);
            mLayoutIngredients.addView(view);
        }
    }

    public void setSelectedItemPosition(int selectedItemPosition) {
        this.mSelectedStepItemPosition = selectedItemPosition;
    }

    private void updateDatabase(){
        eraseDatabase();
        ContentResolver resolver = mContext.getContentResolver();
        ContentValues[] contentValuesArray = new ContentValues[mRecipe.getIngredients().size()];

        ContentValues contentValues;
        List<Ingredient> ingredients = mRecipe.getIngredients();

        for(int i=0; i < ingredients.size(); i++) {
            contentValues = new ContentValues();
            contentValues.put(RecipeIngredientsEntry.COLUMN_QUANTITY,ingredients.get(i).getQuantity());
            contentValues.put(RecipeIngredientsEntry.COLUMN_MEASURE,ingredients.get(i).getMeasure());
            contentValues.put(RecipeIngredientsEntry.COLUMN_INGREDIENT,ingredients.get(i).getIngredient());
            contentValuesArray[i] = contentValues;
        }

        resolver.bulkInsert(
                RecipeIngredientsEntry.CONTENT_URI,
                contentValuesArray
        );

    }

    private void eraseDatabase() {
        mContext.getContentResolver().delete(
                RecipeIngredientsContract.RecipeIngredientsEntry.CONTENT_URI,
                null,
                null
        );
    }
}
