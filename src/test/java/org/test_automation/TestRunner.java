package org.test_automation;

import org.testng.TestNG;

import java.util.Collections;

public class TestRunner {
    public static void main(String[] args) {
        TestNG testng = new TestNG();
        testng.setTestSuites(Collections.singletonList("src/test/resources/testng.xml"));
        testng.run();
    }
}
