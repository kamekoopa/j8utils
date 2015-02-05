package com.github.kamekoopa.j8utils.data;

import com.github.kamekoopa.j8utils.utils.FE1;

import java.util.function.Function;

public abstract class Either<A, B> {

	public static <X, B> Left<X, B> left(X a){
		return new Left<X, B>(a);
	}

	public static <A, X> Right<A, X> right(X b){
		return new Right<A, X>(b);
	}

	public abstract boolean isLeft();

	public abstract boolean isRight();

	public abstract <BB> Either<A, BB> map(Function<B, BB> f);

	public abstract <BB> Either<A, BB> mape(FE1<B, BB> f) throws Exception;

	public abstract <BB> Either<A, BB> flatMap(Function<B, Either<A, BB>> f);

	public abstract <BB> Either<A, BB> flatMape(FE1<B, Either<A, BB>> f) throws Exception;

	public abstract <C> C fold(Function<A, C> fl, Function<B, C> fr);


	public static class Left<A, B> extends Either<A, B> {

		private final A a;

		private Left(A a){
			this.a = a;
		}

		@Override
		public boolean isLeft() {
			return true;
		}

		@Override
		public boolean isRight() {
			return false;
		}

		@Override
		public <BB> Either<A, BB> map(Function<B, BB> f) {
			return new Left<>(a);
		}

		@Override
		public <BB> Either<A, BB> mape(FE1<B, BB> f) throws Exception {
			return new Left<>(a);
		}

		@Override
		public <BB> Either<A, BB> flatMap(Function<B, Either<A, BB>> f) {
			return new Left<>(a);
		}

		@Override
		public <BB> Either<A, BB> flatMape(FE1<B, Either<A, BB>> f) throws Exception {
			return new Left<>(a);
		}

		@Override
		public <C> C fold(Function<A, C> fl, Function<B, C> fr)  {
			return fl.apply(a);
		}
	}

	public static class Right<A, B> extends Either<A,B> {

		private final B b;

		private Right(B b){
			this.b = b;
		}

		@Override
		public boolean isLeft() {
			return false;
		}

		@Override
		public boolean isRight() {
			return true;
		}

		@Override
		public <C> Either<A, C> map(Function<B, C> f) {
			return new Right<>(f.apply(b));
		}

		@Override
		public <BB> Either<A, BB> mape(FE1<B, BB> f) throws Exception {
			return new Right<>(f.applye(b));
		}

		@Override
		public <BB> Either<A, BB> flatMap(Function<B, Either<A, BB>> f) {
			return f.apply(b);
		}

		@Override
		public <BB> Either<A, BB> flatMape(FE1<B, Either<A, BB>> f) throws Exception {
			return f.applye(b);
		}

		@Override
		public <C> C fold(Function<A, C> fl, Function<B, C> fr)  {
			return fr.apply(b);
		}
	}
}
