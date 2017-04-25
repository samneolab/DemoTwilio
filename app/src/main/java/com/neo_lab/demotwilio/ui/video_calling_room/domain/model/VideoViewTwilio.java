package com.neo_lab.demotwilio.ui.video_calling_room.domain.model;

import com.twilio.video.Participant;
import com.twilio.video.VideoTrack;

/**
 * Created by sam_nguyen on 25/04/2017.
 */

public class VideoViewTwilio {

    private VideoTrack videoTrack;

    private String id;

    private Participant participant;

    public VideoViewTwilio(VideoTrack videoTrack, Participant participant) {
        this.videoTrack = videoTrack;
        this.participant = participant;
    }

    public VideoViewTwilio(VideoTrack videoTrack, String id) {
        this.videoTrack = videoTrack;
        this.id = id;
    }

    public VideoTrack getVideoTrack() {
        return videoTrack;
    }

    public void setVideoTrack(VideoTrack videoTrack) {
        this.videoTrack = videoTrack;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Participant getParticipant() {
        return participant;
    }

    public void setParticipant(Participant participant) {
        this.participant = participant;
    }
}
