package com.b2b.support;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Point;
import org.openqa.selenium.Rectangle;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Coordinates;
import org.openqa.selenium.support.pagefactory.ElementLocator;

public class B2BWebElementImpl implements B2BWebElement{
	
	protected static final Logger logger = LogManager.getLogger(B2BBy.class);
	
	private WebElement element;
	private WebElement parent;
	private final ElementLocator locator;
	private final By selector;
	protected String id;
	private long waitTime = 60;
	private LocalDateTime startTime;
	private WebDriver driver;
	
	/**
	 * intialize the B2B webelement with locator
	 * @param locator
	 * @param by
	 */
	public B2BWebElementImpl (ElementLocator locator, By by) {
		this.locator = locator;
		this.selector = by;
		this.element = initElement();
		driver=B2BPageFactory.getDriver();
	}
	/**
	 * intialize the B2B webelement
	 * @param by
	 */
	public B2BWebElementImpl (By by) {
		this.locator = null;
		this.selector = by;
		this.element = initElement();
		driver=B2BPageFactory.getDriver();
	}
	
	/**
	 * 
	 * @param parent
	 * @param by
	 */
	public B2BWebElementImpl (WebElement parent, By by) {
		this.locator = null;
		this.selector = by;
		this.parent = parent;
		this.element = initElement();
		driver=B2BPageFactory.getDriver();
	}
	
	/**
	 * initializing the Webelement
	 * @return
	 */
	private WebElement initElement(){
		WebElement newElement = null;
		List<WebElement> elements;
		
		try {
			cancelTimeouts();
			startTime = LocalDateTime.now();
			while(newElement == null && isWaiting()){
				try{
					if(parent == null) {
						elements = (locator != null) ? locator.findElements() : driver.findElements(selector);
					}else {
						elements = (locator != null) ? locator.findElements() : parent.findElements(selector);
					}
					if(elements.size() > 0){
						newElement = elements.get(0);
						break;
					}else{
						waitForElement();
					}
				}catch(Exception ex){
					handleException(ex);
				}
			}
		} finally {
			restoreTimeouts();			
		}
		
		if(newElement == null){
			throw new NoSuchElementException();
		}
		
		return newElement;
	}
	/**
	 * return the B2B webelement
	 */
	public WebElement getWebElement() {
		return this.element;
	}
	/**
	 * return the selector of B2B Webelement
	 * @return
	 */
	public By getSelector() {
		return this.selector;
	}

	/**
	 * implementing the B2B webelement
	 */
	@Override
	public B2BWebElement findB2BWebElement(By by) {
		return new B2BWebElementImpl(this.element, by);
	}
	
	@Override
	public void click() {
		click(null);
	}
	/**
	 * Click on B2Bwebelement
	 */
	public void click(B2BWebElement containerToScroll) {
		driver=B2BPageFactory.getDriver();
		boolean action = false;
		int MAX_CLICK_RETRIES = 100;
		int clickRetries = 0;
		long scrollPos = 0;
		long maxHeight = 0;
		
		try {
			cancelTimeouts();
			startTime = LocalDateTime.now();
			while(!action && isWaiting()){
				try{
					this.element = initElement();
					this.element.click();
					action = true;
					break;
				}catch(StaleElementReferenceException staleEx){
					logger.info("Stale element exception, trying again.");
					logger.info(staleEx.getMessage());
					waitForElement();
				}catch(NullPointerException nEx){
					logger.info("Ignoring null pointer exception, trying again.");
					logger.info(nEx.getMessage());
					waitForElement();
				}catch(WebDriverException webEx){
					if(webEx.getMessage().contains("Other element would receive the click")) {

						maxHeight = getBrowserScrollHeight();
						do {
							try {							
								try {
									if(containerToScroll == null) {
										((JavascriptExecutor)driver).executeScript("window.scrollBy(0, "+ scrollPos +");");
									}else {
										((JavascriptExecutor)driver).executeScript("arguments[0].scrollBy(0, "+ scrollPos +");", containerToScroll.getWebElement());
									}
									this.element.click();
									action = true;
								} catch (Exception ex) {
									scrollPos += 25;
						        	Thread.sleep(250);
						        	clickRetries++;
								}
							} catch (Exception e) {
								logger.info(e.getMessage());
								clickRetries++;
							}
						}while(!action && (scrollPos < maxHeight)  && (clickRetries < MAX_CLICK_RETRIES));
					}else {
						logger.info("WebDriverException found, trying again");
						logger.info(webEx.getMessage());
						waitForElement();
					}
				}
			}
		} finally {
			restoreTimeouts();			
		}

		if(!action){
			throw new WebDriverException("Failed to click B2BWebElement!");
		}
		
	}

	@Override
	public void submit() {

		boolean action = false;
		
		try {
			cancelTimeouts();
			startTime = LocalDateTime.now();
			while(!action && isWaiting()){
				try{
					this.element = initElement();
					this.element.submit();
					action = true;
					break;
				}catch(Exception ex){
					handleException(ex);
				}
			}
		} finally {
			restoreTimeouts();			
		}

		if(!action){
			throw new WebDriverException("Failed to submit B2BWebElement!");
		}

	}

	@Override
	public void sendKeys(CharSequence... keysToSend) {

		boolean action = false;
		
		try {
			cancelTimeouts();
			startTime = LocalDateTime.now();
			while(!action && isWaiting()){
				try{
					this.element = initElement();
					this.element.sendKeys(keysToSend);
					action = true;
					break;
				}catch(Exception ex){
					handleException(ex);
				}
			}
		} finally {
			restoreTimeouts();			
		}

		if(!action){
			throw new WebDriverException("Failed to send keys B2BWebElement!");
		}
	}

	@Override
	public void clear() {

		boolean action = false;
		
		try {
			cancelTimeouts();
			startTime = LocalDateTime.now();
			while(!action && isWaiting()){
				try{
					this.element = initElement();
					this.element.clear();
					action = true;
					break;
				}catch(Exception ex){
					handleException(ex);
				}
			}
		} finally {
			restoreTimeouts();			
		}

		if(!action){
			throw new WebDriverException("Failed to clear B2BWebElement!");
		}
		
	}

	@Override
	public String getTagName() {

		boolean action = false;
		String tagName = null;
		
		try {
			cancelTimeouts();
			startTime = LocalDateTime.now();
			while(!action && isWaiting()){
				try{
					this.element = initElement();
					tagName = this.element.getTagName();
					action = true;
					break;
				}catch(Exception ex){
					handleException(ex);
				}
			}
		} finally {
			restoreTimeouts();			
		}

		if(!action){
			throw new WebDriverException("Failed to get tag name for B2BWebElement!");
		}
		
		return tagName;
	}

	@Override
	public String getAttribute(String name) {

		boolean action = false;
		String attr = null;
		
		try {
			cancelTimeouts();
			startTime = LocalDateTime.now();
			while(!action && isWaiting()){
				try{
					this.element = initElement();
					attr = this.element.getAttribute(name);
					action = true;
					break;
				}catch(Exception ex){
					handleException(ex);
				}
			}
		} finally {
			restoreTimeouts();			
		}

		if(!action){
			throw new WebDriverException("Failed to get attribute B2BWebElement!");
		}
		return attr;
	}

	@Override
	public boolean isSelected() {

		boolean action = false;
		boolean isSelected = false;
		
		try {
			cancelTimeouts();
			startTime = LocalDateTime.now();
			while(!action && isWaiting()){
				try{
					this.element = initElement();
					action = true;
					isSelected = this.element.isSelected();
					break;
				}catch(Exception ex){
					handleException(ex);
				}
			}
		} finally {
			restoreTimeouts();			
		}

		if(!action){
			throw new WebDriverException("Failed to check isSelected for B2BWebElement!");
		}
		return isSelected;
	}

	@Override
	public boolean isEnabled() {

		boolean action = false;
		boolean enabled = false;
		
		try {
			cancelTimeouts();
			startTime = LocalDateTime.now();
			while(!action && isWaiting()){
				try{
					this.element = initElement();
					enabled = this.element.isEnabled();
					action = true;
					break;
				}catch(Exception ex){
					handleException(ex);
				}
			}
		} finally {
			restoreTimeouts();			
		}

		if(!action){
			throw new WebDriverException("Failed to get isEnabled B2BWebElement!");
		}

		return enabled;
	}

	@Override
	public String getText() {

		boolean action = false;
		String text = null;
		
		try {
			cancelTimeouts();
			startTime = LocalDateTime.now();
			while(!action && isWaiting()){
				try{
					this.element = initElement();
					text = this.element.getText();
					action = true;
					break;
				}catch(Exception ex){
					handleException(ex);
				}
			}
		} finally {
			restoreTimeouts();			
		}

		if(!action){
			throw new WebDriverException("Failed to get text B2BWebElement!");
		}
		return text;
	}

	@Override
	public List<WebElement> findElements(By by) {
		
		boolean action = false;
		List<WebElement> elements = new ArrayList<WebElement>();
		
		try {
			cancelTimeouts();
			startTime = LocalDateTime.now();
			while(!action && isWaiting()){
				try{
					this.element = initElement();
					elements = this.element.findElements(by);
					action = true;
					break;
				}catch(Exception ex){
					handleException(ex);
				}
			}
		} finally {
			restoreTimeouts();		
		}

		if(!action){
			throw new WebDriverException("Failed to find elements in B2BWebElement!");
		}
		
		return elements;
	}

	@Override
	public WebElement findElement(By by) {
		
		boolean action = false;
		WebElement newElement = null;
		
		try {
			cancelTimeouts();
			startTime = LocalDateTime.now();
			while(!action && isWaiting()){
				try{
					this.element = initElement();
					newElement = this.element.findElement(by);
					action = true;
					break;
				}catch(Exception ex){
					handleException(ex);
				}
			}
		} finally {
			restoreTimeouts();			
		}

		if(!action){
			throw new WebDriverException("Failed to find element in B2BWebElement!");
		}
		
		return newElement;
	}

	@Override
	public boolean isDisplayed() {

		boolean action = false;
		boolean displayed = false;
		
		try {
			cancelTimeouts();
			startTime = LocalDateTime.now();
			while(!action && isWaiting()){
				try{
					this.element = initElement();
					displayed = this.element.isDisplayed();
					action = true;
					break;
				}catch(Exception ex){
					handleException(ex);
				}
			}
		} finally {
			restoreTimeouts();			
		}

		if(!action){
			throw new WebDriverException("Failed to get text B2BWebElement!");
		}
		return displayed;
	}

	@Override
	public Point getLocation() {

		boolean action = false;
		Point location = null;
		
		try {
			cancelTimeouts();
			startTime = LocalDateTime.now();
			while(!action && isWaiting()){
				try{
					this.element = initElement();
					location = this.element.getLocation();
					action = true;
					break;
				}catch(Exception ex){
					handleException(ex);
				}
			}
		} finally {
			restoreTimeouts();			
		}

		if(!action){
			throw new WebDriverException("Failed to get location of B2BWebElement!");
		}
		
		return location;
	}

	@Override
	public Dimension getSize() {

		boolean action = false;
		Dimension dimension = null;
		
		try {
			cancelTimeouts();
			startTime = LocalDateTime.now();
			while(!action && isWaiting()){
				try{
					this.element = initElement();
					dimension = this.element.getSize();
					action = true;
					break;
				}catch(Exception ex){
					handleException(ex);
				}
			}
		} finally {
			restoreTimeouts();			
		}

		if(!action){
			throw new WebDriverException("Failed to get size of B2BWebElement!");
		}
		
		return dimension;
	}

	@Override
	public Rectangle getRect() {

		boolean action = false;
		Rectangle rectangle = null;
		
		try {
			cancelTimeouts();
			startTime = LocalDateTime.now();
			while(!action && isWaiting()){
				try{
					this.element = initElement();
					rectangle = this.element.getRect();
					action = true;
					break;
				}catch(Exception ex){
					handleException(ex);
				}
			}
		} finally {
			restoreTimeouts();			
		}
		
		if(!action){
			throw new WebDriverException("Failed to get rect for B2BWebElement!");
		}
		
		return rectangle;
	}

	@Override
	public String getCssValue(String propertyName) {

		boolean action = false;
		String cssValue = "";
		
		try {
			cancelTimeouts();
			startTime = LocalDateTime.now();
			while(!action && isWaiting()){
				try{
					this.element = initElement();
					cssValue = this.element.getCssValue(propertyName);
					action = true;
					break;
				}catch(Exception ex){
					handleException(ex);
				}
			}
		} finally {
			restoreTimeouts();			
		}
		
		if(!action){
			throw new WebDriverException("Failed to get css value B2BWebElement!");
		}
		
		return cssValue;
	}

	@Override
	public <X> X getScreenshotAs(OutputType<X> outputType) throws WebDriverException {
		// TODO Not used at the moment, will implement at a later date
		return null;
	}

	@Override
	public WebElement getWrappedElement() {
		// TODO Not used at the moment, will implement at a later date
		return null;
	}

	@Override
	public Coordinates getCoordinates() {
		// TODO Not used at the moment, will implement at a later date
		return null;
	}
	
	private long getBrowserScrollHeight() {
		//return (long) ((Object) driver).executeJavascript("return document.body.scrollHeight;");
		return (Long) null;
	}
	
	private void handleException(Exception ex) {
		if(ex instanceof StaleElementReferenceException) {
			logger.info("Stale element exception, trying again.");
		}else if(ex instanceof NullPointerException) {
			logger.info("Ignoring null pointer exception, trying again.");
		}else if(ex instanceof WebDriverException) {
			logger.info("WebDriverException found, trying again");
		}else {
			logger.info("Unexpected exception found, trying again");
		}
		
		logger.info(ex.getMessage());
		waitForElement();
	}
	
	private void waitForElement() {
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			//ignore
		} 
	}
	
	private boolean isWaiting() {
		return (Duration.between(startTime, LocalDateTime.now()).getSeconds() < waitTime);
	}
	
	private void cancelTimeouts() {
		//setJQueryImplicitTimeout(0);
		driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
	}
	
	private void restoreTimeouts() {
		//setJQueryImplicitTimeout(AutomationFramework.getDefaultTimeout());
		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
	}

	}
