package selenium;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

public class UITest {

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
		WebElement h1 = driver.findElement(By.xpath("//*[@id=\"wrap\"]/div/h1"));
		Assert.assertEquals("Welcome to Remote!", h1.getText());
	}
	
	@Test
	public void openDevicesPageWhenNotLoggedIn() {
		driver.get("http://localhost:8080/remote/devices");
		Assert.assertEquals("http://localhost:8080/remote/login", driver.getCurrentUrl());
	}
	
	@Test
	public void loginAndLogOutWithAdmin() {
		loginWithCredentials("admin", "admin");
		driver.findElement(By.xpath("//*[@id=\"bs-example-navbar-collapse-1\"]/ul[2]/li[1]/a"));
		logout();
	}

	@Test
	public void loginAndLogOutWithUser() {
		loginWithCredentials("user", "user");
		logout();
	}
	
	@Test
	public void registerWithNewUserAndLoginLogout() {
		String random = "user" + System.currentTimeMillis();
		registerWithCredentials(random, random);
		loginWithCredentials(random, random);
		logout();
	}

	private void registerWithCredentials(String username, String password) {
		driver.get("http://localhost:8080/remote/register");
		WebElement usernameTextField = driver.findElement(By.xpath("//*[@id=\"j_idt15\"]/div[1]/input"));
		usernameTextField.sendKeys(username);
		WebElement passwordTextField = driver.findElement(By.xpath("//*[@id=\"j_idt15\"]/div[2]/input"));
		passwordTextField.sendKeys(password);
		WebElement passwordConfirmTextField = driver.findElement(By.xpath("//*[@id=\"j_idt15\"]/div[3]/input"));
		passwordConfirmTextField.sendKeys(password);
		WebElement sendButton = driver.findElement(By.xpath("//*[@id=\"j_idt15:registerButton\"]"));
		sendButton.click();
		WebElement successMessage = driver.findElement(By.xpath("//*[@id=\"j_idt15\"]/div[4]/span"));
		Assert.assertEquals("Successfully registered.", successMessage.getText());
	}

	private void loginWithCredentials(String username, String password) {
		driver.get("http://localhost:8080/remote/");
		WebElement loginButtonInHeader = driver.findElement(By.xpath("//*[@id=\"bs-example-navbar-collapse-1\"]/ul[2]/li[1]/a"));
		loginButtonInHeader.click();
		Assert.assertEquals("http://localhost:8080/remote/login", driver.getCurrentUrl());
		
		WebElement userNameTextField = driver.findElement(By.xpath("//*[@id=\"j_idt15\"]/div[1]/input"));
		userNameTextField.sendKeys(username);
		WebElement passwordTextField = driver.findElement(By.xpath("//*[@id=\"j_idt15\"]/div[2]/input"));
		passwordTextField.sendKeys(password);
		WebElement sendButton = driver.findElement(By.xpath("//*[@id=\"j_idt15:loginButton\"]"));
		sendButton.click();
		Assert.assertEquals("http://localhost:8080/remote/devices", driver.getCurrentUrl());
	}
	
	private void logout() {
		WebElement userLoggedInButtonInHeader = driver.findElement(By.xpath("//*[@id=\"bs-example-navbar-collapse-1\"]/ul[2]/li[last()]/a"));
		userLoggedInButtonInHeader.click();
		WebElement logoutButtonInMenu = driver.findElement(By.xpath("//*[@id=\"bs-example-navbar-collapse-1\"]/ul[2]/li[last()]/ul/li[3]/a"));
		logoutButtonInMenu.click();
		Assert.assertEquals("http://localhost:8080/remote/index", driver.getCurrentUrl());
	}

	@AfterClass
	public static void tearDown() {
		driver.quit();
	}
	
}
