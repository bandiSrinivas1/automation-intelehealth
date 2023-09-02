package com.b2b.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.TestListenerAdapter;

import com.b2b.test.base.B2BBaseAutomationTest;

import io.qameta.allure.Allure;
import io.qameta.allure.Attachment;

public class B2BBaseTestListener extends TestListenerAdapter {
	
	private static final Logger log = LogManager.getLogger(B2BBaseTestListener.class);
	private static String getTestMethodName(ITestResult iTestResult) {
		return iTestResult.getMethod().getConstructorOrMethod().getName();
	}

	public void saveScreenshotPNG(String screenshotType, WebDriver driver) {
		
		byte[] screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
		Allure.getLifecycle().addAttachment(
				screenshotType + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MMM-yy_hh:mm:ss")),
				"image/png", "png", screenshot);
	}

	// Text attachments for Allure
	@Attachment(value = "{0}", type = "text/plain")
	public static String saveTextLog(String message) {
		return message;
	}

	// HTML attachments for Allure
	@Attachment(value = "{0}", type = "text/html")
	public static String attachHtml(String html) {
		return html;
	}

	@Override
	public void onStart(ITestContext iTestContext) {
		log.info("In onStart method {}", iTestContext.getName());
	}

	@Override
	public void onTestStart(ITestResult iTestResult) {
		log.info(getTestMethodName(iTestResult) + " test is starting.");
	}

	@Override
	public void onTestSuccess(ITestResult iTestResult) {
		log.info(getTestMethodName(iTestResult) + " test is succeed.");
	}

	@Override
	public void onTestFailure(ITestResult iTestResult) {
		log.info(getTestMethodName(iTestResult) + " test is failed.");

		Object testClass = iTestResult.getInstance();
		WebDriver driver = ((B2BBaseAutomationTest) testClass).getChildWebDriver();
		
		if (driver != null) {
			log.info("Screenshot captured for test case: {}",  getTestMethodName(iTestResult));
			saveScreenshotPNG("Test_Failure_Screenshot_", driver);
		}
		
	}
	
	@Override
	public void onTestSkipped(ITestResult iTestResult) {
		log.info(getTestMethodName(iTestResult) + " test is skipped.");
	}

	@Override
	public void onTestFailedButWithinSuccessPercentage(ITestResult iTestResult) {
		log.info("Test failed but it is in defined success ratio {}",  getTestMethodName(iTestResult));
	}
	
	public void onTestFailedWithTimeout(ITestResult iTestResult) {
		log.info(getTestMethodName(iTestResult)+ " test is failed with Timeout.");
		onTestFailure(iTestResult);
	}

}
