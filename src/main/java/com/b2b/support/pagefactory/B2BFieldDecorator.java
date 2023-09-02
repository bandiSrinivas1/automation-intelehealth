package com.b2b.support.pagefactory;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.WrapsElement;
import org.openqa.selenium.interactions.Locatable;
import org.openqa.selenium.support.pagefactory.Annotations;
import org.openqa.selenium.support.pagefactory.ElementLocator;
import org.openqa.selenium.support.pagefactory.ElementLocatorFactory;
import org.openqa.selenium.support.pagefactory.FieldDecorator;
import org.openqa.selenium.support.pagefactory.internal.LocatingElementHandler;
import org.openqa.selenium.support.pagefactory.internal.LocatingElementListHandler;

import com.b2b.support.B2BFindAll;
import com.b2b.support.B2BFindBy;
import com.b2b.support.B2BFindBys;
import com.b2b.support.B2BWebElement;


public class B2BFieldDecorator implements FieldDecorator {

	protected ElementLocatorFactory factory;

	public B2BFieldDecorator(ElementLocatorFactory factory) {
		this.factory = factory;
	}
	/**
	 * return the B2Bwebelement as object
	 * 
	 */
	public Object decorate(ClassLoader loader, Field field) {
		
		By b2BBy = null;
		
		if (!(WebElement.class.isAssignableFrom(field.getType()) || isDecoratableList(field)) 
				&& !(B2BWebElement.class.isAssignableFrom(field.getType()) || isDecoratableList(field))) {
			return null;
		}
		
		ElementLocator locator = factory.createLocator(field);
		if (locator == null) {
			return null;
		}

		Class<?> fieldType = field.getType();
		if (WebElement.class.getName().equals(fieldType.getName())) {
			fieldType = WebElement.class;
		}else {
			fieldType = B2BWebElement.class;
			b2BBy = new Annotations(field).buildBy();
		}

		if (WebElement.class.isAssignableFrom(field.getType())) {
			return proxyForLocator(loader, fieldType, locator, b2BBy);
		} else if (List.class.isAssignableFrom(field.getType())) {
			return proxyForListLocator(loader, locator);
		} else {
			return null;
		}
	}

	private boolean isDecoratableList(Field field) {
		if (!List.class.isAssignableFrom(field.getType())) {
			return false;
		}

		Type genericType = field.getGenericType();
		if (!(genericType instanceof ParameterizedType)) {
			return false;
		}

		Type listType = ((ParameterizedType) genericType).getActualTypeArguments()[0];

		if (!WebElement.class.equals(listType)) {
			return false;
		}

		if (field.getAnnotation(B2BFindBy.class) == null && field.getAnnotation(B2BFindBys.class) == null
				&& field.getAnnotation(B2BFindAll.class) == null) {
			return false;
		}

		return true;
	}

		protected <T> T proxyForLocator(ClassLoader loader, Class<T> interfaceType, ElementLocator locator, By b2BBy) {
		InvocationHandler handler; // = new LocatingElementHandler(locator);
		T proxy;
		Class[] classList;

		if (interfaceType.equals(B2BWebElement.class)) {
			handler = new LocatingCustomElementHandler(locator, b2BBy);
			classList = new Class[] { interfaceType, WebElement.class, WrapsElement.class, Locatable.class };
		} else {
			handler = new LocatingElementHandler(locator);
			classList = new Class[] { WebElement.class, WrapsElement.class, Locatable.class };
		}

		proxy = interfaceType.cast(Proxy.newProxyInstance(loader, classList, handler));
		return proxy;
	}

	@SuppressWarnings("unchecked")
	protected List<WebElement> proxyForListLocator(ClassLoader loader, ElementLocator locator) {
		InvocationHandler handler = new LocatingElementListHandler(locator);

		List<WebElement> proxy;
		proxy = (List<WebElement>) Proxy.newProxyInstance(loader, new Class[] { List.class }, handler);
		return proxy;
	}
}
