package xyz.bnayagrawal.android.bakingapp;

import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.MediaSourceEventListener;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.util.EventLogger;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by bnayagrawal on 23/3/18.
 */

public class RecipeStepDetailsFragment extends Fragment {
    public static final String ARGUMENT_STEP_INSTRUCTION = "step_instruction";
    public static final String ARGUMENT_VIDEO_INSTRUCTION_URL = "video_instruction_url";
    public static final String ARGUMENT_IS_PLAYED_IN_TABLET = "is_played_in_tablet";

    private static final String EXTRA_STEP_INSTRUCTION = "extra_step_instruction";
    private static final String EXTRA_VIDEO_INSTRUCTION_URL = "extra_video_instruction_url";
    private static final String EXTRA_VIDEO_ELAPSED_TIME = "video_elapsed_time";

    @BindView(R.id.playerView)
    PlayerView mPlayerView;

    @BindView(R.id.text_step_instruction)
    TextView mTextStepInstruction;

    private String mStepInstruction;
    private String mVideoInstructionURL;
    private boolean mIsPlayedInTablet;
    private long mElapsedTime = 0;

    private SimpleExoPlayer mPlayer;
    private DataSource.Factory mMediaDataSourceFactory;
    private EventLogger mEventLogger;
    private Handler mHandler;
    private TrackSelection.Factory mVideoTrackSelectionFactory;
    private TrackSelector mTrackSelector;
    private MediaSource mMediaSource;
    private static final DefaultBandwidthMeter BANDWIDTH_METER = new DefaultBandwidthMeter();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        if (null != savedInstanceState) {
            mStepInstruction = savedInstanceState.getString(EXTRA_STEP_INSTRUCTION);
            mVideoInstructionURL = savedInstanceState.getString(EXTRA_VIDEO_INSTRUCTION_URL);
            mElapsedTime = savedInstanceState.getLong(EXTRA_VIDEO_ELAPSED_TIME);
        }

        View view = inflater.inflate(R.layout.fragment_recipe_step_details, container, false);
        ButterKnife.bind(this, view);

        //If this fragment is instantiated by RecipeStepDetailsActivity
        Bundle bundle = getArguments();
        if (null != bundle) {
            if(bundle.containsKey(ARGUMENT_STEP_INSTRUCTION))
                mStepInstruction = bundle.getString(ARGUMENT_STEP_INSTRUCTION);
            if(bundle.containsKey(ARGUMENT_VIDEO_INSTRUCTION_URL))
                mVideoInstructionURL = bundle.getString(ARGUMENT_VIDEO_INSTRUCTION_URL);
            if(bundle.containsKey(ARGUMENT_IS_PLAYED_IN_TABLET))
                mIsPlayedInTablet = bundle.getBoolean(ARGUMENT_IS_PLAYED_IN_TABLET);
        }

        if (!mIsPlayedInTablet && getResources().getConfiguration()
                .orientation == Configuration.ORIENTATION_LANDSCAPE) {
            mTextStepInstruction.setVisibility(View.GONE);
            mPlayerView.getLayoutParams()
                    .height = ViewGroup.LayoutParams.MATCH_PARENT;
        }

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        initExoPlayer();
        updateContents();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPlayer.release();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putString(EXTRA_STEP_INSTRUCTION, mStepInstruction);
        outState.putString(EXTRA_VIDEO_INSTRUCTION_URL, mVideoInstructionURL);
        outState.putLong(EXTRA_VIDEO_ELAPSED_TIME, mPlayer.getCurrentPosition());
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        //If not playing in tablet
        if (!mIsPlayedInTablet) {
            if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                mTextStepInstruction.setVisibility(View.GONE);
                mPlayerView.getLayoutParams()
                        .height = ViewGroup.LayoutParams.MATCH_PARENT;
            } else {
                mTextStepInstruction.setVisibility(View.VISIBLE);
                ViewGroup.LayoutParams params = mPlayerView.getLayoutParams();
                params.height = (int) (256 * getContext().getResources().getDisplayMetrics().density);
                params.width = ViewGroup.LayoutParams.MATCH_PARENT;
                mPlayerView.setLayoutParams(params);
            }
        }
    }

    public void updateInstructions(String stepInstruction, String videoInstructionURL) {
        this.mStepInstruction = stepInstruction;
        this.mVideoInstructionURL = videoInstructionURL;
        this.mElapsedTime = 0;
        updateContents();
    }

    private void updateContents() {
        mTextStepInstruction.setText(mStepInstruction);
        updateMediaSource(mVideoInstructionURL);
    }

    private void initExoPlayer() {
        //Default track selector
        mHandler = new Handler();
        mVideoTrackSelectionFactory = new AdaptiveTrackSelection.Factory(BANDWIDTH_METER);
        mTrackSelector = new DefaultTrackSelector(mVideoTrackSelectionFactory);

        //Bind the player to the view
        mPlayer = ExoPlayerFactory.newSimpleInstance(getContext(), mTrackSelector);
        mPlayerView.setPlayer(mPlayer);

        mEventLogger = new EventLogger(
                new DefaultTrackSelector(new AdaptiveTrackSelection.Factory(BANDWIDTH_METER)));
        mMediaDataSourceFactory = buildDataSourceFactory(true);
        mMediaSource = buildMediaSource(null, mHandler, mEventLogger);

        //set the video source
        mPlayer.prepare(mMediaSource);
    }

    private MediaSource buildMediaSource(Uri uri, @Nullable Handler handler,
                                         @Nullable MediaSourceEventListener listener) {
        return new ExtractorMediaSource.Factory(mMediaDataSourceFactory)
                .createMediaSource(uri, handler, listener);
    }

    private DataSource.Factory buildDataSourceFactory(boolean useBandwidthMeter) {
        return ((BakingApplication) getActivity().getApplication())
                .buildDataSourceFactory(useBandwidthMeter ? BANDWIDTH_METER : null);
    }

    private void updateMediaSource(String url) {
        Uri uri;
        if (url == null || url.length() == 0) {
            uri = null;
            Toast.makeText(getContext(), getString(R.string.video_not_available), Toast.LENGTH_SHORT).show();
        } else
            uri = Uri.parse(url);
        mPlayer.stop();
        mMediaSource = buildMediaSource(uri, mHandler, mEventLogger);
        mPlayer.prepare(mMediaSource);
        if (mElapsedTime > 0)
            mPlayer.seekTo(mElapsedTime);
        mPlayer.setPlayWhenReady(true);
    }
}
