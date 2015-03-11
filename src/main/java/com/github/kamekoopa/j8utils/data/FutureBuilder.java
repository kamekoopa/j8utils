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
					throw e.getCause();
				} catch (Throwable e) {
					throw e;
				}
			});
		}
		public Try<A> tryGet(long time, TimeUnit unit){

			return Try.of(() -> {
				try {
					return Future.this.underlying.get(time, unit);
				} catch (ExecutionException e) {
					throw e.getCause();
				} catch (Throwable e) {
					throw e;
				}
			});
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
