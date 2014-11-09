/*
 * Copyright 2013 Ray Tsang
 * Copyright 2014 Danilo Reinert
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.reinert.gdeferred.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import io.reinert.gdeferred.AlwaysCallback;
import io.reinert.gdeferred.DoneCallback;
import io.reinert.gdeferred.DoneFilter;
import io.reinert.gdeferred.DonePipe;
import io.reinert.gdeferred.FailCallback;
import io.reinert.gdeferred.FailFilter;
import io.reinert.gdeferred.FailPipe;
import io.reinert.gdeferred.ProgressCallback;
import io.reinert.gdeferred.ProgressFilter;
import io.reinert.gdeferred.ProgressPipe;
import io.reinert.gdeferred.Promise;

/**
 * Abstract implementation of {@link io.reinert.gdeferred.Promise}.
 *
 * @param <D> The type of the result received when the promise is done
 * @param <F> The type of the result received when the promise failed
 * @param <P> The type of the progress notification
 */
public abstract class AbstractPromise<D, F, P> implements Promise<D, F, P> {

    protected final Logger log = Logger.getLogger(String.valueOf(AbstractPromise.class));
    protected final List<AlwaysCallback<D, F>> alwaysCallbacks = new ArrayList<AlwaysCallback<D, F>>();
    protected final List<DoneCallback<D>> doneCallbacks = new ArrayList<DoneCallback<D>>();
    protected final List<FailCallback<F>> failCallbacks = new ArrayList<FailCallback<F>>();
    protected final List<ProgressCallback<P>> progressCallbacks = new ArrayList<ProgressCallback<P>>();

    protected F rejectResult;
    protected D resolveResult;
    protected State state = State.PENDING;

    @Override
    public Promise<D, F, P> always(AlwaysCallback<D, F> callback) {
        alwaysCallbacks.add(callback);
        if (!isPending()) triggerAlways(callback, resolveResult, rejectResult);
        return this;
    }

    @Override
    public Promise<D, F, P> done(DoneCallback<D> callback) {
        doneCallbacks.add(callback);
        if (isResolved()) triggerDone(callback, resolveResult);
        return this;
    }

    @Override
    public Promise<D, F, P> fail(FailCallback<F> callback) {
        failCallbacks.add(callback);
        if (isRejected()) triggerFail(callback, rejectResult);
        return this;
    }

    @Override
    public boolean isPending() {
        return state == State.PENDING;
    }

    @Override
    public boolean isRejected() {
        return state == State.REJECTED;
    }

    @Override
    public boolean isResolved() {
        return state == State.RESOLVED;
    }

    @Override
    public Promise<D, F, P> progress(ProgressCallback<P> callback) {
        progressCallbacks.add(callback);
        return this;
    }

    @Override
    public State state() {
        return state;
    }

    @Override
    public Promise<D, F, P> then(DoneCallback<D> callback) {
        return done(callback);
    }

    @Override
    public Promise<D, F, P> then(DoneCallback<D> doneCallback, FailCallback<F> failCallback) {
        done(doneCallback);
        fail(failCallback);
        return this;
    }

    @Override
    public Promise<D, F, P> then(DoneCallback<D> doneCallback, FailCallback<F> failCallback,
                                 ProgressCallback<P> progressCallback) {
        done(doneCallback);
        fail(failCallback);
        progress(progressCallback);
        return this;
    }

    @Override
    public <D_OUT, F_OUT, P_OUT> Promise<D_OUT, F_OUT, P_OUT> then(DoneFilter<D, D_OUT> doneFilter) {
        return new FilteredPromise<D, F, P, D_OUT, F_OUT, P_OUT>(this, doneFilter, null, null);
    }

    @Override
    public <D_OUT, F_OUT, P_OUT> Promise<D_OUT, F_OUT, P_OUT> then(DoneFilter<D, D_OUT> doneFilter,
                                                                   FailFilter<F, F_OUT> failFilter) {
        return new FilteredPromise<D, F, P, D_OUT, F_OUT, P_OUT>(this, doneFilter, failFilter, null);
    }

    @Override
    public <D_OUT, F_OUT, P_OUT> Promise<D_OUT, F_OUT, P_OUT> then(DoneFilter<D, D_OUT> doneFilter,
                                                                   FailFilter<F, F_OUT> failFilter,
                                                                   ProgressFilter<P, P_OUT> progressFilter) {
        return new FilteredPromise<D, F, P, D_OUT, F_OUT, P_OUT>(this, doneFilter, failFilter, progressFilter);
    }

    @Override
    public <D_OUT, F_OUT, P_OUT> Promise<D_OUT, F_OUT, P_OUT> then(DonePipe<D, D_OUT, F_OUT, P_OUT> donePipe) {
        return new PipedPromise<D, F, P, D_OUT, F_OUT, P_OUT>(this, donePipe, null, null);
    }

    @Override
    public <D_OUT, F_OUT, P_OUT> Promise<D_OUT, F_OUT, P_OUT> then(DonePipe<D, D_OUT, F_OUT, P_OUT> donePipe,
                                                                   FailPipe<F, D_OUT, F_OUT, P_OUT> failPipe) {
        return new PipedPromise<D, F, P, D_OUT, F_OUT, P_OUT>(this, donePipe, failPipe, null);
    }

    @Override
    public <D_OUT, F_OUT, P_OUT> Promise<D_OUT, F_OUT, P_OUT> then(DonePipe<D, D_OUT, F_OUT, P_OUT> donePipe,
                                                                   FailPipe<F, D_OUT, F_OUT, P_OUT> failPipe,
                                                                   ProgressPipe<P, D_OUT, F_OUT, P_OUT> progressPipe) {
        return new PipedPromise<D, F, P, D_OUT, F_OUT, P_OUT>(this, donePipe, failPipe, progressPipe);
    }

    protected void triggerAlways(D resolve, F reject) {
        for (AlwaysCallback<D, F> callback : alwaysCallbacks) {
            // TODO check why this try/catch statement
            try {
                triggerAlways(callback, resolve, reject);
            } catch (Exception e) {
                log.log(Level.SEVERE, "An uncaught exception occurred in a AlwaysCallback", e);
            }
        }
    }

    protected void triggerAlways(AlwaysCallback<D, F> callback, D resolve, F reject) {
        callback.onAlways(state, resolve, reject);
    }

    protected void triggerDone(D resolved) {
        for (DoneCallback<D> callback : doneCallbacks) {
            // TODO check why this try/catch statement
            try {
                triggerDone(callback, resolved);
            } catch (Exception e) {
                log.log(Level.SEVERE, "An uncaught exception occurred in a DoneCallback", e);
            }
        }
    }

    protected void triggerDone(DoneCallback<D> callback, D resolved) {
        callback.onDone(resolved);
    }

    protected void triggerFail(F rejected) {
        for (FailCallback<F> callback : failCallbacks) {
            // TODO check why this try/catch statement
            try {
                triggerFail(callback, rejected);
            } catch (Exception e) {
                log.log(Level.SEVERE, "An uncaught exception occurred in a FailCallback", e);
            }
        }
    }

    protected void triggerFail(FailCallback<F> callback, F rejected) {
        callback.onFail(rejected);
    }

    protected void triggerProgress(P progress) {
        for (ProgressCallback<P> callback : progressCallbacks) {
            // TODO check why this try/catch statement
            try {
                triggerProgress(callback, progress);
            } catch (Exception e) {
                log.log(Level.SEVERE, "An uncaught exception occurred in a ProgressCallback", e);
            }
        }
    }

    protected void triggerProgress(ProgressCallback<P> callback, P progress) {
        callback.onProgress(progress);
    }
}
