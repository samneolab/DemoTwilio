package com.neo_lab.demotwilio.ui.video_calling_room;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.neo_lab.demotwilio.R;
import com.neo_lab.demotwilio.ui.video_calling_room.domain.model.VideoViewTwilio;
import com.neo_lab.demotwilio.utils.screen.ScreenUtils;
import com.twilio.video.VideoView;

import java.util.List;

/**
 * Created by sam_nguyen on 25/04/2017.
 */

public class VideoViewAdapter extends RecyclerView.Adapter<VideoViewAdapter.ViewHolder> {

    private Context context;

    private List<VideoViewTwilio> videoViewTwilios;

    private final int height;

    public VideoViewAdapter(List<VideoViewTwilio> videoViewTwilios, Context context) {
        this.videoViewTwilios = videoViewTwilios;
        this.context = context;

        height = ScreenUtils.getScreenHeight(context) - 100 ;
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

        switch (videoViewTwilios.size()) {
            case 1:
                holder.relativeLayout.getLayoutParams().height = height;
                break;
            case 2:
                holder.relativeLayout.getLayoutParams().height = height / 2;
                break;
            case 3:
                holder.relativeLayout.getLayoutParams().height = height / 3;
                break;
            case 4:
                holder.relativeLayout.getLayoutParams().height = height / 2;
                break;
            case 5:
                holder.relativeLayout.getLayoutParams().height = height / 3;
                break;
            case 6:
                holder.relativeLayout.getLayoutParams().height = height / 3;
                break;
            case 7:
                holder.relativeLayout.getLayoutParams().height = height / 4;
                break;
            case 8:
                holder.relativeLayout.getLayoutParams().height = height / 4;
                break;
            case 9:
                holder.relativeLayout.getLayoutParams().height = height / 5;
                break;
            case 10:
                holder.relativeLayout.getLayoutParams().height = height / 5;
                break;
            default:
                holder.relativeLayout.getLayoutParams().height = height / 8;
                break;
        }

        holder.relativeLayout.requestLayout();

    }

    @Override
    public int getItemCount() {
        return videoViewTwilios.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public VideoView videoView;

        public RelativeLayout relativeLayout;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);
            videoView = (VideoView) itemView.findViewById(R.id.video_view);
            relativeLayout = (RelativeLayout) itemView.findViewById(R.id.rl_main);
        }
    }
}
