package com.b2b.base;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;
import java.io.File;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.b2b.support.B2BPageFactory;

public class B2BBaseAutomationPage {
	protected WebDriver driver = null;

	private static final Logger log = LogManager.getLogger(B2BBaseAutomationPage.class);

	public static String TEST_FILE_PATH = null;
	private static Map<String, Properties> CONFIG_FILES_KEY = null;

	public B2BBaseAutomationPage(WebDriver driver) {
		this.driver = driver;

		if (TEST_FILE_PATH == null) {
			TEST_FILE_PATH = getTestFilePath();

			log.debug("In Constructor " + TEST_FILE_PATH);
		}
		log.debug("b2b base page before init In Constructor ");
		B2BPageFactory.initElements(driver, this);
		log.debug("b2b base page after init In Constructor ");
		
	}

	public WebDriver getWebDriver() {
		return this.driver;
	}

	public String getTestFilePath() {
		String path = "src/main/resources";
		File file = new File(path);
		return file.getAbsolutePath();
	}

	protected void selectDropdown(String id, String value) {
		log.info("Starting of selectDropdown method");

		Select conditions = new Select(driver.findElement(By.id(id)));
		conditions.selectByValue(value);

		log.info("Ending of selectDropdown method");
	}

	protected void selectValueFromDropdown(WebElement element, String value) {
		log.info("Starting of selectValueFromDropdown method");

		Select conditions = new Select(element);
		conditions.selectByValue(value);

		log.info("Ending of selectValueFromDropdown method");
	}

	public void clickOnWebElement(WebElement webelement) {
		log.info("Starting of clickOnWebElement method");

		JavascriptExecutor jsExec = (JavascriptExecutor) driver;
		jsExec.executeScript("arguments[0].click();", webelement);

		log.info("Ending of clickOnWebElement method");
	}

	public void scrollDown(int scroll) {
		log.info("Starting of scrollDown method");

		JavascriptExecutor js = (JavascriptExecutor) driver;
		js.executeScript("window.scrollBy(0, " + scroll + ")");

		log.info("Ending of scrollDown method");
	}

	public void refresh() {
		log.info("Starting of refresh method");

		driver.navigate().refresh();

		log.info("Ending of refresh method");
	}

	public void impicitWait() {
		log.info("Starting of impicitWait method");

		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(50));

		log.info("Ending of impicitWait method");
	}

	public void explicitWait(List<WebElement> categoryOptions) {
		log.info("Startng of explicitWait method");

		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(50));
		wait.until(ExpectedConditions.visibilityOfAllElements(categoryOptions));

		log.info("Ending of explicitWait method");
	}

	public void explicitWait(WebElement categoryOptions) {
		log.info("Starting of explicitWait method");

		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(3));
		wait.until(ExpectedConditions.visibilityOf(categoryOptions));

		log.info("Ending of explicitWait method");
	}

	public void pickFromWebElemetList(List<WebElement> webElements, String containsText) {
		log.info("Staritng of pickFromWebElemetList method");

		for (WebElement webElement : webElements) {
			if (webElement.getText().contains(containsText)) {
				this.clickOnWebElement(webElement);
				break;
			}
		}

		log.info("Ending of pickFromWebElemetList method");
	}

	public void pickFromWebElemetList(List<WebElement> webElements, List<WebElement> textWebElements,
			String containsText) {
		log.info("Staritng of pickFromWebElemetList method");

		WebElement webElement = null;
		WebElement textWebElement = null;
		Object[] webElementsArray = webElements.toArray();
		Object[] xPathArray = textWebElements.toArray();

		for (int i = 0; i < webElements.size(); i++) {
			webElement = (WebElement) webElementsArray[i];
			textWebElement = (WebElement) xPathArray[i];
			if (textWebElement.getText().contains(containsText)) {
				this.clickOnWebElement(webElement);
				break;
			}
		}

		log.info("Ending of pickFromWebElemetList method");
	}

	public void uploadFile(String filepath) {

		Robot robot = null;
		try {
			robot = new Robot();

			StringSelection stringSelection = new StringSelection(filepath);
			Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
			clipboard.setContents(stringSelection, null);

			robot.delay(500);
			robot.keyPress(KeyEvent.VK_CONTROL);

			robot.keyPress(KeyEvent.VK_V);
			robot.keyRelease(KeyEvent.VK_V);
			robot.keyRelease(KeyEvent.VK_CONTROL);

			robot.keyPress(KeyEvent.VK_ENTER);

			robot.delay(500);

			robot.keyRelease(KeyEvent.VK_ENTER);

		} catch (AWTException e2) {
			e2.printStackTrace();
		}
	}

	public void scrollIntoView(WebElement element) {
		log.info("Starting of scrollIntoView method");

		JavascriptExecutor jsExec = (JavascriptExecutor) driver;
		jsExec.executeScript("arguments[0].scrollIntoView(true);", element);

		log.info("Ending of scrollIntoView method");
	}

	public void switchToNewWindow() {
		log.info("Starting of switchToNewWindow method");

		ArrayList<String> tab = new ArrayList<String>(driver.getWindowHandles());
		driver.switchTo().window(tab.get(1));

		Set<String> windows = driver.getWindowHandles();
		for (int j = 0; j < 5; j++) {
			if (windows.size() < 2) {
				try {
					Thread.sleep(2000);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}
		windows = driver.getWindowHandles();
		String wins[] = windows.toArray(new String[windows.size()]);
		driver.switchTo().window(wins[1]);

		log.info("Ending of switchToNewWindow method");
	}

	public void closeWindow() {
		log.info("Starting of closeWindow method");

		driver.close();
		ArrayList<String> tab = new ArrayList<String>(driver.getWindowHandles());
		driver.switchTo().window(tab.get(0));

		log.info("Ending of closeWindow method");
	}

	public void clickOutside() {
		log.info("Starting of clickOutside method");

		Actions action = new Actions(driver);
		action.moveByOffset(0, 0).click().build().perform();

		log.info("Ending of clickOutside method");
	}

	public void pickFromWebElement(WebElement webElements, String containsText) {
		log.info("Starting of pickFromWebElement method");

		String el = webElements.toString();
		String xpath = el.substring(el.indexOf("//"), el.length() - 1);
		List<WebElement> lstElements = driver.findElements(By.xpath(xpath));

		for (WebElement webElement : lstElements) {
			if (webElement.getText().contains(containsText)) {
				this.clickOnWebElement(webElement);
				break;
			}
		}
		log.info("Ending of pickFromWebElement method");
	}

	public List<WebElement> getListElement(WebElement webElements) {
		log.info("Starting of getListElement method");

		String el = webElements.toString();
		System.out.println(el);
		String xpath = el.substring(el.indexOf("//"), el.length() - 1);
		System.out.println(xpath);
		List<WebElement> lstElements = driver.findElements(By.xpath(xpath));

		log.info("Ending of getListElement method");
		return lstElements;
	}

	public void pickFromWebElementList(List<WebElement> webElements, List<WebElement> textWebElements,
			String containsText) {
		log.info("Starting of pickFromWebElemetList method");

		WebElement webElement = null;
		WebElement textWebElement = null;
		Object[] webElementsArray = webElements.toArray();
		Object[] xPathArray = textWebElements.toArray();

		for (int i = 0; i < webElements.size(); i++) {
			webElement = (WebElement) webElementsArray[i];
			textWebElement = (WebElement) xPathArray[i];
			if (textWebElement.getText() == containsText) {
				this.clickOnWebElement(webElement);
				break;
			}
		}
		log.info("Ending of pickFromWebElemetList method");
	}

	public List<String> getPropertyList(String name) {
		List<String> list = Arrays.asList(name.toString().split("\\,"));
		System.out.println(list);
		return list;
	}

	public void waitForElementVisibilty(WebElement element, long seconds) {
		WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(seconds));
		wait.until(ExpectedConditions.visibilityOf(element));
		wait.until(ExpectedConditions.elementToBeClickable(element));

	}

	public boolean getAddedContentTexts(List<WebElement> element, String contentName) {
		log.info("Starting of getAddedContentTexts method");
		
		this.explicitWait(element);
		for (WebElement e : element) {
			this.hardWait(2);

			System.out.println(e.getText());
			if (e.getText().equalsIgnoreCase(contentName)) {
				return true;
			}
		}
		log.info("Ending of getAddedContentTexts method");
		return false;
	}

	public void dragAndSort(List<WebElement> webElementList, Integer targetIndex, Integer destIndex) {

		Actions action = new Actions(driver);
		WebElement target = webElementList.get(targetIndex);
		WebElement dest = webElementList.get(destIndex);

		action.click(target).clickAndHold().moveToElement(dest).moveByOffset(0, 50).release().build().perform();
	}

	protected void closeOSWindow() {
		Robot robot = null;
		try {
			if (System.getProperty("os.name").contains("Windows 10")) {
				robot = new Robot();
				for (int i = 0; i < 3; i++) {
					robot.keyPress(KeyEvent.VK_TAB);
					robot.keyRelease(KeyEvent.VK_TAB);

				}
				robot.keyPress(KeyEvent.VK_ENTER);

				robot.delay(500);

				robot.keyRelease(KeyEvent.VK_ENTER);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setTestDataPropertiesMap(Map<String, Properties> testDataPropertiesMap) {
		CONFIG_FILES_KEY = testDataPropertiesMap;
	}

	protected static String getMessage(String fileName, String key) {
//	    log.debug("Enter into getMessage {} , {}", fileName, key);
		return CONFIG_FILES_KEY.get(fileName).getProperty(key, null);
	}

	public void deleteInputFields(WebElement element) {
		if (!System.getProperty("os.name").contains("Mac")) {
			element.sendKeys(Keys.CONTROL + "a");
			element.sendKeys(Keys.DELETE);
		} else {
			element.sendKeys(Keys.COMMAND + "a");
			element.sendKeys(Keys.DELETE);

		}
	}

	public void hardWait(int seconds) {
		try {
			Thread.sleep(seconds * 1000);
		} catch (InterruptedException ex) {
			ex.printStackTrace();
		}
	}

	public WebElement webElementWithReplacement(WebElement webElement, String replaceText) {
		log.info("Starting of webElementWithReplacement method");

		String el = webElement.toString();
		String xpath = el.substring(el.indexOf("//"), el.length() - 1);
		WebElement ele = driver.findElement(By.xpath(xpath.replaceAll("\\$\\{.+\\}", replaceText)));
		log.info("Ending of webElementWithReplacement method");

		return ele;
	}

	public String currentUrl() {
		return driver.getCurrentUrl();
	}
}
