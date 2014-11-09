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
 * FailFilter that returns the same instance (no-op).
 * It is a singleton and must be accessed from {@link FailFilter#NO_OP}.
 *
 * @param <F> Fail type
 */
final class NoOpFailFilter<F> implements FailFilter<F, F> {
    @Override
    public F filterFail(F result) {
        return result;
    }
}
