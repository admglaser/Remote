package selenium;

import static org.junit.Assert.*;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

public class GUITest {

	private static WebDriver driver;
	
	@BeforeClass
	public static void setup() {
		String property = System.getProperty("webdriver.chrome.driver");
		if (property == null || property.isEmpty()) {
			throw new RuntimeException("webdriver.chrome.driver must be set!");
		}
		driver = new ChromeDriver();
	}
	
	@Test
	public void openIndexPage() {
		driver.get("http://localhost:8080/remote/");
		WebElement h1 = driver.findElement(By.xpath("//*[@id=\"wrap\"]/div/h2"));
		WebElement id = driver.findElement(By.xpath("//*[@id=\"j_idt15\"]/div[1]/label"));
		WebElement password = driver.findElement(By.xpath("//*[@id=\"j_idt15\"]/div[2]/label"));
		
		assertEquals("Connect to device", h1.getText());	
		assertEquals("ID:", id.getText());
		assertEquals("Password:", password.getText());
	}
	
	@Test
	public void loginAndLogOutWithAdmin() {
		loginWithCredentials("admin", "admin");
		driver.findElement(By.xpath("//*[@id=\"bs-example-navbar-collapse-1\"]/ul/li/ul/li[2]/a"));
		logout(true);
	}

	@Test
	public void loginAndLogOutWithUser() {
		loginWithCredentials("user", "user");
		logout(false);
	}
	
	@Test
	public void registerWithNewUserAndLoginLogout() {
		String random = "user" + System.currentTimeMillis();
		registerWithCredentials(random, random);
		loginWithCredentials(random, random);
		logout(false);
	}

	private void registerWithCredentials(String username, String password) {
		driver.get("http://localhost:8080/remote/register");
		WebElement usernameTextField = driver.findElement(By.xpath("//*[@id=\"j_idt14\"]/div[1]/input"));
		usernameTextField.sendKeys(username);
		WebElement passwordTextField = driver.findElement(By.xpath("//*[@id=\"j_idt14\"]/div[2]/input"));
		passwordTextField.sendKeys(password);
		WebElement passwordConfirmTextField = driver.findElement(By.xpath("//*[@id=\"j_idt14\"]/div[3]/input"));
		passwordConfirmTextField.sendKeys(password);
		WebElement sendButton = driver.findElement(By.xpath("//*[@id=\"j_idt14:registerButton\"]"));
		sendButton.click();
		WebElement successMessage = driver.findElement(By.xpath("//*[@id=\"j_idt14\"]/div[4]/span"));
		assertEquals("Successfully registered.", successMessage.getText());
	}

	private void loginWithCredentials(String username, String password) {
		driver.get("http://localhost:8080/remote/");
		WebElement loginButtonInHeader = driver.findElement(By.xpath("//*[@id=\"bs-example-navbar-collapse-1\"]/ul/li[1]/a"));
		loginButtonInHeader.click();
		assertEquals("http://localhost:8080/remote/login", driver.getCurrentUrl());
		
		WebElement userNameTextField = driver.findElement(By.xpath("//*[@id=\"j_idt14\"]/div[1]/input"));
		userNameTextField.sendKeys(username);
		WebElement passwordTextField = driver.findElement(By.xpath("//*[@id=\"j_idt14\"]/div[2]/input"));
		passwordTextField.sendKeys(password);
		WebElement sendButton = driver.findElement(By.xpath("//*[@id=\"j_idt14:loginButton\"]"));
		sendButton.click();
		assertEquals("http://localhost:8080/remote/index", driver.getCurrentUrl());
		WebElement userLoggedInButtonInHeader = driver.findElement(By.xpath("//*[@id=\"bs-example-navbar-collapse-1\"]/ul/li/a"));
		assertEquals("Logged in as " + username, userLoggedInButtonInHeader.getText());
	}
	
	private void logout(boolean isAdmin) {
		WebElement userLoggedInButtonInHeader = driver.findElement(By.xpath("//*[@id=\"bs-example-navbar-collapse-1\"]/ul/li/a"));
		userLoggedInButtonInHeader.click();
		WebElement logoutButtonInMenu = null;
		if (isAdmin) {
			logoutButtonInMenu = driver.findElement(By.xpath("//*[@id=\"bs-example-navbar-collapse-1\"]/ul/li/ul/li[4]/a"));

		} else {
			logoutButtonInMenu = driver.findElement(By.xpath("//*[@id=\"bs-example-navbar-collapse-1\"]/ul/li/ul/li[3]/a"));
		}
		logoutButtonInMenu.click();
		assertEquals("http://localhost:8080/remote/index", driver.getCurrentUrl());
		WebElement loginButtonInHeader = driver.findElement(By.xpath("//*[@id=\"bs-example-navbar-collapse-1\"]/ul/li[1]/a"));
		assertEquals("Login", loginButtonInHeader.getText());
	}

	@AfterClass
	public static void tearDown() {
		driver.quit();
	}
	
}
