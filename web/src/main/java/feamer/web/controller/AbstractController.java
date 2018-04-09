package feamer.web.controller;

import java.util.HashMap;

import feamer.web.service.TemplateService;

public abstract class AbstractController {
	
	public abstract void register();
	
	public String render(HashMap<String, Object> model, String viewName) {
		return TemplateService.getInstance().render(model, viewName);
	}

}
