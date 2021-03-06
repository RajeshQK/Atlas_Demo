package com.qaprosoft.carina.demo;

import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.BeforeTest;

import com.qaprosoft.carina.core.foundation.cucumber.CucumberBaseTest;
import com.qaprosoft.carina.core.foundation.utils.R;
import com.qaprosoft.carina.demo.utils.M1CloudActivities;

import cucumber.api.CucumberOptions;

@CucumberOptions(features = "src/test/resources/features/Qtrac.feature", glue = "com.qaprosoft.carina.demo.cucumber.steps", format = {
		"pretty", "html:target/cucumber-core-test-report", "pretty:target/cucumber-core-test-report.txt",
		"json:target/cucumber-core-test-report.json", "junit:target/cucumber-core-test-report.xml" })

public class QtracCucumberTest2 extends CucumberBaseTest {
	// @Parameters({"DeviceName"})
	@BeforeTest
	public void setCustCaps() throws Exception {
		M1CloudActivities mobile = new M1CloudActivities();
		DesiredCapabilities capabilities = new DesiredCapabilities();
		capabilities = mobile.setCapabilities("Samsung_Galaxy_J4_Plus.properties");
		R.CONFIG.getProperties().put("capabilities",capabilities);
		getDriver("DEFAULT", capabilities, R.CONFIG.get("selenium_host"));
	}
}
