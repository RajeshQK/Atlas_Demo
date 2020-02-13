package com.qaprosoft.carina.demo.gui.pages;

import org.apache.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

import com.qaprosoft.carina.core.foundation.webdriver.decorator.ExtendedWebElement;
import com.qaprosoft.carina.core.gui.AbstractPage;

public class EasyTravelBookingPage extends AbstractPage{
	
	
	 Logger LOGGER = Logger.getLogger(EasyTravelBookingPage.class);
	 
	    @FindBy(xpath="//input[@id='iceform:destination']")
	    private ExtendedWebElement tripdest;
	    
	    @FindBy(xpath="//input[@class='iceSelInpDateOpenPopup'][1]")
	    private ExtendedWebElement frmdatemenu;
	    
	    @FindBy(xpath="//span[@id='iceform:fromDate_text_1601']")
	    private ExtendedWebElement frmdate;
	    
	    
	    @FindBy(xpath="//input[@id='iceform:toDate_cb']")
	    private ExtendedWebElement todatemenu;
	    
	    @FindBy(xpath="//span[@id='iceform:toDate_text_1629']")
	    private ExtendedWebElement todate;
	    
	    @FindBy(xpath="//input[@id='iceform:search']")
	    private ExtendedWebElement searchicon;
	    
	    @FindBy(xpath="//*[@id='recommendation']/div/div[2]/div[2]/a")
	    private ExtendedWebElement booknow;

	    @FindBy(xpath="//a[@id='iceform:bookReviewNext']")
	    private ExtendedWebElement transactionbtn;

    public EasyTravelBookingPage(WebDriver driver) {
		super(driver);
	}
	
	public void enterJourneydetails(String dest) {
		tripdest.type(dest);
		todatemenu.click();
		frmdatemenu.click();
		frmdate.click();
		todatemenu.click();
		todate.click();
		searchicon.click();
	}
	
	public void booknow() {
		booknow.click();
	}
	
	
	
	public void hotelConfirmation() {
		transactionbtn.click();
	}
	
}
