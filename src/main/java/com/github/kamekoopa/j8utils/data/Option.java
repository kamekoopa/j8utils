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

import com.github.kamekoopa.j8utils.utils.F3;
import com.github.kamekoopa.j8utils.utils.F4;
import com.github.kamekoopa.j8utils.utils.F5;
import com.github.kamekoopa.j8utils.utils.FE1;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public abstract class Option<A> implements Iterable<A> {

	private static final None none = new None();

	public static <A> Option<A> of(A a){
		if(a == null){
			return none();
		}else{
			return new Some<>(a);
		}
	}

	public static <A> Option<A> from(Optional<A> optional){
		if( optional == null || !optional.isPresent() ) {
			return none();
		}else{
			return of(optional.get());
		}
	}

	@SuppressWarnings("unchecked")
	public static <A> Option<A> none(){
		return none;
	}

	public abstract boolean isSome();

	public abstract boolean isNone();

	public abstract <B> Option<B> map(Function<A, B> f);

	public abstract <B> Option<B> mape(FE1<A, B> f) throws Throwable;

	public abstract <B> Option<B> flatMap(Function<A, Option<B>> f);

	public abstract <B> Option<B> flatMape(FE1<A, Option<B>> f) throws Throwable;

	public abstract <B> B fold(Supplier<B> none, Function<A, B> f);

	public abstract A getOrElse(Supplier<A> def);

	public abstract Option<A> ifEmpty(Supplier<A> ifEmpty);

	public abstract Option<A> or(Supplier<Option<A>> optionSupplier);

	public Stream<A> stream() {
		return StreamSupport.stream(this.spliterator(), false);
	}

	public <B, X> Option<X> ap(Option<B> ob, BiFunction<A, B, X> f) {
		return this.flatMap(a -> ob.map(b -> f.apply(a, b)));
	}

	public <B, C, X> Option<X> ap(Option<B> ob, Option<C> oc, F3<A, B, C, X> f) {
		return this.flatMap(a -> ob.flatMap(b -> oc.map(c -> f.apply(a, b, c))));
	}

	public <B, C, D, X> Option<X> ap(Option<B> ob, Option<C> oc, Option<D> od, F4<A, B, C, D, X> f) {
		return this.flatMap(a -> ob.flatMap(b -> oc.flatMap(c -> od.map(d -> f.apply(a, b, c, d)))));
	}

	public <B, C, D, E, X> Option<X> ap(Option<B> ob, Option<C> oc, Option<D> od, Option<E> oe, F5<A, B, C, D, E, X> f) {
		return this.flatMap(a -> ob.flatMap(b -> oc.flatMap(c -> od.flatMap(d -> oe.map( e->f.apply(a, b, c, d, e))))));
	}


	public static final class Some<A> extends Option<A> {

		private final A a;

		private Some(A a) {
			this.a = a;
		}

		@Override
		public boolean isSome() {
			return true;
		}

		@Override
		public boolean isNone() {
			return false;
		}

		@Override
		public <B> Option<B> map(Function<A, B> f) {
			return new Some<>(f.apply(a));
		}

		@Override
		public <B> Option<B> mape(FE1<A, B> f) throws Throwable {
			return new Some<>(f.apply(a));
		}

		@Override
		public <B> Option<B> flatMap(Function<A, Option<B>> f) {
			return f.apply(a);
		}

		@Override
		public <B> Option<B> flatMape(FE1<A, Option<B>> f) throws Throwable {
			return f.apply(a);
		}

		@Override
		public <B> B fold(Supplier<B> none, Function<A, B> f) {
			return f.apply(a);
		}

		@Override
		public A getOrElse(Supplier<A> def){
			return a;
		}

		@Override
		public Option<A> ifEmpty(Supplier<A> ifEmpty) {
			return this;
		}

		@Override
		public Option<A> or(Supplier<Option<A>> optionSupplier) {
			return this;
		}

		@Override
		public Iterator<A> iterator() {
			return new Iterator<A>() {
				private boolean called = false;
				@Override
				public boolean hasNext() {
					if (!called){
						called = true;
						return true;
					}else{
						return false;
					}
				}

				@Override
				public A next() {
					return a;
				}
			};
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (!(o instanceof Some)) return false;

			Some some = (Some) o;

			return a.equals(some.a);
		}

		@Override
		public int hashCode() {
			return a.hashCode();
		}

		@Override
		public String toString() {
			return "Some("+a+")";
		}
	}

	public static final class None<A> extends Option<A> {
		private None(){}

		@Override
		public boolean isSome() {
			return false;
		}

		@Override
		public boolean isNone() {
			return true;
		}

		@Override
		public <B> Option<B> map(Function<A, B> f) {
			return none();
		}

		@Override
		public <B> Option<B> mape(FE1<A, B> f) throws Throwable {
			return none();
		}

		@Override
		public <B> Option<B> flatMap(Function<A, Option<B>> f) {
			return none();
		}

		@Override
		public <B> Option<B> flatMape(FE1<A, Option<B>> f) throws Throwable {
			return none();
		}

		@Override
		public <B> B fold(Supplier<B> none, Function<A, B> f) {
			return none.get();
		}

		@Override
		public A getOrElse(Supplier<A> def){
			return def.get();
		}

		@Override
		public Option<A> ifEmpty(Supplier<A> ifEmpty) {
			return Option.of(ifEmpty.get());
		}

		@Override
		public Option<A> or(Supplier<Option<A>> optionSupplier) {
			return optionSupplier.get();
		}

		@Override
		public Iterator<A> iterator() {
			return new Iterator<A>() {
				@Override
				public boolean hasNext() {
					return false;
				}

				@Override
				public A next() {
					throw new NoSuchElementException("none");
				}
			};
		}

		@Override
		public String toString() {
			return "None";
		}
	}
}
