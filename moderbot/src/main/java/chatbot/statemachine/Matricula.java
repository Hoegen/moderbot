package chatbot.statemachine;

import chatbot.Conversation;

public class Matricula implements State {
	final static String stateName = "Matricula";
	private final Conversation c;
	private final int setor = 1;

	public Matricula(Conversation c) {
		this.c = c;
		arrive();
	}

	@Override
	public void arrive() {
		c.setSetor(setor);
		
		c.sndMsg("*Voc� tem interesse em:*\n\n" + "*1 -* P�s-gradua��o online\n" + "*2 -* Gradua��o online\n"
				+ "*3 -* 2� gradua��o online\n" + "*4 -* 2� licenciatura online\n"
				+ "*5 -* Forma��o pedag�gica online\n" + "*6 -* Extens�o online");
	}

	@Override
	public void depart() {
		int caso = State.getIntCase(c);
		
		switch (caso) {
		case 1:
			c.setInteresse("P�ss-Gradua��o Online");
			break;
		case 2:
			c.setInteresse("Gradua��o Online");
			break;
		case 3:
			c.setInteresse("2� Gradua��o Online");
			break;
		case 4:
			c.setInteresse("2� Licenciatura Online");
			break;
		case 5:
			c.setInteresse("Forma��o Pedag�gica Online");
			break;
		case 6:
			c.setInteresse("Extens�o Online");
			break;
		default:
			State.opcaoInvalida(c);
			return;
		}
		c.setState(new PegaNome(c));
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
