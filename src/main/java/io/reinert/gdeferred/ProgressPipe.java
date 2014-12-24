/*
 * Copyright 2013 Ray Tsang
 * Copyright 2014 Danilo Reinert
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
package io.reinert.gdeferred;

/**
 * Pipe callback for monitoring progress.
 *
 * @param <P>     Type of the input
 * @param <D_OUT> Type of the done output from this filter
 * @param <F_OUT> Type of the fail output from this filter
 * @param <P_OUT> Type of the progress output from this filter
 *
 * @see Promise#then(DoneCallback, FailCallback, ProgressCallback)
 */
public interface ProgressPipe<P, D_OUT, F_OUT, P_OUT> {
    Promise<D_OUT, F_OUT, P_OUT> pipeProgress(final P result);
}
