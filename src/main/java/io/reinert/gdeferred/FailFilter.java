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
 * Filter callback to handle promise rejection.
 *
 * @param <F>     Type of the input
 * @param <F_OUT> Type of the output from this filter
 *
 * @see Promise#then(DoneFilter, FailFilter)
 */
public interface FailFilter<F, F_OUT> {

    NoOpFailFilter NO_OP = new NoOpFailFilter();

    F_OUT filterFail(final F result);
}
