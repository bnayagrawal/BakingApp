package xyz.bnayagrawal.android.bakingapp;

import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.MediaSourceEventListener;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
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

public class RecipeStepDetailsFragment extends Fragment
        implements ExoPlayer.EventListener {
    private static final String TAG = RecipeStepDetailsFragment.class.getSimpleName();
    public static final String ARGUMENT_STEP_INSTRUCTION = "arg_step_instruction";
    public static final String ARGUMENT_VIDEO_INSTRUCTION_URL = "arg_video_instruction_url";
    public static final String ARGUMENT_IS_PLAYED_IN_TABLET = "arg_is_played_in_tablet";
    public static final String ARGUMENT_STEP_IMAGE_URL = "arg_step_image_url";

    private static final String EXTRA_STEP_INSTRUCTION = "extra_step_instruction";
    private static final String EXTRA_VIDEO_INSTRUCTION_URL = "extra_video_instruction_url";
    private static final String EXTRA_STEP_IMAGE_URL = "extra_step_image_url";
    private static final String EXTRA_VIDEO_ELAPSED_TIME = "extra_video_elapsed_time";
    private static final String EXTRA_VIDEO_PLAYBACK_STATE = "extra_video_playback_state";

    @BindView(R.id.playerView)
    PlayerView mPlayerView;

    @BindView(R.id.text_step_instruction)
    TextView mTextStepInstruction;

    @BindView(R.id.image_recipe_step)
    ImageView mImageRecipeStep;

    private String mStepInstruction;
    private String mVideoInstructionURL;
    private String mStepImageUrl;
    private boolean mIsPlayedInTablet;
    private long mElapsedTime = 0;
    private boolean mIsPlayWhenReady;
    private boolean mDoNotCallUpdateContents;

    private SimpleExoPlayer mPlayer;
    private DataSource.Factory mMediaDataSourceFactory;
    private EventLogger mEventLogger;
    private Handler mHandler;
    private TrackSelection.Factory mVideoTrackSelectionFactory;
    private TrackSelector mTrackSelector;
    private MediaSessionCompat mMediaSession;
    private MediaSource mMediaSource;
    private PlaybackStateCompat.Builder mStateBuilder;
    private static final DefaultBandwidthMeter BANDWIDTH_METER = new DefaultBandwidthMeter();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_recipe_step_details, container, false);
        ButterKnife.bind(this, view);

        //If this fragment is instantiated by RecipeStepDetailsActivity
        Bundle bundle = getArguments();
        if (null != bundle) {
            if (bundle.containsKey(ARGUMENT_STEP_INSTRUCTION))
                mStepInstruction = bundle.getString(ARGUMENT_STEP_INSTRUCTION);
            if (bundle.containsKey(ARGUMENT_VIDEO_INSTRUCTION_URL))
                mVideoInstructionURL = bundle.getString(ARGUMENT_VIDEO_INSTRUCTION_URL);
            if(bundle.containsKey(ARGUMENT_STEP_IMAGE_URL))
                mStepImageUrl = bundle.getString(ARGUMENT_STEP_IMAGE_URL);
            if (bundle.containsKey(ARGUMENT_IS_PLAYED_IN_TABLET)) {
                mIsPlayedInTablet = bundle.getBoolean(ARGUMENT_IS_PLAYED_IN_TABLET);
                if(mIsPlayedInTablet) mDoNotCallUpdateContents = true;
            }
        }

        if (null != savedInstanceState) {
            mStepInstruction = savedInstanceState.getString(EXTRA_STEP_INSTRUCTION);
            mVideoInstructionURL = savedInstanceState.getString(EXTRA_VIDEO_INSTRUCTION_URL);
            mStepImageUrl = savedInstanceState.getString(EXTRA_STEP_IMAGE_URL);
            mElapsedTime = savedInstanceState.getLong(EXTRA_VIDEO_ELAPSED_TIME);
            mIsPlayWhenReady = savedInstanceState.getBoolean(EXTRA_VIDEO_PLAYBACK_STATE);

            //Redundant, but i have to set it to false
            //else onResume() method wont call updateContents()
            //when a config change occurs.
            this.mDoNotCallUpdateContents = false;
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
    public void onResume() {
        super.onResume();
        initializeMediaSession();
        initExoPlayer();
        //This if statement may be a little confusing. But this is to
        //prevent calling updateContents when this fragment is first
        //instantiated by RecipeDetailsActivity to display in tablet.
        //The use of updateContents() is to update the instruction text
        //and video or image if provided for a step. But since RecipeDetailActivity
        //do not provide any content information when instantiating this fragment,
        //there is no need to call updateContents()
        if(mDoNotCallUpdateContents) return;
        updateContents();
    }

    @Override
    public void onStop() {
        super.onStop();
        if(mPlayer != null) {
            mElapsedTime = mPlayer.getCurrentPosition();
            mIsPlayWhenReady = mPlayer.getPlayWhenReady();
            mPlayer.stop();
            mPlayer.release();
            mPlayer = null;
            mMediaSession.setActive(false);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putString(EXTRA_STEP_INSTRUCTION, mStepInstruction);
        outState.putString(EXTRA_VIDEO_INSTRUCTION_URL, mVideoInstructionURL);
        outState.putString(EXTRA_STEP_IMAGE_URL,mStepImageUrl);
        outState.putLong(EXTRA_VIDEO_ELAPSED_TIME, mPlayer.getCurrentPosition());
        outState.putBoolean(EXTRA_VIDEO_PLAYBACK_STATE,mPlayer.getPlayWhenReady());
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
        mPlayer.addListener(this);
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
        Uri uri = null;
        if (url == null || url.length() == 0) {
            Toast.makeText(getContext(), getString(R.string.video_not_available), Toast.LENGTH_SHORT).show();
            //Show recipe step image, if provided
            mImageRecipeStep.setVisibility(View.VISIBLE);
            if(mStepImageUrl != null && mStepImageUrl.length() > 0) {
                Glide.with(getContext()).load(mStepImageUrl).into(mImageRecipeStep);
            }
        } else {
            //Hide image if visible
            mImageRecipeStep.setVisibility(View.GONE);
            uri = Uri.parse(url);
        }
        mPlayer.stop();
        mMediaSource = buildMediaSource(uri, mHandler, mEventLogger);
        mPlayer.prepare(mMediaSource);
        //Restore playback position
        if (mElapsedTime > 0)
            mPlayer.seekTo(mElapsedTime);
        mPlayer.setPlayWhenReady(mIsPlayWhenReady);
    }

    private void initializeMediaSession() {
        mMediaSession = new MediaSessionCompat(getContext(), TAG);

        mMediaSession.setFlags(
                MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                        MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);

        mMediaSession.setMediaButtonReceiver(null);
        mStateBuilder = new PlaybackStateCompat.Builder()
                .setActions(
                        PlaybackStateCompat.ACTION_PLAY |
                                PlaybackStateCompat.ACTION_PAUSE |
                                PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS |
                                PlaybackStateCompat.ACTION_PLAY_PAUSE);

        mMediaSession.setPlaybackState(mStateBuilder.build());
        mMediaSession.setCallback(new MySessionCallback());
        mMediaSession.setActive(true);
    }

    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest, int reason) {

    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

    }

    @Override
    public void onLoadingChanged(boolean isLoading) {

    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        if((playbackState == ExoPlayer.STATE_READY) && playWhenReady){
            mStateBuilder.setState(PlaybackStateCompat.STATE_PLAYING,
                    mPlayer.getCurrentPosition(), 1f);
        } else if((playbackState == ExoPlayer.STATE_READY)){
            mStateBuilder.setState(PlaybackStateCompat.STATE_PAUSED,
                    mPlayer.getCurrentPosition(), 1f);
        }
        mMediaSession.setPlaybackState(mStateBuilder.build());
    }

    @Override
    public void onRepeatModeChanged(int repeatMode) {

    }

    @Override
    public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {

    }

    @Override
    public void onPositionDiscontinuity(int reason) {

    }

    @Override
    public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

    }

    @Override
    public void onSeekProcessed() {

    }

    private class MySessionCallback extends MediaSessionCompat.Callback {
        @Override
        public void onPlay() {
            mPlayer.setPlayWhenReady(true);
        }

        @Override
        public void onPause() {
            mPlayer.setPlayWhenReady(false);
        }

        @Override
        public void onSkipToPrevious() {
            mPlayer.seekTo(0);
        }
    }
}
