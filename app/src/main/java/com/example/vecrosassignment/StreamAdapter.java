package com.example.vecrosassignment;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vecrosassignment.R;
import com.example.vecrosassignment.database.Stream;
import com.example.vecrosassignment.videoplayer.VideoPlayerActivity;

import java.util.List;

public class StreamAdapter extends RecyclerView.Adapter<StreamAdapter.StreamViewHolder> {

    private final List<Stream> streamList;
    private final Context context;

    public StreamAdapter(Context context, List<Stream> streamList) {
        this.context = context;
        this.streamList = streamList;
    }

    @NonNull
    @Override
    public StreamViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.itemsaved, parent, false);
        return new StreamViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StreamViewHolder holder, int position) {
        Stream stream = streamList.get(position);
        holder.textViewName.setText(stream.getName());
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, VideoPlayerActivity.class);
            intent.putExtra("RTSP_URL", stream.getRtsp());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return streamList.size();
    }

    static class StreamViewHolder extends RecyclerView.ViewHolder {
        TextView textViewName;

        public StreamViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.heading);
        }
    }
}
