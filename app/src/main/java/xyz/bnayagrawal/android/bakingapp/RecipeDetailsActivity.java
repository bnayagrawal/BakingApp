package xyz.bnayagrawal.android.bakingapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.FrameLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import xyz.bnayagrawal.android.bakingapp.adapter.RecipeStepsListAdapter;
import xyz.bnayagrawal.android.bakingapp.model.Recipe;

public class RecipeDetailsActivity extends AppCompatActivity
        implements RecipeStepsListAdapter.OnRecipeStepClickListener {

    private static final String TAG = RecipeDetailsActivity.class.getSimpleName();
    private static final String FRAGMENT_RECIPE_DETAILS_TAG = "fragment_recipe_details";
    private static final String FRAGMENT_RECIPE_STEP_DETAILS_TAG = "fragment_recipe_step_details";
    public static final String EXTRA_RECIPE = "recipe";

    @Nullable
    @BindView(R.id.layout_recipe_step_details_container)
    FrameLayout mLayoutRecipeStepDetailsContainer;

    private Recipe mRecipe;
    private boolean mIsTwoPane;

    private MasterRecipeDetailsFragment mFragmentRecipeDetails;
    private RecipeStepDetailsFragment mFragmentRecipeStepDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_details);
        ButterKnife.bind(this);

        Bundle bundle = getIntent().getExtras();
        if (null != bundle && bundle.containsKey(EXTRA_RECIPE))
            mRecipe = bundle.getParcelable(EXTRA_RECIPE);

        if (null != mLayoutRecipeStepDetailsContainer)
            mIsTwoPane = true;

        if (null == savedInstanceState) {
            mFragmentRecipeDetails = new MasterRecipeDetailsFragment();
            Bundle arguments = new Bundle();
            arguments.putParcelable(MasterRecipeDetailsFragment.ARGUMENT_RECIPE, mRecipe);
            mFragmentRecipeDetails.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.layout_recipe_details_container, mFragmentRecipeDetails, FRAGMENT_RECIPE_DETAILS_TAG)
                    .commit();

            if (mIsTwoPane) {
                arguments = new Bundle();
                arguments.putBoolean(RecipeStepDetailsFragment.ARGUMENT_IS_PLAYED_IN_TABLET,true);
                mFragmentRecipeStepDetails = new RecipeStepDetailsFragment();
                mFragmentRecipeStepDetails.setArguments(arguments);
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.layout_recipe_step_details_container,
                                mFragmentRecipeStepDetails,
                                FRAGMENT_RECIPE_STEP_DETAILS_TAG).commit();
            }
        } else {
            mFragmentRecipeDetails = (MasterRecipeDetailsFragment) getSupportFragmentManager()
                    .findFragmentByTag(FRAGMENT_RECIPE_DETAILS_TAG);
            if (mIsTwoPane)
                mFragmentRecipeStepDetails = (RecipeStepDetailsFragment) getSupportFragmentManager()
                        .findFragmentByTag(FRAGMENT_RECIPE_STEP_DETAILS_TAG);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRecipeStepClicked(int position) {
        if (!mIsTwoPane) {
            Intent intent = new Intent(this, RecipeStepDetailsActivity.class);
            intent.putExtra(RecipeStepDetailsActivity.EXTRA_STEP_NUMBER, position);
            intent.putExtra(RecipeStepDetailsActivity.EXTRA_RECIPE, mRecipe);
            startActivity(intent);
        } else {
            mFragmentRecipeDetails.setSelectedItemPosition(position);
            mFragmentRecipeStepDetails.updateInstructions(
                    mRecipe.getSteps().get(position).getDescription(),
                    mRecipe.getSteps().get(position).getVideoURL()
            );
        }
    }
}
