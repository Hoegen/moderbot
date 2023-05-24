package chatbot.statemachine;

import chatbot.Conversation;

public class PegaNome implements State {
	final static String stateName = "PegaNome";
	private final Conversation c;
	
	public PegaNome(Conversation c) {
		this.c = c;
		arrive();
	}

	@Override
	public void arrive() {
		c.sndMsg("Que maravilha! Fico muito feliz com o seu interesse.\n\n" + "Por favor, qual o seu nome?");
	}

	@Override
	public void depart() {
		c.setNome(c.getMsg());
		c.setState(new PegaEmail(c));
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
