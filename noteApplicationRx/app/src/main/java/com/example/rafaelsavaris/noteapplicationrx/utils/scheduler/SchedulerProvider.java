package com.example.rafaelsavaris.noteapplicationrx.utils.scheduler;

import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by rafael.savaris on 14/02/2018.
 */

public class SchedulerProvider implements BaseScheduler{

    private static SchedulerProvider mInstance;

    public static synchronized SchedulerProvider getInstance(){

        if (mInstance == null){
            mInstance = new SchedulerProvider();
        }

        return mInstance;

    }

    private SchedulerProvider(){}

    @Override
    public Scheduler computation() {
        return Schedulers.computation();
    }

    @Override
    public Scheduler io() {
        return Schedulers.io();
    }

    @Override
    public Scheduler ui() {
        return AndroidSchedulers.mainThread();
    }
}
