package com.b2b.support;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.WrapsElement;
import org.openqa.selenium.interactions.Locatable;

public interface B2BWebElement extends WebElement, WrapsElement, Locatable{

	B2BWebElement findB2BWebElement(By by);
	void click(B2BWebElement parameterViewPort);
	WebElement getWebElement();
}
