package org.test_automation.LoginUtil;

import org.test_automation.BaseTest;
import org.test_automation.DBConnectivity.DBUtil;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Instant;

public class ForgetPasswordTest extends BaseTest {


    @Test(priority = 1)
    public void forgetPasswordUrl() {


        WebElement forgotPasswordLink = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//a[contains(text(),'Forgot password?')]")
        ));
        forgotPasswordLink.click();

        for (int i = 0; i < userDetails.size(); i++) {

            WebElement usernameField = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//input[@placeholder='Username']")
            ));

            usernameField.click();
            usernameField.clear();
            usernameField.sendKeys(userDetails.get(i).getUserName());

            WebElement resetButton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//button[contains(text(),'Reset Password')]")
            ));

            try {
                resetButton.click();
                // Capture Success Message
                WebElement successMsg = wait.until(ExpectedConditions.visibilityOfElementLocated(
                        By.xpath("//p[contains(text(),'A password reset link has been sent')]")
                ));

                // Highlight in GREEN
                ((JavascriptExecutor) driver).executeScript("arguments[0].style.border='3px solid green';", successMsg);

                String messageText = successMsg.getText();
                System.out.println("Captured Success Message: " + messageText);
                //db saved status

                DBUtil.forgetValidation(userDetails.get(i).getUserName(), messageText, "Success");


                Assert.assertEquals(messageText,
                        "A password reset link has been sent to your email. Please check your inbox and follow the instructions to reset your password.",
                        "Success message mismatch!");

                // Take Success Screenshot
                File successScreenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
                Instant now = Instant.now();
                File successDestination = new File("D:\\TestingScreenshot\\"+userDetails.get(i).getUserName()+"_"+now.toEpochMilli()+"_highlighted_success_message.png");
                Files.copy(successScreenshot.toPath(), successDestination.toPath());

                System.out.println("Success Screenshot saved at: " + successDestination.getAbsolutePath());

            } catch (TimeoutException e) {
                try {
                    resetButton.click();

                    WebElement errorMsg = wait.until(ExpectedConditions.visibilityOfElementLocated(
                            By.xpath("//p[contains(text(),'Invalid Username')]")
                    ));

                    // Highlight in RED
                    ((JavascriptExecutor) driver).executeScript("arguments[0].style.border='3px solid red';", errorMsg);

                    String errorText = errorMsg.getText();
                    System.out.println("Captured Error Message: " + errorText);

                    DBUtil.forgetValidation(userDetails.get(i).getUserName(), errorText, "Failure");


                    // Updated assertion to match the full message
                    Assert.assertTrue(errorText.contains("Invalid Username! Please try again."), "Unexpected error message!");

                    // Take Error Screenshot
                    File errorScreenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
                    Instant now = Instant.now();
                    File errorDestination = new File("D:\\TestingScreenshot\\" + userDetails.get(i).getUserName() + now.toEpochMilli() + "_highlighted_error_message.png");
                    Files.copy(errorScreenshot.toPath(), errorDestination.toPath());
                    System.out.println("Error Screenshot saved at: " + errorDestination.getAbsolutePath());

                } catch (TimeoutException | IOException ex) {
                    System.out.println("Error block exception: " + ex.getMessage());
                }
            } catch (IOException e) {
               e.printStackTrace();
            }

        }

    }


}
