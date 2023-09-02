package com.b2b.support.pagefactory;

import java.lang.reflect.Field;

import org.openqa.selenium.SearchContext;
import org.openqa.selenium.support.pagefactory.ElementLocator;
import org.openqa.selenium.support.pagefactory.ElementLocatorFactory;

public class B2BElementLocatorFactory implements ElementLocatorFactory {

	private final SearchContext searchContext;

	public B2BElementLocatorFactory(SearchContext searchContext) {
		this.searchContext = searchContext;
	}

	public ElementLocator createLocator(Field field) {
		return new B2BElementLocator(searchContext, field);
	}
}
