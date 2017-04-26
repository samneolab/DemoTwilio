package com.neo_lab.demotwilio.ui.video_calling_room;

import com.neo_lab.demotwilio.model.Token;
import com.neo_lab.demotwilio.ui.base.BasePresenter;
import com.neo_lab.demotwilio.ui.base.BaseView;
import com.twilio.video.Participant;
import com.twilio.video.VideoTrack;

/**
 * Created by sam_nguyen on 18/04/2017.
 */

public interface VideoCallingRoomContract {

    interface View extends BaseView {

        void getLocalProperties();

        void initializeCallingVideoRoom();

        void connectToVideoRoom(String roomName, String accessToken);

        void createLocalMedia();

        void setAudioFocus(boolean focus);

        void addParticipant(Participant participant);

        void addParticipantVideo(VideoTrack videoTrack, Participant participant);

        void moveLocalVideoToThumbnailView();

        void removeParticipant(Participant participant);

        void removeParticipantVideo(VideoTrack videoTrack);

        void initializeRecyclerViewVideoView();

        void moveLocalVideoToPrimaryView();

        void onListenerRequestVideoToken(boolean status, String message, Token token);

        void updateStatusRequestVideoToken(boolean status, String message);

    }

    interface Presenter extends BasePresenter<View> {

        void requestTokenCallingVideo(String deviceId, String userName);

    }
}
