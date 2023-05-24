package chatbot.statemachine;

import chatbot.Conversation;

public class Atendimento implements State {
	final static String stateName = "Atendimento";
	private final Conversation c;
	private final int setor = 2;

	public Atendimento(Conversation c) {
		this.c = c;
		arrive();
	}

	@Override
	public void arrive() {
		c.setSetor(setor);
		c.sndMsg("Texto atendimento");
	}

	@Override
	public void depart() {
		c.setState(new PegaNome(c));
	}

	@Override
	public String stateName() {
		return stateName;
	}

	@Override
	public boolean reads() {
		return false;
	}

}
