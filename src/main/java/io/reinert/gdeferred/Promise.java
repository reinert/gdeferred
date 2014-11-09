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
package io.reinert.gdeferred;

/**
 * Promise interface to observe when some action has occurred on the corresponding {@link Deferred} object.
 * <p/>
 * A promise object should be obtained from {@link Deferred#promise()}, or by using DeferredManager.
 * <p/>
 * <pre>
 * <code>
 * Deferred deferredObject = new DeferredObject();
 * Promise promise = deferredObject.promise();
 * promise.done(new DoneCallback() {
 *   void onDone(Object result) {
 *       // Done!
 *   }
 * });
 *
 * // another thread using the same deferredObject
 * deferredObject.resolve("OK");
 *
 * </code>
 * </pre>
 *
 * @param <D> Type used for {@link #done(DoneCallback)}
 * @param <F> Type used for {@link #fail(FailCallback)}
 * @param <P> The type of the progress notification
 *
 * @author Ray Tsang
 * @author Danilo Reinert
 * @see Deferred#resolve(Object)
 * @see Deferred#reject(Object)
 * @see Deferred#notify(Object)
 */
public interface Promise<D, F, P> {

    /**
     * State of a Promise
     */
    enum State {
        /**
         * The Promise is still pending - it could be created, submitted for execution, or currently running, but not
         * yet finished.
         */
        PENDING,

        /**
         * The Promise has finished running and a failure occurred. Thus, the Promise is rejected.
         *
         * @see Deferred#reject(Object)
         */
        REJECTED,

        /**
         * The Promise has finished running successfully. Thus the Promise is resolved.
         *
         * @see Deferred#resolve(Object)
         */
        RESOLVED
    }

    /**
     * This method will register {@link AlwaysCallback} so that when it's always triggered regardless of whether the
     * corresponding Deferred object was resolved or rejected.
     * <p/>
     * You can register multiple {@link AlwaysCallback} by calling the method multiple times. The order of callback
     * trigger is based on the order you call this method.
     * <p/>
     * <pre>
     * <code>
     * promise.always(new AlwaysCallback(){
     *   void onAlways(Context context, Object result, Object rejection) {
     *     if (context.getState() == State.RESOLVED) {
     *       // do something w/ result
     *     } else {
     *       // do something w/ rejection
     *     }
     *   }
     * });
     * </code>
     * </pre>
     *
     * @param callback The callback to be executed when the promise is done or failed
     *
     * @return The current promise
     *
     * @see Deferred#resolve(Object)
     * @see Deferred#reject(Object)
     */
    Promise<D, F, P> always(AlwaysCallback<D, F> callback);

    /**
     * This method will register {@link DoneCallback} so that when a Deferred object is resolved ({@link
     * Deferred#resolve(Object)}), {@link DoneCallback} will be triggered.
     * <p/>
     * You can register multiple {@link DoneCallback} by calling the method multiple times. The order of callback
     * trigger is based on the order you call this method.
     * <p/>
     * <pre>
     * <code>
     * promise.progress(new DoneCallback(){
     *   void onDone(Object done) {
     *     ...
     *   }
     * });
     * </code>
     * </pre>
     *
     * @param callback The callback to be executed when the promise is done
     *
     * @return The current promise
     *
     * @see Deferred#resolve(Object)
     */
    Promise<D, F, P> done(DoneCallback<D> callback);

    /**
     * This method will register {@link FailCallback} so that when a Deferred object is rejected ({@link
     * Deferred#reject(Object)}), {@link FailCallback} will be triggered.
     * <p/>
     * You can register multiple {@link FailCallback} by calling the method multiple times. The order of callback
     * trigger is based on the order you call this method.
     * <p/>
     * <pre>
     * <code>
     * promise.fail(new FaillCallback(){
     *   void onFail(Object rejection) {
     *     ...
     *   }
     * });
     * </code>
     * </pre>
     *
     * @param callback The callback to be executed when the promise has failed
     *
     * @return The current promise
     *
     * @see Deferred#reject(Object)
     */
    Promise<D, F, P> fail(FailCallback<F> callback);

    /**
     * @return {@code true} if is pending, {@code false} otherwise
     *
     * @see State#PENDING
     */
    boolean isPending();

    /**
     * @return {@code true} if is rejected, {@code false} otherwise
     *
     * @see State#REJECTED
     */
    boolean isRejected();

    /**
     * @return {@code true} if is resolved, {@code false} otherwise
     *
     * @see State#RESOLVED
     */
    boolean isResolved();

    /**
     * This method will register {@link ProgressCallback} so that when a Deferred object is notified of progress ({@link
     * Deferred#notify(Object)}), {@link ProgressCallback} will be triggered.
     * <p/>
     * You can register multiple {@link ProgressCallback} by calling the method multiple times. The order of callback
     * trigger is based on the order you call this method.
     * <p/>
     * <pre>
     * <code>
     * promise.progress(new ProgressCallback(){
     *   void onProgress(Object progress) {
     *     // e.g., update progress in the GUI while the background task is still running.
     *   }
     * });
     * </code>
     * </pre>
     *
     * @param callback The callback to be executed when a progress notification is sent
     *
     * @return The current promise
     *
     * @see Deferred#notify(Object)
     */
    Promise<D, F, P> progress(ProgressCallback<P> callback);

    State state();

    /**
     * Equivalent to {@link #done(DoneCallback)}.
     *
     * @param doneCallback {@link #done(DoneCallback)}
     *
     * @return the same promise
     */
    Promise<D, F, P> then(DoneCallback<D> doneCallback);

    /**
     * Equivalent to {@link #done(DoneCallback)} and then {@link FailCallback}.
     *
     * @param doneCallback {@link #done(DoneCallback)}
     * @param failCallback {@link #fail(FailCallback)}
     *
     * @return the same promise
     */
    Promise<D, F, P> then(DoneCallback<D> doneCallback,
                          FailCallback<F> failCallback);

    /**
     * Equivalent to {@link #done(DoneCallback)}, then {@link FailCallback}, then {@link #progress(ProgressCallback)}.
     *
     * @param doneCallback     {@link #done(DoneCallback)}
     * @param failCallback     {@link #fail(FailCallback)}
     * @param progressCallback {@link #progress(ProgressCallback)}
     *
     * @return the same promise
     */
    Promise<D, F, P> then(DoneCallback<D> doneCallback,
                          FailCallback<F> failCallback,
                          ProgressCallback<P> progressCallback);

    /**
     * Equivalent to then(doneFilter, null, null).
     *
     * @param doneFilter the done filter callback
     *
     * @return the output promise from executed filter
     *
     * @see {@link #then(DoneFilter, FailFilter, ProgressFilter)}
     */
    <D_OUT, F_OUT, P_OUT> Promise<D_OUT, F_OUT, P_OUT> then(DoneFilter<D, D_OUT> doneFilter);

    /**
     * Equivalent to then(doneFilter, failFilter, null).
     *
     * @param doneFilter the done filter callback
     * @param failFilter the fail filter callback
     *
     * @return the output promise from executed filter
     *
     * @see {@link #then(DoneFilter, FailFilter, ProgressFilter)}
     */
    <D_OUT, F_OUT, P_OUT> Promise<D_OUT, F_OUT, P_OUT> then(DoneFilter<D, D_OUT> doneFilter,
                                                            FailFilter<F, F_OUT> failFilter);

    /**
     * If any of the filter is not specified, a default No Op filter would be used.
     * This is also known as "piping", or "chaining" of callbacks and being able to modify the return value.
     * <p/>
     * <pre>
     * <code>
     * Deferred deferred = new DeferredObject();
     * Promise promise = deferred.promise();
     * Promise filtered = promise.then(new DoneFilter<Integer, Integer>() {
     *   Integer filterDone(Integer result) {
     *     return result * 10;
     *   }
     * });
     *
     * filtered.then(new DoneCallback<Integer>() {
     *   void onDone(Integer result) {
     *     System.out.println(result);
     *   }
     * });
     *
     * deferred.resolve(1); // prints 10
     * </code>
     * </pre>
     *
     * @param doneFilter the done filter callback
     * @param failFilter the fail filter callback
     * @param progressFilter the progress filter callback
     *
     * @return the output promise from executed filter
     */
    <D_OUT, F_OUT, P_OUT> Promise<D_OUT, F_OUT, P_OUT> then(DoneFilter<D, D_OUT> doneFilter,
                                                            FailFilter<F, F_OUT> failFilter,
                                                            ProgressFilter<P, P_OUT> progressFilter);

    /**
     * Equivalent to then(DonePipe, null, null).
     *
     * @param donePipe the done pipe callback
     *
     * @return the output promise returned from executed pipe
     */
    <D_OUT, F_OUT, P_OUT> Promise<D_OUT, F_OUT, P_OUT> then(DonePipe<D, D_OUT, F_OUT, P_OUT> donePipe);

    /**
     * Equivalent to then(DonePipe, FailPipe, null).
     *
     * @param donePipe the done pipe callback
     * @param failPipe the fail pipe callback
     *
     * @return the output promise returned from executed pipe
     */
    <D_OUT, F_OUT, P_OUT> Promise<D_OUT, F_OUT, P_OUT> then(DonePipe<D, D_OUT, F_OUT, P_OUT> donePipe,
                                                            FailPipe<F, D_OUT, F_OUT, P_OUT> failPipe);

    /**
     * This method is similar to jQuery's pipe() method, where a new {@link Promise} is returned by the the pipe filter
     * instead of the original. This is useful to handle return values and then rewiring it to different callbacks.
     * <p/>
     * <pre>
     * <code>
     * promise.then(new DonePipe<Integer, Integer, String, Void>() {
     *   Deferred<Integer, Void, Void> pipeDone(Integer result) {
     *     // Reject values greater than 100
     *     if (result > 100) {
     *       return new DeferredObject<Integer, Void, Void>().reject("Failed");
     *     } else {
     *       return new DeferredObject<Integer, Void, Void>().resolve(result);
     *     }
     *   }
     * }).done(...)
     * .fail(...);
     * </code>
     * </pre>
     *
     * @param donePipe the done pipe callback
     * @param failPipe the fail pipe callback
     * @param progressPipe the progress pipe callback
     *
     * @return the output promise returned from executed pipe
     */
    <D_OUT, F_OUT, P_OUT> Promise<D_OUT, F_OUT, P_OUT> then(DonePipe<D, D_OUT, F_OUT, P_OUT> donePipe,
                                                            FailPipe<F, D_OUT, F_OUT, P_OUT> failPipe,
                                                            ProgressPipe<P, D_OUT, F_OUT, P_OUT> progressPipe);

}
