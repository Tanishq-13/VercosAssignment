package com.example.vecrosassignment.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "streams")
public class Stream {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String name;
    public String rtsp;

    public Stream(String name, String rtsp) {
        this.name = name;
        this.rtsp = rtsp;
    }

    public String getName() {
        return name;
    }

    public String getRtsp() {
        return rtsp;
    }
}
