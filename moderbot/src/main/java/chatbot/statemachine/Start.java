package chatbot.statemachine;

import chatbot.Conversation;

public class Start implements State {
	final static String stateName = "Start";
	final Conversation c;

	public Start(Conversation c) {
		this.c = c;
		arrive();
	}
	
	@Override
	public void arrive() {
	}

	@Override
	public void depart() {
		c.setState(new BoasVindas(c));
	}

	@Override
	public String stateName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean reads() {
		return true;
	}

}
