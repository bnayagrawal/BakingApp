package xyz.bnayagrawal.android.bakingapp;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import xyz.bnayagrawal.android.bakingapp.model.Ingredient;
import xyz.bnayagrawal.android.bakingapp.model.Recipe;
import xyz.bnayagrawal.android.bakingapp.model.Step;

/**
 * Created by bnayagrawal on 23/3/18.
 */

public class MasterRecipeDetailsFragment extends Fragment {
    public static final String ARGUMENT_RECIPE = "recipe";
    private static final String EXTRA_SELECTED_STEP_ITEM_POSITION = "extra_selected_step_item_position";

    private OnRecipeStepItemClickListener mCallback;

    public interface OnRecipeStepItemClickListener {
        void onRecipeStepItemClicked(int position);
    }

    @Nullable @BindView(R.id.layout_split_recipe_details_container)
    LinearLayout mLayoutSplitRecipeDetailsContainer;

    @Nullable @BindView(R.id.layout_ingredients_container)
    LinearLayout mLayoutIngredients;

    @BindView(R.id.list_recipe_steps)
    ListView mListRecipeSteps;

    private Recipe mRecipe;
    private View lastStepItemColoredView;
    private boolean mIsSplitRecipeDetails = false;
    private int mSelectedStepItemPosition = -1;
    private View mStepsHeaderView;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mCallback = (OnRecipeStepItemClickListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnRecipeStepItemClickListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        if(null != savedInstanceState) {
            mSelectedStepItemPosition = savedInstanceState.getInt(EXTRA_SELECTED_STEP_ITEM_POSITION);
        }

        View view = inflater.inflate(R.layout.fragment_recipe_details, container, false);
        ButterKnife.bind(this, view);

        Bundle bundle = getArguments();
        if (bundle != null && bundle.containsKey(ARGUMENT_RECIPE))
            mRecipe = bundle.getParcelable(ARGUMENT_RECIPE);

        if(null != mLayoutSplitRecipeDetailsContainer)
            mIsSplitRecipeDetails = true;

        if(!mIsSplitRecipeDetails) {
            //inflate ingredients list
            mLayoutIngredients = (LinearLayout) getLayoutInflater().inflate(R.layout.partial_recipe_ingredients,null);
        }

        String[] steps = getStepDescriptions();
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                getContext(), android.R.layout.simple_list_item_1, steps);

        mListRecipeSteps.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //change the background color to white for previously selected item
                if(null != lastStepItemColoredView)
                    lastStepItemColoredView.setBackgroundColor(getResources().getColor(R.color.white));
                //change the background color of currently selected item
                view.setBackgroundColor(getResources().getColor(R.color.colorPrimaryLight));
                lastStepItemColoredView = view;
                //ListView headers are counted as items, so needs to be subtracted
                position -= mListRecipeSteps.getHeaderViewsCount();
                mCallback.onRecipeStepItemClicked(position);
                mSelectedStepItemPosition = position;
            }
        });


        //Inflate ingredients
        inflateIngredientList();

        //Add ingredients list as header item to listView if not in split mode
        if(!mIsSplitRecipeDetails)
            mListRecipeSteps.addHeaderView(mLayoutIngredients,null,false);

        //Add steps header view
        //Inflate steps header item
        mStepsHeaderView = (TextView) getLayoutInflater().inflate(R.layout.item_steps_header,null);
        mListRecipeSteps.addHeaderView(mStepsHeaderView,null,false);

        mListRecipeSteps.setAdapter(adapter);
        if(mSelectedStepItemPosition != -1) {
            View child = getChildFromListView();
            if(null != child)
                child.setBackgroundColor(getResources().getColor(R.color.colorPrimaryLight));
            else
                Log.d("pos","null");
        }
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putInt(EXTRA_SELECTED_STEP_ITEM_POSITION, mSelectedStepItemPosition);
        super.onSaveInstanceState(outState);
    }

    private String[] getStepDescriptions() {
        List<Step> stepList = mRecipe.getSteps();
        String[] steps = new String[stepList.size()];

        for (int i = 0; i < stepList.size(); i++)
            steps[i] = stepList.get(i).getShortDescription();

        return steps;
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

    private View getChildFromListView() {
        int firstChildPosition = mListRecipeSteps.getFirstVisiblePosition() - mListRecipeSteps.getHeaderViewsCount();
        int wantedChildPosition = mSelectedStepItemPosition - firstChildPosition;
        if(wantedChildPosition < 0 || wantedChildPosition >= mListRecipeSteps.getChildCount())
            return null;
        else
            return mListRecipeSteps.getChildAt(wantedChildPosition);
    }
}
