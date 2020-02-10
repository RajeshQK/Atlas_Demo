package com.qaprosoft.carina.demo.utils;

import org.json.JSONArray;
import org.json.JSONObject;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;

import com.qaprosoft.carina.core.foundation.utils.Configuration;
import com.qaprosoft.carina.core.foundation.utils.mobile.IMobileUtils;

public class Get_PerformanceStats implements IMobileUtils {

	private WebDriver driver;
	private JavascriptExecutor javaScript;
	public JSONArray json_array_resources = new JSONArray();

	public Get_PerformanceStats(WebDriver driver) {
		this.driver = driver;
		javaScript = (JavascriptExecutor) driver;
	}

	public JSONArray resource_data() {
		Object resource = null;
		String driverType = Configuration.getDriverType().toUpperCase();
		switch(driverType){
		case"DESKTOP":{
			resource = javaScript.executeScript("return JSON.stringify(window.performance.getEntries())",
					new Object[0]);
			JSONArray json_array = new JSONArray(resource.toString());
			
			System.out.println(json_array.length());
			for (Object object : json_array) {
				try {
					JSONObject json_object = new JSONObject();
					JSONObject temp = (JSONObject) object;
					json_object.put("url", temp.getString("name"));
					json_object.put("start_time", temp.getDouble("startTime"));
					json_object.put("encoded_body_size", temp.getDouble("encodedBodySize"));
					json_object.put("decoded_body_size", temp.getDouble("decodedBodySize"));
					json_object.put("transfer_size", temp.getDouble("transferSize"));
					json_object.put("dns", temp.getDouble("domainLookupEnd") - temp.getDouble("domainLookupStart"));
					json_object.put("redirect", temp.getDouble("redirectEnd") - temp.getDouble("redirectStart"));
					json_object.put("connect", temp.getDouble("connectEnd") - temp.getDouble("connectStart"));
					if (temp.getDouble("secureConnectionStart") == 0.0D) {
						json_object.put("ssl", 0);
					} else {
						json_object.put("ssl", temp.getDouble("connectEnd") - temp.getDouble("secureConnectionStart"));
					}

					json_object.put("ttfb", temp.getDouble("responseStart") - temp.getDouble("requestStart"));
					json_object.put("response", temp.getDouble("responseEnd") - temp.getDouble("responseStart"));
					if (temp.has("domInteractive")) {
						json_object.put("dom_interactive",
								temp.getDouble("domInteractive") - temp.getDouble("responseEnd"));
					} else {
						json_object.put("dom_interactive", 0);
					}
					if (temp.has("domComplete")) {
						json_object.put("dom_loaded", temp.getDouble("domComplete") - temp.getDouble("domInteractive"));
					} else {
						json_object.put("dom_loaded", 0);
					}
					if (temp.has("loadEventEnd")) {
						json_object.put("page_load", temp.getDouble("loadEventEnd") - temp.getDouble("startTime"));
					} else {
						json_object.put("page_load", temp.getDouble("responseEnd") - temp.getDouble("startTime"));
					}
					json_array_resources.put(json_object);
				} catch (Exception localException) {
				}
			}
			javaScript.executeScript("return window.performance.clearResourceTimings()", new Object[0]);
		}
		case"MOBILE":{
		
			resource = "Mobile performance stats";
			/*AppiumDriver<?> driver = (AppiumDriver<?>) castDriver();
			
			WebDriver drv = driver;
			if (drv instanceof EventFiringWebDriver) {
			drv = ((EventFiringWebDriver) drv).getWrappedDriver();
			resource = javaScript.executeScript("return JSON.stringify(window.performance.getEntries())",
					new Object[0]);
			}
			AndroidDriver<?> aDriver = (AndroidDriver<?>) drv;
			performanceData = ((HasSupportedPerformanceDataType) driver).getPerformanceData("com.tatamotors.egurucrm", "cpuinfo", 5);
			resource = performanceData;*/
		}
		}
		
		
		
		return json_array_resources;
	}
}
