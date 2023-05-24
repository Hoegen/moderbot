package chatbot.statemachine;

import org.json.JSONObject;

import chatbot.Conversation;
import hubspot.Api;
import hubspot.Selenium;
import hubspot.tasks.AddContactToThread;

public class ConfirmaDados implements State {
	final static String stateName = "confirmaDados";
	private final Conversation c;
	
	public ConfirmaDados(Conversation c) {
		this.c = c;
		arrive();
	}

	@Override
	public void arrive() {
		validaDados(c);
		
		c.sndMsg("Certo! Suas informações são:\n" 
		+ "\nNome:        " + c.getNome() 
		+ "\nEmail:         " + c.getEmail()
		+ "\nWhatsApp:     " + c.getCel()
		+ (c.getInteresse().equals("") ? "" : ("\nInteresse:    " + c.getInteresse())) 
		+ "\nCorreto?"
		+ "\n*1* - Sim" + "\n*2* - Não");
	}

	@Override
	public void depart() {
		int simounao = simOuNao(c);
		switch(simounao) {
			case 1:
				//se o contato nÃ£o estiver registrado, registrar contato
				if(c.getContactid().equals("")) {
					if(c.putContact().equals("erro")) {
						c.setState(new Erro(c));
						return;
					};
				}else {//caso contrÃ¡rio, atualiza o contato
					c.patchContact();
				}
				//associa a conversa com o contato no hubspot
				Selenium.addToQueue(new AddContactToThread(c.getThreadid(), c.getEmail(), 10));
				
				c.setState(new EncaminhaAtendente(c));
				break;
				
			case 2:
				c.sndMsg("Ok! Vamos recomeçar então");
				c.setState(new BoasVindas(c));
				break;
				
			default:
				State.opcaoInvalida(c);
				break;
		} 
	}

	@Override
	public String stateName() {
		return stateName;
	}
	
	private static boolean validaDados(Conversation c) {
		JSONObject dados = Api.apiSearchContactByEmail(c.getEmail());
		if (dados.getInt("total") > 0) {
			dados = dados.getJSONArray("results").getJSONObject(0);
			c.sndMsg("Encontrei seu email aqui na nossa lista de contatos.");
			c.setNome(dados.getJSONObject("properties").getString("firstname"));
			c.setContactid(dados.getString("id"));
			return true;
		} else {
			return false;
		}
	}
	
	private static int simOuNao(Conversation c) {
		String mensagem = c.getMsg();
		if (	   mensagem.toLowerCase().contains("sim")  || mensagem.toLowerCase().contains("correto")
				|| mensagem.toLowerCase().contains("isso") || mensagem.toLowerCase().contains("aham")
				|| mensagem.toLowerCase().contains("ss")   || mensagem.toLowerCase().contains("ok")
				|| mensagem.replaceAll("[^0-9]", "").equals("1")) {
			
			return 1;
			
		} else if (mensagem.toLowerCase().contains("nao") || mensagem.toLowerCase().contains("nn")
					|| mensagem.toLowerCase().contains("não") || mensagem.toLowerCase().contains("errado")
					|| mensagem.replaceAll("[^0-9]", "").equals("2")) {
			
			return 2;
		}
		return 3;
	}

	@Override
	public boolean reads() {
		return true;
	}

}
