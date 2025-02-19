package com.example.vecrosassignment.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface StreamDao {
    @Insert
    void insert(Stream stream);

    @Query("SELECT * FROM streams")
    List<Stream> getAllStreams();
}
