package hubspot;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.By.ByClassName;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import hubspot.tasks.Task;
import properties.Properties;

public class Selenium {
	static ChromeOptions options;
	static ChromeDriver driver;
	static WebDriverWait wdw;
	
	static boolean open = false;
	static ArrayList<Task> queue = new ArrayList<Task>();

	static String password = Properties.getPassword();
	static String username = Properties.getUsername();

	public static void abreHubspot() {
		new Thread(() -> abreHubspot(true)).start();
	}

	public static void abreHubspot(Boolean keepopen) {
		// Permite o "LEMBRE-ME"
		options = new ChromeOptions();
		options.addArguments("user-data-dir=C:\\Path");
        options.addArguments("--remote-allow-origins=*");
		driver = new ChromeDriver(options);
		wdw = new WebDriverWait(driver, 10);
		
		driver.get("https://app.hubspot.com/live-messages/23472314/inbox/");

		sleep(1000);

		dealWithLogInPage();

		open = true;

		//addContactToThread("3983241480","leticia.mata@rocky.ag",10);
		
//		mantÃ©m aberto e processa fila
		while (keepopen) {
			executeQueue();
		}
	}
	
	public static void addToQueue(Task task) {
		queue.add(task);
	}

	static void executeQueue() {
		if (queue.size() > 0) {
			System.out.println("Tarefa da fila COMEÇA_____________________________________________________________________________");
			try {
				queue.get(0).doTask();
				System.out.println("Tarefa da filma TERMINA____________________________________________________________________________");
			}catch (Exception e) {
				e.printStackTrace();
				System.out.println("Tarefa da filma FALHOU_____________________________________________________________________________");
			}
			queue.remove(0);
		}else {
			sleep(1000);
		}
	}
	
	public static void tryDisassociate() {
		try {
			wdw.until(ExpectedConditions.presenceOfElementLocated(ByClassName.className("iyWoFR")));
			Actions action = new Actions(driver);
			action.moveToElement(driver.findElement(ByClassName.className("iyWoFR"))).build().perform();
			sleep(100);
			driver.findElement(ByClassName.className("cUwHUG")).click();
			driver.findElement(ByText("Yes, disassociate")).click();
		}catch(Exception e) {
			e.printStackTrace();
		}
	}

	public static void addContactToThread(String threadid, String contactemail) throws Exception {
		System.out.println("A");
		
		openThread(threadid);
		
		tryDisassociate();
		
		wdw.until(ExpectedConditions.presenceOfElementLocated(ByText("+ Add Contact")));
		driver.findElement(ByText("+ Add Contact")).click();
		
		wdw.until(ExpectedConditions.presenceOfElementLocated(By.tagName("iframe")));
		
		//clica no formulÃ¡rio
		driver.switchTo().frame("object-builder-ui");
		clicaFormulario(1, contactemail);
		driver.switchTo().defaultContent();
		
		//clica no contato
		driver.switchTo().frame("object-builder-ui");
		clicaContato(1, contactemail);
		driver.switchTo().defaultContent();
		
		//clica em "salvar"
		driver.switchTo().frame("object-builder-ui");
		clicaSalvar(1);
		driver.switchTo().defaultContent();
	}

	public static void addContactToThread(String threadid, String contactemail, int n) {
		try {
			System.out.println("tentativa " + n);
			addContactToThread(threadid, contactemail);
		} catch(Exception e) {
			e.printStackTrace();
			if(n > 1) {
				addContactToThread(threadid, contactemail, n-1);
			}else {
				System.out.println("ASSOCIAR CONTATO FALHOU");
			}
		}
	}
	
	private static void clicaSalvar(int n) throws Exception {
		try {
			System.out.println("tryin' salvar");
			wdw.until(ExpectedConditions.presenceOfElementLocated(ByText("Save")));
			wdw.until(ExpectedConditions.elementToBeClickable(ByText("Save")));
			driver.findElement(ByText("Save")).click();
			driver.switchTo().defaultContent();
		} catch(Exception e) {
			e.printStackTrace();
			if(n > 1) {
				clicaSalvar(n-1);
			}else {
				throw new Exception("não conseguiu clicar salvar");
			}
		}
	}
	
	private static void clicaContato(int n, String contactemail) throws Exception {
		try {
			System.out.println("tryin' contato");
			wdw.until(ExpectedConditions.presenceOfElementLocated(ByText("("+contactemail+")")));
			driver.findElement(ByText("("+contactemail+")")).click();
			
//			List<WebElement> elements_email = driver.findElements(ByText(contactemail));
//			
//			for(int i = 0; i < elements_email.size(); i++) {
//				if(0 >= elements_email.get(i).findElement(By.xpath("../../..")).findElements(By.className("hRRJUY")).size()) {
//					elements_email.remove(i);
//					i--;
//				}
//			}
//			
//			if(elements_email.isEmpty()) {
//				System.out.println("LISTA VAZIA");
//				throw new Exception("LISTA VAZIA");
//			}else {
//				elements_email.get(0).click();
//			}
			
		} catch(Exception e) {
			e.printStackTrace();
			if(n > 1) {
				clicaContato(n-1, contactemail);
			}else {
				throw new Exception("não conseguiu clicar contato");
			}
		}
	}
	
	private static void clicaFormulario(int n, String contactemail) throws Exception {
		try {
			System.out.println("tryin' formulario");
			wdw.until(ExpectedConditions.presenceOfElementLocated(By.id("UIFormControl-7")));
			WebElement findContactForm = driver.findElement(By.id("UIFormControl-7")); 
			findContactForm.click();
			findContactForm.sendKeys("("+contactemail+")");
		} catch(Exception e) {
			e.printStackTrace();
			if(n > 1) {
				clicaFormulario(n-1, contactemail);
			}else {
				throw new Exception("não conseguiu clicar formulário");
			}
		}
	}

	static WebElement findButton(By by) {
		List<WebElement> matches = driver.findElements(by);
		for (int i = 0; i < matches.size(); i++) {
			System.out.println("class = " + matches.get(i));
			if (matches.get(i).getClass().toString().toLowerCase().contains("button"))
				return matches.get(i);
		}
		return null;
	}

	static void openThread(String threadid) {
		driver.get("https://app.hubspot.com/live-messages/23472314/inbox/" + threadid);
	}

	static void sleep(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	static void dealWithLogInPage() {
		if (findElement(By.id("username")) != null) {
			login();
		}
	}

	static void login() {
		findElement(By.id("username")).sendKeys(username);
		findElement(By.id("password")).sendKeys(password);
		findElement(By.id("password")).sendKeys(Keys.ENTER);
	}

	static WebElement findElement(By locator) {
		try {
			return driver.findElement(locator);
		} catch (NoSuchElementException e) {
			return null;
		}
	}

	private static By ByText(String text) {
		return By.xpath ("//*[contains(text(),'"+text+"')]");
	}

}
