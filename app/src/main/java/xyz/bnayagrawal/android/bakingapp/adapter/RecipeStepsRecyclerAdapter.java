package xyz.bnayagrawal.android.bakingapp.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import xyz.bnayagrawal.android.bakingapp.R;
import xyz.bnayagrawal.android.bakingapp.model.Step;

/**
 * Created by bnayagrawal on 28/3/18.
 */

public class RecipeStepsRecyclerAdapter extends RecyclerView.Adapter<RecipeStepsRecyclerAdapter.ViewHolder> {

    private Context mContext;
    private List<Step> mSteps;
    private OnRecipeStepClickListener mCallback;

    private ViewHolder mLastSelectedStepView;
    private Integer mCurrentlySelectedItemPosition;

    public interface OnRecipeStepClickListener {
        void onRecipeStepClicked(int id);
    }

    public RecipeStepsRecyclerAdapter(Context mContext, List<Step> mSteps, @Nullable Integer selectedItemPosition) {
        this.mContext = mContext;
        this.mSteps = mSteps;
        this.mCurrentlySelectedItemPosition = selectedItemPosition;
        try {
            mCallback = (OnRecipeStepClickListener) mContext;
        } catch (ClassCastException e) {
            throw new ClassCastException(mContext.toString()
                    + " must implement OnRecipeStepClickListener");
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.item_recipe_step, parent, false);
        return new RecipeStepsRecyclerAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        holder.mTextRecipeStep.setText(mSteps.get(position).getShortDescription());

        /* THIS (HIGHLIGHTING)DOES NOT WORK PROPERLY DUE TO RECYCLING BEHAVIOUR OF RECYCLER VIEW */

        //Since view is getting recycled, have to change background color of selected item
        //if the view is being recreated;
        if (mCurrentlySelectedItemPosition != null && mCurrentlySelectedItemPosition == position)
            holder.mTextRecipeStep.setBackgroundColor(mContext.getResources().getColor(R.color.colorPrimaryLight));
        else
            holder.mTextRecipeStep.setBackgroundColor(mContext.getResources().getColor(R.color.white));

        holder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Reset background color of last selected item to white
                //if the view is not recycled.
                if (mLastSelectedStepView != null)
                    mLastSelectedStepView.mTextRecipeStep.setBackgroundColor(
                            mContext.getResources().getColor(R.color.white)
                    );

                //Maintain position and view reference of currently selected step
                mCurrentlySelectedItemPosition = position;
                mLastSelectedStepView = holder;

                //Change background color of selected item
                holder.mTextRecipeStep.setBackgroundColor(mContext.getResources().getColor(R.color.colorPrimaryLight));
                mCallback.onRecipeStepClicked(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mSteps.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        View root;

        @BindView(R.id.text_recipe_step)
        TextView mTextRecipeStep;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this,view);
            root = view;
        }
    }
}
