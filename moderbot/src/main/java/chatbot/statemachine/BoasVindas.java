package chatbot.statemachine;

import chatbot.Conversation;

public class BoasVindas implements State {
	final static String stateName = "BoasVindas";
	final Conversation c;
	
	public BoasVindas(Conversation c) {
		this.c = c;
		arrive();
	}
	
	@Override
	public void arrive() {
		c.sndMsg("Olá!\nSou a Mari, assistente virtual da UniBF!\n\n" + "*Digite*\n"
				+ "*1* - para o setor de matrícula\n" 
				+ "*2* - para o setor de Atendimento ao Aluno\n"
				+ "*3* - caso queira se tornar nosso parceiro");
	}

	@Override
	public void depart() {
		int caso = State.getIntCase(c);
		
		c.sndMsg("Você escolheu a opção " + caso + ".\n\nEncaminhando...");
		
		switch (caso) {
		case 1:
			c.setState(new Matricula(c));
			break;
		case 2:
			c.setState(new Atendimento(c));
			break;
		case 3:
			c.setState(new Parceiro(c));
			break;
		default:
			State.opcaoInvalida(c);
			return;
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
