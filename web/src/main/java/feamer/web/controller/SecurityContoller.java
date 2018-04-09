package feamer.web.controller;

import org.json.JSONObject;
import org.json.JSONTokener;

import feamer.web.service.SecurityService;
import spark.Spark;

/**
 * Ther SecurityController serves the endpoints for security relevant interaction
 * @author Tobias
 *
 */
public class SecurityContoller extends AbstractController{

	public void register() {
		
		/*
		 * register new user 
		 */
		Spark.post("/register", (req, res) -> {

			JSONObject json = new JSONObject(new JSONTokener(req.body()));

			String username = json.getString("username");
			String password = json.getString("password");

			boolean stat = SecurityService.getInstance().register(username, password);

			if (stat) {
				System.out.println("sucessfull registered: " + username);
				return "{\"status\":\"ok\"}";
			} else {
				System.out.println("error on register: " + username);
				return "{\"status\":\"error\"}";
			}

		});
		
		/*
		 * validate the given token 
		 */
		Spark.get("/validate", (req, res) -> {
			String headerToken = req.headers("Authorization");
			String cockieToken = req.cookie("token");
			
			JSONObject result = new JSONObject();
			
			if (headerToken != null) {
				result.put("header", SecurityService.getInstance().validateToken(headerToken));
			} else {
				result.put("header", "not present");
			}
			
			if (cockieToken != null) {
				result.put("cockie", SecurityService.getInstance().validateToken(cockieToken));
			} else {
				result.put("cockie", "not present");
			}
			
			return result.toString();
		});

		/*
		 * request a Authorization token
		 */
		Spark.post("/login", (req, res) -> {
			JSONObject json = new JSONObject(new JSONTokener(req.body()));
			String username = json.getString("username");
			String password = json.getString("password");
			String token = SecurityService.getInstance().authenticate(username, password);
			if (token.isEmpty()) {
				System.out.println("wrong authentication");
				Spark.halt(401);
			}
			System.out.println("sucessfully authenticated with token: " + token);
			res.cookie("token", token);
			return token;
		});
	}
}
