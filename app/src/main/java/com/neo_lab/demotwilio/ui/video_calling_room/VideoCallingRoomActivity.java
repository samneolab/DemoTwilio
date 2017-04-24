package com.neo_lab.demotwilio.ui.video_calling_room;

import android.Manifest;
import android.content.Context;
import android.media.AudioManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.neo_lab.demotwilio.R;
import com.neo_lab.demotwilio.model.Token;
import com.neo_lab.demotwilio.share_preferences_manager.Key;
import com.neo_lab.demotwilio.share_preferences_manager.SharedPreferencesManager;
import com.neo_lab.demotwilio.ui.base.BaseActivity;
import com.neo_lab.demotwilio.ui.video_calling_room.domain.usecase.GetToken;
import com.neo_lab.demotwilio.use_case.UseCaseHandler;
import com.twilio.video.AudioTrack;
import com.twilio.video.CameraCapturer;
import com.twilio.video.ConnectOptions;
import com.twilio.video.LocalAudioTrack;
import com.twilio.video.LocalMedia;
import com.twilio.video.LocalVideoTrack;
import com.twilio.video.Media;
import com.twilio.video.Participant;
import com.twilio.video.Room;
import com.twilio.video.RoomState;
import com.twilio.video.TwilioException;
import com.twilio.video.Video;
import com.twilio.video.VideoRenderer;
import com.twilio.video.VideoTrack;
import com.twilio.video.VideoView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class VideoCallingRoomActivity extends BaseActivity implements VideoCallingRoomContract.View {

    private static final String TAG = "VideoCallingRoomActivity";

    private VideoCallingRoomContract.Presenter presenter;

    private String deviceId;

    private String userName;

    private String roomNumber;
    /*
     * A Room represents communication between a local participant and one or more participants.
     */
    private Room room;

    /*
     * A VideoView receives frames from a local or remote video track and renders them
     * to an associated view.
     */
//    @BindView(R.id.primary_video_view) VideoView primaryVideoView;

    private List<VideoView> videoViews = new ArrayList<>();

    @BindView(R.id.thumbnail_video_view) VideoView thumbnailVideoView;
    /*
     * Android application UI elements
     */
    @BindView(R.id.video_status_textview) TextView videoStatusTextView;

    private CameraCapturer cameraCapturer;

    private LocalMedia localMedia;

    private LocalAudioTrack localAudioTrack;

    private LocalVideoTrack localVideoTrack;

    @BindView(R.id.dis_connect_action_fab) FloatingActionButton disConnectActionFab;

    @BindView(R.id.switch_camera_action_fab) FloatingActionButton switchCameraActionFab;

    @BindView(R.id.local_video_action_fab) FloatingActionButton localVideoActionFab;

    @BindView(R.id.mute_action_fab) FloatingActionButton muteActionFab;

    private AudioManager audioManager;

    private String participantIdentity;

    private int previousAudioMode;

    private VideoRenderer localVideoView;

    private boolean disconnectedFromOnDestroy;

    @BindView(R.id.video_container) RelativeLayout videoContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize Presenter
        presenter = new VideoCallingRoomPresenter(UseCaseHandler.getInstance(), new GetToken(this));
        presenter.attachView(this);

        setContentView(R.layout.activity_video_calling_room);

        ButterKnife.bind(this);

        getLocalProperties();

        showUI();

        initializeCallingVideoRoom();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        switch (requestCode) {
            case PERMISSIONS_REQUEST_CODE:

                if (checkRequestPermissionsResult(grantResults)) {
                    createLocalMedia();
                    presenter.requestTokenCallingVideo(deviceId, userName);
                } else {
                    Toast.makeText(this, R.string.permissions_message_needed, Toast.LENGTH_LONG).show();
                }

                break;

            default:
                break;
        }
    }

    @Override
    protected  void onResume() {
        super.onResume();
        /*
         * If the local video track was removed when the app was put in the background, add it back.
         */
        if (localMedia != null && localVideoTrack == null) {
            localVideoTrack = localMedia.addVideoTrack(true, cameraCapturer);
            localVideoTrack.addRenderer(localVideoView);
        }
    }

    @Override
    protected void onPause() {
        /*
         * Remove the local video track before going in the background. This ensures that the
         * camera can be used by other applications while this app is in the background.
         *
         * If this local video track is being shared in a Room, participants will be notified
         * that the track has been removed.
         */
        if (localMedia != null && localVideoTrack != null) {
            localMedia.removeVideoTrack(localVideoTrack);
            localVideoTrack = null;
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        /*
         * Always disconnect from the room before leaving the Activity to
         * ensure any memory allocated to the Room resource is freed.
         */
        if (room != null && room.getState() != RoomState.DISCONNECTED) {
            room.disconnect();
            disconnectedFromOnDestroy = true;
        }

        /*
         * Release the local media ensuring any memory allocated to audio or video is freed.
         */
        if (localMedia != null) {
            localMedia.release();
            localMedia = null;
        }

        presenter.detachView();

        super.onDestroy();
    }



    @Override
    public void getLocalProperties() {

        // Get Device Id
        deviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        userName = SharedPreferencesManager.getInstance(VideoCallingRoomActivity.this).
                getString(Key.USER_NAME);
        roomNumber = SharedPreferencesManager.getInstance(VideoCallingRoomActivity.this).
                getString(Key.ROOM_NUMBER);

    }

    @Override
    public void showUI() {
        VideoView videoView = (VideoView) findViewById(R.id.primary_video_view);
        videoViews.add(videoView);

    }


    @Override
    public void initializeCallingVideoRoom() {

         /*
         * Enable changing the volume using the up/down keys during a conversation
         */
        setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);

        /*
         * Needed for setting/abandoning audio focus during call
         */
        audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);

        /*
         * Check camera and microphone permissions. Needed in Android M.
         */

        String[] permissions = { Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO };

        if (!isSpecificPermissionsGranted(permissions)) {
            requestSpecificPermissions(permissions, R.string.permissions_message_needed);
        } else {
            createLocalMedia();
            presenter.requestTokenCallingVideo(deviceId, userName);
        }
        /*
         * Set the initial state of the UI
         */
        intializeActionFabs();

    }

    private void intializeActionFabs() {

        switchCameraActionFab.show();
        switchCameraActionFab.setOnClickListener(switchCameraClickListener());

        localVideoActionFab.show();
        localVideoActionFab.setOnClickListener(localVideoClickListener());

        muteActionFab.show();
        muteActionFab.setOnClickListener(muteClickListener());

        disConnectActionFab.show();
        disConnectActionFab.setOnClickListener(disConnectVideoCallingRoom());

    }

    private void createLocalMedia() {
        localMedia = LocalMedia.create(this);

        // Share your microphone
        localAudioTrack = localMedia.addAudioTrack(true);

        // Share your camera
        cameraCapturer = new CameraCapturer(this, CameraCapturer.CameraSource.FRONT_CAMERA);
        localVideoTrack = localMedia.addVideoTrack(true, cameraCapturer);
        videoViews.get(0).setMirror(true);
        localVideoTrack.addRenderer(videoViews.get(0));
        localVideoView = videoViews.get(0);
    }

    /*
     * Room events listener
     */
    private Room.Listener roomListener() {
        return new Room.Listener() {
            @Override
            public void onConnected(Room room) {
                videoStatusTextView.setText("Connected to the room number " + room.getName() + "\nThere is only you in this room\nPlease wait for another participant");
                setTitle(room.getName());

                for (Map.Entry<String, Participant> entry : room.getParticipants().entrySet()) {
                    addParticipant(entry.getValue());
                    break;
                }
            }

            @Override
            public void onConnectFailure(Room room, TwilioException e) {
                videoStatusTextView.setText("Failed to connect");
            }

            @Override
            public void onDisconnected(Room room, TwilioException e) {
                videoStatusTextView.setText("Disconnected from " + room.getName());
                VideoCallingRoomActivity.this.room = null;
                // Only reinitialize the UI if disconnect was not called from onDestroy()
                if (!disconnectedFromOnDestroy) {
                    setAudioFocus(false);
                    intializeActionFabs();
                    moveLocalVideoToPrimaryView();
                }
            }

            @Override
            public void onParticipantConnected(Room room, Participant participant) {
                addParticipant(participant);

            }

            @Override
            public void onParticipantDisconnected(Room room, Participant participant) {
                removeParticipant(participant);
            }

            @Override
            public void onRecordingStarted(Room room) {
                /*
                 * Indicates when media shared to a Room is being recorded. Note that
                 * recording is only available in our Group Rooms developer preview.
                 */
                Log.d(TAG, "onRecordingStarted");
            }

            @Override
            public void onRecordingStopped(Room room) {
                /*
                 * Indicates when media shared to a Room is no longer being recorded. Note that
                 * recording is only available in our Group Rooms developer preview.
                 */
                Log.d(TAG, "onRecordingStopped");
            }
        };
    }

    private Media.Listener mediaListener() {
        return new Media.Listener() {

            @Override
            public void onAudioTrackAdded(Media media, AudioTrack audioTrack) {
                videoStatusTextView.setText("onAudioTrackAdded");
                videoStatusTextView.setText("You are in the room number " + roomNumber);
            }

            @Override
            public void onAudioTrackRemoved(Media media, AudioTrack audioTrack) {
                videoStatusTextView.setText("onAudioTrackRemoved");
            }

            @Override
            public void onVideoTrackAdded(Media media, VideoTrack videoTrack) {
                videoStatusTextView.setText("onVideoTrackAdded");
                addParticipantVideo(videoTrack);
            }

            @Override
            public void onVideoTrackRemoved(Media media, VideoTrack videoTrack) {
                videoStatusTextView.setText("onVideoTrackRemoved");
                removeParticipantVideo(videoTrack);
            }

            @Override
            public void onAudioTrackEnabled(Media media, AudioTrack audioTrack) {

            }

            @Override
            public void onAudioTrackDisabled(Media media, AudioTrack audioTrack) {

            }

            @Override
            public void onVideoTrackEnabled(Media media, VideoTrack videoTrack) {

            }

            @Override
            public void onVideoTrackDisabled(Media media, VideoTrack videoTrack) {

            }
        };
    }


    private void connectToVideoRoom(String roomName, String accessToken) {

        setAudioFocus(true);
        ConnectOptions connectOptions = new ConnectOptions.Builder(accessToken)
                .roomName(roomName)
                .localMedia(localMedia)
                .build();

        room = Video.connect(VideoCallingRoomActivity.this, connectOptions, roomListener());
    }


    private void setAudioFocus(boolean focus) {
        if (focus) {
            previousAudioMode = audioManager.getMode();
            // Request audio focus before making any device switch.
            audioManager.requestAudioFocus(null, AudioManager.STREAM_VOICE_CALL,
                    AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
            /*
             * Use MODE_IN_COMMUNICATION as the default audio mode. It is required
             * to be in this mode when playout and/or recording starts for the best
             * possible VoIP performance. Some devices have difficulties with
             * speaker mode if this is not set.
             */
            audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
        } else {
            audioManager.setMode(previousAudioMode);
            audioManager.abandonAudioFocus(null);
        }
    }


    private View.OnClickListener switchCameraClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cameraCapturer != null) {
                    CameraCapturer.CameraSource cameraSource = cameraCapturer.getCameraSource();
                    cameraCapturer.switchCamera();
                    if (thumbnailVideoView.getVisibility() == View.VISIBLE) {
                        thumbnailVideoView.setMirror(cameraSource == CameraCapturer.CameraSource.BACK_CAMERA);
                    } else {
                        videoViews.get(0).setMirror(cameraSource == CameraCapturer.CameraSource.BACK_CAMERA);
                    }
                }
            }
        };
    }

    private View.OnClickListener localVideoClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                 * Enable/disable the local video track
                 */
                if (localVideoTrack != null) {
                    boolean enable = !localVideoTrack.isEnabled();
                    localVideoTrack.enable(enable);
                    int icon;
                    if (enable) {
                        icon = R.drawable.ic_videocam_green_24px;
                        switchCameraActionFab.show();
                    } else {
                        icon = R.drawable.ic_videocam_off_red_24px;
                        switchCameraActionFab.hide();
                    }
                    localVideoActionFab.setImageDrawable(
                            ContextCompat.getDrawable(VideoCallingRoomActivity.this, icon));
                }
            }
        };
    }

    private View.OnClickListener muteClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                 * Enable/disable the local audio track. The results of this operation are
                 * signaled to other Participants in the same Room. When an audio track is
                 * disabled, the audio is muted.
                 */
                if (localAudioTrack != null) {
                    boolean enable = !localAudioTrack.isEnabled();
                    localAudioTrack.enable(enable);
                    int icon = enable ?
                            R.drawable.ic_mic_green_24px : R.drawable.ic_mic_off_red_24px;
                    muteActionFab.setImageDrawable(ContextCompat.getDrawable(
                            VideoCallingRoomActivity.this, icon));
                }
            }
        };
    }

    private View.OnClickListener disConnectVideoCallingRoom() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new MaterialDialog.Builder(VideoCallingRoomActivity.this)
                        .title(R.string.app_name)
                        .content(R.string.confirmation_message_leaving_video_calling_room)
                        .positiveText(R.string.action_message_yes)
                        .negativeText(R.string.action_message_no)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                                finish();

                            }
                        })
                        .show();

            }
        };
    }


    /*
     * Called when participant joins the room
     */
    private void addParticipant(Participant participant) {
        /*
         * This app only displays video for one additional participant per Room
         */
//        if (thumbnailVideoView.getVisibility() == View.VISIBLE) {
//            return;
//        }
        participantIdentity = participant.getIdentity();
        videoStatusTextView.setText("Participant "+ participantIdentity + " joined");

        /*
         * Add participant renderer
         */
        if (participant.getMedia().getVideoTracks().size() > 0) {
            addParticipantVideo(participant.getMedia().getVideoTracks().get(0));
        }

        /*
         * Start listening for participant media events
         */
        participant.getMedia().setListener(mediaListener());
    }

    /*
     * Set primary view as renderer for participant video track
     */
    private void addParticipantVideo(VideoTrack videoTrack) {
        moveLocalVideoToThumbnailView();
        videoViews.get(0).setMirror(false);
        videoTrack.addRenderer(videoViews.get(0));
    }

    private void moveLocalVideoToThumbnailView() {
        if (thumbnailVideoView.getVisibility() == View.GONE) {
            thumbnailVideoView.setVisibility(View.VISIBLE);
            localVideoTrack.removeRenderer(videoViews.get(0));
            localVideoTrack.addRenderer(thumbnailVideoView);
            localVideoView = thumbnailVideoView;
            thumbnailVideoView.setMirror(cameraCapturer.getCameraSource() ==
                    CameraCapturer.CameraSource.FRONT_CAMERA);
        }
    }

    /*
     * Called when participant leaves the room
     */
    private void removeParticipant(Participant participant) {
        videoStatusTextView.setText("Participant "+participant.getIdentity()+ " left.");
        if (!participant.getIdentity().equals(participantIdentity)) {
            return;
        }

        /*
         * Remove participant renderer
         */
        if (participant.getMedia().getVideoTracks().size() > 0) {
            removeParticipantVideo(participant.getMedia().getVideoTracks().get(0));
        }
        participant.getMedia().setListener(null);
        moveLocalVideoToPrimaryView();
    }

    private void removeParticipantVideo(VideoTrack videoTrack) {
        videoTrack.removeRenderer(videoViews.get(0));
    }

    private void moveLocalVideoToPrimaryView() {
        if (thumbnailVideoView.getVisibility() == View.VISIBLE) {
            localVideoTrack.removeRenderer(thumbnailVideoView);
            thumbnailVideoView.setVisibility(View.GONE);
            localVideoTrack.addRenderer(videoViews.get(0));
            localVideoView = videoViews.get(0);
            videoViews.get(0).setMirror(cameraCapturer.getCameraSource() ==
                    CameraCapturer.CameraSource.FRONT_CAMERA);
        }
    }

    @Override
    public void onListenerRequestVideoToken(boolean status, String message, Token token) {

        if (status) {
            connectToVideoRoom(roomNumber, token.getToken());
        } else {
            updateStatusRequestVideoToken(status, message);
        }

    }

    @Override
    public void updateStatusRequestVideoToken(boolean status, String message) {
        if (!status) {
            videoStatusTextView.setText(message);
        }


    }

    @Override
    public BaseActivity getActivity() {
        return VideoCallingRoomActivity.this;
    }
}
