package com.github.kamekoopa.j8utils.data;

import com.github.kamekoopa.j8utils.utils.FE1;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Function;

public abstract class Option<A> implements Iterable<A> {

	private static final None none = new None();

	@SuppressWarnings("unchecked")
	public static <A> Option<A> of(A a){
		if(a == null){
			return none;
		}else{
			return new Some<>(a);
		}
	}

	@SuppressWarnings("unchecked")
	public static <A> Option<A> from(Optional<A> optional){
		if( optional == null || !optional.isPresent() ) {
			return none;
		}else{
			return of(optional.get());
		}
	}

	@SuppressWarnings("unchecked")
	public static <A> Option<A> none(){
		return none;
	}

	public abstract <B> Option<B> map(Function<A, B> f);

	public abstract <B> Option<B> mape(FE1<A, B> f) throws Exception;

	public abstract <B> Option<B> flatMap(Function<A, Option<B>> f);

	public abstract <B> Option<B> flatMap(FE1<A, Option<B>> f) throws Exception;

	public abstract <B> B fold(LazyVal<B> none, Function<A, B> f);



	public static final class Some<A> extends Option<A> {

		private final A a;

		private Some(A a) {
			this.a = a;
		}

		@Override
		public <B> Option<B> map(Function<A, B> f) {
			return new Some<>(f.apply(a));
		}

		@Override
		public <B> Option<B> mape(FE1<A, B> f) throws Exception {
			return new Some<>(f.applye(a));
		}

		@Override
		public <B> Option<B> flatMap(Function<A, Option<B>> f) {
			return f.apply(a);
		}

		@Override
		public <B> Option<B> flatMap(FE1<A, Option<B>> f) throws Exception {
			return f.applye(a);
		}

		@Override
		public <B> B fold(LazyVal<B> none, Function<A, B> f) {
			return f.apply(a);
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
	}

	public static final class None<A> extends Option<A> {
		private None(){}

		@Override
		public <B> Option<B> map(Function<A, B> f) {
			return none();
		}

		@Override
		public <B> Option<B> mape(FE1<A, B> f) throws Exception {
			return none();
		}

		@Override
		public <B> Option<B> flatMap(Function<A, Option<B>> f) {
			return none();
		}

		@Override
		public <B> Option<B> flatMap(FE1<A, Option<B>> f) throws Exception {
			return none();
		}

		@Override
		public <B> B fold(LazyVal<B> none, Function<A, B> f) {
			return none.get();
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
	}
}
