package feamer.web.controller;

import feamer.web.service.TemplateService;
import spark.Spark;

/**
 * This controller serves the subsites of the webpage
 * @author Tobias
 *
 */
public class WebController extends AbstractController{

	@Override
	public void register() {
		Spark.get("/web/register", (req, res) -> {
			return TemplateService.getInstance().render(null, "web/register");
		});
		
		Spark.get("/web/login", (req, res) -> {
			return TemplateService.getInstance().render(null, "web/login");
		});	
	}
}
