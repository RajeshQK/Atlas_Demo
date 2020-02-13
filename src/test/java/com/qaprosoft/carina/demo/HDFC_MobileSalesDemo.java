package com.qaprosoft.carina.demo;

import com.qaprosoft.carina.core.foundation.AbstractTest;
import com.qaprosoft.carina.core.foundation.utils.Configuration;
import com.qaprosoft.carina.core.foundation.utils.R;
import com.qaprosoft.carina.core.foundation.utils.factory.DeviceType.Type;
import com.qaprosoft.carina.core.foundation.utils.ownership.MethodOwner;
import com.qaprosoft.carina.demo.mobile.gui.pages.common.MobileSalesLoginPageBase;
import com.qaprosoft.carina.demo.utils.M1CloudActivities;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;
import java.util.Random;

import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

public class HDFC_MobileSalesDemo extends AbstractTest {

	public DesiredCapabilities capabilities = new DesiredCapabilities();

	public void setCustCaps(String deviceName)
			throws Exception {/*
								 * //String propFile =
								 * propertiesFile(jenkinsJobEnvironment);
								 * 
								 * String path =
								 * "src/main/resources/m1Cloud/android/"+
								 * jenkinsJobEnvironment; Properties prop = new
								 * Properties(); InputStream input = new
								 * FileInputStream(path); prop.load(input);
								 * 
								 * DesiredCapabilities capabilities = new
								 * DesiredCapabilities();
								 * capabilities.setCapability(
								 * "Capability_Username",prop.getProperty(
								 * "Capability_Username"));
								 * capabilities.setCapability(
								 * "Capability_ApiKey",prop.getProperty(
								 * "Capability_ApiKey"));
								 * capabilities.setCapability(
								 * "Capability_ApplicationName",
								 * prop.getProperty("Capability_ApplicationName"
								 * )); capabilities.setCapability(
								 * "Capability_DurationInMinutes",
								 * prop.getProperty(
								 * "Capability_DurationInMinutes"));
								 * capabilities.setCapability(
								 * "Capability_DeviceFullName",prop.getProperty(
								 * "Capability_DeviceFullName"));
								 * capabilities.setCapability("deviceType",prop.
								 * getProperty("deviceType"));
								 * capabilities.setCapability("platformName",
								 * prop.getProperty("platformName"));
								 * capabilities.setCapability("automationName",
								 * prop.getProperty("automationName"));
								 * capabilities.setCapability("appActivity",prop
								 * .getProperty("appActivity"));
								 * capabilities.setCapability("appPackage",prop.
								 * getProperty("appPackage"));
								 * capabilities.setCapability(
								 * "autoGrantPermissions", true);
								 * capabilities.setCapability("systemPort", new
								 * Random().nextInt(100)+1024);
								 * R.CONFIG.getProperties().setProperty(
								 * "deviceName",
								 * prop.getProperty("Capability_DeviceFullName")
								 * );
								 * 
								 * getDriver("default", capabilities,
								 * R.CONFIG.get("selenium_host"));
								 */

		M1CloudActivities mobile = new M1CloudActivities();

		capabilities = mobile.setCapabilities(deviceName);
	}

	/*
	 * public String propertiesFile(String jenkinsJobEnvironment){
	 * System.out.println(System.getProperty("stageName")); String propName =
	 * null;
	 * 
	 * if(jenkinsJobEnvironment.equals("DEV")){ propName =
	 * "Samsung_Galaxy_J5_Prime.properties"; } else
	 * if(jenkinsJobEnvironment.equals("QA")){ propName =
	 * "Samsung_Galaxy_J7_Prime.properties"; } else{ propName =
	 * "OnePlus7.properties"; } return propName; }
	 */

	@Test(testName = "testMobileSalesDiary")
	@MethodOwner(owner = "admin")
	@Parameters(value = { "deviceName" })
	public void testMobileSalesDiary(String deviceName) throws Exception {
		// setCustCaps(deviceName);
		M1CloudActivities mobile = new M1CloudActivities();

		capabilities = mobile.setCapabilities(deviceName);
		MobileSalesLoginPageBase mobileD = initPage(getDriver("default", capabilities, R.CONFIG.get("selenium_host")),
				MobileSalesLoginPageBase.class);
		mobileD.login("12306", "Hdfc@123");
		mobileD.homePage();
		mobileD.logout();
	}

}
