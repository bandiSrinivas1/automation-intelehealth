package com.b2b.support;

import java.io.Serializable;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.WrapsDriver;


public abstract class B2BBy extends By {
	
	protected static final Logger logger = LogManager.getLogger(B2BBy.class);
	
		/**
		 * return selector
		 * @param jQuerySelector
		 * @return
		 */
	public static By jQuery(final String jQuerySelector) {
		if (jQuerySelector == null)
			throw new IllegalArgumentException(
					"Cannot find elements with the jQuery selector.");

		return new ByjQuery(jQuerySelector);
	}
	
	public static class ByjQuery extends By implements Serializable {
		private static final long serialVersionUID = -3791116861519490967L;
		private final String selector;
		private final String jQuerySingleSelectorFormat = "return %s.get(0)";
		private final String jQueryMultiSelectorFormat = "return %s.get()";
		
	    public ByjQuery(String selector) {
	      this.selector = selector;
	    }
	    
	    /**
	     * return locator of list of B2Bwebelement
	     */
	    @SuppressWarnings("unchecked")
		@Override
	    public List<WebElement> findElements(SearchContext context) {
	    	List<WebElement> elementsLocated = null;
	    	long MAX_TIMEOUT_MILLISECONDS = 10000;
	    	
	    	int WAIT_TIME = 100;
	    	int time = 0;
	    	try{
	    		do{
	    			if (context instanceof WrapsDriver){
	    				elementsLocated = (List<WebElement>) ((JavascriptExecutor) ((WrapsDriver) context).getWrappedDriver()).executeScript(String.format(jQueryMultiSelectorFormat, selector));
    				}else{
    					elementsLocated = (List<WebElement>)((JavascriptExecutor) context).executeScript(String.format(jQueryMultiSelectorFormat, selector));
		    		}
    				Thread.sleep(WAIT_TIME);
    				time += WAIT_TIME;
	    		}while((elementsLocated == null || elementsLocated.size() == 0) && time < MAX_TIMEOUT_MILLISECONDS);
	    	}catch(Exception ex){
	    		logger.debug(ex);
	    		}
	    	
	    	return elementsLocated;
	    }
	    /**
	     * find the locator of B2Bwebelement
	     */
	    @Override
	    public WebElement findElement(SearchContext context) {
	    	WebElement elementLocated = null;
	    	//long MAX_TIMEOUT_MILLISECONDS = AutomationFrameworkTest.getJQueryImplicitTimeout() * 1000;
	    	long MAX_TIMEOUT_MILLISECONDS = 10000;
	    	int WAIT_TIME = 100;
	    	int time = 0;
	    	try{
	    		do{
	    			if (context instanceof WrapsDriver){
	    				elementLocated =  (WebElement) ((JavascriptExecutor) ((WrapsDriver) context).getWrappedDriver()).executeScript(String.format(jQuerySingleSelectorFormat, selector));
	    			}else{
	    				elementLocated =   (WebElement)((JavascriptExecutor) context).executeScript(String.format(jQuerySingleSelectorFormat, selector));
	    			}
	    			Thread.sleep(WAIT_TIME);
	    			time += WAIT_TIME;
		    	 }while(elementLocated == null && time < MAX_TIMEOUT_MILLISECONDS);
	    	}catch(Exception ex){
	    		logger.debug(ex);}
	    	
	    	return elementLocated;
	    }
	    /**
	     * return the locator of B2Bwebelement as a string format
	     */
	    @Override
	    public String toString() {
	      return "By.jQuerySelector: " + selector;
	    }
	  }
	
	 }
