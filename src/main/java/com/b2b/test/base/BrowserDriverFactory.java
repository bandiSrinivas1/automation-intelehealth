package com.b2b.test.base;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.safari.SafariDriver;

import io.github.bonigarcia.wdm.WebDriverManager;

public class BrowserDriverFactory {

	private static final Logger logger = LogManager.getLogger(BrowserDriverFactory.class);

	private ThreadLocal<WebDriver> driver = new ThreadLocal<WebDriver>();
	private String browser = null;
	private Boolean isHeadLess = false;
	private String osName = System.getProperty("os.name");
	private String LINUX_OS = "Linux";
	private String WINDOWS_OS = "Linux";
	private String MAC_OS = "Mac OS X";
	private String remote_url = "http://34.93.230.101:4444/";

	public BrowserDriverFactory() {
		this.browser = "chrome";
		this.isHeadLess = false;
	}

	public BrowserDriverFactory(String browser) {
		logger.debug("Browser is " + browser);

		this.browser = browser.toLowerCase();
		this.isHeadLess = false;
	}

	public BrowserDriverFactory(String browser, Boolean isHeadLess) {
		this.browser = browser.toLowerCase();
		this.isHeadLess = isHeadLess;
	}
	
	public BrowserDriverFactory(String browser, Boolean isHeadLess,String remoteURL) {
		
		this.browser = browser.toLowerCase();
		this.isHeadLess = isHeadLess;
		this.remote_url = remoteURL;
		
	}

	public WebDriver createDriver() {

		switch (browser) {
		case "chrome":
			this.driver.set(getChromeDriver());
			break;

		case "firefox":
			this.driver.set(getFirefoxDriver());
			break;

		case "safari":
			this.driver.set(getSafariDriver());
			break;

		case "IE":
			this.driver.set(getIEDriver());
			break;

		case "Chromium":
			this.driver.set(getChromiumDriver());
			break;
			
		case "grid":
			this.driver.set(getChromeRemoteDriver());
			break;

		default:
			logger.debug("No browser details mentioned, using Chrome driver");
			this.driver.set(getChromeDriver());
			break;

		}
		return this.driver.get();
	}

	private WebDriver getIEDriver() {
		WebDriverManager.iedriver().setup();
		this.driver.set(new InternetExplorerDriver());
		return this.driver.get();
	}

	private WebDriver getChromiumDriver() {
		WebDriverManager.chromiumdriver().setup();
		this.driver.set(new EdgeDriver());
		return this.driver.get();

	}

	private WebDriver getSafariDriver() {
		String browserDriverPath = "/usr/bin/safaridriver";

		if (browserDriverPath.contains("safaridriver")) {
			System.setProperty("webdriver.safari.driver", browserDriverPath);
			this.driver.set(new SafariDriver());
		}

		logger.debug("Safari driver path " + browserDriverPath);
		return this.driver.get();
	}

	private WebDriver getChromeDriver() {

		if (this.osName.contains("Linux")) {
			return getChromeLinuxDriver();
		} else {
			return getChromeWindowDriver();
		}
	}

	private WebDriver getFirefoxDriver() {

		if (this.osName.contains("Linux")) {
			return getFirefoxLinuxDriver();
		} else {
			return getFirefoxWindowsDriver();
		}
	}

	private WebDriver getFirefoxLinuxDriver() {

		WebDriver driver = null;
		WebDriverManager.firefoxdriver().setup();
		FirefoxOptions options = new FirefoxOptions();
		options.setHeadless(true);
		options.addArguments("--no-sandbox");
		driver = new FirefoxDriver(options);

		return driver;
	}

	private WebDriver getFirefoxWindowsDriver() {
		WebDriver driver = null;
		WebDriverManager.firefoxdriver().setup();
		driver = new FirefoxDriver();
		return driver;
	}

	private WebDriver getChromeWindowDriver() {
		WebDriver driver = null;
		WebDriverManager.chromedriver().setup();

		ChromeOptions options = new ChromeOptions();

		options.addArguments("enable-automation");
		options.addArguments("--headless");
		options.addArguments("--no-sandbox");
		options.addArguments("--disable-extensions");
		options.addArguments("--dns-prefetch-disable");
		options.addArguments("--disable-gpu");
		options.addArguments("--remote-allow-origins=*");
		options.setPageLoadStrategy(PageLoadStrategy.NORMAL);

		options.setHeadless(this.isHeadLess);
		driver = new ChromeDriver(options);
		return driver;
	}

	private WebDriver getChromeLinuxDriver() {
		WebDriver driver = null;
		WebDriverManager.chromedriver().setup();

		ChromeOptions options = new ChromeOptions();

		options.addArguments("enable-automation");
		options.addArguments("--headless");
		options.addArguments("--no-sandbox");
		options.addArguments("--disable-extensions");
		options.addArguments("--dns-prefetch-disable");
		options.addArguments("--disable-gpu");
		options.setPageLoadStrategy(PageLoadStrategy.NORMAL);

		options.setHeadless(true);
		options.addArguments("--headless"); // Bypass OS security model, MUST BE THE VERY FIRST OPTION
		options.addArguments("--window-size=1920,1080");
		options.setPageLoadStrategy(PageLoadStrategy.EAGER);// del
		options.addArguments("--disable-browser-side-navigation"); // del
		options.addArguments("--disable-dev-shm-usage"); // del
		options.addArguments("--disable-gpu");
		options.addArguments("--no-sandbox");

		// options.setBinary("/opt/google/chrome/google-chrome");
		Map<String, Object> prefs = new HashMap<String, Object>();
		prefs.put("profile.default_content_settings.popups", 0);
		options.setExperimentalOption("prefs", prefs);

		DesiredCapabilities capabilities = new DesiredCapabilities();
		capabilities.setBrowserName("CHROME");
		capabilities.setCapability(ChromeOptions.CAPABILITY, options);
		capabilities.setCapability(CapabilityType.SUPPORTS_NETWORK_CONNECTION, true);
		capabilities.setCapability("applicationCacheEnabled", "true");

		driver = new ChromeDriver(options);
		return driver;
	}
	
	private WebDriver getChromeRemoteDriver() {
		
		WebDriver driver = null;
		DesiredCapabilities capabilities = new DesiredCapabilities();
		capabilities.setBrowserName("chrome");
		capabilities.setCapability("headless", this.isHeadLess);
		
		try {
			driver = new RemoteWebDriver(new URL(this.remote_url),capabilities);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		
		return driver;
	}

}
