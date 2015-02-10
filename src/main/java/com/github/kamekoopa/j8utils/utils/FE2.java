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
public interface FE2<A, B, C> extends BiFunction<A, B, C> {

	@Override
	public default C apply(A a, B b) {
		try {
			return applye(a, b);
		}catch (Exception e){
			throw new RuntimeException(e);
		}
	}

	public C applye(A A, B b) throws Exception;
}
