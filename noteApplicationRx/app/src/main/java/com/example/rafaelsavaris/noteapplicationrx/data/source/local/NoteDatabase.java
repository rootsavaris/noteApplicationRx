package com.example.rafaelsavaris.noteapplicationrx.data.source.local;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.example.rafaelsavaris.noteapplicationrx.data.model.Note;

/**
 * Created by rafael.savaris on 04/01/2018.
 */

@Database(exportSchema = false, entities = {Note.class}, version = 1)
public abstract class NoteDatabase extends RoomDatabase {

    private static NoteDatabase mInstance;

    public abstract NoteDao noteDao();

    private static final Object sLock = new Object();

    public static NoteDatabase getInstance(Context context){

        synchronized (sLock){

            if (mInstance == null){
                mInstance = Room.databaseBuilder(context.getApplicationContext(), NoteDatabase.class, "Notes.db").build();
            }

            return mInstance;

        }

    }

}
