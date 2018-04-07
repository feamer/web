package feamer.web.service;

import java.util.HashMap;
import java.util.UUID;

import feamer.web.dto.UserDTO;

public class SecurityService {
	private HashMap<String, String> tokens = new HashMap<>();

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

	public String authenticate(String username, String hasword) {
		
		UserDTO user = DataService.getInstance().getUser(username);
		if (user == null) {
			return "";
		}
		String password = user.getPassword();
		
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
	
	public String getUserFromToken(String token) {
		return tokens.entrySet().stream().filter(entry-> entry.getValue().equals(token)).map(entry -> entry.getKey()).findFirst().orElse(null);
	}

	public boolean validateToken(String token) {
		if (token == null) {
			return false;
		}
		return tokens.containsValue(token);
	}

}
