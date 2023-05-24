package hubspot;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.OffsetDateTime;

import org.apache.http.HttpHeaders;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import properties.Properties;

public class Api {
	private static String token = Properties.getHbstoken();
	private static String apiurl = "https://api.hubapi.com/";
	private static final int allowedCallsPerSecond = 150 -1;
	private static final long AllowedMillisBetweenCalls = 1000/allowedCallsPerSecond; 
	private static OffsetDateTime lastCallTime = OffsetDateTime.now();
	
	public static String apiGet(String link) {
		System.out.println("Calling GET on " + link);
		setLastCallTime();
		try {
			URL url = new URL(link);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestProperty("Authorization", "Bearer " + token);
			InputStream is = conn.getInputStream();
			return new String(is.readAllBytes(), StandardCharsets.UTF_8);
			
		}catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static String apiPostJson(String link, byte[] content) {
		System.out.println("Calling POST on "+ link);
		System.out.println("Content:\n" + new String(content));
		setLastCallTime();
		try {
			URL url = new URL(link);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestProperty("Authorization", "Bearer " + token);
			conn.setRequestMethod("POST");
			conn.setDoOutput(true);
			conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
			OutputStream os = conn.getOutputStream();
			
			os.write(content);
			
			String retorno = new String( conn.getInputStream().readAllBytes());
			//System.out.println( new JSONObject(retorno).toString(4));
			return retorno;
			
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "erro";
	}
	
	public static String apiPatchJson(String link, byte[] content) {
		try {
			HttpPatch patch = new HttpPatch(link);
			String jasao = new String(content);
			StringEntity json = new StringEntity(jasao);
			
			patch.setEntity(json);
			patch.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);
			patch.setHeader("accept", "application/json");
			patch.setHeader("content-type", "application/json");
			
			System.out.println("BBBBBBBBBBBBBB\n" + EntityUtils.toString(patch.getEntity()));
			
			CloseableHttpClient httpClient = HttpClients.createDefault();
			
			setLastCallTime();
			
			CloseableHttpResponse response = httpClient.execute(patch);
			
			
			System.out.println("AAAAAAAAAA\n" + response + "\n" + EntityUtils.toString(response.getEntity()));
			
			
			
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public static JSONObject apiSearchContactByEmail(String email) {
		JSONObject json = new JSONObject();
		JSONObject filterGroups = new JSONObject();
		JSONObject filters = new JSONObject();
		filters.put("propertyName", "email");
		filters.put("operator", "EQ");
		filters.put("value", email);
		filterGroups.append("filters", filters);
		json.append("filterGroups", filterGroups);
		
		return new JSONObject( Api.apiPostJson("https://api.hubspot.com/crm/v3/objects/contacts/search", json.toString().getBytes()));
	}
	
	public static JSONArray getThreads(){
		String input = apiGet(getApiurl() + "conversations/v3/conversations/threads/");
		if(input == null) return null;
		
		JSONArray threads = null;
		JSONTokener tokener = new JSONTokener(input);
		JSONObject object = new JSONObject(tokener);
		
		threads = object.getJSONArray("results");
		
		for(int i = 0; i < threads.length(); i++) {
			//se fizer mais do que 23h58m da última mensagem
			if(OffsetDateTime.parse(threads.getJSONObject(i).getString("latestMessageTimestamp")).plusMinutes(23*60 + 58).isBefore(OffsetDateTime.now()) ) {
				threads.remove(i);
				i--;
			}
		}		
		return threads;
	}
	
	
	static JSONArray getMessages(String threadid){
		String input = apiGet(getApiurl() + "conversations/v3/conversations/threads/" + threadid + "/messages");
		
		JSONArray messages = null;
		JSONTokener tokener = new JSONTokener(input);
		JSONObject object = new JSONObject(tokener);
		
		messages = object.getJSONArray("results");
		
		//filtra mensagens
		for(int i = 0; i < messages.length(); i++) {
			if(messages.getJSONObject(i).getString("type").equals("MESSAGE")) {
				
			}else {
				messages.remove(i);
				i--;
			}
		}
		return messages;
	}
	
	
	public static JSONObject lastMessage(String threadid) {
		JSONArray messages = getMessages(threadid);
		if(messages.length() < 1) return null;
		
		OffsetDateTime time_of_last_message = OffsetDateTime.parse(messages.getJSONObject(0).getString("createdAt"));
		OffsetDateTime current_message_time = null;
		int index_of_last_message = 0;
		
		for(int i = 1; i < messages.length(); i++) {
			current_message_time = OffsetDateTime.parse(messages.getJSONObject(i).getString("createdAt"));
			
			if(time_of_last_message.isBefore(current_message_time)) {
				index_of_last_message = i;
				time_of_last_message = current_message_time;
			}
		}
		
		return messages.getJSONObject(index_of_last_message);
	}
	
	
	static void sendMsg(String threadid, String text) {
		JSONObject lastmessage = lastMessage(threadid);
		sendMsg(lastmessage, threadid, text);
	}
	
	
	public static void sendMsg(JSONObject lastmessage, String threadid, String text) {
		JSONObject json = new JSONObject();
		
		
		//Ler documentação, completando os parâmetros, em https://developers.hubspot.com/docs/api/conversations/conversations
		
		json.accumulate("type", "MESSAGE");
		json.accumulate("text", text);
		
		//system.out.println("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\n" + getRecipient(lastmessage).toString(4) + "\nAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
		
		json.accumulate("senderActorId", senderActorId(lastmessage));
		json.append("recipients", getRecipient(lastmessage).get(0));
		json.accumulate("channelId", lastmessage.getString("channelId"));
		json.accumulate("channelAccountId", lastmessage.getString("channelAccountId"));
		json.accumulate("subject", "");
		
		apiPostJson(getApiurl() + "conversations/v3/conversations/threads/" + threadid + "/messages", json.toString().getBytes(StandardCharsets.UTF_8));
		//String response = 
		//System.out.println(response);
	}
	
	
	private static JSONArray getRecipient(JSONObject message) {
		JSONArray recipients = null;
		
		if(message.getString("direction").equals("INCOMING")) {
			recipients = message.getJSONArray("senders");
			//system.out.println(recipients);
		}else if(message.getString("direction").equals("OUTGOING")) {
			recipients = message.getJSONArray("recipients");
			//system.out.println(recipients);
		}
		
		if(recipients.getJSONObject(0).has("deliveryIdentifier")){
			String type = recipients.getJSONObject(0).getJSONObject("deliveryIdentifier").getString("type");
			String value = recipients.getJSONObject(0).getJSONObject("deliveryIdentifier").getString("value");
			JSONObject pair = new JSONObject();
			pair.put("type", type);
			pair.put("value", value);
			
			recipients.getJSONObject(0).remove("deliveryIdentifier");
			recipients.getJSONObject(0).put("deliveryIdentifiers", new JSONArray().put(pair));
		}
		
		return recipients;
	}
	
	
	private static String senderActorId(JSONObject message) {
		if(message.getString("direction").equals("OUTGOING")) {
			//system.out.println(message.getJSONArray("senders").getJSONObject(0).getString("actorId"));
			return message.getJSONArray("senders").getJSONObject(0).getString("actorId");
		}else if(message.getString("direction").equals("INCOMING")) {
			//system.out.println("VALOR MÍSTICO: CORRIGIR");
			return "A-49610131";
		}
		return null;
	}

	public static String getApiurl() {
		return apiurl;
	}

	public static void setApiurl(String apiurl) {
		Api.apiurl = apiurl;
	}
	
	public static void setLastCallTime() {
		long timeSpent = Duration.between(lastCallTime, OffsetDateTime.now()).toMillis();
		
		if(timeSpent < AllowedMillisBetweenCalls) {
			try {
				Thread.sleep(AllowedMillisBetweenCalls - timeSpent);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		lastCallTime = OffsetDateTime.now();
	}
	
}