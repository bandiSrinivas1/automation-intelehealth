package com.b2b.support.pagefactory;

import java.lang.reflect.Field;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.pagefactory.ElementLocator;


/**
 * The B2B element locator, which will lazily locate an element or an element list on a page. 
 */
public class B2BElementLocator implements ElementLocator {
  private final SearchContext searchContext;
  private final boolean shouldCache;
  private final By by;
  private WebElement cachedElement;
  private List<WebElement> cachedElementList;

  /**
   * Creates a new B2Belement locator.
   * 
   * @param searchContext The context to use when finding the element
   * @param field The field on the Page Object that will hold the located value
   */
  public B2BElementLocator(SearchContext searchContext, Field field) {
    this.searchContext = searchContext;
    B2BAnnotationsBuild annotations = new B2BAnnotationsBuild(field);
    shouldCache = annotations.isLookupCached();
    by = annotations.buildBy();
  }

  /**
   * Find the element.
   */
  public WebElement findElement() {
    if (cachedElement != null && shouldCache) {
      return cachedElement;
    }

    WebElement element = searchContext.findElement(by);
    if (shouldCache) {
      cachedElement = element;
    }

    return element;
  }

  /**
   * Find the element list.
   */
  public List<WebElement> findElements() {
    if (cachedElementList != null && shouldCache) {
      return cachedElementList;
    }

    List<WebElement> elements = searchContext.findElements(by);
    if (shouldCache) {
      cachedElementList = elements;
    }

    return elements;
  }
}
