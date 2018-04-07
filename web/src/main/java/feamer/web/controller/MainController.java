package feamer.web.controller;

import spark.Spark;

public class MainController {
	
	
	
	public void register () {
		
		Spark.post("/rest/upload", (req, res) -> {
			return "";
		});
	}

}
