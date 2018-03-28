package xyz.bnayagrawal.android.bakingapp;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.wasabeef.recyclerview.animators.FadeInUpAnimator;
import xyz.bnayagrawal.android.bakingapp.adapter.RecipeStepsRecyclerAdapter;
import xyz.bnayagrawal.android.bakingapp.model.Ingredient;
import xyz.bnayagrawal.android.bakingapp.model.Recipe;
import xyz.bnayagrawal.android.bakingapp.provider.RecipeIngredientsContract;
import xyz.bnayagrawal.android.bakingapp.provider.RecipeIngredientsContract.RecipeIngredientsEntry;
import xyz.bnayagrawal.android.bakingapp.widget.BakingAppWidgetProvider;

/**
 * Created by bnayagrawal on 23/3/18.
 */

public class MasterRecipeDetailsFragment extends Fragment {
    public static final String ARGUMENT_RECIPE = "recipe";
    private static final String EXTRA_SELECTED_STEP_ITEM_POSITION = "extra_selected_step_item_position";
    private static final String EXTRA_RECIPE = "recipe";

    @Nullable
    @BindView(R.id.layout_split_recipe_details_container)
    ConstraintLayout mLayoutSplitRecipeDetailsContainer;

    @BindView(R.id.layout_ingredients_container)
    LinearLayout mLayoutIngredients;

    @BindView(R.id.recycler_recipe_steps)
    RecyclerView mRecyclerRecipeSteps;

    private Context mContext;
    private Recipe mRecipe;
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

        initRecyclerView();
        inflateIngredientList();

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

    private void initRecyclerView() {
        RecyclerView.LayoutManager layoutManager =
                new LinearLayoutManager(
                        getContext(),
                        LinearLayoutManager.VERTICAL,
                        false
                );

        //Item decorator (divider)
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(
                mRecyclerRecipeSteps.getContext(),
                DividerItemDecoration.VERTICAL
        );
        mRecyclerRecipeSteps.addItemDecoration(dividerItemDecoration);

        mRecyclerRecipeSteps.setLayoutManager(layoutManager);
        FadeInUpAnimator animator = new FadeInUpAnimator();
        animator.setAddDuration(150);
        animator.setChangeDuration(150);
        animator.setRemoveDuration(300);
        animator.setMoveDuration(300);
        mRecyclerRecipeSteps.setItemAnimator(animator);

        RecipeStepsRecyclerAdapter mAdapter =
                new RecipeStepsRecyclerAdapter(
                        mContext,
                        mRecipe.getSteps(),
                        (mSelectedStepItemPosition == -1) ? null : mSelectedStepItemPosition
                );

        mRecyclerRecipeSteps.setAdapter(mAdapter);
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

    private void updateDatabase() {
        eraseDatabase();
        ContentResolver resolver = mContext.getContentResolver();
        ContentValues[] contentValuesArray = new ContentValues[mRecipe.getIngredients().size()];

        ContentValues contentValues;
        List<Ingredient> ingredients = mRecipe.getIngredients();

        for (int i = 0; i < ingredients.size(); i++) {
            contentValues = new ContentValues();
            contentValues.put(RecipeIngredientsEntry.COLUMN_QUANTITY, ingredients.get(i).getQuantity());
            contentValues.put(RecipeIngredientsEntry.COLUMN_MEASURE, ingredients.get(i).getMeasure());
            contentValues.put(RecipeIngredientsEntry.COLUMN_INGREDIENT, ingredients.get(i).getIngredient());
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
