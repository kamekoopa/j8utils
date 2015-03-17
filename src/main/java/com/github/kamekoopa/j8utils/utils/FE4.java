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
public interface FE4<A, B, C, D, X> extends F4<A, B, C, D, X> {

	public static <A, B, C, D, X> FE4<A, B, C, D, X> from(F4<A, B, C, D, X> f){
		return f::apply;
	}

	@Override
	public default X apply(A a, B b, C c, D d) {
		try {
			return applye(a, b, c, d);
		}catch (Exception e){
			throw new RuntimeException(e);
		}
	}

	public X applye(A A, B b, C c, D d) throws Exception;
}