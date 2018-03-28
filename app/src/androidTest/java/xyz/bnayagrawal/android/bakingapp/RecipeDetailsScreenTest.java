package xyz.bnayagrawal.android.bakingapp;

import android.content.Intent;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

import xyz.bnayagrawal.android.bakingapp.model.Ingredient;
import xyz.bnayagrawal.android.bakingapp.model.Recipe;
import xyz.bnayagrawal.android.bakingapp.model.Step;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.anything;

/**
 * Created by bnayagrawal on 27/3/18.
 */

//This test will click on a recipe step and launch the RecipeStepDetailsActivity if running on mobile,
//or if its running on tablet it will play the video in the details view.
@RunWith(AndroidJUnit4.class)
public class RecipeDetailsScreenTest {
    @Rule
    public ActivityTestRule<RecipeDetailsActivity> mActivityTestRule =
            new ActivityTestRule<RecipeDetailsActivity>(RecipeDetailsActivity.class) {
                @Override
                protected Intent getActivityIntent() {
                    //Since The RecipeDetailsActivity is dependent on intent i have pass a fake one.
                    Intent intent = new Intent();

                    ArrayList<Ingredient> ingredients = new ArrayList<>();
                    ingredients.add(new Ingredient(2.5, "Cup", "Egg"));

                    ArrayList<Step> steps = new ArrayList<>();
                    steps.add(new Step(
                                    0,
                                    "Crack the eggs",
                                    "Put eggs in blablabla",
                                    null,
                                    null
                            )
                    );

                    Recipe recipe = new Recipe(
                            0,
                            "Chocolate Cake",
                            ingredients,
                            steps,
                            5,
                            " "
                    );

                    intent.putExtra(RecipeDetailsActivity.EXTRA_RECIPE, recipe);
                    return intent;
                }
            };

    @Test
    public void clickRecipeStep_playVideoOrLaunchStepDetailsActivity() {
        onView(ViewMatchers.withId(R.id.recycler_recipe_steps))
                .perform(RecyclerViewActions.actionOnItemAtPosition(0,
                        click()));
    }
}
