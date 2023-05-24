package chatbot;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import hubspot.Api;

public class Inboxes {
	private ArrayList<Inbox> inboxes;

	Inboxes(){
		inboxes = processJSONArray(Api.getThreads());
	}
	
	
	void update() {
		JSONArray threads = Api.getThreads();
		if(threads == null) {
			System.out.println("SEM CONEXÃO");
			return;
		}else if(threads.length() == 0) {
			System.out.println("SEM THREADS");
			return;
		}
		System.out.println("updating... ... ...\n");
//		System.out.println(threads.length() + " > " + totalConversations() + "?");
		if(threads.length() > totalConversations()) {
			//System.out.println("YES; UPDATING\n\n");
			updateInboxes(threads);
		}else {
			//System.out.println(" NO; IGNORING\n\n");
		}
		
		for(int i = 0; i < inboxes.size(); i ++) {
			inboxes.get(i).processAndUpdate(threads);
		}
	}
	
	
	void updateInboxes(JSONArray threads) {

		Boolean inboxidfound = false;
		String inboxId = "";
		
		for(int i = 0; i < threads.length(); i++) {
			inboxidfound = false;
			inboxId = threads.getJSONObject(i).getString("inboxId");
			
			for(int j = 0; j < inboxes.size(); j++) { 
				if(inboxId.equals(inboxes.get(j).getId())) {
					inboxidfound = true;
					
					inboxes.get(j).addConversation(new Conversation(threads.getJSONObject(i).getString("id"), inboxId));
					break;
				}
			}
			if(!inboxidfound && !inboxId.equals("")) {
				inboxes.add(new Inbox(inboxId));
				inboxes.get(inboxes.size()-1).addConversation(new Conversation(threads.getJSONObject(i).getString("id"), inboxId));
			}
		}
	}
	
	
	ArrayList<Inbox> processJSONArray(JSONArray array){
		ArrayList<Inbox> list = new ArrayList<Inbox>();
		String inboxid;
		JSONObject conversation;
		Boolean foundinbox = false;
		if(array == null) array = new JSONArray();
		
		for(int i = 0; i < array.length(); i++) {
			foundinbox = false;
			conversation = array.getJSONObject(i);
			inboxid = conversation.getString("inboxId");
			
			
			//Procura um inbox jÃ¡ existente com o id do elemento atual
			for(int j = 0; j < list.size(); j++) {
				if(list.get(j).getId().equals(inboxid)) {
					list.get(j).addConversation(new Conversation(conversation.getString("id"), inboxid));
					foundinbox = true;
					break;
				}
			}
			if(!foundinbox) {
				list.add(new Inbox(inboxid));
				list.get(list.size()-1).addConversation(new Conversation(conversation.getString("id"), inboxid));
			}
		}
		return list;
	}
	
	int totalConversations() {
		int total = 0;
		for(int i = 0; i < inboxes.size(); i++) {
			total += inboxes.get(i).getInbox().size();
		}
		return total;
	}
	
	public ArrayList<Inbox> getInboxes() {
		return inboxes;
	}

	public void setInboxes(ArrayList<Inbox> inboxes) {
		this.inboxes = inboxes;
	}
	
}
