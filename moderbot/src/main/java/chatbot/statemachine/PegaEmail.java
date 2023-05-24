package chatbot.statemachine;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import chatbot.Conversation;

public class PegaEmail implements State {
	final static String stateName = "PegaEmail";
	private final Conversation c;
	
	public PegaEmail(Conversation c) {
		this.c = c;
		arrive();
	}

	@Override
	public void arrive() {
		c.sndMsg("Qual o seu e-mail?");
	}

	@Override
	public void depart() {
		String email = validaEmail(c.getMsg());

		if (email == "") {
			State.opcaoInvalida(c);
			return;
		}
		c.setEmail(email);
		
		c.setState(new ConfirmaDados(c));
	}

	@Override
	public String stateName() {
		return stateName;
	}
	


	private static String validaEmail(String msg) {
		String mensagem = msg;
		String[] palavras = mensagem.split(" ");
		String email = "";
		for(int i = 0; i < palavras.length; i++) {
			if(isValidEmailAddress(palavras[i])) {
				email = palavras[i];
				break;
			}
		}
		return email.toLowerCase();
	}

	private static boolean isValidEmailAddress(String email) {
		String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
		Pattern p = Pattern.compile(ePattern);
		Matcher m = p.matcher(email);
		return m.matches();
	}

	@Override
	public boolean reads() {
		return true;
	}

}
