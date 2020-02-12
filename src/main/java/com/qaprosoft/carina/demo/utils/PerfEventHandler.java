package com.qaprosoft.carina.demo.utils;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.events.EventFiringWebDriver;
import org.openqa.selenium.support.events.WebDriverEventListener;
import org.testng.ITestResult;
import org.testng.Reporter;

import com.qaprosoft.carina.core.foundation.commons.SpecialKeywords;
import com.qaprosoft.carina.core.foundation.utils.Configuration;
import com.qaprosoft.carina.core.foundation.utils.R;
import com.qaprosoft.carina.core.foundation.webdriver.Screenshot;
import com.qaprosoft.carina.core.foundation.webdriver.core.factory.impl.DesktopFactory;
import com.qaprosoft.carina.core.foundation.webdriver.core.factory.impl.MobileFactory;
import com.qaprosoft.zafira.client.ZafiraSingleton;
import com.qaprosoft.zafira.models.dto.TestArtifactType;

public class PerfEventHandler implements WebDriverEventListener {

	private static final Logger LOGGER = Logger.getLogger(PerfEventHandler.class);
	protected TestArtifactType vncArtifact;

	/*
	 * public PerfEventHandler(TestArtifactType vncArtifact) { this.vncArtifact
	 * = vncArtifact; }
	 */

	private final static ThreadLocal<String> currentPositiveMessage = new ThreadLocal<String>();
	private final static ThreadLocal<String> currentNegativeMessage = new ThreadLocal<String>();

	public void afterChangeValueOf(WebElement element, WebDriver driver) {
		String comment = "After change value";
		captureScreenshot(comment, driver, element, false);
	}

	public void afterClickOn(WebElement element, WebDriver driver) {
		String driverType = R.CONFIG.getProperties().getProperty("platformType").toUpperCase();

		switch (driverType) {
		

		case "MOBILE": {
		WebDriver drv = driver;
		 
		if (drv instanceof EventFiringWebDriver) {
		((EventFiringWebDriver) drv).getWrappedDriver();
		}
		PerformanceStats ptStats = new PerformanceStats(driver);
		long endTime = System.currentTimeMillis();
		ptStats.getResponseEndTime(endTime);
		ptStats.getPerformanceStats("PGID_" + R.CONFIG.getProperties().getProperty("class_name"));
		String comment = "Element clicked";
		captureScreenshot(comment, driver, element, false);
		break;
		}
		
		case"WEB": {
			PerformanceStats ptStats = new PerformanceStats(driver);
			long endTime = System.currentTimeMillis();
			ptStats.getResponseEndTime(endTime);
			ptStats.getPerformanceStats("PGID_" + driver.getTitle());
			String comment = "Element clicked";
			captureScreenshot(comment, driver, element, false);
			break;
		}
		}

	}

	public void afterFindBy(By arg0, WebElement arg1, WebDriver arg2) {

	}

	public void afterNavigateBack(WebDriver driver) {
		onAfterAction("Navigated back", driver);
	}

	public void afterNavigateForward(WebDriver driver) {
		onAfterAction("Navigated forward", driver);
	}

	public void afterNavigateTo(String url, WebDriver driver) {
		String comment = String.format("URL '%s' opened", url);
		onAfterAction(comment, driver);
	}

	public void afterScript(String arg0, WebDriver arg1) {

	}

	@Override
	public void afterAlertAccept(WebDriver driver) {
		onAfterAction("Alert accepted", driver);
	}

	@Override
	public void afterAlertDismiss(WebDriver driver) {
		onAfterAction("Alert dismissed", driver);
	}

	@Override
	public void afterNavigateRefresh(WebDriver driver) {
		onAfterAction("Page refreshed", driver);
	}

	@Override
	public void afterChangeValueOf(WebElement element, WebDriver driver, CharSequence[] keysToSend) {
		String comment = String.format("Text '%s' typed", charArrayToString(keysToSend));
		captureScreenshot(comment, driver, element, false);
	}

	@Override
	public void afterGetText(WebElement element, WebDriver driver, String text) {
	}

	public void beforeChangeValueOf(WebElement arg0, WebDriver arg1) {
		onBeforeAction();
	}

	public void beforeClickOn(WebElement arg0, WebDriver arg1) {
		onBeforeAction();
		PerformanceStats ptStats = new PerformanceStats(arg1);
		long beforeStartTime = System.currentTimeMillis();
		ptStats.getResponseStartTime(beforeStartTime);
	}

	public void beforeFindBy(By arg0, WebElement arg1, WebDriver arg2) {
		onBeforeAction();
	}

	public void beforeNavigateBack(WebDriver arg0) {
		onBeforeAction();
	}

	public void beforeNavigateForward(WebDriver arg0) {
		onBeforeAction();
	}

	public void beforeNavigateTo(String arg0, WebDriver arg1) {
		onBeforeAction();
	}

	public void beforeScript(String arg0, WebDriver arg1) {
		onBeforeAction();
	}

	public void onException(Throwable thr, WebDriver driver) {
		LOGGER.debug("DriverListener->onException starting...");
		driver = castDriver(driver);
		// [VD] make below code as much safety as possible otherwise potential
		// recursive failure could occur with driver related issue.
		// most suspicious are capture screenshots, generating dumps etc
		if (thr.getMessage() == null) {
			return;
		}

		// TODO: hopefully castDriver at the beginning resolve root cause of the
		// recursive onException calls
		if (thr.getStackTrace().toString()
				.contains("com.qaprosoft.carina.core.foundation.webdriver.listener.DriverListener.onException")
				|| thr.getStackTrace().toString()
						.contains("Unable to capture screenshot due to the WebDriverException")) {
			LOGGER.error("Do not generate screenshot for invalid driver!");
			// prevent recursive crash for onException
			return;
		}

		if (thr.getMessage().contains("Method has not yet been implemented")
				|| thr.getMessage().contains("Method is not implemented")) {
			// do nothing
			return;
		}

		// handle use-case when application crashed on iOS but tests continue to
		// execute something because doesn't raise valid exception
		// Example:

		// 10:25:20 2018-09-14 10:29:39 DriverListener [TestNG-31] [ERROR]
		// [iPhone_6s] An unknown server-side error occurred while
		// processing the command. Original error: The application under
		// test with bundle id 'Q5AWL8WCY6' is not running,
		// possibly crashed (WARNING: The server did not provide any
		// stacktrace information)

		// TODO: investigate if we run @AfterMethod etc system events after this
		// crash
		if (thr.getMessage().contains("is not running, possibly crashed")) {
			throw new RuntimeException(thr);
		}

		String urlPrefix = "";
		try {
			// [VD] commented as too many issues observed due to this feature
			// if (!isMobile(driver)) {
			// urlPrefix = "url: " + driver.getCurrentUrl() + "\n";
			// }
			// 1. if you see mess with afterTest carina actions and Timer
			// startup failure you should follow steps #2+ to determine root
			// cause.
			// Driver initialization 'default' FAILED! Retry 1 of 1 time -
			// Operation already started: mobile_driverdefault
			// 2. carefully track all preliminary exception for the same thread
			// to detect 1st problematic exception
			// 3. 99% those root exception means that we should prohibit
			// screenshot generation for such use-case
			// 4. if 3rd one is true just update Screenshot.isCaptured() adding
			// part of the exception to the list
			// handle cases which should't be captured
			if (Screenshot.isCaptured(thr.getMessage())) {
				captureScreenshot(urlPrefix + thr.getMessage(), driver, null, true);
			}
		} catch (Exception e) {
			if (!e.getMessage().isEmpty() && (e.getMessage().contains("Method has not yet been implemented")
					|| (e.getMessage().contains("Method is not implemented")))) {
				LOGGER.debug("Unrecognized exception detected in DriverListener->onException! " + e.getMessage(), e);
			} else {
				LOGGER.error("Unrecognized exception detected in DriverListener->onException! " + e.getMessage(), e);
			}
		} catch (Throwable e) {
			LOGGER.error(
					"Take a look to the logs above for current thread and add exception into the exclusion for Screenshot.isCaptured(). "
							+ e.getMessage(),
					e);
		}

		LOGGER.debug("DriverListener->onException finished.");
	}

	@Override
	public void beforeAlertAccept(WebDriver driver) {
		onBeforeAction();
	}

	private void onBeforeAction() {
		// 4a. if "tzid" not exist inside vncArtifact and exists in Reporter ->
		// register new vncArtifact in Zafira.
		// 4b. if "tzid" already exists in current artifact but in Reporter
		// there is another value. Then this is use case for class/suite mode
		// when we share the same
		// driver across different tests

		/*ITestResult res = Reporter.getCurrentTestResult();
		if (res != null && res.getAttribute("ztid") != null) {
			Long ztid = (Long) res.getAttribute("ztid");
			if (ztid != vncArtifact.getTestId() && vncArtifact != null && !StringUtils.isBlank(vncArtifact.getName())) {
				vncArtifact.setTestId(ztid);
				LOGGER.debug("Registered live video artifact " + vncArtifact.getName() + " into zafira");
				ZafiraSingleton.INSTANCE.getClient().addTestArtifact(vncArtifact);
			}

		}*/
	}

	@Override
	public void beforeAlertDismiss(WebDriver driver) {
		onBeforeAction();
	}

	@Override
	public void beforeNavigateRefresh(WebDriver driver) {
		onBeforeAction();
	}

	@Override
	public void beforeChangeValueOf(WebElement element, WebDriver driver, CharSequence[] keysToSend) {
		onBeforeAction();
	}

	private String charArrayToString(CharSequence[] csa) {
		String s = StringUtils.EMPTY;
		if (csa != null) {
			StringBuilder sb = new StringBuilder();
			for (CharSequence cs : csa) {
				sb.append(String.valueOf(cs));
			}
			s = sb.toString();
		}
		return s;
	}

	@Override
	public void beforeSwitchToWindow(String windowName, WebDriver driver) {
		onBeforeAction();
	}

	@Override
	public void afterSwitchToWindow(String windowName, WebDriver driver) {
	}

	@Override
	public <X> void beforeGetScreenshotAs(OutputType<X> target) {
		onBeforeAction();
	}

	@Override
	public <X> void afterGetScreenshotAs(OutputType<X> target, X screenshot) {

	}

	@Override
	public void beforeGetText(WebElement element, WebDriver driver) {
	}

	private void captureScreenshot(String comment, WebDriver driver, WebElement element, boolean errorMessage) {
		driver = castDriver(driver);
		if (getMessage(errorMessage) != null) {
			comment = getMessage(errorMessage);
		}

		LOGGER.debug("DriverListener->captureScreenshot starting...");
		try {
			if (errorMessage) {
				LOGGER.error(comment);
				Screenshot.captureFailure(driver, comment); // in case of
															// failure
			} else {
				LOGGER.info(comment);
				Screenshot.capture(driver, comment);
			}
		} catch (Exception e) {
			LOGGER.debug("Unrecognized failure detected in DriverListener->captureScreenshot: " + e.getMessage(), e);
		} finally {
			resetMessages();
		}
		LOGGER.debug("DriverListener->captureScreenshot finished...");
	}

	private WebDriver castDriver(WebDriver drv) {
		if (drv instanceof EventFiringWebDriver) {
			drv = ((EventFiringWebDriver) drv).getWrappedDriver();
		}
		return drv;
	}

	public static String getMessage(boolean errorMessage) {
		if (errorMessage) {
			return currentNegativeMessage.get();
		} else {
			return currentPositiveMessage.get();
		}
	}

	public static void setMessages(String positiveMessage, String negativeMessage) {
		currentPositiveMessage.set(positiveMessage);
		currentNegativeMessage.set(negativeMessage);
	}

	private void resetMessages() {
		currentPositiveMessage.remove();
		currentNegativeMessage.remove();
	}

	private void onAfterAction(String comment, WebDriver driver) {
		captureScreenshot(comment, driver, null, false);
	}
}