package chatbot;

import java.time.OffsetDateTime;

import org.json.JSONObject;

import chatbot.statemachine.*;
import hubspot.Api;

public class Conversation {

	// FUNCIONAMENTO
	JSONObject lastmessage;
	private OffsetDateTime latestMessageTimestamp;

	private final String threadid;
	private final String inboxid;
	private String contactid = "";

	private int status = 0;
	private State state;
	private int laststatus = 0;
	private int setor = 0;
	private boolean incoming;

	// INFORMAÃ‡Ã•ES
	private String cel = "";
	private String nome = "";
	private String email = "";
	private String interesse = "";

	public Conversation(String threadid, String inboxid) {
		this.threadid = threadid;
		this.inboxid = inboxid;
		update();
		setCel();
		state = new Start(this);
		if(incoming) state.depart();
	}

	public void sndMsg(String txt) {
		Api.sendMsg(lastmessage, threadid, txt);
	}

	void process() {
		if(incoming) {
			state.depart();
			System.out.println(this.state.getClass() + " = STATE");
			while(!state.reads()) {
				process();
			}
		}
		//StateMachine.stateIteration(this);
	}
	
	public void patchContact() {
		String[] arguments = {
				"estado_maquina," + status,
				"hs_whatsapp_phone_number," + cel,
				"qual_setor_," + setor,
				"link_de_conversa,"+ "https://app.hubspot.com/live-messages/23472314/inbox/" + threadid
		};
		
		JSONObject contact = new JSONObject();
		JSONObject properties = new JSONObject();
		String key = "";
		String value = "";
		
		for(int i = 0; i < properties.length(); i++) {
			if(arguments[i].split(",").length != 2) {
				continue;
			}
			key   = arguments[i].split(",")[0];
			value = arguments[i].split(",")[1];
			properties.put(key, value);
		}
		
		contact.put("properties", properties);
		
		System.out.println("conversation71");
		String response = Api.apiPatchJson(Api.getApiurl() + "crm/v3/objects/contacts" + contactid, contact.toString().getBytes());
		System.out.println("conversation73");
		System.out.println(response);
		System.out.println("conversation75");
	}

	public String putContact() {
		JSONObject contact = new JSONObject();
		JSONObject properties = new JSONObject();
		String antenome = this.nome.split(" ")[0];
		String sobrnome = this.nome.replace(antenome, "");
		
		properties.put("firstname", antenome);
		properties.put("lastname", sobrnome);
		properties.put("email", this.email);
		properties.put("hs_whatsapp_phone_number", this.cel);
		properties.put("qual_setor_", this.setor);
		properties.put("link_de_conversa", "https://app.hubspot.com/live-messages/23472314/inbox/" + threadid);
		
		contact.put("properties", properties);
		
		System.out.println("conversation93");
		String response = Api.apiPostJson(Api.getApiurl() + "crm/v3/objects/contacts", contact.toString().getBytes());
		System.out.println("conversation95");
		if(response.equals("erro")) {
			System.out.println(response);
			return response;
		}
		
		try {
			contactid = new JSONObject(response).getString("id");
			setContactid(contactid);
			System.out.println("RESPONSE:");
			System.out.println(new JSONObject(response).toString(4));
			
		}catch(Exception e) {
			e.printStackTrace();
			
		}
		return response;
	}

	void update() {
		try {
			lastmessage = Api.lastMessage(threadid);
			incoming = lastmessage.getString("direction").equals("INCOMING");
			latestMessageTimestamp = OffsetDateTime.parse(lastmessage.getString("createdAt"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public boolean equals(JSONObject thread) {
		return thread.getString("id").equals(threadid);
	}

	// GETTERS E SETTERS - Deixai toda a esperanÃ§a, VÃ³s que entrais!

	public String getThreadid() {
		return threadid;
	}

	public int getStatus() {
		return status;
	}

	public int getLastStatus() {
		return laststatus;
	}

	public void setStatus(int status) {
		laststatus = this.status;
		this.status = status;
	}

	public JSONObject getLastmessage() {
		return lastmessage;
	}

	public String getMsg() {
		return lastmessage.getString("text");
	}

	public void setLastmessage(JSONObject lastmessage) {
		this.lastmessage = lastmessage;
	}

	public boolean isIncoming() {
		return incoming;
	}

	public void setIncoming(boolean incoming) {
		this.incoming = incoming;
	}

	public OffsetDateTime getLatestMessageTimestamp() {
		return latestMessageTimestamp;
	}

	public void setLatestMessageTimestamp(OffsetDateTime latestMessageTimestamp) {
		this.latestMessageTimestamp = latestMessageTimestamp;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome.replaceAll("[^A-z äãáàçéèíóöõúü]", "");
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getInteresse() {
		return interesse;
	}

	public void setInteresse(String interesse) {
		this.interesse = interesse;
	}

	public String getInboxid() {
		return inboxid;
	}

	public int getSetor() {
		return setor;
	}

	public void setSetor(int setor) {
		this.setor = setor;
	}

	public String getCel() {
		return cel;
	}

	public void setCel(String cel) {
		this.cel = cel;
	}

	public void setCel() {
		JSONObject deliveryIdentifier = new JSONObject();
		try {
			if (lastmessage.getString("direction").equals("OUTGOING")) {
				deliveryIdentifier = lastmessage.getJSONArray("recipients").getJSONObject(0)
						.getJSONArray("deliveryIdentifiers").getJSONObject(0);
			} else {
				deliveryIdentifier = lastmessage.getJSONArray("senders").getJSONObject(0)
						.getJSONObject("deliveryIdentifier");
			}
			if (deliveryIdentifier.getString("type").equals("HS_PHONE_NUMBER")) {
				setCel(deliveryIdentifier.getString("value"));
			}
		} catch (Exception e) {
			this.cel = "erro";
			e.printStackTrace();
		}
	}

	public String getContactid() {
		return contactid;
	}

	public void setContactid(String contactid) {
		this.contactid = contactid;
	}

	public State getState() {
		return state;
	}

	public void setState(State state) {
		System.out.println("putting: " + state.getClass());
		this.state = state;
	}

}
