/*
 * Copyright 2013 Ray Tsang
 * Copyright 2013 Danilo Reinert
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.reinert.gdeferred.impl;

import io.reinert.gdeferred.DoneCallback;
import io.reinert.gdeferred.DoneFilter;
import io.reinert.gdeferred.FailCallback;
import io.reinert.gdeferred.FailFilter;
import io.reinert.gdeferred.ProgressCallback;
import io.reinert.gdeferred.ProgressFilter;
import io.reinert.gdeferred.Promise;

/**
 * Promise that handle filtering.
 *
 * @param <D> Done input
 * @param <F> Fail input
 * @param <P> Progress input
 * @param <D_OUT> Done output
 * @param <F_OUT> Fail output
 * @param <P_OUT> Progress output
 */
public class FilteredPromise<D, F, P, D_OUT, F_OUT, P_OUT> extends DeferredObject<D_OUT, F_OUT, P_OUT>
        implements Promise<D_OUT, F_OUT, P_OUT> {

    private final DoneFilter<D, D_OUT> doneFilter;
    private final FailFilter<F, F_OUT> failFilter;
    private final ProgressFilter<P, P_OUT> progressFilter;

    @SuppressWarnings("unchecked")
    public FilteredPromise(final Promise<D, F, P> promise, final DoneFilter<D, D_OUT> doneFilter,
                           final FailFilter<F, F_OUT> failFilter, final ProgressFilter<P, P_OUT> progressFilter) {
        this.doneFilter = doneFilter == null ? DoneFilter.NO_OP : doneFilter;
        this.failFilter = failFilter == null ? FailFilter.NO_OP : failFilter;
        this.progressFilter = progressFilter == null ? ProgressFilter.NO_OP : progressFilter;

        promise.done(new DoneCallback<D>() {
            @Override
            public void onDone(D result) {
                FilteredPromise.this.resolve(FilteredPromise.this.doneFilter.filterDone(result));
            }
        }).fail(new FailCallback<F>() {

            @Override
            public void onFail(F result) {
                FilteredPromise.this.reject(FilteredPromise.this.failFilter.filterFail(result));
            }
        }).progress(new ProgressCallback<P>() {

            @Override
            public void onProgress(P progress) {
                FilteredPromise.this.notify(FilteredPromise.this.progressFilter.filterProgress(progress));
            }
        });
    }
}
