package chatbot.statemachine;

import chatbot.Conversation;

public interface State {
	void arrive();
	
	void depart();
	
	String stateName();
	
	boolean reads();
	
	static void opcaoInvalida(Conversation c) {
		c.sndMsg("Desculpe, n�o entendi sua mensagem. Por favor, envie uma mensagem v�lida");
	}
	
	static int getIntCase(Conversation c) {
		String mensagem = c.getMsg().replaceAll("[^0-9]", "");
		if(mensagem.equals("")) {
			return -99;
		}else {
			return Integer.parseInt(mensagem);
		}
	}
}
