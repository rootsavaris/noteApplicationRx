package com.example.rafaelsavaris.noteapplicationrx.utils;

import android.support.annotation.NonNull;

import java.util.concurrent.Executor;

/**
 * Created by rafael.savaris on 17/01/2018.
 */

public class SingleExecutor extends AppExecutors {

    private static Executor instance = new Executor() {
        @Override
        public void execute(@NonNull Runnable command) {
            command.run();
        }
    };

    public SingleExecutor(){
        super(instance, instance, instance);
    }

}
