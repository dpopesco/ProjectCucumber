package stepDefinitions;

import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import pageObjects.AddCustomerPage;
import pageObjects.LoginPage;
import pageObjects.SearchCustomerPage;

import java.io.FileInputStream;
import java.io.IOException;
import java.time.Duration;
import java.util.Properties;

public class Steps extends BaseClass {

    @Before
    public void setup() throws IOException {

        //Reading properties
        configProperties = new Properties();
        FileInputStream configPropFile = new FileInputStream("config.properties");
        configProperties.load(configPropFile);

        //String localDir = System.getProperty("user.dir");
        //System.setProperty("webdriver.chrome.driver", localDir + "/chromedriver.exe");
        logger = Logger.getLogger("CucumberProject"); //added logger
        PropertyConfigurator.configure("log4j.properties");

        String browser = configProperties.getProperty("browser");

        if (browser.equals("chrome")) {
            System.setProperty("webdriver.chrome.driver", configProperties.getProperty("chromepath"));
            driver = new ChromeDriver();
        } else if (browser.equals("firefox")) {
            System.setProperty("webdriver.gecko.driver", configProperties.getProperty("firefoxpath"));
            driver = new FirefoxDriver();
        }

        logger.info("****Launching browser****");
    }

    @Given("User Launch Chrome browser")
    public void user_launch_chrome_browser() {
        lp = new LoginPage(driver);
    }

    @When("User opens URL {string}")
    public void user_opens_url(String url) {
        logger.info("****Opening url****");
        driver.get(url);
        driver.manage().window().maximize();
    }

    @When("User enters Email as {string} and Password as {string}")
    public void user_enters_email_as_and_password_as(String email, String password) {
        logger.info("****Providing login details****");
        lp.setUserName(email);
        lp.setPassword(password);
    }

    @When("Click on Login")
    public void click_on_login() {
        logger.info("****Started login****");
        lp.clickLogin();
    }

    @Then("Page Title should be {string}")
    public void page_title_should_be(String title) {
        if (driver.getPageSource().contains("Login was unsuccessful")) {
            driver.close();
            logger.info("****Login failed****");
            Assert.assertTrue(false);
        } else {
            logger.info("****Login passed****");
            Assert.assertEquals(title, driver.getTitle());
        }

    }

    @When("User click on Log out link")
    public void user_click_on_log_out_link() {
        lp.clickLogout();
        wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("Email")));
    }

    @Then("close browser")
    public void close_browser() {
        logger.info("****Closing browser****");
        driver.close();
    }

    //Customer feature step definitions

    @Then("User can view Dashboard")
    public void user_can_view_dashboard() {
        addCustomer = new AddCustomerPage(driver);
        Assert.assertEquals("Dashboard / nopCommerce administration", addCustomer.getPageTitle());
    }

    @When("User click on customers Menu")
    public void user_click_on_customers_menu() throws InterruptedException {
        Thread.sleep(3000);
        addCustomer.clickOnCustomersMenu();
    }

    @When("click on customers Menu Item")
    public void click_on_customers_menu_item() throws InterruptedException {
        Thread.sleep(2000);
        addCustomer.clickOnCustomersMenuItem();
    }

    @When("click on Add new button")
    public void click_on_add_new_button() throws InterruptedException {
        addCustomer.clickOnAddNew();
        Thread.sleep(3000);
    }

    @Then("User can view Add new customer page")
    public void user_can_view_add_new_customer_page() {
        Assert.assertEquals("Add a new customer / nopCommerce administration", addCustomer.getPageTitle());
    }

    @When("User enter customer info")
    public void user_enter_customer_info() throws InterruptedException {
        logger.info("****Adding a new customer****");
        String email = randomString() + "@gmail.com";
        addCustomer.setEmail(email);
        addCustomer.setPassword("Pass123!");
        addCustomer.setFirstName("Dora");
        addCustomer.setLastName("Marin");
        addCustomer.setGender("Female");
        addCustomer.setDateOfBirth("12/17/1997");
        addCustomer.setCompanyName("Business");
        //Registered - default
        //The customer cannot be in both 'Guests' and 'Registered' customer roles
        //Add the customer to 'Guests' or 'Registered' customer role
        addCustomer.setCustomerRoles("Guests");
        Thread.sleep(2000);
        addCustomer.setManagerOfVendor("Vendor 2");
        addCustomer.setAdminComment("This is for testing.");
    }

    @When("click on Save button")
    public void click_on_save_button() throws InterruptedException {
        logger.info("****Saving customer data****");
        addCustomer.clickOnSave();
        Thread.sleep(2000);
    }

    @Then("User can view confirmation message {string}")
    public void user_can_view_confirmation_message(String msg) {
        Assert.assertTrue(driver.findElement(By.tagName("body")).getText()
                .contains("The new customer has been added successfully"));
    }

    //steps for searching a customer using email id
    @When("Enter customer Email")
    public void enter_customer_email() {
        logger.info("****Searching customer by email****");
        searchCustomer = new SearchCustomerPage(driver);
        searchCustomer.setEmail("victoria_victoria@nopCommerce.com");
    }

    @When("Click on search button")
    public void click_on_search_button() throws InterruptedException {
        searchCustomer.clickSearchButton();
        Thread.sleep(3000);
    }

    @Then("User should find Email in the Search table")
    public void user_should_find_email_in_the_search_table() {
        boolean status = searchCustomer.searchCustomerByEmail("victoria_victoria@nopCommerce.com");
        Assert.assertTrue(status);
    }

    //search steps for First Name and Last Name
    @When("Enter customer FirstName")
    public void enter_customer_first_name() {
        logger.info("****Searching customer by name****");
        searchCustomer = new SearchCustomerPage(driver);
        searchCustomer.setFirstName("Victoria");
    }

    @When("Enter customer LastName")
    public void enter_customer_last_name() {
        searchCustomer.setLastName("Terces");
    }

    @Then("User should find Name in the Search table")
    public void user_should_find_name_in_the_search_table() {
        boolean status = searchCustomer.searchCustomerByName("Victoria Terces");
        Assert.assertTrue(status);
    }
}



