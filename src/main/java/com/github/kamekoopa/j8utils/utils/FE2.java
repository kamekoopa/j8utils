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

import java.util.function.BiFunction;

@FunctionalInterface
public interface FE2<A, B, X> {

	static <A, B, X> FE2<A, B, X> from(BiFunction<? super A, ? super B, ? extends X> f){
		return f::apply;
	}

	X apply(A a, B b) throws Exception;

	default FE2<B, A, X> flip() {
		return (b, a) -> this.apply(a, b);
	}

	default FE1<A, FE1<B, X>> curried() {
		return a -> b -> this.apply(a, b);
	}
}
