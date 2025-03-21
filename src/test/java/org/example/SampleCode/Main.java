package org.example.SampleCode;

import org.json.JSONArray;
import org.json.JSONObject;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.List;

public class Main {

    private static final String USER_NAME = "scott";
    private static final String PASS_WORD = "scott";
    private static final long THREAD_SECONDS = 3000;
    private static final long WAIT_SECONDS = 45;


    public static void main(String[] args) throws InterruptedException {
        System.setProperty("webdriver.chrome.driver", "D:\\chromedriver-win64\\chromedriver.exe");



        // Initialize WebDriver
        ChromeDriver driver = new ChromeDriver();
        driver.get("http://18.215.63.38:8095/#/auth/login");
        driver.manage().window().maximize();

        loggingFunction(driver);

        Thread.sleep(THREAD_SECONDS);

        locationSelected(driver);


        Thread.sleep(THREAD_SECONDS);

        System.out.println("Logging in...");

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(WAIT_SECONDS));

        boolean isWelcomeFound = false;
        int attempts = 0;
        int maxAttempts = 10; // Adjust as needed

        do {
            try {
                WebElement welcomeText = wait.until(ExpectedConditions.presenceOfElementLocated(
                        By.xpath("//li/span[contains(text(),'Welcome')]")
                ));

                if (welcomeText.isDisplayed()) {
                    System.out.println("Login successful! 'Welcome' text found.");
                    isWelcomeFound = true;
                    // Call the next function after login
                }
            } catch (TimeoutException e) {
                System.out.println("Retrying... 'Welcome' text not found yet.");
            }

            attempts++;
            Thread.sleep(THREAD_SECONDS); // Wait for 3 seconds before retrying
        } while (!isWelcomeFound && attempts < maxAttempts);

        if (isWelcomeFound) {
        try {
            String jsonData = new String(Files.readAllBytes(Paths.get("D:\\TestingData\\testing_data.json")));
            JSONArray patients = new JSONArray(jsonData);


            for (int i = 27; i < patients.length(); i++) {
                try {
                    Thread.sleep(THREAD_SECONDS);

                    JSONObject patient = patients.getJSONObject(i);

                    String patientName = patient.getString("patientName");
                    String patientAge = patient.getString("patientAge");
                    String patientPhone = patient.getString("patientPhone");
                    String doctorName = patient.getString("doctorName");
                    String admissionType = patient.getString("admissionType");
                    String gender = patient.getString("gender");
                    String scanType = patient.getString("scanType");

                    System.out.println("Processing patient: " + patientName);

                    patientRegister(driver, patientName, patientAge, patientPhone, gender, "Patient Registration");
                    Thread.sleep(THREAD_SECONDS);

                    createAppointment(driver, patientName, admissionType, doctorName, scanType, "Create Appointment");
                    Thread.sleep(THREAD_SECONDS);

                    checkingAppoinment(driver, patientName, "View Appointments");
                    Thread.sleep(THREAD_SECONDS);

                    addPrescription(driver, patientName, "Current Admissions");
                    Thread.sleep(THREAD_SECONDS);

                    pharmacyBill(driver, patientName, "Pharmacy");
                    Thread.sleep(2000);

                    menuPanalClick(driver, "Dashboard");
                    Thread.sleep(THREAD_SECONDS);

                } catch (Exception e) {
                    System.out.println("Error processing patient: " + e.getMessage());
                    e.printStackTrace();
                    // Continue to the next patient instead of stopping the loop
                }
            }


        } catch (IOException e) {
            e.printStackTrace();
        }



        }
    }

    private static void patientRegister(ChromeDriver driver, String patientName, String patientAge, String patientPhone, String gender,String panel) {

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(WAIT_SECONDS));
        menuPanalClick(driver,panel);

        WebElement firstNameField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@formcontrolname='firstName']")));

        firstNameField.sendKeys(patientName);


        WebElement ageField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@formcontrolname='age']")));

        ageField.sendKeys(patientAge);


        WebElement phoneNumberField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@formcontrolname='phoneNumber']")));

        phoneNumberField.sendKeys(patientPhone);


        JavascriptExecutor js1 = (JavascriptExecutor) driver;
        WebElement element = driver.findElement(By.xpath("//input[@formcontrolname='gender' and @value='"+gender+"']"));
        js1.executeScript("arguments[0].click();", element);


        // Step 1: Click the MatSelect dropdown to open the options
        WebElement matSelect = wait.until(ExpectedConditions.elementToBeClickable(By.id("mat-select-0")));
        matSelect.click();

        // Step 2: Wait for the options to be visible
        WebElement cityOption = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//span[contains(text(), 'Chennai')]")
        ));

        // Step 3: Click on the option to select it
        cityOption.click();

        WebElement submitButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(text(),'Submit')]")));
        submitButton.click();

    }

    private static void menuPanalClick(ChromeDriver driver, String panel) {
        try {
            Thread.sleep(THREAD_SECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(WAIT_SECONDS));

        visibleOrNotMenu(driver);

        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.className("page-loader-wrapper")));

        WebElement panelClick = wait.until(
                ExpectedConditions.elementToBeClickable(By.xpath("//a[contains(text(),'"+panel+"')]"))
        );


        panelClick.click();
    }

    private static void pharmacyBill(ChromeDriver driver, String patientName,String panel) throws InterruptedException {

        menuPanalClick(driver,panel);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(WAIT_SECONDS));


        // Wait for the row containing "SharmaA"
        WebElement row = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//tr[td/span[contains(text(),'" + patientName + "')]]")
        ));

        WebElement billButton = row.findElement(By.xpath(".//button[@title='Bill']"));

        wait.until(ExpectedConditions.elementToBeClickable(billButton)).click();


        try {
            Thread.sleep(THREAD_SECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        // Wait for the "Generate Bill" button to be present
        WebElement generateBillButton = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//button[contains(text(),'Generate Bill')]")
        ));


        wait.until(ExpectedConditions.elementToBeClickable(generateBillButton)).click();

        System.out.println("Generate Bill button clicked successfully.");

        try {
            Thread.sleep(THREAD_SECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        WebElement payButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[contains(text(),'Pay')]")
        ));

// Scroll to the button (if needed)
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", payButton);
        Thread.sleep(THREAD_SECONDS); // Small delay to ensure visibility

// Click the button
        payButton.click();

        System.out.println("Pay button clicked successfully.");


        // Switch to the print dialog window (if applicable)
//        Set<String> windowHandles = driver.getWindowHandles();
//        for (String window : windowHandles) {
//            driver.switchTo().window(window);
//        }
//
//        System.out.println("Print dialog closed.");

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        Robot robot = null;
        try {
            robot = new Robot();
        } catch (AWTException e) {
            throw new RuntimeException(e);
        }


        Thread.sleep(2000); // Wait for print dialog

        // Press ESC to cancel print
        robot.keyPress(KeyEvent.VK_ESCAPE);
        robot.keyRelease(KeyEvent.VK_ESCAPE);

    }

    private static void addPrescription(ChromeDriver driver, String patientName,String panel) {


        menuPanalClick(driver,panel);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(WAIT_SECONDS));

        try {


            WebElement patientRow = findAndClickDropdownAndPrescription(patientName, driver, wait);
            if (patientRow != null) {
                System.out.println("Dropdown clicked successfully.");
            } else {
                System.out.println("Patient not found.");
            }

            Thread.sleep(THREAD_SECONDS);
            WebElement addButton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.id("current-admission-prescribedAdd")
            ));
            addButton.click();



            WebElement medicineInput = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//input[@placeholder='Enter Medicine']")
            ));
            medicineInput.sendKeys("Sulphasala");

            Thread.sleep(THREAD_SECONDS); // Adjust if necessary

            WebElement selectedOption = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//mat-option//span[contains(text(),'Sulphasalazine Tablet  50 50 Tablets')]")
            ));
            selectedOption.click();


            WebElement quantityInput = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//input[@type='number' and @title='Quantity']")
            ));

            quantityInput.clear();
            quantityInput.sendKeys("10");


            WebElement saveCloseButton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//button[contains(text(), 'Save & Close')]")
            ));

            saveCloseButton.click();

            System.out.println("Successfully created Prescription");


        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private static void visibleOrNotMenu(ChromeDriver driver) {

        WebElement menuButton = driver.findElement(By.id("mega-menu-nav-btn"));
        if (menuButton.isDisplayed()) {
            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript("arguments[0].click();", menuButton);
            System.out.println("Clicked on Menu Button");
        } else {
            System.out.println("Menu Button is not visible, skipping click action.");
        }
        try {
            Thread.sleep(THREAD_SECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private static void checkingAppoinment(ChromeDriver driver, String patientName,String panel) throws InterruptedException {


        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(WAIT_SECONDS));


        menuPanalClick(driver,panel);


        try {
            // Locate the row containing 'SharmaE'
            WebElement row = wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.xpath("//td[span[contains(text(),'" + patientName + "')]]/parent::tr")
            ));

            // Find the "Check In" button in the same row
            WebElement checkInButton = row.findElement(By.xpath(".//button[@title='Check In']"));

            // Wait and Click the button
            wait.until(ExpectedConditions.elementToBeClickable(checkInButton)).click();

            System.out.println("Successfully Checking Appointment");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }


    }



    private static void locationSelected(ChromeDriver driver) {
        //locations eelct
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(WAIT_SECONDS));
        WebElement locationDropdown = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//select[@title='Location']")));

// Wait for the option to be visible before selecting
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//select[@title='Location']//option[text()='Navaur branch']")));

        Select select = new Select(locationDropdown);
        select.selectByVisibleText("Navaur branch");



        WebDriverWait waitSubmit = new WebDriverWait(driver, Duration.ofSeconds(WAIT_SECONDS));
        WebElement proceedButton = waitSubmit.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[text()='Proceed Next']")));

        proceedButton.click();
    }

    private static void loggingFunction(ChromeDriver driver) throws InterruptedException {
        WebElement usernameField = driver.findElement(By.id("signin-email"));

        typeSlowly(usernameField, USER_NAME, 200);
        WebElement passwordField = driver.findElement(By.id("signin-password"));
        typeSlowly(passwordField, PASS_WORD, 200);

        WebElement loginButton = driver.findElement(By.cssSelector("button[type='submit']"));
        loginButton.click();

    }

    private static void createAppointment(ChromeDriver driver, String patientName, String admissionType, String doctorName, String scanType,String panel) throws InterruptedException {

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(WAIT_SECONDS));
        try {
            Thread.sleep(THREAD_SECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        try {
            Thread.sleep(THREAD_SECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        menuPanalClick(driver, panel);

        WebElement patientCodeInput = wait.until(
                ExpectedConditions.elementToBeClickable(By.name("patientCode"))
        );
        patientCodeInput.click();
        Thread.sleep(1000); // Ensure dropdown gets triggered

        patientCodeInput.sendKeys(Keys.BACK_SPACE); // Clear previous input
        Thread.sleep(500);
        patientCodeInput.sendKeys(patientName);


        List<WebElement> options = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath("//mat-option")));

        boolean found = false;
        for (WebElement option : options) {
            if (option.getText().contains(patientName)) {
                option.click();
                found = true;
                break;
            }
        }

        if (!found) {
            System.out.println("Patient name not found in dropdown.");
        }


        WebElement purposeDropdown = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("select[formcontrolname='purpose']")
        ));

        Select select = new Select(purposeDropdown);
        select.selectByVisibleText(admissionType);




        if(admissionType.equals("Scan"))
        {
            WebElement dropdown = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//select[@formcontrolname='scanType']")));
            Select selectScan = new Select(dropdown);
            selectScan.selectByVisibleText(scanType);
        }


        WebElement selectDoctorId = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("select[formcontrolname='doctorId']")
        ));

        Select selectDr = new Select(selectDoctorId);
        selectDr.selectByVisibleText(doctorName);


        WebElement saveButton = driver.findElement(By.id("saveNdCloseAp"));
        saveButton.click();

    }

    public static void typeSlowly(WebElement element, String text, int delayMillis) throws InterruptedException {
        for (char ch : text.toCharArray()) {
            element.sendKeys(String.valueOf(ch));
            Thread.sleep(delayMillis);
        }
    }

    public boolean isElementVisible(By locator, WebDriver driver) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
            wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }
    public static WebElement findPatientRowElement(WebDriver driver, WebDriverWait wait, String patientName) {
        int pageNumber = 1;

        while (true) {
            try {
                // Try finding the patient row
                WebElement row = wait.until(ExpectedConditions.presenceOfElementLocated(
                        By.xpath("//td[span[contains(text(),'" + patientName + "')]]/parent::tr")
                ));
                System.out.println("Patient found on page " + pageNumber);
                return row; // Return the WebElement if found
            } catch (TimeoutException e) {
                // Patient not found on this page, check for the next page
                WebElement nextPage = null;
                try {
                    nextPage = wait.until(ExpectedConditions.elementToBeClickable(
                            By.xpath("//li[@class='ng-star-inserted']/a[span[text()='" + (pageNumber + 1) + "']]")
                    ));
                } catch (TimeoutException ex) {
                    System.out.println("No more pages available. Patient not found.");
                    return null; // Patient not found in any page
                }

                // Click next page and wait for reload
                nextPage.click();
                pageNumber++;
                wait.until(ExpectedConditions.stalenessOf(nextPage)); // Ensure new page loads
            }
        }
    }

    public static WebElement findAndClickDropdownAndPrescription(String patientName, WebDriver driver, WebDriverWait wait) {
        WebElement row = null;

        while (true) {
            try {
                // Step 1: Find the row containing the patient name
                row = wait.until(ExpectedConditions.refreshed(
                        ExpectedConditions.presenceOfElementLocated(By.xpath("//td[span[contains(text(),'" + patientName + "')]]/parent::tr"))
                ));
                System.out.println("Patient row found.");

                // Step 2: Find and click the dropdown inside this row
                WebElement dropdownIcon = row.findElement(By.xpath(".//span[contains(@class,'ti-angle-double-down')]"));
                wait.until(ExpectedConditions.elementToBeClickable(dropdownIcon)).click();
                System.out.println("Dropdown icon clicked successfully.");

                // Step 3: Locate and click "Prescription" inside the same row

                Thread.sleep(1000);
                WebElement prescriptionOption = wait.until(ExpectedConditions.refreshed(
                        ExpectedConditions.presenceOfElementLocated(
                                By.xpath("//td[span[contains(text(),'" + patientName + "')]]/following-sibling::td//span[contains(text(),'Prescription')]")
                        )
                ));
                wait.until(ExpectedConditions.elementToBeClickable(prescriptionOption)).click();
                System.out.println("Clicked on Prescription option.");

                return row; // Return the WebElement after clicking the dropdown and Prescription

            } catch (StaleElementReferenceException e) {
                System.out.println("StaleElementReferenceException caught. Retrying...");
            } catch (TimeoutException e) {
                // Step 4: If patient row is not found, check if there is a next page button
                List<WebElement> nextPageButton = driver.findElements(By.xpath("//li[@class='ng-star-inserted']/a/span[text()='2']"));
                if (!nextPageButton.isEmpty()) {
                    System.out.println("Patient not found, navigating to the next page...");
                    nextPageButton.get(0).click();
                    wait.until(ExpectedConditions.stalenessOf(nextPageButton.get(0))); // Wait for page to reload
                } else {
                    System.out.println("Patient not found on any page.");
                    return null; // Return null if the patient is not found
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }




}