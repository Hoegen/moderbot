package hubspot.tasks;

import java.time.OffsetDateTime;

import hubspot.Selenium;

public class AddContactToThread implements Task {
	String threadId;
	String contactEmail;
	static final int TEMPO_DE_ESPERA = 5;
	int n;
	OffsetDateTime timecreated;
	
	public AddContactToThread(String threadId, String contactEmail){
		this.threadId = threadId;
		this.contactEmail = contactEmail;
		n = 0;
		this.timecreated = OffsetDateTime.now();
	}

	public AddContactToThread(String threadId, String contactEmail, int n){
		this.threadId = threadId;
		this.contactEmail = contactEmail;
		this.n = n;
		this.timecreated = OffsetDateTime.now();
		}

	@Override
	public void doTask() {
		if(timecreated.plusSeconds(TEMPO_DE_ESPERA).isAfter(OffsetDateTime.now()))
			try {
				Thread.sleep(TEMPO_DE_ESPERA*1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
		if(n < 1) {
			Selenium.addContactToThread(threadId, contactEmail, 0);
		}else {
			Selenium.addContactToThread(threadId, contactEmail, n);
		}
	}
	
}
