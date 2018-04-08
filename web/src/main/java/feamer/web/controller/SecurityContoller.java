package feamer.web.controller;

import org.json.JSONObject;
import org.json.JSONTokener;

import feamer.web.service.SecurityService;
import feamer.web.service.TemplateService;
import spark.Spark;

public class SecurityContoller {

	public void register () {
		System.out.println("Security Controller");
		Spark.post("/register", (req, res) -> {
			
			JSONObject json = new JSONObject(new JSONTokener(req.body()));
			
			String username = json.getString("username");
			String password = json.getString("password");
			
			boolean stat = SecurityService.getInstance().register(username, password);
			
			if (stat) {
				System.out.println("sucessfull registered: "+username);
				return "{\"status\":\"ok\"}";
			} else {
				System.out.println("error on register: "+ username);
				return "{\"status\":\"error\"}";
			}
			
			
		});
		
		Spark.get("/web/register", (req, res) -> {
			return TemplateService.getInstance().render(null, "web/register");
		});
		
		Spark.get("/web/login", (req, res) -> {
			return TemplateService.getInstance().render(null, "web/login");
		});
		
		Spark.post("/login", (req, res) -> {
			JSONObject json = new JSONObject(new JSONTokener(req.body()));
			String username = json.getString("username");
			String password = json.getString("password");
			String token = SecurityService.getInstance().authenticate(username, password);
			if (token.isEmpty()) {
				System.out.println("wrong authentication");
				Spark.halt(401);
			}
			System.out.println("sucessfully authenticated with token: "+token);
			res.cookie("token", token);
			return token;
		});
	}
}
