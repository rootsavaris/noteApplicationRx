package com.example.rafaelsavaris.noteapplicationrx.utils.scheduler;

import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by rafael.savaris on 14/02/2018.
 */

public class ImmediateSchedulerProvider implements BaseScheduler{

    private static ImmediateSchedulerProvider mInstance;

    public static synchronized ImmediateSchedulerProvider getInstance(){

        if (mInstance == null){
            mInstance = new ImmediateSchedulerProvider();
        }

        return mInstance;

    }

    private ImmediateSchedulerProvider(){}

    @Override
    public Scheduler computation() {
        return Schedulers.trampoline();
    }

    @Override
    public Scheduler io() {
        return Schedulers.trampoline();
    }

    @Override
    public Scheduler ui() {
        return Schedulers.trampoline();
    }
}
