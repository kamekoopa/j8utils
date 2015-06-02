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
import java.util.function.*;
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

	public abstract Option<A> filter(Predicate<? super A> p);

	public abstract <B> Option<B> map(Function<? super A, ? extends B> f);

	public abstract <B> Option<B> mape(FE1<? super A, ? extends B> f) throws Exception;

	public abstract <B> Option<B> flatMap(Function<? super A, ? extends Option<? extends B>> f);

	public abstract <B> Option<B> flatMape(FE1<? super A, ? extends Option<? extends B>> f) throws Exception;

	public abstract <B> B fold(Supplier<? extends B> none, Function<? super A, ? extends B> f);

	public abstract A getOrElse(Supplier<? extends A> def);

	public abstract Option<A> ifEmpty(Supplier<? extends A> ifEmpty);

	public abstract Option<A> or(Supplier<? extends Option<? extends A>> optionSupplier);

	public abstract A unsafeGet() throws Exception;

	public abstract Option<A> peek(Runnable ifNone, Consumer<? super A> ifSome);

	public Stream<A> stream() {
		return StreamSupport.stream(this.spliterator(), false);
	}

	public <B, X> Option<X> ap(Option<? extends B> ob, BiFunction<? super A, ? super B, ? extends X> f) {
		return this.flatMap(a -> ob.map(b -> f.apply(a, b)));
	}

	public <B, C, X> Option<X> ap(Option<? extends B> ob, Option<? extends C> oc, F3<? super A, ? super B, ? super C, ? extends X> f) {
		return this.flatMap(a -> ob.<X>flatMap(b -> oc.map(c -> f.apply(a, b, c))));
	}

	public <B, C, D, X> Option<X> ap(Option<? extends B> ob, Option<? extends C> oc, Option<? extends D> od, F4<? super A, ? super B, ? super C, ? super D, ? extends X> f) {
		return this.flatMap(a -> ob.<X>flatMap(b -> oc.<X>flatMap(c -> od.map(d -> f.apply(a, b, c, d)))));
	}

	public <B, C, D, E, X> Option<X> ap(Option<? extends B> ob, Option<? extends C> oc, Option<? extends D> od, Option<? extends E> oe, F5<? super A, ? super B, ? super C, ? super D, ? super E, ? extends X> f) {
		return this.flatMap(a -> ob.<X>flatMap(b -> oc.<X>flatMap(c -> od.<X>flatMap(d -> oe.map( e->f.apply(a, b, c, d, e))))));
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
		public Option<A> filter(Predicate<? super A> p){
			if(p.test(a)){
				return this;
			}else{
				return none();
			}
		}

		@Override
		public <B> Option<B> map(Function<? super A, ? extends B> f) {
			return new Some<>(f.apply(a));
		}

		@Override
		public <B> Option<B> mape(FE1<? super A, ? extends B> f) throws Exception {
			return new Some<>(f.apply(a));
		}

		@Override
		public <B> Option<B> flatMap(Function<? super A, ? extends Option<? extends B>> f) {
			return f.apply(a).map(Function.<B>identity());
		}

		@Override
		public <B> Option<B> flatMape(FE1<? super A, ? extends Option<? extends B>> f) throws Exception {
			return f.apply(a).map(Function.<B>identity());
		}

		@Override
		public <B> B fold(Supplier<? extends B> none, Function<? super A, ? extends B> f) {
			return f.apply(a);
		}

		@Override
		public A getOrElse(Supplier<? extends A> def){
			return a;
		}

		@Override
		public Option<A> ifEmpty(Supplier<? extends A> ifEmpty) {
			return this;
		}

		@Override
		public Option<A> or(Supplier<? extends Option<? extends A>> optionSupplier) {
			return this;
		}

		@Override
		public A unsafeGet() throws Exception {
			return a;
		}

		@Override
		public Option<A> peek(Runnable ifNone, Consumer<? super A> ifSome) {
			ifSome.accept(a);
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
		public Option<A> filter(Predicate<? super A> p){
			return none();
		}

		@Override
		public <B> Option<B> map(Function<? super A, ? extends B> f) {
			return none();
		}

		@Override
		public <B> Option<B> mape(FE1<? super A, ? extends B> f) throws Exception {
			return none();
		}

		@Override
		public <B> Option<B> flatMap(Function<? super A, ? extends Option<? extends B>> f) {
			return none();
		}

		@Override
		public <B> Option<B> flatMape(FE1<? super A, ? extends Option<? extends B>> f) throws Exception {
			return none();
		}

		@Override
		public <B> B fold(Supplier<? extends B> none, Function<? super A, ? extends B> f) {
			return none.get();
		}

		@Override
		public A getOrElse(Supplier<? extends A> def){
			return def.get();
		}

		@Override
		public Option<A> ifEmpty(Supplier<? extends A> ifEmpty) {
			return Option.of(ifEmpty.get());
		}

		@Override
		public Option<A> or(Supplier<? extends Option<? extends A>> optionSupplier) {
			return optionSupplier.get().map(Function.<A>identity());
		}

		@Override
		public A unsafeGet() throws Exception {
			throw new NoSuchElementException();
		}

		@Override
		public Option<A> peek(Runnable ifNone, Consumer<? super A> ifSome) {
			ifNone.run();
			return this;
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
