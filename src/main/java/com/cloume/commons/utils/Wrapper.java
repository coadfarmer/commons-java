package com.cloume.commons.utils;

public class Wrapper<T> {
	private T object;
	
	protected Wrapper(T object) {
		this.object = object;
	}
	
	public void set(T newValue) {
		object = newValue;
	}
	public T get() {
		return object;
	}
	
	static public <R> Wrapper<R> wrap(R object) {
		return new Wrapper<R>(object);
	}
}
