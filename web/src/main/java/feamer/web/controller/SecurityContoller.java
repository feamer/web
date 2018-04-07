package feamer.web.controller;

import org.json.JSONObject;
import org.json.JSONTokener;

import feamer.web.service.SecurityService;
import spark.Spark;

public class SecurityContoller {

	public void register () {
		System.out.println("Security Controller");
		Spark.post("/register", (req, res) -> {
			
			JSONObject json = new JSONObject(new JSONTokener(req.body()));
			
			String username = json.getString("username");
			String password = json.getString("username");
			
			boolean stat = SecurityService.getInstance().register(username, password);
			
			if (stat) {
				return "{\"status\":\"ok\"}";
			} else {
				return "{\"status\":\"error\"}";
			}
			
			
		});
		
		Spark.post("/login", (req, res) -> {
			JSONObject json = new JSONObject(new JSONTokener(req.body()));
			String username = json.getString("username");
			String password = json.getString("password");
			String token = SecurityService.getInstance().authenticate(username, password);
			if (token.isEmpty()) {
				Spark.halt(401);
			}
			return token;
		});
	}
}
