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

package com.github.kamekoopa.j8utils.data;

import com.github.kamekoopa.j8utils.utils.SE;

public class LazyVal<A> {

	private final SE<A> supplier;
	private A cache = null;

	private LazyVal(SE<A> supplier){
		this.supplier = supplier;
	}

	public static <A> LazyVal<A> of(SE<A> supplier){
		return new LazyVal<>(supplier);
	}

	public A get() {

		try {
			return gete();
		}catch(RuntimeException e){
			throw e;
		}catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public A gete() throws Exception {

		if(cache == null){
			this.cache = supplier.get();
		}

		return cache;
	}
}
