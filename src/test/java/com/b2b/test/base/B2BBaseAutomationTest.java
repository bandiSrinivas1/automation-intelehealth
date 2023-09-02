package com.b2b.test.base;

import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.function.Function;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Listeners;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;

import com.b2b.support.ParameterProvider;
import com.b2b.utils.AutomationUtil;
import com.b2b.utils.B2BBaseTestListener;
import com.b2b.utils.B2BConstants;
import com.clasplusapp.web.pages.ClassplusLoginPage;
import com.classplusapp.common.WebDriversEnum;

@Listeners(B2BBaseTestListener.class)
public abstract class B2BBaseAutomationTest {

	private static final Logger log = LogManager.getLogger(B2BBaseAutomationTest.class);
	protected static final String BASE_DIR = System.getProperty("user.dir");
	protected static final String FILE_SEPARATOR = System.getProperty("file.separator");
	protected ClassplusLoginPage loginPage;

	protected static String browserDriverPath = null;
	protected static String loginURL = null;
	protected static Map<String, String> CHROM_DRIVER_MAP = new HashMap<String, String>();
	protected ThreadLocal<WebDriver> childWebDriver = new ThreadLocal<WebDriver>();
	private boolean isHeadLess = false;
	private String langugage = "en";
	private String browser = "chrome";
	private String remoteURL = "http://192.168.0.114:4444/";
	protected static Properties testDataProp = null;
	protected static Properties expectedAssertionsProp = null;
	protected static Properties langXPathsProp = null;
	private static Map<WebDriversEnum, WebDriver> webDriverPool = new Hashtable<WebDriversEnum, WebDriver>();

	protected static final String emailAddress = System.getProperty("store_email_address") != null
			? System.getProperty("store_email_address")
			: "gunjan@classplusapp.com";
	protected static final String tutorMobileNumber = System.getProperty("store_tutor_mobile_number") != null
			? System.getProperty("store_tutor_mobile_number")
			: "9711153079";
	protected static final String studentMobileNumber = System.getProperty("store_student_mobile_number") != null
			? System.getProperty("store_student_mobile_number")
			: "7290085267";
	protected static final String facultyOneMobileNumber = System.getProperty("store_faculty_mobile_number") != null
			? System.getProperty("store_faculty_mobile_number")
			: "9966119838";
	protected static final String parentMobileNumber = System.getProperty("store_parent_mobile_number") != null
			? System.getProperty("store_parent_mobile_number")
			: "9899805993";
	protected static final String facultyTwoMobileNumber = System.getProperty("store_faculty_two_mobile_number") != null
			? System.getProperty("store_faculty_two_mobile_number")
			: "9885672403";
	protected static final String tutorTwoMobileNumber = System.getProperty("store_tutor_two_mobile_number") != null
			? System.getProperty("store_tutor_two_mobile_number")
			: "6868594886";
	protected static final String orgCode = System.getProperty("orgCode") != null
			? System.getProperty("orgCode")
			: "learn";
	

	@BeforeSuite
	@Parameters({ "siteURL", "language", "browser", "headless", "remoteURL" })
	public void initTestAutomation(@Optional("https://webp-gcp.classplusapp.com") String siteURL,
			@Optional("en") String lang, @Optional("chrome") String browser, @Optional("false") boolean headless,
			@Optional("http://34.93.230.101:4444/") boolean remoteURL) {
		if (siteURL != null) {
			loginURL = siteURL;
		}
		this.browser = browser;
		this.isHeadLess = headless;
		this.langugage = lang;
		System.out.println("+++++++++++++++++++++++++++++++++++++++" + lang);

		this.loadProjectConfigFiles();

		if (testDataProp == null) {
			FileReader testDataReader = null;
			FileReader assertionsReader = null;
			FileReader langxPathReader = null;

			try {

				String testDataFile = AutomationUtil.getLanguageFilePath(B2BConstants.TEST_DATA_PROPERTIES, lang);
				testDataReader = new FileReader(testDataFile);
				testDataProp = new Properties();
				testDataProp.load(testDataReader);

				String expectedAssertionFile = AutomationUtil
						.getLanguageFilePath(B2BConstants.EXPECTED_ASSERTIONS_PROPERTIES, lang);
				assertionsReader = new FileReader(expectedAssertionFile);
				expectedAssertionsProp = new Properties();
				expectedAssertionsProp.load(assertionsReader);

				String langxPathFile = AutomationUtil.getLanguageFilePath(B2BConstants.LANG_XPATHS_PROPERTIES, lang);
				langxPathReader = new FileReader(langxPathFile);
				langXPathsProp = new Properties();
				langXPathsProp.load(langxPathReader);

			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					if (testDataReader != null) {
						testDataReader.close();
					}
					assertionsReader.close();
					langxPathReader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		log.debug("Site URL :{} ", loginURL);
	}

	public void loadProjectConfigFiles() {
		ParameterProvider.getInstance().loadPropertiesFiles(this.langugage);
	}

	protected synchronized void quitDriver(WebDriver driver, WebDriversEnum driverName) {
		log.info("Starting of method quitDriver in BaseAutomationTest");

		try {
			if (driver != null) {
				driver.quit();
				driver = null;
				log.debug(driver + ", Web driver quit successfully in BaseAutomationTest");
			}
		} catch (Exception ex) {
			log.error(ex.getMessage());
			driver = null;
		}
		log.info("Ending of method quitDriver in BaseAutomationTest");
	}

	/**
	 * This method is used for get driver
	 * 
	 * @param webDriver
	 * @return
	 */

	protected WebDriver getWebDriver(WebDriversEnum webDriver) {
		log.info("Starting of method getWebDriver");
		WebDriver driver = webDriverPool.get(webDriver);

		if (driver != null) {
			return driver;
		}

		BrowserDriverFactory factory = null;
		if (this.browser == "grid")
			factory = new BrowserDriverFactory(this.browser, this.isHeadLess, this.remoteURL);
		else {
			factory = new BrowserDriverFactory(this.browser, this.isHeadLess);
		}
		driver = factory.createDriver();

		driver.manage().window().maximize();
		driver.manage().deleteAllCookies();
		driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(60));
		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(60));

		log.info("***************** Driver Successfully Created **************** {} ", driver.getTitle());

		log.info("End of method getWebDriver");
		webDriverPool.put(webDriver, driver);
		this.childWebDriver.set(driver);
		return driver;

	}

	public void goToSite(WebDriver driver) throws Exception {
		log.debug("Login URL {}", loginURL);
		driver.get(loginURL);
	}

	public void logBrowserConsoleErrors(WebDriver driver) {
		LogEntries logentries = driver.manage().logs().get(LogType.BROWSER);
		for (LogEntry logentrey : logentries) {
			log.error("===========================");
			log.error(logentrey);
			log.error("===========================");
		}
	}

	public WebDriver getChildWebDriver() {
		return this.childWebDriver.get();
	}

	public void launchWebSite(WebDriver driver) throws Exception {
		log.info("Starting of initSiteLogin method");
		
		driver.get(loginURL);
		
		log.info("Ending of initSiteLogin method");
	}

	public void fluentWaitForElement(WebDriver childDriver, String xPath) {

		try {

			// Reference : https://www.guru99.com/implicit-explicit-waits-selenium.html
			Wait<WebDriver> wait = new FluentWait<WebDriver>(childDriver).withTimeout(Duration.ofSeconds(3))
					.pollingEvery(Duration.ofSeconds(2)).ignoring(Exception.class);

			wait.until(new Function<WebDriver, WebElement>() {
				public WebElement apply(WebDriver driver) {
					return driver.findElement(By.xpath(xPath));
				}
			});

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public String covertDateFormatinYYYYMMDDByEnterDays(String date, int days) throws ParseException {
		SimpleDateFormat sourceDateFormat = new SimpleDateFormat("dd-MM-yyyy");

		Date parseDate = sourceDateFormat.parse(date);

		SimpleDateFormat targetDateFormat = new SimpleDateFormat("yyyy/MM/dd");
		
		Calendar cal = Calendar.getInstance();
	      cal.setTime(parseDate);
	      cal.add(Calendar.DATE, days);
	      parseDate = cal.getTime();
	      System.out.println("Date:::"+ targetDateFormat.format(parseDate));
	    
		return targetDateFormat.format(parseDate);
	}

	public List<String> getPropertyList(String name) {
		List<String> list = Arrays.asList(name.toString().split("\\,"));
		System.out.println(list);
		return list;
	}

	@BeforeMethod
	public void logBeforeEachTestMethod(Method testMethod) {
		log.info("Enter into {}", testMethod.getName());
	}

	@AfterMethod
	public void logAfterEachTestMethod(Method testMethod) {
		log.info("Exit from {}", testMethod.getName());
	}

}