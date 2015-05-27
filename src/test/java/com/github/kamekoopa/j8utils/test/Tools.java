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

package com.github.kamekoopa.j8utils.test;

public class Tools {
	private Tools(){}

	public static class A {
		public final int a;
		public A(int a) {
			this.a = a;
		}
	}

	public static class B extends A {
		public final int b;
		public B(int a, int b) {
			super(a);
			this.b = b;
		}
	}

	public static class C extends B {
		public final int c;
		public C(int a, int b, int c) {
			super(a, b);
			this.c = c;
		}
	}

	public static class D extends C {
		public final int d;
		public D(int a, int b, int c, int d) {
			super(a, b, c);
			this.d = d;
		}
	}

	public static class E extends D {
		public final int e;
		public E(int a, int b, int c, int d, int e) {
			super(a, b, c, d);
			this.e = e;
		}
	}

	public static class F extends E {
		public final int f;
		public F(int a, int b, int c, int d, int e, int f) {
			super(a, b, c, d, e);
			this.f = f;
		}
	}
}
