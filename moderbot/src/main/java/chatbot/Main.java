package chatbot;

import java.time.OffsetDateTime;

import org.json.JSONObject;

import hubspot.Api;
import hubspot.Selenium;
import properties.Properties;

public class Main {
	public static void main(String []args) {
		
		new Properties();
		
		Selenium.abreHubspot();
		
		bot();
		
	}
	
	public static void testaPostURL(String url, byte[] content) {
		String stringed = Api.apiPostJson(url, content);
		System.out.println(stringed);
		JSONObject jsoned = new JSONObject(stringed);
		
		System.out.println(jsoned.toString(4));
	}
	
	public static void testaGetURL(String url) {
		String stringed = Api.apiGet(url);
		System.out.println(stringed);
		JSONObject jsoned = new JSONObject(stringed);
		
		System.out.println(jsoned.toString(4));
	}
	
	public static void bot() {
		OffsetDateTime now = OffsetDateTime.now();
		OffsetDateTime then = now;
		
		Inboxes inboxes = new Inboxes();
		
		while(true) {
			//atualiza aproximadamente a cada 2 segundos
			now = OffsetDateTime.now();
			if(now.minusSeconds(2).isBefore(then)) continue;
			then = now;
			
			try {
				
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			inboxes.update();
			System.out.println("Nº D INBX: " + inboxes.getInboxes().size());
			System.out.println("CONVERSAS: " + inboxes.totalConversations());
		}
	}
}
