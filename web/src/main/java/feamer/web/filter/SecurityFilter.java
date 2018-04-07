package feamer.web.filter;

import feamer.web.service.SecurityService;
import spark.Spark;

public class SecurityFilter {

	public void apply () {
		Spark.before("/ws", (req, res) ->{
			String token = req.headers("Authentication");
			if (!checkAuthentication(token)) {
				Spark.halt(401);
			}
		});
		
		Spark.before("/rest/*", (req, res) ->{
			System.out.println("check rest authentication");
			String token = req.headers("Authorization");
			if (!checkAuthentication(token)) {
				Spark.halt(401);
			}
		});
		
	}
	
	public boolean checkAuthentication (String token) {
		
		return SecurityService.getInstance().validateToken(token);
	}
}
