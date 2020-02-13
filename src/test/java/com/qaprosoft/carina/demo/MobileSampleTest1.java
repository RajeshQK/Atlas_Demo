package com.qaprosoft.carina.demo;

import org.apache.commons.lang3.RandomStringUtils;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.Assert;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.qaprosoft.carina.core.foundation.AbstractTest;
import com.qaprosoft.carina.core.foundation.utils.R;
import com.qaprosoft.carina.core.foundation.utils.ownership.MethodOwner;
import com.qaprosoft.carina.demo.mobile.gui.pages.common.CarinaDescriptionPageBase;
import com.qaprosoft.carina.demo.mobile.gui.pages.common.LoginPageBase;
import com.qaprosoft.carina.demo.mobile.gui.pages.common.WelcomePageBase;
import com.qaprosoft.carina.demo.utils.M1CloudActivities;

public class MobileSampleTest1 extends AbstractTest {

	public DesiredCapabilities capabilities = new DesiredCapabilities();


	@Test(testName = "testLoginUser")
	@MethodOwner(owner = "admin")
	@Parameters(value = { "deviceName" })
	public void testLoginUser(String deviceName) throws Exception {
		M1CloudActivities mobile = new M1CloudActivities();

		capabilities = mobile.setCapabilities(deviceName);
		String username = "Test user";
		String password = RandomStringUtils.randomAlphabetic(10);
		WelcomePageBase welcomePage = initPage(getDriver("default", capabilities, R.CONFIG.get("selenium_host")),
				WelcomePageBase.class);
		Assert.assertTrue(welcomePage.isPageOpened(), "Welcome page isn't opened");
		LoginPageBase loginPage = welcomePage.clickNextBtn();
		Assert.assertFalse(loginPage.isLoginBtnActive(), "Login button is active when it should be disabled");
		loginPage.typeName(username);
		loginPage.typePassword(password);
		loginPage.selectMaleSex();
		loginPage.checkPrivacyPolicyCheckbox();
		CarinaDescriptionPageBase carinaDescriptionPage = loginPage.clickLoginBtn();
		Assert.assertTrue(carinaDescriptionPage.isPageOpened(), "Carina description page isn't opened");
	}


}
