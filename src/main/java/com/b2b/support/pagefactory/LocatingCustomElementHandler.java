package com.b2b.support.pagefactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.support.pagefactory.ElementLocator;

import com.b2b.support.B2BWebElement;
import com.b2b.support.B2BWebElementImpl;

public class LocatingCustomElementHandler implements InvocationHandler {
	private final ElementLocator locator;
	private final By b2Bby;

	public LocatingCustomElementHandler(ElementLocator locator, By b2BBy) {
		this.locator = locator;
		this.b2Bby = b2BBy;
	}
	/**
	 * B2Bwebelement implemets with locatir and B2Bby
	 */
	public Object invoke(Object object, Method method, Object[] objects) throws Throwable {
		B2BWebElement element;
		try {
			element = new B2BWebElementImpl(locator, b2Bby);
			} catch (NoSuchElementException e) {
			if ("toString".equals(method.getName())) {
				return "Proxy element for: " + locator.toString();
				
			}
			throw e;
		}

		if ("getWrappedElement".equals(method.getName())) {
			return element;
		}

		try {
			return method.invoke(element, objects);
		} catch (InvocationTargetException e) {
			// Unwrap the underlying exception
			throw e.getCause();
		}
	}
}