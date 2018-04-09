package feamer.web.controller;

import feamer.web.service.WebsocketService;
import spark.Spark;

/**
 * This controller serves the websocket endpoint.
 * @author Tobias
 *
 */
public class WebsocketController extends AbstractController{
	
	public void register () {
		Spark.webSocket("/ws", WebsocketService.class);
	}

}
