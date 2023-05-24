package chatbot.statemachine;

import chatbot.Conversation;

public class Erro implements State {
	final static String stateName = "Erro";
	private final Conversation c;
	
	public Erro(Conversation c) {
		this.c = c;
		arrive();
	}

	@Override
	public void arrive() {
		c.sndMsg("Houve um erro com o cadastro do seu contato.\n"
				+ "Seu caso será encaminhado a um atendente");
		c.setContactid("erro");
	}

	@Override
	public void depart() {
		c.setState(new BoasVindas(c));
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
