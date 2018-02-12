package com.example.rafaelsavaris.noteapplicationrx.utils;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

import java.util.concurrent.Executor;

/**
 * Created by rafael.savaris on 04/01/2018.
 */

public class MainThreadExecutor implements Executor {

    private Handler mHandler = new Handler(Looper.getMainLooper());


    @Override
    public void execute(@NonNull Runnable command) {
        mHandler.post(command);
    }

}
