package feamer.web.service;

import java.util.HashMap;

import spark.ModelAndView;
import spark.template.thymeleaf.ThymeleafTemplateEngine;

public class TemplateService {
	private final String prefix = "/resources/";
	private final String suffix = ".html";
	private static TemplateService service;
	
	private ThymeleafTemplateEngine templateEngine;
	
	private TemplateService () {
		init();
	}

	
	public static TemplateService getInstance () {
		if (service == null) {
			service = new TemplateService();
		}
		return service;
	}
	
	public ThymeleafTemplateEngine getTemplateEngine() {
		return templateEngine;
	}
	
	public String render (HashMap<String, Object> model, String viewName) {
		if (model == null)
			model = new HashMap<>();
		return service.templateEngine.render(new ModelAndView(model, viewName));
	}
	
	private void init() {
		templateEngine = new ThymeleafTemplateEngine(prefix, suffix);
	}
}
