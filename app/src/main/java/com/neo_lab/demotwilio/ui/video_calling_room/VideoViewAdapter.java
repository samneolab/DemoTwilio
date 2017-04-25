package com.neo_lab.demotwilio.ui.video_calling_room;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.neo_lab.demotwilio.R;
import com.neo_lab.demotwilio.ui.video_calling_room.domain.model.VideoViewTwilio;
import com.twilio.video.VideoView;

import java.util.List;

/**
 * Created by sam_nguyen on 25/04/2017.
 */

public class VideoViewAdapter extends RecyclerView.Adapter<VideoViewAdapter.ViewHolder> {

    private List<VideoViewTwilio> videoViewTwilios;

    public VideoViewAdapter(List<VideoViewTwilio> videoViewTwilios) {
        this.videoViewTwilios = videoViewTwilios;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View view = inflater.inflate(R.layout.video_view, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        VideoViewTwilio videoViewTwilio = videoViewTwilios.get(position);
        holder.videoView.setMirror(false);
        videoViewTwilio.getVideoTrack().addRenderer(holder.videoView);

    }

    @Override
    public int getItemCount() {
        return videoViewTwilios.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public VideoView videoView;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            videoView = (VideoView) itemView.findViewById(R.id.video_view);
        }
    }
}
