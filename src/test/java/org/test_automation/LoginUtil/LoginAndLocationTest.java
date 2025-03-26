package org.test_automation.LoginUtil;

import org.test_automation.BaseTest;
import org.test_automation.DBConnectivity.DBUtil;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;

public class LoginAndLocationTest extends BaseTest {

    public Boolean isAlreadyLogin = false;

    @BeforeClass
    public void setUp() {
        if (loginTextInPanel("Welcome")) {
            isAlreadyLogin = true;
        } else {
            isLoginSuccessful = false;
        }
    }

    private boolean loginTextInPanel(String expectedText) {
        try {
            // Wait for visibility of all matching elements
            List<WebElement> elements = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(
                    By.xpath("//span[contains(text(),'Welcome') or contains(text(),'Login')]")
            ));

// Iterate through the list
            for (WebElement element : elements) {
                System.out.println("Texting found: " + element.getText());
                if (element.getText().contains("Welcome")) {
                    return true;
                } else if (element.getText().contains("Login")) {
                    return false;
                }
            }

        } catch (Exception e) {
            return false;
        }
        return false;
    }


    @Test(priority = 1)
    public void testLogin() {

        if (!isAlreadyLogin) {
            for (int i = 0; i < userDetails.size(); i++) {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                WebElement resultElement = null;


                for (int attempt = 1; attempt <= 5; attempt++) {
                    System.out.println("Attempt " + attempt + " for user: " + userDetails.get(i).getUserName());

                    WebElement usernameField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("signin-email")));
                    WebElement passwordField = driver.findElement(By.id("signin-password"));
                    WebElement loginButton = driver.findElement(By.cssSelector("button[type='submit']"));

                    typeSlowly(usernameField, userDetails.get(i).getUserName(), 200);
                    typeSlowly(passwordField, userDetails.get(i).getPassword(), 200);
                    loginButton.click();


                    final Boolean[] isFound = {false};
                    while (!isFound[0]) {
                        try {
                            resultElement = wait.until(driver -> {
                                List<By> locators = Arrays.asList(
                                        By.xpath("//span[text()='Welcome']"),
                                        By.xpath("//p[normalize-space(text())='Select Your Location']"),
                                        By.xpath("//p[contains(text(),'Your account has been temporarily locked')]"),
                                        By.xpath("//p[contains(text(),'Username or Password entered is incorrect')]"),
                                        By.xpath("//div[contains(@class, 'container-2')]/p[contains(text(),'Invalid Username')]")
                                );
                                isFound[0] = true;
                                for (By locator : locators) {
                                    List<WebElement> elements = driver.findElements(locator);
                                    if (!elements.isEmpty() && elements.get(0).isDisplayed()) {
                                        //System.out.println("Elemenets"+elements.toString());
                                        return elements.get(0);
                                    }
                                }
                                return null;
                            });
                        } catch (Exception e) {
                            threadTimer(2000);
                            JavascriptExecutor js = (JavascriptExecutor) driver;
                            js.executeScript("location.reload()");
                            isFound[0] = false;
                        }
                    }
                    // System.out.println("==================result set "+resultElement+""+resultElement.getText().trim()+"================");
                    if (resultElement != null) {
                        String resultText = resultElement.getText().trim();

                        if (resultText.contains("Select Your Location")) {
                            System.out.println("Login Successfully");
                            DBUtil.userNameValidation(userDetails.get(i).getUserName(), userDetails.get(i).getPassword(), "Login Successfully", "Success");

                            isLoginSuccessful = true;
                            break;
                        } else if (resultText.contains("temporarily locked")) {
                            System.out.println("Account Locked: " + resultText);
                            DBUtil.userNameValidation(userDetails.get(i).getUserName(), userDetails.get(i).getPassword(), resultText, "Locked");
                            break;
                        } else if (resultText.contains("Username or Password entered is incorrect")) {
                            System.out.println("Login failed: " + resultText);
                            DBUtil.userNameValidation(userDetails.get(i).getUserName(), userDetails.get(i).getPassword(), resultText, "Failed (Attempt " + attempt + ")");
                        } else if (resultText.contains("Invalid Username")) {

                            DBUtil.userNameValidation(userDetails.get(i).getUserName(), userDetails.get(i).getPassword(), resultText, "Failed (Attempt " + attempt + ")");
                            break;
                        } else if (resultText.contains("Welcome")) {
                            isSingleLocation = true;
                            isLoginSuccessful = true;
                            System.out.println("Login Successfully Welcome");
                            DBUtil.userNameValidation(userDetails.get(i).getUserName(), userDetails.get(i).getPassword(), "Login Successfully", "Success");
                            break;
                        } else {
                            isSingleLocation = true;
                            isLoginSuccessful = true;
                            System.out.println("Login Successfully");
                            DBUtil.userNameValidation(userDetails.get(i).getUserName(), userDetails.get(i).getPassword(), "Login Successfully", "Success");
                            isLoginSuccessful = true;
                            break;
                        }
                    } else {
                        System.out.println("No success or error message found.");
                        DBUtil.userNameValidation(userDetails.get(i).getUserName(), userDetails.get(i).getPassword(), "No success or error message", "Unknown");
                    }

                }
            }
        }


    }

    public void clearFieldsWithHighlight(WebElement usernameField, WebElement passwordField, WebElement resultElement) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].value = '';", usernameField);
        ((JavascriptExecutor) driver).executeScript("arguments[0].value = '';", passwordField);
        ((JavascriptExecutor) driver).executeScript("arguments[0].style.border='2px solid red'", resultElement);
        usernameField.click();
    }


    @Test(priority = 2, dependsOnMethods = {"testLogin"})
    public void testLocationSelection() {
        if (isAlreadyLogin) {
            isLoginSuccessful = true;
            isSingleLocation = true;
        }
        if (!isLoginSuccessful) {
            System.out.println("⛔ Skipping location selection as login failed.");
            return;
        }
        if (!isSingleLocation) {

            System.out.println("✅ Proceeding to Location Selection");
            wait = new WebDriverWait(driver, Duration.ofSeconds(40));


            try {
                WebElement locationDropdown = wait.until(ExpectedConditions.elementToBeClickable(
                        By.xpath("//select[@title='Location']")));
                wait.until(ExpectedConditions.visibilityOfElementLocated(
                        By.xpath("//select[@title='Location']//option[text()='Navaur branch']")));

                Select select = new Select(locationDropdown);
                select.selectByVisibleText("Navaur branch");
                System.out.println("🎯 Selected 'Navaur branch'");

                WebElement proceedButton = wait.until(ExpectedConditions.elementToBeClickable(
                        By.xpath("//button[normalize-space(text())='Proceed Next']")));
                proceedButton.click();
                System.out.println("🚀 Clicked 'Proceed Next'");

                boolean isDashboardLoaded = false;
                boolean isWelcomeFound = false;
                while (!isWelcomeFound) {
                    try {
                        WebElement welcomeText = driver.findElement(
                                By.xpath("//span[contains(text(),'Welcome')]"));
                        if (welcomeText.isDisplayed()) {
                            isWelcomeFound = true;
                            isDashboardLoaded = true;
                        }
                    } catch (NoSuchElementException e) {
                        Thread.sleep(500);
                    }
                }


            } catch (TimeoutException e) {
                System.out.println("⚠️ Timeout while selecting location or loading dashboard: " + e.getMessage());
            } catch (Exception e) {
                System.out.println("⚠️ Unexpected error: " + e.getMessage());
            }
        } else {
            isLoginSuccessful = true;
            isSingleLocation = true;
        }

    }


    public void typeSlowly(WebElement element, String text, int delayMillis) {
        for (char ch : text.toCharArray()) {
            element.sendKeys(String.valueOf(ch));
            try {
                Thread.sleep(delayMillis);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
