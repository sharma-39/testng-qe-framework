package org.test_automation;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONArray;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.locks.ReentrantLock;

public class BaseTest {

    protected static WebDriver driver; // Static WebDriver to share across classes
    protected WebDriverWait wait;
    protected boolean isLoginSuccessful = false;
    protected boolean isSingleLocation = false;
    protected boolean isDashboardLoaded = false;
    protected JSONArray tempPatientData;
    protected JsonNode tempStockData;
    protected Boolean isAgeInMonth = false;
    protected List<String> ageLabel = new ArrayList<>();
    protected Boolean isAgeInYear = false;
    protected List<UserDetails> userDetails = new ArrayList<>();

    public Integer billNumber = null;

    private static String patientSearchCode = null;
    private static final ReentrantLock patientSearchLock = new ReentrantLock();

    /* ========== Thread-Safe Patient Search Code Methods ========== */

    /**
     * Sets the patient search code in a thread-safe manner
     *
     * @param code The patient code to set
     */
    public static void setPatientSearchCode(String code) {
        System.out.println("set function" + code);
        patientSearchLock.lock();
        try {
            System.out.println("[DEBUG] Setting patientSearchCode to: " + code +
                    " (Thread: " + Thread.currentThread().getId() + ")");
            patientSearchCode = code;
        } finally {
            patientSearchLock.unlock();
        }
    }

    /**
     * Gets the current patient search code in a thread-safe manner
     *
     * @return The current patient search code
     */
    public static String getPatientSearchCode() {
        patientSearchLock.lock();
        try {
            System.out.println("[DEBUG] Getting patientSearchCode: " + patientSearchCode +
                    " (Thread: " + Thread.currentThread().getId() + ")");
            return patientSearchCode;
        } finally {
            patientSearchLock.unlock();
        }
    }

    @BeforeSuite
    public void setUpSuite() {
        // Initialize WebDriver and perform login only once
        if (driver == null) { // Ensure initialization happens only once
            String env = ConfigReader.getProperty("env");
            String baseUrl = ConfigReader.getProperty("url." + env);
            String driverPath = Paths.get("src/test/resources/chromedriver.exe").toAbsolutePath().toString();
            System.setProperty("webdriver.chrome.driver", driverPath);
            driver = new ChromeDriver();
            driver.manage().window().maximize();
            driver.get(baseUrl);
            wait = new WebDriverWait(driver, Duration.ofSeconds(55));

            // Perform login logic here
            System.out.println("Logging in... (BeforeSuite)");
            isLoginSuccessful = true; // Mark login as successful
            clearPatientSearchCode();
        }
    }

    @BeforeClass
    public void setUpClass() {
        // Set up class-specific configurations
        wait = new WebDriverWait(driver, Duration.ofSeconds(55));

        try {
            String filePath = System.getProperty("user.dir") + "/src/test/resources/testing_data_dev.json";
            String jsonData = new String(Files.readAllBytes(Paths.get(filePath)));
            tempPatientData = new JSONArray(jsonData);

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(new File("src/test/resources/stockData.json"));
            tempStockData = jsonNode;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        userDetails = List.of(
                new UserDetails("FAC-989-support", "Admin@123")
        );

        ageLabel.add("Age In Years And Months");
        ageLabel.add("Age In Years");
    }

    @AfterClass
    public void tearDownClass() {
        // Clean up class-specific resources
    }

    @AfterSuite
    public void tearDownSuite() {
        // Close the browser after the entire suite
        if (driver != null) {
            //  driver.quit();
        }
    }

    public void threadTimer(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void menuPanelClick(String panel, Boolean subPanel, String subPanelName, String screenCheck) {
        if (screenCheck.isEmpty()) {
            wait = new WebDriverWait(driver, Duration.ofSeconds(50));
            threadTimer(3000);
            WebElement menuButton = driver.findElement(By.id("mega-menu-nav-btn"));
            if (menuButton.isDisplayed()) {
                JavascriptExecutor js = (JavascriptExecutor) driver;
                js.executeScript("arguments[0].click();", menuButton);
            } else {
                System.out.println(" ⚠ Menu Button is not visible, skipping click action.");
            }
            threadTimer(3000);

            wait.until(ExpectedConditions.invisibilityOfElementLocated(By.className("page-loader-wrapper")));

            System.out.println("Parent panel Name: " + panel);

            if (subPanel) {
                // Step 1: Click "Master" to Expand the Dropdown
                WebElement masterMenu = wait.until(ExpectedConditions.elementToBeClickable(
                        By.xpath("//a[normalize-space()='" + panel + "']")));
                masterMenu.click();
                System.out.println("✅ Clicked on " + panel);

                // Step 2: Click "Pharmacy" Inside the Dropdown
                WebElement pharmacyLink = wait.until(ExpectedConditions.elementToBeClickable(
                        By.xpath("//ul[contains(@class, 'show-submenu')]//a[normalize-space()='" + subPanelName + "']")));

                JavascriptExecutor js = (JavascriptExecutor) driver;
                js.executeScript("arguments[0].scrollIntoView(true);", pharmacyLink);
                js.executeScript("arguments[0].style.border='3px solid blue';", pharmacyLink); // Highlight

                try {
                    pharmacyLink.click();
                } catch (Exception e) {
                    System.out.println("Using JavaScript click as fallback.");
                    js.executeScript("arguments[0].click();", pharmacyLink);
                }

                System.out.println("✅ Clicked on " + subPanelName);
            } else {
                WebElement panelClick = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//a[contains(text(),'" + panel + "')]")));

                JavascriptExecutor js = (JavascriptExecutor) driver;
                js.executeScript("arguments[0].style.border='3px solid red';", panelClick); // Highlight
                js.executeScript("arguments[0].scrollIntoView(true);", panelClick);
                panelClick.click();
                System.out.println("✅ Panel Click: " + panel);
            }
        } else {
            verifyPanelName(screenCheck);
        }
    }

    public Boolean verifyPanelName(String expectedText) {
        try {
            WebElement breadcrumb = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//li[@class='breadcrumb-item active breadcrums-data' and normalize-space()='" + expectedText + "']")
            ));
            System.out.println("Breadcrumb found: " + breadcrumb.getText());
            if (breadcrumb.getText().contains(expectedText)) {
                return true;
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }

    public WebElement warningMessagePurchase() {
        try {
            WebElement resultElement = wait.until(driver -> {
                List<By> locators = List.of(
                        By.xpath("//div[contains(@class, 'container-2')]/p[contains(normalize-space(),'Successfully')]"),
                        By.xpath("//div[contains(@class, 'container-2')]/p[contains(text(),'Already Exists')]"),
                        By.xpath("//div[contains(@class, 'container-2')]/p")
                );

                for (By locator : locators) {
                    List<WebElement> elements = driver.findElements(locator);
                    if (!elements.isEmpty() && elements.get(0).isDisplayed()) {
                        return elements.get(0);
                    }
                }
                return null;
            });
            return resultElement;
        } catch (Exception e) {
            return null;
        }
    }

    public List<WebElement> getMandatoryFields() {
        return driver.findElements(By.xpath("//div[contains(@class, 'error-msg')]"));
    }

    public void clickButtonElement(By locator) {
        System.out.println("locater" + locator);
        threadTimer(500);
        try {
            WebElement element = wait.until(ExpectedConditions.elementToBeClickable(locator));
            try {
                if (element.isEnabled()) {
                    element.click();
                }
            } catch (Exception e) {
                element.click();
            }
        } catch (Exception e) {
            System.out.println("Normal click failed, using JavaScript click..." + e);
            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript("arguments[0].click();", driver.findElement(locator));
        }
    }


    public void filterSearchClick() {


// Wait until the button is clickable
        WebElement searchButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[@title='Search']")
        ));

// Scroll into view
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", searchButton);
        try {
            Thread.sleep(500); // Small wait for smooth UI
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

// Click the button using JavaScript to avoid interception issues
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", searchButton);
    }


    public void filterSearchElemenet(String searchValue, String searchFiler, String type) {


        if (type.equals("Text")) {

// Wait for the input field inside the "Patient Code" column
            WebElement fieldsElement = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//th[@title='" + searchFiler + "']//input[contains(@class, 'form-control')]")
            ));

// Scroll into view (if needed)
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", fieldsElement);
            try {
                Thread.sleep(500); // Small delay for UI adjustment
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

// Clear any existing text and enter new value
            fieldsElement.clear();
            fieldsElement.sendKeys(searchValue); // Replace with actual Patient Code

            fieldsElement.sendKeys(Keys.ENTER);

            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        } else {
            WebElement expiryDateField = driver.findElement(By.id(searchFiler));
            expiryDateField.click();
            selectDatePicker(searchValue);
        }
    }

    public static void clearPatientSearchCode() {
        patientSearchLock.lock();
        try {
            System.out.println("[DEBUG] Clearing patientSearchCode" +
                    " (Thread: " + Thread.currentThread().getId() + ")");
            patientSearchCode = null;
        } finally {
            patientSearchLock.unlock();
        }
    }

    public void filterRangeSelect(String range) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        By visiblePicker = By.cssSelector("div.daterangepicker.ltr.show-ranges.opensright[style*='display: block']");
        wait.until(ExpectedConditions.visibilityOfElementLocated(visiblePicker)); // Ensure the picker is visible

        By oneMonthOption = By.cssSelector("div.daterangepicker.ltr.show-ranges.opensright[style*='display: block'] div.ranges li[data-range-key='" + range + "']");
        WebElement oneMonthElement = wait.until(ExpectedConditions.elementToBeClickable(oneMonthOption));

        oneMonthElement.click();
    }

    public void selectDatePicker(String dateValue) {
        System.out.println("date value: " + dateValue);
        String[] dateFormat = dateValue.split("-");
        String dateText = String.valueOf(Integer.parseInt(dateFormat[0])); // Day
        String monthText = convertMMToMonth(dateFormat[1]); // Convert MM to Month name
        String yearText = dateFormat[2]; // Year

        System.out.println("date: " + dateText + " month: " + monthText + " year: " + yearText);

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));

        // Select Year
        WebElement yearDropdown = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div.daterangepicker[style*='display: block'] select.yearselect")));
        Select selectYear = new Select(yearDropdown);
        selectYear.selectByVisibleText(yearText);
        System.out.println("Selected Year: " + yearText);

        // Wait for the month dropdown to be ready
        wait.until(ExpectedConditions.textToBePresentInElementLocated(By.cssSelector("div.daterangepicker[style*='display: block'] select.yearselect"), yearText));

        // Select Month
        WebElement monthDropdown = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div.daterangepicker[style*='display: block'] select.monthselect")));
        Select selectMonth = new Select(monthDropdown);
        selectMonth.selectByVisibleText(monthText);
        System.out.println("Selected Month: " + monthText);

        // Wait for the day picker to refresh
        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("div.daterangepicker[style*='display: block'] td.available")));

        // Select Day
        List<WebElement> days = driver.findElements(By.cssSelector("div.daterangepicker[style*='display: block'] td.available"));
        for (WebElement day : days) {
            System.out.println("get Days" + day.getText());
            if (day.getText().trim().equals(dateText)) { // Fixing Integer.parseInt issue
                day.click();
                System.out.println("Selected Day: " + dateText);
                break;
            }
        }
        System.out.println("Date selection complete.");
    }


    public String convertMMToMonth(String monthNumber) {
        int monthInt = Integer.parseInt(monthNumber);
        return Month.of(monthInt).getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
    }
}