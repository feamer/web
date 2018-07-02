package feamer.web.service;

import java.util.Map.Entry;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import feamer.web.dto.UserDTO;

public class SecurityService {
	private ConcurrentHashMap<String, String> tokens = new ConcurrentHashMap<>();
	//token time to life
	private static int TTTL = 3600000;

	private static SecurityService service;

	private SecurityService() {
	}

	public static SecurityService getInstance() {
		if (service == null) {
			service = new SecurityService();
		}

		return service;
	}

	private String generateToken() {
		String token = UUID.randomUUID().toString().toUpperCase() + "." + System.currentTimeMillis();

		return token;
	}

	public boolean register(String username, String hashword) {
		UserDTO user = DataService.getInstance().getUser(username);
		
		if (user == null) {
			DataService.getInstance().addNewUser(DataService.generateID(), username, hashword);
			return true;
		}

		return false;
	}
	
	public void destroyUserTokens (String username) {
		tokens.remove(username);
	}

	public String authenticate(String username, String hashword) {
		
		UserDTO user = DataService.getInstance().getUser(username);
		if (user == null) {
			return "";
		}
		String password = user.getPassword();
		
		if (password != null && password.equals(hashword)) {
			String token = "";
			if (tokens.containsKey(username)) {
				token = tokens.get(username);
			} else {
				token = generateToken();
				tokens.put(username, token);
			}
			cleanTokenStorage();
			return token;
		}
		return "";

	}
	
	public String getUserFromToken(String token) {
		return tokens.entrySet().stream().filter(entry-> entry.getValue().equals(token)).map(entry -> entry.getKey()).findFirst().orElse(null);
	}

	public boolean validateToken(String token) {
		
		if (token == null) {
			return false;
		}
		
		if (!tokens.containsValue(token)){
			return false;
		}
		
		if (!token.contains(".")){
			return false;
		}
		
		int timestamp = Integer.parseInt(token.split(".")[1]);
		
		if (timestamp + TTTL < System.currentTimeMillis()){
			return false;
		}
		
		//token is valid
		return true;
	}
	
	private void cleanTokenStorage(){
		for (Entry<String, String> entry: tokens.entrySet()){
			if (!validateToken(entry.getValue())){
				tokens.remove(entry.getKey());
			}
		}
	}

}
