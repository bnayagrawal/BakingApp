package xyz.bnayagrawal.android.bakingapp;

import android.content.res.Configuration;
import android.graphics.PorterDuff;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.exoplayer2.Player;

import butterknife.BindView;
import butterknife.ButterKnife;
import xyz.bnayagrawal.android.bakingapp.model.Recipe;
import xyz.bnayagrawal.android.bakingapp.model.Step;

public class RecipeStepDetailsActivity extends AppCompatActivity {
    public static final String EXTRA_STEP_NUMBER = "recipe_step_number";
    public static final String EXTRA_RECIPE = "recipe";
    private static final String TAG = RecipeStepDetailsActivity.class.getSimpleName();
    private static final String FRAGMENT_RECIPE_STEP_DETAILS_TAG = "fragment_recipe_step_details";

    @BindView(R.id.layout_recipe_step_navigation_container)
    ConstraintLayout mNavigationContainer;

    @BindView(R.id.layout_recipe_step_details_container_root)
    ConstraintLayout mContainerRoot;

    @BindView(R.id.image_button_left)
    ImageButton mImageButtonPrevious;

    @BindView(R.id.image_button_right)
    ImageButton mImageButtonNext;

    @BindView(R.id.text_navigation)
    TextView mTextNavigation;

    private int mCurrentStepNumber = 0;
    private Recipe mRecipe;
    private RecipeStepDetailsFragment mFragmentRecipeStepDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_step_details);
        ButterKnife.bind(this);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null && bundle.containsKey(EXTRA_RECIPE)) {
            mRecipe = bundle.getParcelable(EXTRA_RECIPE);
            mCurrentStepNumber = bundle.getInt(EXTRA_STEP_NUMBER);
        } else {
            finish();
        }

        mFragmentRecipeStepDetails = new RecipeStepDetailsFragment();
        Step currentStep = mRecipe.getSteps().get(mCurrentStepNumber);
        bundle = new Bundle();
        bundle.putBoolean(RecipeStepDetailsFragment.ARGUMENT_IS_PLAYED_IN_TABLET,false);
        bundle.putString(RecipeStepDetailsFragment.ARGUMENT_STEP_INSTRUCTION,currentStep.getDescription());
        bundle.putString(RecipeStepDetailsFragment.ARGUMENT_VIDEO_INSTRUCTION_URL,currentStep.getVideoURL());
        bundle.putString(RecipeStepDetailsFragment.ARGUMENT_STEP_IMAGE_URL,currentStep.getThumbnailURL());
        mFragmentRecipeStepDetails.setArguments(bundle);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.layout_recipe_step_details_container, mFragmentRecipeStepDetails, FRAGMENT_RECIPE_STEP_DETAILS_TAG)
                .commit();

        updatePaginationText();
        updateButtonAction();

        if(isLandscape()){
            mNavigationContainer.setVisibility(View.GONE);
            ActionBar actionBar = getSupportActionBar();
            if(null != actionBar) actionBar.hide();
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        ActionBar actionBar = getSupportActionBar();
        if(newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            mNavigationContainer.setVisibility(View.GONE);
            if(null != actionBar) actionBar.hide();
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else {
            mNavigationContainer.setVisibility(View.VISIBLE);
            updatePaginationText();
            if(null != actionBar) actionBar.show();
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
    }

    private void updateButtonAction() {
        //For previous page
        if(mCurrentStepNumber == 0) {
            mImageButtonPrevious.setColorFilter(
                    ContextCompat.getColor(RecipeStepDetailsActivity.this, R.color.buttonDisabled),
                    PorterDuff.Mode.MULTIPLY);
            mImageButtonPrevious.setOnClickListener(null);
        } else if(mCurrentStepNumber > 0 && mCurrentStepNumber <= mRecipe.getSteps().size()){
            mImageButtonPrevious.setColorFilter(null);
            mImageButtonPrevious.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCurrentStepNumber--;
                    updatePaginationText();
                    Step step = mRecipe.getSteps().get(mCurrentStepNumber);
                    mFragmentRecipeStepDetails.updateInstructions(
                            step.getDescription(),
                            step.getVideoURL()
                    );
                    updateButtonAction();
                }
            });
        }

        //For next page
        if(mCurrentStepNumber != (mRecipe.getSteps().size() - 1)) {
            mImageButtonNext.setColorFilter(null);
            mImageButtonNext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCurrentStepNumber++;
                    updatePaginationText();
                    Step step = mRecipe.getSteps().get(mCurrentStepNumber);
                    mFragmentRecipeStepDetails.updateInstructions(
                            step.getDescription(),
                            step.getVideoURL()
                    );
                    updateButtonAction();
                }
            });
        } else {
            mImageButtonNext.setColorFilter(
                    ContextCompat.getColor(RecipeStepDetailsActivity.this, R.color.buttonDisabled),
                    PorterDuff.Mode.MULTIPLY);
            mImageButtonNext.setOnClickListener(null);
        }
    }

    private boolean isLandscape() {
        return getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
    }

    private void updatePaginationText() {
        String step = getString(R.string.steps) + " " + (mCurrentStepNumber + 1) + "/" + mRecipe.getSteps().size();
        mTextNavigation.setText(step);
    }
}
