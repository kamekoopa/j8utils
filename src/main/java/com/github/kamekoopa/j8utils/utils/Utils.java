package com.github.kamekoopa.j8utils.utils;

import com.github.kamekoopa.j8utils.data.Option;
import com.github.kamekoopa.j8utils.data.Try;
import com.github.kamekoopa.j8utils.data.Tuple2;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Utils {

	private Utils(){}

	public static <A> Option<A> get(List<A> list, int i){
		return Try.of(() -> list.get(i)).toOption();
	}

	public static <K, V> Option<V> get(Map<K, V> map, K key){
		return Try.of(() -> map.get(key)).toOption();
	}

	public static <A> A head(List<A> list){
		try {
			return list.get(0);
		}catch (IndexOutOfBoundsException e){
			throw new NoSuchElementException();
		}
	}

	public static <A> Option<A> headOption(List<A> list) {
		return get(list, 0);
	}

	public static <A> List<A> tail(List<A> list){
		return tail(list, ArrayList::new);
	}

	public static <A> List<A> tail(List<A> list, Supplier<? extends List<A>> listImpl){

		if(list.isEmpty()) {
			throw new NoSuchElementException();
		}

		List<A> result = listImpl.get();
		for (int i = 1; i < list.size(); i++) {
			result.add(list.get(i));
		}
		return result;
	}

	public static <A> List<Tuple2<Integer, A>> zipWithIndex(List<A> list) {
		Supplier<List<Tuple2<Integer, A>>> s = ArrayList::new;
		return zipWithIndex(list, s);
	}

	public static <A> List<Tuple2<Integer, A>> zipWithIndex(List<A> list, Supplier<List<Tuple2<Integer, A>>> listImpl) {

		List<Integer> indexes = IntStream.range(0, list.size())
			.mapToObj(Integer::valueOf)
			.collect(Collectors.toList());

		return zip(indexes, list, listImpl);
	}

	public static <A, B> List<Tuple2<A, B>> zip(List<A> list1, List<B> list2){
		Supplier<List<Tuple2<A, B>>> s = ArrayList::new;
		return zip(list1, list2, s);
	}

	public static <A, B> List<Tuple2<A, B>> zip(List<A> list1, List<B> list2, Supplier<List<Tuple2<A, B>>> listImpl) {

		List<Tuple2<A, B>> list = listImpl.get();
		if(list1.size() > list2.size()){

			for (int i = 0; i < list2.size(); i++) {
				list.add(Tuple2.of(list1.get(i), list2.get(i)));
			}
		}else{
			for (int i = 0; i < list1.size(); i++) {
				list.add(Tuple2.of(list1.get(i), list2.get(i)));
			}
		}

		return list;
	}
}
