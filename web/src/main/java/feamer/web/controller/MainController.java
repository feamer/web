package feamer.web.controller;

import java.util.HashMap;

import org.json.JSONObject;

import feamer.web.service.DataService;
import feamer.web.service.SecurityService;
import feamer.web.service.TemplateService;
import spark.Spark;

/**
 * This Controller serves unsecured routes for basic information and functions
 * @author Tobias
 *
 */
public class MainController extends AbstractController{

	public void register() {

				
		Spark.get("/info", (res, req) -> {
			return DataService.getInstance().allData();
		});

		Spark.get("/", (req, res) -> {
			HashMap<String, Object> model = new HashMap<>();

			return TemplateService.getInstance().render(model, "index");
		});
		
		Spark.get("/health", (req, res) -> {
			JSONObject status = new JSONObject();
			
			status.put("serverstatus", "up");
			
			return status.toString();
		});
		
		Spark.get("/login", (req, res) -> {
			return TemplateService.getInstance().render(null, "/web/login");
		});
		
		Spark.get("/connected/history", (req, res) -> {
			
			HashMap<String, Object> model = new HashMap<>();
			String token = req.headers("Authorization");
			if (token == null) {
				token = req.cookie("token");
			}
			String name = SecurityService.getInstance().getUserFromToken(token);
			System.out.println("post history: "+token);
			String user = SecurityService.getInstance().getUserFromToken(token);
			System.out.println("history for user:" +user);
			model.put("allHistory", DataService.getInstance().getHistory(user));
			model.put("user", DataService.getInstance().getUser(name));
			return TemplateService.getInstance().render(model, "web/connected/history");
		});
	}

}
