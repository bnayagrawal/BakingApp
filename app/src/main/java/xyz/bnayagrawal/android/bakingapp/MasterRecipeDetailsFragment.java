package xyz.bnayagrawal.android.bakingapp;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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

    private OnRecipeStepItemClickListener mCallback;

    public interface OnRecipeStepItemClickListener {
        void onRecipeStepItemClicked(int position);
    }

    @BindView(R.id.list_recipe_steps)
    ListView mListRecipeSteps;

    @BindView(R.id.layout_ingredients)
    LinearLayout mLayoutIngredients;

    private Recipe mRecipe;
    private View lastStepItemColoredView;

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
        View view = inflater.inflate(R.layout.fragment_recipe_details, container, false);
        ButterKnife.bind(this, view);

        Bundle bundle = getArguments();
        if (bundle != null && bundle.containsKey(ARGUMENT_RECIPE))
            mRecipe = bundle.getParcelable(ARGUMENT_RECIPE);

        inflateIngredientList();

        String[] steps = getStepDescriptions();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                getContext(), android.R.layout.simple_list_item_1, steps);
        mListRecipeSteps.setAdapter(adapter);
        mListRecipeSteps.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //change the background color to white for previously selected item
                if(null != lastStepItemColoredView)
                    lastStepItemColoredView.setBackgroundColor(getResources().getColor(R.color.white));
                //change the background color of currently selected item
                view.setBackgroundColor(getResources().getColor(R.color.colorPrimaryLight));
                lastStepItemColoredView = view;
                mCallback.onRecipeStepItemClicked(position);
            }
        });

        return view;
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
}