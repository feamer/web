package feamer.web.service;

import java.util.HashMap;
import java.util.UUID;

public class SecurityService {

	private HashMap<String, String> users = new HashMap<>();
	private HashMap<String, String> tokens = new HashMap<>();

	private static SecurityService service;

	private SecurityService() {
		users.put("tobi", "c219c4dc4b7ff6be7a7090459bc6d06a879a1577");
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
		if (!users.keySet().contains(username)) {
			users.put(username, hashword);
			return true;
		}

		return false;
	}
	
	public void destroyUserTokens (String username) {
		tokens.remove(username);
	}

	public String authenticate(String username, String hasword) {
		
		String password = users.get(username);
		
		if (password != null && password.equals(hasword)) {
			String token = "";
			if (tokens.containsKey(username)) {
				token = tokens.get(username);
			} else {
				token = generateToken();
				tokens.put(username, token);
			}
			
			return token;
		}
		return "";

	}

	public boolean validateToken(String token) {
		return tokens.containsValue(token);
	}

}
