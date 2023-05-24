package properties;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import javax.swing.filechooser.FileSystemView;

import org.json.JSONObject;

public class Properties {
	private static String username = "";
	private static String password = "";
	private static String hbstoken = "";
	
	public Properties(){
		File file = new File(FileSystemView.getFileSystemView().getDefaultDirectory().getPath() + "\\HubsBot\\properties.txt");
		try {
			Scanner sc = new Scanner(file);
			String[] line;
			JSONObject props = new JSONObject();
			while(sc.hasNext()) {
				line = sc.nextLine().split("=");
				if(line.length != 2) {
					line = new String().split("=");
					continue;
				}
				props.put(line[0], line[1]);
			}
			sc.close();
			
			if(props.has("username"))
				username = props.getString("username");
			if(props.has("password"))
				password = props.getString("password");
			if(props.has("hbstoken"))
				hbstoken = props.getString("hbstoken");
			
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public static String getUsername() {
		return username;
	}

	public static void setUsername(String username) {
		Properties.username = username;
	}

	public static String getPassword() {
		return password;
	}

	public static void setPassword(String password) {
		Properties.password = password;
	}

	public static String getHbstoken() {
		return hbstoken;
	}

	public static void setHbstoken(String hbstoken) {
		Properties.hbstoken = hbstoken;
	}
}