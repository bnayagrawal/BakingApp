package xyz.bnayagrawal.android.bakingapp.adapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import xyz.bnayagrawal.android.bakingapp.R;
import xyz.bnayagrawal.android.bakingapp.model.Step;

/**
 * Created by bnayagrawal on 25/3/18.
 */

public class RecipeStepsListAdapter extends BaseAdapter {

    private Context mContext;
    private OnRecipeStepClickListener mCallback;
    private ArrayList<Step> mSteps;
    private ViewHolder mLastSelectedStepView;
    private Integer mCurrentlySelectedItemPosition;

    public interface OnRecipeStepClickListener {
        void onRecipeStepClicked(int id);
    }

    public RecipeStepsListAdapter(Context context, ArrayList<Step> steps, @Nullable Integer selectedItemPosition) {
        this.mContext = context;
        this.mSteps = steps;
        this.mCurrentlySelectedItemPosition = selectedItemPosition;
        try {
            mCallback = (OnRecipeStepClickListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnRecipeStepClickListener");
        }
    }

    @Override
    public int getCount() {
        return mSteps.size();
    }

    @Override
    public Object getItem(int position) {
        return mSteps.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mSteps.get(position).getId();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView != null) {
            holder = (ViewHolder) convertView.getTag();
        } else {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_recipe_step, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }

        holder.textRecipeStep.setText(mSteps.get(position).getShortDescription());

        //Since view is getting recycled, have to change background color of selected item
        //if the view is being recreated;
        if (mCurrentlySelectedItemPosition != null && mCurrentlySelectedItemPosition == position)
            holder.textRecipeStep.setBackgroundColor(mContext.getResources().getColor(R.color.colorPrimaryLight));

        //Set click listener
        holder.textRecipeStep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Reset background color of last selected item to white
                //if the view is not recycled.
                if (mLastSelectedStepView != null)
                    mLastSelectedStepView.textRecipeStep.setBackgroundColor(
                            mContext.getResources().getColor(R.color.white)
                    );

                //Maintain position and view reference of currently selected step
                mCurrentlySelectedItemPosition = position;
                mLastSelectedStepView = holder;

                //Change background color of selected item
                holder.textRecipeStep.setBackgroundColor(mContext.getResources().getColor(R.color.colorPrimaryLight));
                //Notify an item has been selected
                mCallback.onRecipeStepClicked(position);
            }
        });

        return convertView;
    }

    static class ViewHolder {
        @BindView(R.id.text_recipe_step)
        TextView textRecipeStep;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
