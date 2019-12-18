package com.cloume.commons.verify;

import com.cloume.commons.verify.checker.IChecker;

public interface IRule {
	String getKey();
	IChecker getChecker();
	
	boolean isOptional();
}
