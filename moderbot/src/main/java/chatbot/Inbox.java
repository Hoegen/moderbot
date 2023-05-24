package chatbot;

import java.time.OffsetDateTime;
import java.util.ArrayList;

import org.json.JSONArray;

public class Inbox {
	private String id;
	private ArrayList<Conversation> inbox;
	
	Inbox(String id){
		this.id = id;
		inbox = new ArrayList<Conversation>();
	}

	public void processAndUpdate() {
		for(int i = 0; i < inbox.size(); i ++) {
			//remove conversa se ela tiver 23h59m
			if(inbox.get(i).getLatestMessageTimestamp().plusHours(23).plusMinutes(59).isBefore(OffsetDateTime.now())) {
				inbox.remove(i);
				i--;
				continue;
			}
			inbox.get(i).update();
			inbox.get(i).process();
		}
	}

	public void processAndUpdate(JSONArray threads) {
		boolean samethread = false;
		boolean newermessg = false;
		OffsetDateTime latestMessageTimestamp;
		
		for(int i = 0; i < inbox.size(); i ++) {
			for(int j = 0; j < threads.length(); j++) {
				latestMessageTimestamp = OffsetDateTime.parse(threads.getJSONObject(j).getString("latestMessageTimestamp"));
				
				samethread = inbox.get(i).equals(threads.getJSONObject(j));
				newermessg = latestMessageTimestamp.isAfter(inbox.get(i).getLatestMessageTimestamp());
				
				//Se a conversa do JSON equivale à conversa do programa java e ela tem mensagem mais recente
				if(samethread && newermessg) {
					inbox.get(i).update();
					inbox.get(i).process();
				}
			}
		}
	}
	
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public ArrayList<Conversation> getInbox() {
		return inbox;
	}
	public void setInbox(ArrayList<Conversation> inbox) {
		this.inbox = inbox;
	}
	public void addConversation(Conversation conversation) {
		String threadId = conversation.getThreadid();
		for(int i = 0; i < inbox.size(); i++) {
			if(threadId.equals(inbox.get(i).getThreadid())) {
				return;
			}
		}
		
		System.out.println("CONVERSATION ADDED");
		this.inbox.add(conversation);
	}
	
	
}
