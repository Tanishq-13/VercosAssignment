package com.example.vecrosassignment.homefragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.vecrosassignment.R;
import com.example.vecrosassignment.videoplayer.VideoPlayerActivity;

public class HomeFragment extends Fragment {

    private EditText editTextRtsp;
    private Button btnGetStreaming;

    public HomeFragment() { }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        editTextRtsp = view.findViewById(R.id.editTextRtsp);
        btnGetStreaming = view.findViewById(R.id.btnGetStreaming);

        btnGetStreaming.setOnClickListener(v -> {
            String rtspUrl = editTextRtsp.getText().toString().trim();
            if (!rtspUrl.isEmpty()) {
                Intent intent = new Intent(getActivity(), VideoPlayerActivity.class);
                intent.putExtra("RTSP_URL", rtspUrl);
                startActivity(intent);
            } else {
                editTextRtsp.setError("Please enter a valid RTSP URL");
            }
        });

        return view;
    }
}
