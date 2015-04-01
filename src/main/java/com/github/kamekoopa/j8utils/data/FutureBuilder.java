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

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public class FutureBuilder {

	private final Option<Executor> executorOpt;

	private FutureBuilder(Option<Executor> executorOpt) {
		this.executorOpt = executorOpt;
	}

	public static FutureBuilder build(){
		return new FutureBuilder(Option.none());
	}

	public static FutureBuilder buildWith(Executor executor) {
		return new FutureBuilder(Option.of(executor));
	}

	public <A> Future<A> run(Supplier<A> supplier) {
		return new Future<>(supplier);
	}



	public class Future<A> {

		private final CompletableFuture<A> underlying;

		private Future(Supplier<A> supplier) {
			this.underlying = supplyAsync(supplier);
		}

		private Future(CompletableFuture<A> underlying) {
			this.underlying = underlying;
		}

		public <B> Future<B> map(Function<A, B> f) {
			return new Future<>(thenApplyAsync(f));
		}

		public <B> Future<B> flatMap(Function<A, Future<B>> f) {
			return new Future<>(
				thenComposeAsync(v -> f.apply(v).underlying)
			);
		}

		public Try<A> tryGet() {

			return Try.of(() -> {
				try {
					return Future.this.underlying.get();
				} catch (ExecutionException e) {
					try {
						throw e.getCause();
					}catch (Error | RuntimeException re){
						throw re;
					}catch (Throwable other){
						throw new RuntimeException(other);
					}
				} catch (Exception e) {
					throw e;
				}
			});
		}
		public Try<A> tryGet(long time, TimeUnit unit){

			return Try.of(() -> {
				try {
					return Future.this.underlying.get(time, unit);
				} catch (ExecutionException e) {
					try {
						throw e.getCause();
					}catch (Error | RuntimeException re){
						throw re;
					}catch (Throwable other){
						throw new RuntimeException(other);
					}
				} catch (Exception e) {
					throw e;
				}
			});
		}

		public <B, X> Future<X> ap(Future<B> fb, BiFunction<A, B, X> f) {
			return this.flatMap(a -> fb.map(b -> f.apply(a, b)));
		}

		public <B, C, X> Future<X> ap(Future<B> fb, Future<C> fc, F3<A, B, C, X> f) {
			return this.flatMap(a -> fb.flatMap(b -> fc.map(c -> f.apply(a, b, c))));
		}

		public <B, C, D, X> Future<X> ap(Future<B> fb, Future<C> fc, Future<D> fd, F4<A, B, C, D, X> f) {
			return this.flatMap(a -> fb.flatMap(b -> fc.flatMap(c -> fd.map(d -> f.apply(a, b, c, d)))));
		}

		public <B, C, D, E, X> Future<X> ap(Future<B> fb, Future<C> fc, Future<D> fd, Future<E> fe, F5<A, B, C, D, E, X> f) {
			return this.flatMap(a -> fb.flatMap(b -> fc.flatMap(c -> fd.flatMap(d -> fe.map( e->f.apply(a, b, c, d, e))))));
		}


		private CompletableFuture<A> supplyAsync(Supplier<A> supplier){

			return applyExecutor(
				CompletableFuture::supplyAsync,
				CompletableFuture::supplyAsync,
				supplier
			);
		}

		private <B> CompletableFuture<B> thenApplyAsync(Function<A, B> f) {

			return applyExecutor(
				this.underlying::thenApplyAsync,
				this.underlying::thenApplyAsync,
				f
			);
		}

		private <B> CompletableFuture<B> thenComposeAsync(Function<A, CompletableFuture<B>> f) {

			return applyExecutor(
				this.underlying::thenComposeAsync,
				this.underlying::thenComposeAsync,
				f
			);
		}

		private <X, B> CompletableFuture<B> applyExecutor(
			Function<X, CompletableFuture<B>> f1,
			BiFunction<X, Executor, CompletableFuture<B>> f2,
		    X x
		) {

			return executorOpt.fold(
				() -> f1.apply(x),
				executor -> f2.apply(x, executor)
			);
		}
	}
}
