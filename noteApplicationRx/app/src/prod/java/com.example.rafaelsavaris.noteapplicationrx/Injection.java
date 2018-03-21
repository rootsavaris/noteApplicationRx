package com.example.rafaelsavaris.noteapplicationrx;

import android.content.Context;

import com.example.rafaelsavaris.noteapplicationrx.data.source.NotesRepository;
import com.example.rafaelsavaris.noteapplicationrx.data.source.local.NoteDatabase;
import com.example.rafaelsavaris.noteapplicationrx.data.source.local.NotesLocalDataSource;
import com.example.rafaelsavaris.noteapplicationrx.data.source.remote.NotesRemoteDataSource;
import com.example.rafaelsavaris.noteapplicationrx.utils.scheduler.BaseScheduler;
import com.example.rafaelsavaris.noteapplicationrx.utils.scheduler.SchedulerProvider;


/**
 * Created by rafael.savaris on 18/10/2017.
 */

public class Injection {

    public static NotesRepository providesNotesRepository(Context context){

        NoteDatabase database = NoteDatabase.getInstance(context);

        return NotesRepository.getInstance(NotesRemoteDataSource.getInstance(), NotesLocalDataSource.getInstance(database.noteDao()));

    }

    public static BaseScheduler provideShedulerProvider(){
        return SchedulerProvider.getInstance();
    }

}
