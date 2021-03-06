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

@FunctionalInterface
public interface FE3<A, B, C, X> {

	static <A, B, C, X> FE3<A, B, C, X> from(F3<? super A, ? super B, ? super C, ? extends X> f){
		return f::apply;
	}

	X apply(A a, B b, C c) throws Exception;
}
