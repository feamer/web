package feamer.web.controller;

import java.util.HashMap;

import feamer.web.service.TemplateService;
import spark.ModelAndView;
import spark.Spark;
import spark.template.thymeleaf.ThymeleafTemplateEngine;

public class MainController {
	
	
	
	public void register () {
		
		Spark.post("/rest/upload", (req, res) -> {
			return "";
		});
		
		Spark.get("/", (req, res) -> {
			HashMap<String, Object> model = new HashMap<>();
			
			return TemplateService.getInstance().render(model, "index");
		});
	}

}
