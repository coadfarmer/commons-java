package com.cloume.commons.utils;

public final class Arrays {

	@SafeVarargs
	static public <T> T[] from(T ...elements) {
		return elements;
	}
}
