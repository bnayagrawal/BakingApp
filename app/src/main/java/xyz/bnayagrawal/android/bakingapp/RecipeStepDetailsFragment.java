package xyz.bnayagrawal.android.bakingapp;

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
import xyz.bnayagrawal.android.bakingapp.util.ExoPlayerApplication;

/**
 * Created by bnayagrawal on 23/3/18.
 */

public class RecipeStepDetailsFragment extends Fragment {
    public static final String ARGUMENT_STEP_INSTRUCTION = "step_instruction";
    public static final String ARGUMENT_VIDEO_INSTRUCTION_URL = "video_instruction_url";

    @BindView(R.id.playerView)
    PlayerView mPlayerView;

    @BindView(R.id.text_step_instruction)
    TextView mTextStepInstruction;

    private String mStepInstruction;
    private String mVideoInstructionURL;

    private boolean mLoadedInstructionsFromArguments;
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
        //If this fragment is instantiated by RecipeStepDetailsActivity
        Bundle bundle = getArguments();
        if (null != bundle) {
            mStepInstruction = bundle.getString(ARGUMENT_STEP_INSTRUCTION);
            mVideoInstructionURL = bundle.getString(ARGUMENT_VIDEO_INSTRUCTION_URL);
            mLoadedInstructionsFromArguments = true;
        }

        View view = inflater.inflate(R.layout.fragment_recipe_step_details, container, false);
        ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        initExoPlayer();
        if (mLoadedInstructionsFromArguments)
            updateContents();
    }

    @Override
    public void onStop() {
        super.onStop();
        mPlayer.release();
    }

    public void updateInstructions(String stepInstruction, String videoInstructionURL) {
        this.mStepInstruction = stepInstruction;
        this.mVideoInstructionURL = videoInstructionURL;
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
        return ((ExoPlayerApplication) getActivity().getApplication())
                .buildDataSourceFactory(useBandwidthMeter ? BANDWIDTH_METER : null);
    }

    private void updateMediaSource(String url) {
        Uri uri;
        if (url == null || url.length() == 0) {
            uri = null;
            Toast.makeText(getContext(),getString(R.string.video_not_available),Toast.LENGTH_SHORT).show();
        }
        else
            uri = Uri.parse(url);
        mPlayer.stop();
        mMediaSource = buildMediaSource(uri, mHandler, mEventLogger);
        mPlayer.prepare(mMediaSource);
        mPlayer.setPlayWhenReady(true);
    }

}
