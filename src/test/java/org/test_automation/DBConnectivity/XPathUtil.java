package org.test_automation.DBConnectivity;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;

public class XPathUtil {


    /**
     * Universal dropdown selector supporting multiple UI technologies
     *
     * @param identifier - locator value (formControlName, title, XPath, etc.)
     * @param value      - value to select
     * @param type       - dropdown type enum
     * @param formId
     * @param driver
     * @param wait
     */
    public void selectField(String identifier, String value, DropdownType type, String formId, WebDriver driver, WebDriverWait wait) {
        try {
            switch (type) {
                case STANDARD:
                    handleStandardSelect(identifier, value, wait, driver);
                    break;
                case ANGULAR_TITLE:
                    handleAngularTitleSelect(identifier, value, driver, wait);
                    break;
                case MATERIAL:
                    handleMaterialSelect(identifier, value, wait, driver);
                    break;
                case XPATH:
                    handleXPathSelect(identifier, value, wait, driver);
                    break;

                case NORMAL_SELECT:
                    handleNormalSelect(identifier, value, driver, wait);
                    break;
                case FORM_ID:
                    handleFormSelect(formId, identifier, value, wait, driver);
                    break;
                case NG_SELECT:
                    ngSelect(identifier, value, driver, wait);
                case DISPLAY_NONE:
                    selectDisplayNone(identifier, value, driver, wait);
                    break;
                default:
                    throw new IllegalArgumentException("Unsupported dropdown type: " + type);
            }
            threadTimer(500);
        } catch (Exception e) {
            System.err.println("Failed to select '" + value + "' in " + type + " dropdown: " + e.getMessage());
            throw e;
        }
    }

    private void selectDisplayNone(String identifier, String value, WebDriver driver, WebDriverWait wait) {
        String cssSelector =
                "div[style*='display: block'] select[formcontrolname='" + identifier + "']," + // No space after comma
                        "div[style*='display: block'] app-select[formcontrolname='" + identifier + "'] select";

        WebElement dropdown = wait.until(
                ExpectedConditions.elementToBeClickable(By.cssSelector(cssSelector))
        );
        new Select(dropdown).selectByVisibleText(value);
    }

    private void ngSelect(String identifier, String value, WebDriver driver, WebDriverWait wait) {
        // Step 1: Locate the ng-select dropdown and click to open it
        WebElement ngSelectDropdown = driver.findElement(By.cssSelector("ng-select[title='" + identifier + "']"));
        ngSelectDropdown.click();

// Step 2: Wait until the dropdown options are visible
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("ng-dropdown-panel .ng-dropdown-panel-items")));

// Step 3: Select the desired option by its visible text
        List<WebElement> options = driver.findElements(By.cssSelector("ng-dropdown-panel .ng-option"));
        for (WebElement option : options) {
            if (option.getText().equals(value)) {
                option.click();
                break;
            }
        }

    }

    private void handleNormalSelect(String identifier, String value, WebDriver driver, WebDriverWait wait) {

        System.out.println("identifier::====" + value);
        System.out.println("value::====" + value);
        WebElement dropdown = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("select[title='" + identifier + "']")));
        threadTimer(2000);

        new Select(dropdown).selectByVisibleText(value);

    }

    private void handleFormSelect(String formId, String identifier, String value, WebDriverWait wait, WebDriver driver) {
        WebElement uomDropdown = driver.findElement(By.xpath("//form[@id='" + formId + "']//select[@title='" + identifier + "']"));


        threadTimer(2000);

        new Select(uomDropdown).selectByVisibleText(value);

    }

    private void handleStandardSelect(String formControlName, String value, WebDriverWait wait, WebDriver driver) {
        WebElement dropdown = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("select[formcontrolname='" + formControlName + "'], " + "app-select[formcontrolname='" + formControlName + "'] select")));


        Select select = new Select(dropdown);
        List<WebElement> options = select.getOptions();

        boolean isOptionPresent = options.stream()
                .anyMatch(option -> option.getText().contains(value)); // Use `contains` instead of `equals`

        if (isOptionPresent) {
            // Select the first option that contains the text
            select.getOptions().stream()
                    .filter(option -> option.getText().contains(value))
                    .findFirst()
                    .ifPresent(WebElement::click);
        } else {
            throw new RuntimeException("No option contains text: " + value);
        }
    }

    private void handleAngularTitleSelect(String title, String value, WebDriver driver, WebDriverWait wait) {
        WebElement dropdown = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("app-select[title='" + title + "'] select")));
        threadTimer(2000);
        new Select(dropdown).selectByVisibleText(value);
    }

    // ================== HANDLER IMPLEMENTATIONS ================== //

    public void threadTimer(int threadTime) {
        try {
            Thread.sleep(threadTime);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void handleMaterialSelect(String labelText, String value, WebDriverWait wait, WebDriver driver) {
        // Find and click the mat-select element
        WebElement matSelect = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//mat-label[contains(.,'" + labelText + "')]/ancestor::mat-form-field//mat-select")));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", matSelect);

        // Select the option
        WebElement option = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//mat-option//span[contains(text(),'" + value + "')]")));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", option);
    }

    private void handleXPathSelect(String xpath, String value, WebDriverWait wait, WebDriver driver) {
        WebElement dropdown = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(xpath)));
        new Select(dropdown).selectByVisibleText(value);
    }

    public void fillTextField(String fieldPathId, String value, WebDriverWait wait) {

        WebElement inputField = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//input[@formcontrolname='" + fieldPathId + "'] | " + "//app-input-text[@formcontrolname='" + fieldPathId + "']//input | " + "//app-input-number[@formcontrolname='" + fieldPathId + "']//input | " + "//textarea[@formcontrolname='" + fieldPathId + "'] " + "| app-textarea[@formcontrolname='" + fieldPathId + "']//textarea")));
        if (inputField.isEnabled()) {
            inputField.clear();
            inputField.sendKeys(value);
            inputField.sendKeys(Keys.TAB);
            threadTimer(500);
        }
    }


    // fill the input field and text format

    //option select
    public void optionSelect(String formControlName, String value, String childValue, WebDriverWait wait, WebDriver driver) {
        try {
            WebElement radioButton = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//input[@formcontrolname='" + formControlName + "'][@value='" + value + "'] | //label[span[contains(text(), '" + value + "')]]/input[@formcontrolname='" + formControlName + "'] ")));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", radioButton);
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", radioButton);
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", radioButton);
            System.out.println("Selected radio button: " + value);
        } catch (TimeoutException e) {
            System.out.println("Radio button with value '" + value + "' not found!");
        }
    }

    //app text area fill
    public void fillTextArea(String formControlName, String value, WebDriverWait wait, WebDriver driver, String appText) {
        // Use CSS selector to target the Angular component structure
        String cssSelector = String.format("app-textarea[formcontrolname='%s'] textarea", formControlName);

        // Wait for the textarea to be interactable
        WebElement textarea = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(cssSelector)));

        // Clear existing text using JavaScript (more reliable for Angular fields)
        ((JavascriptExecutor) driver).executeScript("arguments[0].value = '';", textarea);

        // Send keys and validate input
        textarea.sendKeys(value);

        // Verify the value was entered

        threadTimer(500);  // Your existing delay
    }

    public void closePrintScreen() {
        try {
            Robot robot = new Robot();
            robot.delay(1000); // Wait before sending key
            robot.keyPress(KeyEvent.VK_ESCAPE); // Press escape key
            robot.keyRelease(KeyEvent.VK_ESCAPE); // Release escape key
            threadTimer(4000); // Wait for the screen to close
        } catch (AWTException ignored) {
            ignored.printStackTrace();
        }
    }

    public void commonSaveAndClose(String id, WebDriver driver, WebDriverWait wait, Boolean formIn, By commonLocator) {

        By saveCloseLocator;
        if (id.isEmpty()) {
            if (formIn) {
                saveCloseLocator = By.xpath("//form[@id='" + id + "']//button[contains(@class, 'saveNdClose') and normalize-space()='Save & Close'] | " + "//button[normalize-space()='Save & Close']");

            } else {
                saveCloseLocator = By.xpath("//div[contains(@style, 'display: block')]" + "//button[normalize-space()='Save & Close']");
            }
        } else {
            saveCloseLocator = commonLocator;
        }
        try {
            WebElement element = wait.until(ExpectedConditions.elementToBeClickable(saveCloseLocator));

            if (element.isEnabled()) {
                element.click();
            }

        } catch (TimeoutException | NoSuchElementException e) {
            System.out.println("error throw" + e.getMessage());

            WebElement element = wait.until(ExpectedConditions.elementToBeClickable(saveCloseLocator));

            if (element.isEnabled()) {
                element.click();
            }
        }

        errorMessageAndResponseMessage(wait);

        threadTimer(2000);
    }

    private String errorMessageAndResponseMessage(WebDriverWait wait) {
        return wait.until(driver -> {
            // Error messages (only these should cause test failure)
            List<String> errorMessages = Arrays.asList(
                    "Error 403",
                    "Error 0",
                    "Error 2",
                    "Something Went Wrong",
                    "Internal"
            );

            // XPath locators for detecting messages
            List<By> messageLocators = Arrays.asList(
                    By.xpath("//div[contains(@class, 'container-2')]/p"),
                    By.xpath("//p"),
                    By.xpath("//div[contains(@class, 'toast-right-top')]//p")
            );

            // Iterate through locators to find a message
            for (By locator : messageLocators) {
                List<WebElement> elements = driver.findElements(locator);
                if (!elements.isEmpty() && elements.get(0).isDisplayed()) {
                    String messageText = elements.get(0).getText().trim();
                    System.out.println("🔍 Message Found: " + messageText);

                    // Fail only if message is in errorMessages list
                    if (errorMessages.contains(messageText)) {
                        System.out.println("⚠️ Error Message Detected: " + messageText);
                        Assert.fail("Test failed due to error message: " + messageText);
                    }

                    // ✅ Return the found message text
                    return messageText;
                }
            }

            return null;
        });
    }

    public void clickCheckBox(Boolean autoPost, WebDriverWait wait, WebDriver driver, String text) {

        By autoPostCheckbox = By.cssSelector("input[formcontrolname='autoPost']");

// First check existence
        WebElement checkbox = wait.until(ExpectedConditions.presenceOfElementLocated(autoPostCheckbox));


        if (!autoPost) {
            if (checkbox.isSelected()) {
                System.out.println("already selected");
            } else {
                if (autoPost) {
                    wait.until(ExpectedConditions.elementToBeClickable(checkbox)).click();
                }
            }
        }

// Then check clickability

    }

    public void clickButtonElement(By locator, WebDriver driver, WebDriverWait wait) {
        //System.out.println("locater" + locator);
        threadTimer(500);
        try {
            WebElement element = wait.until(ExpectedConditions.elementToBeClickable(locator));

            if (element.isEnabled()) {
                element.click();
            }

        } catch (Exception e) {
            System.out.println("Normal click failed, using JavaScript click..." + e);
            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript("arguments[0].click();", driver.findElement(locator));
        }
    }


    public enum DropdownType {
        STANDARD,       // HTML <select> with formControlName
        ANGULAR_TITLE,  // Angular component with title attribute
        MATERIAL,       // Material-UI <mat-select>
        XPATH, NORMAL_SELECT, NG_SELECT, FORM_ID, DISPLAY_NONE,// Custom XPath locator
    }
}
