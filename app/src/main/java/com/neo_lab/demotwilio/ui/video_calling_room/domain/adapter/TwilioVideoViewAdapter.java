package com.neo_lab.demotwilio.ui.video_calling_room.domain.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alibaba.android.vlayout.DelegateAdapter;
import com.alibaba.android.vlayout.LayoutHelper;
import com.alibaba.android.vlayout.VirtualLayoutManager;
import com.neo_lab.demotwilio.R;
import com.twilio.video.Participant;
import com.twilio.video.VideoTrack;
import com.twilio.video.VideoView;

import java.util.List;

/**
 * Created by sam_nguyen on 03/05/2017.
 */

public class TwilioVideoViewAdapter extends DelegateAdapter.Adapter<TwilioVideoViewAdapter.ViewHolder> {

    private Context context;

    private LayoutHelper layoutHelper;

    private VirtualLayoutManager.LayoutParams layoutParams;

    private List<VideoTrack> videoTracks;


    public TwilioVideoViewAdapter(Context context, LayoutHelper layoutHelper, List<VideoTrack> videoTracks) {
        this(context, layoutHelper, new VirtualLayoutManager.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 500), videoTracks);
    }

    public TwilioVideoViewAdapter(Context context, LayoutHelper layoutHelper, VirtualLayoutManager.LayoutParams layoutParams, List<VideoTrack> videoTracks) {
        this.context = context;
        this.layoutHelper = layoutHelper;
        this.layoutParams = layoutParams;
        this.videoTracks = videoTracks;
    }

    @Override
    public LayoutHelper onCreateLayoutHelper() {
        return layoutHelper;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.twilio_video_view_item, parent, false));

    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

//        holder.itemView.setLayoutParams(new VirtualLayoutManager.LayoutParams(layoutParams));

    }

    @Override
    protected void onBindViewHolderWithOffset(ViewHolder holder, int position, int offsetTotal) {

        Log.e("TwilioVideoViewAdapter", "onBindViewHolderWithOffset");

        VideoView videoView = (VideoView) holder.itemView.findViewById(R.id.video_view);

        VideoTrack videoTrack = this.videoTracks.get(position);

        videoTrack.addRenderer(videoView);
        /*
         * Add participant renderer
         *
         *
         */

    }

    @Override
    public int getItemCount() {
        return videoTracks.size();
    }
//
//    public static class ViewHolder extends RecyclerView.ViewHolder {
//        // Your holder should contain a member variable
//        // for any view that will be set as you render a row
//        public VideoView videoView;
//
//        public TextView tvParticipantName;
//
//        // We also create a constructor that accepts the entire item row
//        // and does the view lookups to find each subview
//        public ViewHolder(View itemView) {
//            // Stores the itemView in a public final member variable that can be used
//            // to access the context from any ViewHolder instance.
//            super(itemView);
//
//            videoView = (VideoView) itemView.findViewById(R.id.video_view);
//
//            tvParticipantName = (TextView) itemView.findViewById(R.id.tv_name_participant);
//        }
//    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        public static volatile int existing = 0;
        public static int createdTimes = 0;

        public ViewHolder(View itemView) {
            super(itemView);
            createdTimes++;
            existing++;
        }

        @Override
        protected void finalize() throws Throwable {
            existing--;
            super.finalize();
        }
    }
}
