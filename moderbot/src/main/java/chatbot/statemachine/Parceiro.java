package chatbot.statemachine;

import chatbot.Conversation;

public class Parceiro implements State {
	final static String stateName = "Parceiro";
	private final Conversation c;
	private final int setor = 3;

	public Parceiro(Conversation c){
		this.c = c;
		arrive();
	}
	
	@Override
	public void arrive() {
		c.setSetor(setor);
		c.sndMsg("Texto parceiro");
	}

	@Override
	public void depart() {
		c.setState(new PegaNome(c));
	}

	@Override
	public String stateName() {
		return this.stateName();
	}

	@Override
	public boolean reads() {
		return false;
	}

}
