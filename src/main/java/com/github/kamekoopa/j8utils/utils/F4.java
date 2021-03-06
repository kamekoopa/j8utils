/*
 * Copyright 2015 kamekoopa
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

package com.github.kamekoopa.j8utils.utils;

import java.util.Objects;
import java.util.function.Function;

@FunctionalInterface
public interface F4<A, B, C, D, X> {

	X apply(A a, B b, C c, D d);

	default <V> F4<A, B, C, D, V> andThen(Function<? super X, ? extends V> after) {
		Objects.requireNonNull(after);
		return (A a, B b, C c, D d) -> after.apply(apply(a, b, c, d));
	}

	default Function<A, Function<B, Function<C, Function<D, X>>>> curried() {
		return a -> b -> c -> d -> apply(a, b, c, d);
	}
}
