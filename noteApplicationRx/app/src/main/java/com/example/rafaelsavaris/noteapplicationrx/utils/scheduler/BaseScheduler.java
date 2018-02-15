package com.example.rafaelsavaris.noteapplicationrx.utils.scheduler;

import io.reactivex.Scheduler;

/**
 * Created by rafael.savaris on 14/02/2018.
 */

public interface BaseScheduler {

    Scheduler computation();

    Scheduler io();

    Scheduler ui();

}
