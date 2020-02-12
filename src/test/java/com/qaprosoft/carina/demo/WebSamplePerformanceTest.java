package com.qaprosoft.carina.demo;

import java.lang.reflect.Method;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;


import com.qaprosoft.carina.core.foundation.utils.tag.Priority;
import com.qaprosoft.carina.core.foundation.utils.tag.TestPriority;
import com.qaprosoft.carina.core.foundation.utils.tag.TestTag;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.qaprosoft.carina.core.foundation.AbstractTest;
import com.qaprosoft.carina.core.foundation.dataprovider.annotations.XlsDataSourceParameters;
import com.qaprosoft.carina.core.foundation.utils.R;
import com.qaprosoft.carina.core.foundation.utils.ownership.MethodOwner;
import com.qaprosoft.carina.demo.gui.components.FooterMenu;
import com.qaprosoft.carina.demo.gui.components.NewsItem;
import com.qaprosoft.carina.demo.gui.components.compare.ModelSpecs;
import com.qaprosoft.carina.demo.gui.pages.BrandModelsPage;
import com.qaprosoft.carina.demo.gui.pages.CompareModelsPage;
import com.qaprosoft.carina.demo.gui.pages.HomePage;
import com.qaprosoft.carina.demo.gui.pages.ModelInfoPage;
import com.qaprosoft.carina.demo.gui.pages.NewsPage;
import com.qaprosoft.carina.demo.utils.TestExecutionDetails;

public class WebSamplePerformanceTest extends AbstractTest {

	TestExecutionDetails testDetails = new TestExecutionDetails();
	Logger LOGGER = Logger.getLogger(WebSamplePerformanceTest.class);
	//@BeforeMethod
	public void setUp(String testName) {

		LoggingPreferences logPrefs = new LoggingPreferences();
		logPrefs.enable(LogType.PERFORMANCE, Level.ALL);

		Map<String, Object> perfLogPrefs = new HashMap<String, Object>();
		perfLogPrefs.put("traceCategories", "browser,devtools.timeline,devtools"); // comma-separated
																					// trace
																					// categories

		Map<String, Object> chromeOptions = new HashMap<String, Object>();
		chromeOptions.put("perfLoggingPrefs", perfLogPrefs);
		

		DesiredCapabilities cap = DesiredCapabilities.chrome();
		cap.setCapability("goog:loggingPrefs", logPrefs);
		cap.setCapability(ChromeOptions.CAPABILITY, chromeOptions);
		
		System.out.println(cap);

		getDriver("default", cap, R.CONFIG.get("selenium_host"));

	}
	
	@BeforeMethod (alwaysRun = true)
	public void beforeMethod(ITestContext ctx,Method method) {
	    try {
	        String suiteName = ctx.getCurrentXmlTest().getSuite().getName();
	        testDetails.setSuiteName(suiteName);
	        String testMethodName = method.getName();
	        testDetails.setTestName(testMethodName);
	        LOGGER.info("Suite Name: "+suiteName);
	        LOGGER.info("Test Name: "+testMethodName);
	        
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
	
	
	@Test(dataProvider = "SingleDataProvider", description = "JIRA#AUTO-0008")
	@MethodOwner(owner = "qpsdemo")
	@TestPriority(Priority.P1)
	@TestTag(name = "area test", value = "data provider")
	@TestTag(name = "specialization", value = "xlsx")
	@XlsDataSourceParameters(path = "xls/demo.xlsx", sheet = "GSMArena", dsUid = "TUID")

	public void testModelSpecs(HashMap<String, String> testDetails) {
		// Open GSM Arena home page and verify page is opened
		setUp("testModelSpecs");
		System.out.println(testDetails);

		String brand = testDetails.get("brand");
		String model = testDetails.get("model");
		String display = testDetails.get("display");
		String camera = testDetails.get("camera");
		String ram = testDetails.get("ram");
		String battery = testDetails.get("battery");

		HomePage homePage = new HomePage(getDriver());
		homePage.open();
		BrandModelsPage productsPage = homePage.selectBrand(brand);
		// Select phone model
		ModelInfoPage productInfoPage = productsPage.selectModel(model);
	}

	@Test(description = "JIRA#AUTO-0009")
	@MethodOwner(owner = "qpsdemo")
	@TestPriority(Priority.P1)
	@TestTag(name = "area test", value = "web")
	public void testCompareModels() {
		// Open GSM Arena home page and verify page is opened
		setUp("testCompareModels");
		HomePage homePage = new HomePage(getDriver());
		homePage.open();
		Assert.assertTrue(homePage.isPageOpened(), "Home page is not opened");
		// Open model compare page
		FooterMenu footerMenu = homePage.getFooterMenu();
		Assert.assertTrue(footerMenu.isUIObjectPresent(2), "Footer menu wasn't found!");
		CompareModelsPage comparePage = footerMenu.openComparePage();
		// Compare 3 models
		List<ModelSpecs> specs = comparePage.compareModels("Samsung Galaxy J3", "Samsung Galaxy J5",
				"Samsung Galaxy J7 Pro");
	}

	@Test(description = "JIRA#AUTO-0010")
	@MethodOwner(owner = "qpsdemo")
	public void testNewsSearch() {
		setUp("testNewsSearch");
		HomePage homePage = new HomePage(getDriver());
		homePage.open();
		Assert.assertTrue(homePage.isPageOpened(), "Home page is not opened!");

		NewsPage newsPage = homePage.getFooterMenu().openNewsPage();
		Assert.assertTrue(newsPage.isPageOpened(), "News page is not opened!");

		final String searchQ = "iphone";
		List<NewsItem> news = newsPage.searchNews(searchQ);
		Assert.assertFalse(CollectionUtils.isEmpty(news), "News not found!");
		for (NewsItem n : news) {
			System.out.println(n.readTitle());
		}
	}

}
