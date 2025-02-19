package com.example.vecrosassignment.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Stream.class}, version = 1, exportSchema = false)
public abstract class StreamDatabase extends RoomDatabase {
    private static volatile StreamDatabase INSTANCE;

    public abstract StreamDao streamDao();

    public static StreamDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (StreamDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    StreamDatabase.class, "streams_db")
                            .allowMainThreadQueries() // Only for testing, use background thread in production
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
