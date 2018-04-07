package feamer.web.controller;

import feamer.web.service.WebsocketService;
import spark.Spark;

public class WebsocketController {
	
	public void register () {
		Spark.webSocket("/file", WebsocketService.class);
	}

}
