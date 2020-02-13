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
import com.qaprosoft.carina.demo.mobile.gui.pages.common.ContactUsPageBase;
import com.qaprosoft.carina.demo.mobile.gui.pages.common.LoginPageBase;
import com.qaprosoft.carina.demo.mobile.gui.pages.common.UIElementsPageBase;
import com.qaprosoft.carina.demo.mobile.gui.pages.common.WebViewPageBase;
import com.qaprosoft.carina.demo.mobile.gui.pages.common.WelcomePageBase;
import com.qaprosoft.carina.demo.utils.M1CloudActivities;
import com.qaprosoft.carina.demo.utils.MobileContextUtils;
import com.qaprosoft.carina.demo.utils.MobileContextUtils.View;

public class MobileSampleTest2 extends AbstractTest {

	public DesiredCapabilities capabilities = new DesiredCapabilities();



	@Test(testName = "testWebView")
	@MethodOwner(owner = "admin")
	@Parameters(value = { "deviceName" })
	public void testWebView(String deviceName) throws Exception {
		M1CloudActivities mobile = new M1CloudActivities();

		capabilities = mobile.setCapabilities(deviceName);
		WelcomePageBase welcomePage = initPage(getDriver("default", capabilities, R.CONFIG.get("selenium_host")),
				WelcomePageBase.class);
		LoginPageBase loginPage = welcomePage.clickNextBtn();
		loginPage.login();
		WebViewPageBase webViewPageBase = initPage(getDriver(), WebViewPageBase.class);
		MobileContextUtils contextHelper = new MobileContextUtils();
		contextHelper.switchMobileContext(View.WEB);
		ContactUsPageBase contactUsPage = webViewPageBase.goToContactUsPage();
		contactUsPage.typeName("John Doe");
		contactUsPage.typeEmail("some@email.com");
		contactUsPage.typeQuestion("This is a message");
		contactUsPage.submit();
		Assert.assertTrue(contactUsPage.isSuccessMessagePresent() || contactUsPage.isRecaptchaPresent(),
				"message was not sent or captcha was not displayed");
	}


}
