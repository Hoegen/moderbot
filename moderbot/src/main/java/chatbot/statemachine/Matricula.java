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
		
		c.sndMsg("*Você tem interesse em:*\n\n" + "*1 -* Pós-graduação online\n" + "*2 -* Graduação online\n"
				+ "*3 -* 2ª graduação online\n" + "*4 -* 2ª licenciatura online\n"
				+ "*5 -* Formação pedagógica online\n" + "*6 -* Extensão online");
	}

	@Override
	public void depart() {
		int caso = State.getIntCase(c);
		
		switch (caso) {
		case 1:
			c.setInteresse("Póss-Graduação Online");
			break;
		case 2:
			c.setInteresse("Graduação Online");
			break;
		case 3:
			c.setInteresse("2ª Graduação Online");
			break;
		case 4:
			c.setInteresse("2ª Licenciatura Online");
			break;
		case 5:
			c.setInteresse("Formação Pedagógica Online");
			break;
		case 6:
			c.setInteresse("Extensão Online");
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
