package com.qaprosoft.carina.demo.utils;

public class TestExecutionDetails {
 
	public static String suiteName;
	public static String testCase;

	public void setSuiteName(String SuiteName){
		this.suiteName = SuiteName;
	}
    
    public void setTestName( String TestCaseID){
		this.testCase = TestCaseID;
	}
	
    public static String getSuiteName(){
		return suiteName;
	}
    
    public static String getTestName(){
		return testCase;
	}
}