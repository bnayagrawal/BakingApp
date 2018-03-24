package xyz.bnayagrawal.android.bakingapp;

import android.graphics.PorterDuff;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import xyz.bnayagrawal.android.bakingapp.model.Recipe;
import xyz.bnayagrawal.android.bakingapp.model.Step;

public class RecipeStepDetailsActivity extends AppCompatActivity {
    public static final String EXTRA_STEP_NUMBER = "recipe_step_number";
    public static final String EXTRA_RECIPE = "recipe";
    private static final String TAG = RecipeStepDetailsActivity.class.getSimpleName();
    private static final String FRAGMENT_RECIPE_STEP_DETAILS_TAG = "fragment_recipe_step_details";

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
        bundle.putString(RecipeStepDetailsFragment.ARGUMENT_STEP_INSTRUCTION,currentStep.getDescription());
        bundle.putString(RecipeStepDetailsFragment.ARGUMENT_VIDEO_INSTRUCTION_URL,currentStep.getVideoURL());
        mFragmentRecipeStepDetails.setArguments(bundle);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.layout_recipe_step_details_container, mFragmentRecipeStepDetails, FRAGMENT_RECIPE_STEP_DETAILS_TAG)
                .commit();

        updatePaginationText();
        updateButtonAction();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
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

    private void updatePaginationText() {
        String step = getString(R.string.steps) + " " + (mCurrentStepNumber + 1) + "/" + mRecipe.getSteps().size();
        mTextNavigation.setText(step);
    }
}
