package com.b2b.support;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.pagefactory.ElementLocatorFactory;
import org.openqa.selenium.support.pagefactory.FieldDecorator;

import com.b2b.support.pagefactory.B2BFieldDecorator;
import com.b2b.support.pagefactory.B2BElementLocatorFactory;

public class B2BPageFactory {

	public static WebDriver childDriver;
	public static <T> T initElements(WebDriver driver, Class<T> pageClassToProxy) {
	    T page = instantiatePage(driver, pageClassToProxy);
	    initElements(driver, page);
	    return page;
	  }
	/**
	 * initializing driver with class objects
	 * @param driver
	 * @param page
	 */
	  public static void initElements(WebDriver driver, Object page) {
	    final WebDriver driverRef = driver;
	    childDriver=driver;
	    initElements(new B2BElementLocatorFactory(driverRef), page);
	  }
	  /**
	   * return the webdriver
	   * @return
	   */
	  public static WebDriver getDriver()
	  {
		  return childDriver;
	  }
	  public static void initElements(ElementLocatorFactory factory, Object page) {
	    final ElementLocatorFactory factoryRef = factory;
	    initElements(new B2BFieldDecorator(factoryRef), page);
	  }

	  public static void initElements(FieldDecorator decorator, Object page) {
	    Class<?> proxyIn = page.getClass();
	    while (proxyIn != Object.class) {
	      proxyFields(decorator, page, proxyIn);
	      proxyIn = proxyIn.getSuperclass();
	    }
	  }

	  private static void proxyFields(FieldDecorator decorator, Object page, Class<?> proxyIn) {
	    Field[] fields = proxyIn.getDeclaredFields();
	    for (Field field : fields) {
	      Object value = decorator.decorate(page.getClass().getClassLoader(), field);
	      if (value != null) {
	        try {
	          field.setAccessible(true);
	          field.set(page, value);
	        } catch (IllegalAccessException e) {
	          throw new RuntimeException(e);
	        }
	      }
	    }
	  }

	  private static <T> T instantiatePage(WebDriver driver, Class<T> pageClassToProxy) {
	    try {
	      try {
	        Constructor<T> constructor = pageClassToProxy.getConstructor(WebDriver.class);
	        return constructor.newInstance(driver);
	      } catch (NoSuchMethodException e) {
	        return pageClassToProxy.newInstance();
	      }
	    } catch (InstantiationException e) {
	      throw new RuntimeException(e);
	    } catch (IllegalAccessException e) {
	      throw new RuntimeException(e);
	    } catch (InvocationTargetException e) {
	      throw new RuntimeException(e);
	    }
	  }
}
