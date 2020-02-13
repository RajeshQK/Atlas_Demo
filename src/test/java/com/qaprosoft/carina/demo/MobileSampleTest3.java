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

public class MobileSampleTest3 extends AbstractTest {

	public DesiredCapabilities capabilities = new DesiredCapabilities();


	@Test(testName = "testUIElements")
	@MethodOwner(owner = "admin")
	@Parameters(value = { "deviceName" })
	public void testUIElements(String deviceName) throws Exception {
		M1CloudActivities mobile = new M1CloudActivities();

		capabilities = mobile.setCapabilities(deviceName);
		WelcomePageBase welcomePage = initPage(getDriver("default", capabilities, R.CONFIG.get("selenium_host")),
				WelcomePageBase.class);
		LoginPageBase loginPage = welcomePage.clickNextBtn();
		CarinaDescriptionPageBase carinaDescriptionPage = loginPage.login();
		UIElementsPageBase uiElements = carinaDescriptionPage.navigateToUIElementsPage();
		final String text = "some text";
		final String date = "22/10/2018";
		final String email = "some@email.com";
		uiElements.typeText(text);
		Assert.assertEquals(uiElements.getText(), text, "Text was not typed");
		uiElements.typeDate(date);
		Assert.assertEquals(uiElements.getDate(), date, "Date was not typed");
		uiElements.typeEmail(email);
		Assert.assertEquals(uiElements.getEmail(), email, "Email was not typed");
		uiElements.swipeToFemaleRadioButton();
		uiElements.checkCopy();
		Assert.assertTrue(uiElements.isCopyChecked(), "Copy checkbox was not checked");
		uiElements.clickOnFemaleRadioButton();
		Assert.assertTrue(uiElements.isFemaleRadioButtonSelected(), "Female radio button was not selected!");
		uiElements.clickOnOtherRadioButton();
		Assert.assertTrue(uiElements.isOthersRadioButtonSelected(), "Others radio button was not selected!");
	}

}
