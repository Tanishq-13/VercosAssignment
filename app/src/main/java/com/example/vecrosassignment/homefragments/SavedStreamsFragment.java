package com.example.vecrosassignment.homefragments;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vecrosassignment.R;
import com.example.vecrosassignment.StreamAdapter;
import com.example.vecrosassignment.database.Stream;
import com.example.vecrosassignment.database.StreamDatabase;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class SavedStreamsFragment extends Fragment {

    private RecyclerView recyclerView;
    private StreamAdapter streamAdapter;
    private TextView noStreamsText;
    private Button btnStartStreaming;
    private View emptyLayout;
    private StreamDatabase streamDatabase;

    public SavedStreamsFragment() { }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_saved_stream, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        noStreamsText = view.findViewById(R.id.noStreamsText);
        btnStartStreaming = view.findViewById(R.id.btnStartStreaming);
        emptyLayout = view.findViewById(R.id.emptyLayout);
        FloatingActionButton fabAddStream = view.findViewById(R.id.fab_add_stream);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        streamDatabase = StreamDatabase.getInstance(getContext());

        fabAddStream.setOnClickListener(v -> showAddStreamDialog());

        btnStartStreaming.setOnClickListener(v -> {
            // Navigate to Home Page
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.frame_layout, new HomeFragment())
                    .commit();
        });

        loadSavedStreams();

        return view;
    }

    private void loadSavedStreams() {
        List<Stream> savedStreams = streamDatabase.streamDao().getAllStreams();
        if (savedStreams.isEmpty()) {
            emptyLayout.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            emptyLayout.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            streamAdapter = new StreamAdapter(getContext(),savedStreams);
            recyclerView.setAdapter(streamAdapter);
        }
    }

    private void showAddStreamDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View dialogView = inflater.inflate(R.layout.dialog_add_stream, null);
        builder.setView(dialogView);

        EditText editName = dialogView.findViewById(R.id.editStreamName);
        EditText editRtsp = dialogView.findViewById(R.id.editStreamRtsp);
        Button btnSave = dialogView.findViewById(R.id.btnSaveStream);

        AlertDialog dialog = builder.create();
        dialog.show();

        btnSave.setOnClickListener(v -> {
            String name = editName.getText().toString().trim();
            String rtsp = editRtsp.getText().toString().trim();

            if (!name.isEmpty() && !rtsp.isEmpty()) {
                Stream newStream = new Stream(name, rtsp);
                streamDatabase.streamDao().insert(newStream);
                loadSavedStreams();
                dialog.dismiss();
            }
        });
    }
}
