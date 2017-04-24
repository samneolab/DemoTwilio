package com.neo_lab.demotwilio.di.components;

import com.neo_lab.demotwilio.ui.video_calling_room.VideoCallingRoomActivity;

import dagger.Component;

/**
 * Created by sam_nguyen on 21/04/2017.
 */

@Component(modules = NetComponent.class)
public interface NetComponent {
    void inject(VideoCallingRoomActivity activity);
}
