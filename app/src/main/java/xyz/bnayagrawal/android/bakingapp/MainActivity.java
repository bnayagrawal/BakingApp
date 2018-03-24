package xyz.bnayagrawal.android.bakingapp;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.wasabeef.recyclerview.animators.FadeInUpAnimator;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import xyz.bnayagrawal.android.bakingapp.adapter.RecipeRecyclerAdapter;
import xyz.bnayagrawal.android.bakingapp.model.Recipe;
import xyz.bnayagrawal.android.bakingapp.net.RecipeService;
import xyz.bnayagrawal.android.bakingapp.util.ServerUtil;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String EXTRA_RECIPE_LIST = "recipe_list";

    @BindView(R.id.recycler_view_recipe)
    RecyclerView mRecyclerViewRecipe;

    @BindView(R.id.layout_progress_view)
    ConstraintLayout mLayoutProgressView;

    @BindView(R.id.layout_error_view)
    ConstraintLayout mLayoutErrorView;

    @BindView(R.id.text_error_message)
    TextView mTextErrorMessage;

    @BindView(R.id.button_retry)
    Button mButtonRetry;

    private Retrofit mRetrofit;
    private RecipeService mRecipeService;
    private retrofit2.Call<List<Recipe>> mCall;

    private ArrayList<Recipe> mRecipes;
    private RecipeRecyclerAdapter mAdapter;

    private Animation mFadeInAnimation;
    private Animation mFadeOutAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mFadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        mFadeOutAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_out);

        initRecyclerView();
        initRetrofit();

        if(null != savedInstanceState) {
            mRecipes = savedInstanceState.getParcelableArrayList(EXTRA_RECIPE_LIST);
            mAdapter.notifyDataSetChanged();
        } else {
            fetchRecipeList();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if(mRecipes != null)
            outState.putParcelableArrayList(EXTRA_RECIPE_LIST,mRecipes);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != mCall && mCall.isExecuted())
            mCall.cancel();
    }

    private void initRecyclerView() {
        RecyclerView.LayoutManager layoutManager =
                new GridLayoutManager(
                        this,
                        getSpanCountBasedOnScreenWidth()
                );

        mRecyclerViewRecipe.setLayoutManager(layoutManager);
        mRecyclerViewRecipe.setItemAnimator(new FadeInUpAnimator());

        mRecipes = new ArrayList<>();
        mAdapter = new RecipeRecyclerAdapter(this, mRecipes);
        mRecyclerViewRecipe.setAdapter(mAdapter);
    }

    private void initRetrofit() {
        mRetrofit = new Retrofit.Builder()
                .baseUrl(ServerUtil.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    private void fetchRecipeList() {
        if (null == mRecipeService)
            mRecipeService = mRetrofit.create(RecipeService.class);
        if (null != mCall && mCall.isExecuted())
            mCall.cancel();
        toggleProgressView(true);

        mCall = mRecipeService.getRecipeList();
        mCall.enqueue(new Callback<List<Recipe>>() {
            @Override
            public void onResponse(Call<List<Recipe>> call, Response<List<Recipe>> response) {
                if (response.isSuccessful()) {
                    List<Recipe> recipes = response.body();
                    if (null == recipes) {
                        return;
                    }

                    for (Recipe recipe : recipes) {
                        mRecipes.add(recipe);
                        mAdapter.notifyItemInserted(mRecipes.size());
                    }
                    toggleProgressView(false);
                } else {
                    showErrorView(getString(R.string.error));
                }
            }

            @Override
            public void onFailure(Call<List<Recipe>> call, Throwable t) {
                t.printStackTrace();
                showErrorView(getString(R.string.error));
            }
        });
    }

    private int getSpanCountBasedOnScreenWidth() {
        switch ((String)mRecyclerViewRecipe.getTag()) {
            case "layout-default":
                return 1;
            case "layout-default-land":
                return 2;
            case "layout-sw600dp":
                return 2;
            case "layout-sw600dp-land":
                return 4;
            case "layout-sw720dp":
                return 4;
            case "layout-sw720dp-land":
                return 6;
            default:
                return 1;
        }
    }

    private boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    private void toggleProgressView(boolean show) {
        if (show) {
            mRecyclerViewRecipe.setVisibility(View.GONE);
            mLayoutErrorView.setVisibility(View.GONE);
            mLayoutProgressView.setVisibility(View.VISIBLE);
            mLayoutProgressView.startAnimation(mFadeInAnimation);
        } else {
            mLayoutProgressView.setVisibility(View.GONE);
            mLayoutProgressView.startAnimation(mFadeOutAnimation);
            mRecyclerViewRecipe.setVisibility(View.VISIBLE);
        }
    }

    private void showErrorView(String message) {
        mTextErrorMessage.setText(message);
        mLayoutErrorView.setVisibility(View.VISIBLE);
        mLayoutErrorView.startAnimation(mFadeInAnimation);
        mRecyclerViewRecipe.setVisibility(View.GONE);
        mLayoutProgressView.setVisibility(View.GONE);
    }

    private void hideErrorView() {
        mLayoutErrorView.setVisibility(View.GONE);
        mLayoutErrorView.startAnimation(mFadeOutAnimation);
        mRecyclerViewRecipe.setVisibility(View.VISIBLE);
    }
}
