package xyz.bnayagrawal.android.bakingapp;

import android.content.Intent;
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
    public void clickRecipeStep_openRecipeStepDetailsActivity() {
        //I have added 2 as position because in mobile the "ingredients list"(position 0) and
        //the "steps label" (position 1) are added as list headers, so first item in the list
        //will be in "position 2"
        onData(anything()).inAdapterView(withId(R.id.list_recipe_steps)).atPosition(2).perform(click());
        //If you have to test this on tablet, then set the position value 1, because in tablet only the
        //"steps label" is added as header to step list, so the position of first step in list will be 1
    }
}
