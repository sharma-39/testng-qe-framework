package org.example.Listener;

import org.testng.ITestListener;
import org.testng.ITestResult;

public class AllTestListener implements ITestListener {

    @Override
    public void onTestStart(ITestResult result) {
        // Dynamically set the test name based on the scenario
        try {
            String scenarioName = (String) result.getParameters()[0]; // Scenario name is the first parameter
            result.setTestName("Test: " + scenarioName);
            System.out.println("Test Started: " + scenarioName);
        }catch (Exception e)
        {
            System.out.println("Test Started: " + result.getName());
        }
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        try {
            String scenarioName = (String) result.getParameters()[0]; // Scenario name is the first parameter
            result.setTestName("Test: " + scenarioName);
            System.out.println("Test Success: " + scenarioName);
        }catch (Exception e)
        {
            System.out.println("Test Success: " + result.getName());
        }
    }

    @Override
    public void onTestFailure(ITestResult result) {

        try {
            String scenarioName = (String) result.getParameters()[0]; // Scenario name is the first parameter
            result.setTestName("Test: " + scenarioName);
            System.out.println("Test Failed: " + scenarioName);
        }catch (Exception e)
        {
            System.out.println("Test Failed: " + result.getName());
        }

    }

    @Override
    public void onTestSkipped(ITestResult result) {
        try {
            String scenarioName = (String) result.getParameters()[0]; // Scenario name is the first parameter
            result.setTestName("Test: " + scenarioName);
            System.out.println("Test Skipped: " + scenarioName);
        }catch (Exception e)
        {
            System.out.println("Test Skipped: " + result.getName());
        }
    }
}