package chatbot.statemachine;

import chatbot.Conversation;

public class EncaminhaAtendente implements State {
	final static String stateName = "EncaminhaAtendentes";
	private final Conversation c;
	
	public EncaminhaAtendente(Conversation c) {
		this.c = c;
		arrive();
	}
	
	@Override
	public void arrive() {
		c.sndMsg("Vou te encaminhar para um atendente!");
	}

	@Override
	public void depart() {
		if(c.getMsg().equals("#inicio")) {
			c.setState(new BoasVindas(c));
			c.setInteresse("");
			c.setSetor(0);
			c.setContactid("");
			c.setEmail("");
		}
	}

	@Override
	public String stateName() {
		return stateName;
	}

	@Override
	public boolean reads() {
		return true;
	}

}
