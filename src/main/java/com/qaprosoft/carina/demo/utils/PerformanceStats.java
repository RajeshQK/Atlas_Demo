package com.qaprosoft.carina.demo.utils;

import java.util.Date;

import org.apache.log4j.Logger;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.json.JSONArray;
import org.json.JSONObject;
import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.response.Response;
import com.jayway.restassured.specification.RequestSpecification;
import com.qaprosoft.carina.core.gui.AbstractPage;
import com.qaprosoft.zafira.listener.ZafiraEventRegistrar;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.text.SimpleDateFormat;

public class PerformanceStats extends AbstractPage {

	Logger LOGGER = Logger.getLogger(PerformanceStats.class);

	public static String index_name;

	public PerformanceStats(WebDriver driver) {
		super(driver);
	}

	TestExecutionDetails testDetails;

	public long startTime, endTime;

	public void getPerformanceStats(String pageName) {

		WebDriver driver = getDriver();
		Get_PerformanceStats getPerfStats = new Get_PerformanceStats(driver);
		JSONArray PerfStats = getPerfStats.resource_data();
		try {
			System.out.println("******Performance Stats: " + addDetailsJSON(PerfStats, pageName));
		} catch (Exception e) {
			System.out.println("******Performance Stats Exception :-" + e.getMessage());
		}
	}

	public JSONObject addDetailsJSON(JSONArray perfStats, String PageName) throws Exception {
		JSONObject pageName = new JSONObject();
		index_name = createIndex_Name();
		createIndex();
		try {
			int ZafiraRunID = (int) ZafiraEventRegistrar.getTestRun().get().getId();
			//int ZafiraRunID = 123654;
			LOGGER.info("ZafiraRunID: " + ZafiraRunID);
			String SuiteName = TestExecutionDetails.getSuiteName();
			LOGGER.info("SuiteName: " + SuiteName);
			String TestCaseID = TestExecutionDetails.getTestName();
			LOGGER.info("TestCaseID: " + TestCaseID);
			pageName.put("performance details", perfStats);

			pushtoElastic(ZafiraRunID, SuiteName, TestCaseID, PageName, perfStats.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}

		return pageName;
	}

	@SuppressWarnings("resource")
	public void pushtoElastic(int ZafiraRunID, String SuiteName, String TestCaseID, String PageName, String pagestat)
			throws IOException {

		/*
		 * TransportClient client = client = new
		 * PreBuiltTransportClient(Settings.EMPTY) .addTransportAddress(new
		 * TransportAddress(InetAddress.getByName("192.168.209.129"), 9300));
		 */
		
		  /*TransportClient client = client = new
		  PreBuiltTransportClient(Settings.EMPTY) .addTransportAddress(new
		  TransportAddress(InetAddress.getByName("192.168.2.17"), 9300));*/
		 
		TransportClient client = client = new PreBuiltTransportClient(Settings.EMPTY)
				.addTransportAddress(new TransportAddress(InetAddress.getByName("atlas.corp.qualitykiosk.com"), 9200));

		BulkRequestBuilder bulkRequest = client.prepareBulk();

		bulkRequest.add(client.prepareIndex(index_name, "doc")
	//	bulkRequest.add(client.prepareIndex("early_pt", "_doc")
				.setSource(XContentFactory.jsonBuilder().startObject().field("RunID", ZafiraRunID) //
						.field("SuiteName", SuiteName).field("TestCaseID", TestCaseID).field("PageName", PageName)
						.field("PageStat", pagestat) // Mention
						.field("PageResponseTime", calculateResponseTime()) // Pagestat
						.endObject()));

		BulkResponse bulkResponse = bulkRequest.get();
		if (bulkResponse.hasFailures()) {
			System.out.println(bulkResponse.buildFailureMessage());
		}
	}

	public void getResponseStartTime(long time) {
		startTime = time;
	}

	public void getResponseEndTime(long time) {
		endTime = time;
	}

	public long calculateResponseTime() {

		long milliseconds = endTime - startTime;
		long totalTime = (milliseconds / 1000) % 60;
		return totalTime;
	}

	public void beforeMethod(ITestContext ctx, Method method) {
		try {
			String suiteName = ctx.getCurrentXmlTest().getSuite().getName();
			testDetails.setSuiteName(suiteName);
			String testMethodName = method.getName();
			testDetails.setTestName(testMethodName);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void createIndex() {
		RestAssured.baseURI = "http://atlas.corp.qualitykiosk.com:9200/" + index_name;

		RequestSpecification httpRequest = RestAssured.given();

		Response verify_index = httpRequest.request().get();

		if (verify_index.getStatusCode() == 200) {
			LOGGER.info(index_name + " index is present");
		} else {
			JSONObject type = new JSONObject();
			type.put("type", "keyword");

			JSONObject field = new JSONObject();
			field.put("RunID", type);
			field.put("SuiteName", type);
			field.put("TestCaseID", type);
			field.put("PageName", type);
			field.put("PageStat", type);
			field.put("PageResponseTime", type);

			JSONObject properties = new JSONObject();
			properties.put("properties", field);

			JSONObject doc = new JSONObject();
			doc.put("doc", properties);

			JSONObject mapping = new JSONObject();
			mapping.put("mappings", doc);

			System.out.println(mapping);

			httpRequest.header("Content-Type", "application/json");

			httpRequest.body(mapping.toString());

			Response response = httpRequest.request().put();

			System.out.println(response.getStatusCode());
		}

	}

	public String createIndex_Name() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
		Date date = new Date();
		String index_name = "perf_log_" + dateFormat.format(date).toString();
		return index_name;
	}
}