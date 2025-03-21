package org.example;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONArray;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class BaseTest {

    protected WebDriver driver;
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

    @BeforeClass
    public void setUp() {
        String env = ConfigReader.getProperty("env");
        String baseUrl = ConfigReader.getProperty("url." + env);
        System.setProperty("webdriver.chrome.driver", "D:\\chromedriver-win64\\chromedriver.exe");
        //   Use WebDriverManager to avoid hardcoded path
        //without open just run background
//        ChromeOptions options = new ChromeOptions();
//        options.addArguments("--headless"); // Run in Jenkins without GUI
//        options.addArguments("--no-sandbox");
//        options.addArguments("--disable-dev-shm-usage");
//        driver = new ChromeDriver(options);
// local and open chrome and testing
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.get(baseUrl);
        wait = new WebDriverWait(driver, Duration.ofSeconds(55));

        try {
            String filePath = System.getProperty("user.dir") + "/src/test/resources/testing_data_dev.json";
            String jsonData = new String(Files.readAllBytes(Paths.get(filePath)));
            tempPatientData = new JSONArray(jsonData);

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(new File("src/test/resources/stockData.json"));
            tempStockData= jsonNode;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        userDetails = List.of(
//                new UserDetails("incorrectUsername", "correctPassword"), // Username Incorrect
//                new UserDetails("FAC-973-support", "incorrectPassword"), // Username and Password Incorrect
//                new UserDetails("Sharma", "correctPassword") ,
//                new UserDetails("scott", "scott")
                new UserDetails("FAC-989-support", "Admin@123")
                // Account Locked (after multiple attempts)
        );

        ageLabel.add("Age In Years And Months");
        ageLabel.add("Age In Years");
    }

    public void threadTimer(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void menuPanelClick(String panel, Boolean subPanel, String subPanelName, String screenCheck) {
        if(screenCheck.isEmpty()) {
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


            System.out.println("Parent panel Name" + panel);

            if (subPanel) {

                // Step 1: Click "Master" to Expand the Dropdown
                WebElement masterMenu = wait.until(ExpectedConditions.elementToBeClickable(
                        By.xpath("//a[normalize-space()='" + panel + "']")));
                masterMenu.click();
                System.out.println("✅ Clicked on Master");

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

                System.out.println("✅ Clicked on Pharmacy");


            } else {
                WebElement panelClick = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//a[contains(text(),'" + panel + "')]")));

                JavascriptExecutor js = (JavascriptExecutor) driver;
                js.executeScript("arguments[0].style.border='3px solid red';", panelClick); // Highlight
                js.executeScript("arguments[0].scrollIntoView(true);", panelClick);
                panelClick.click();
                System.out.println("✅ Panel Click :-" + panel);
            }
        }

        else {
            verifyPanelName(screenCheck);
        }

    }

    protected void verifyPanelName(String expectedText) {
        WebElement breadcrumb = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//li[@class='breadcrumb-item active breadcrums-data' and normalize-space()='" + expectedText + "']")
        ));
        System.out.println("Breadcrumb found: " + breadcrumb.getText());
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

    public List<WebElement> getMantoraryFields() {
        return driver.findElements(By.xpath("//div[contains(@class, 'error-msg')]"));
    }


    @AfterClass
    public void tearDown() {
       // driver.quit();
    }

    public void clickButtonElement(By locator) {

        threadTimer(500);
        try {
            WebElement element = wait.until(ExpectedConditions.elementToBeClickable(locator));
            element.click();
        } catch (Exception e) {
            //System.out.println("Normal click failed, using JavaScript click...");

        }

    }
}
