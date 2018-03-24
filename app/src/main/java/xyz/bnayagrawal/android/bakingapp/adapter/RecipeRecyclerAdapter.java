package xyz.bnayagrawal.android.bakingapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import xyz.bnayagrawal.android.bakingapp.R;
import xyz.bnayagrawal.android.bakingapp.RecipeDetailsActivity;
import xyz.bnayagrawal.android.bakingapp.model.Recipe;

/**
 * Created by bnayagrawal on 23/3/18.
 */

public class RecipeRecyclerAdapter extends RecyclerView.Adapter<RecipeRecyclerAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<Recipe> mRecipes;

    public RecipeRecyclerAdapter(Context context, ArrayList<Recipe> recipes) {
        this.mContext = context;
        this.mRecipes = recipes;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.item_recipe, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        String recipeImageUrl = mRecipes.get(position).getImage();
        if(recipeImageUrl != null && recipeImageUrl.length() > 0)
            Glide.with(mContext).load(recipeImageUrl).into(holder.recipeImage);
        holder.recipeName.setText(mRecipes.get(position).getName());

        holder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, RecipeDetailsActivity.class);
                intent.putExtra(RecipeDetailsActivity.EXTRA_RECIPE,mRecipes.get(position));
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mRecipes.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        View root;

        @BindView(R.id.image_recipe)
        ImageView recipeImage;

        @BindView(R.id.text_recipe_name)
        TextView recipeName;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this,view);
            root = view;
        }
    }
}
