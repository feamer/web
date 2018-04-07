package feamer.web;

import feamer.web.controller.MainController;
import feamer.web.controller.SecurityContoller;
import feamer.web.controller.WebsocketController;
import feamer.web.filter.SecurityFilter;
import feamer.web.service.DataService;
import spark.Spark;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        App app = new App();
        DataService.getInstance().checkInitialState();
    }
    
    WebsocketController websocket = new WebsocketController();
    MainController main = new MainController();
    SecurityContoller security = new SecurityContoller();
    
    SecurityFilter filter = new SecurityFilter();
    
    public App () {
    	
    	Spark.port(80);
    	Spark.staticFiles.location("/resources/");
    	
    	System.out.println("register websockets");
    	websocket.register();
    	System.out.println("websockets finished");
    	System.out.println("register main routes");
    	main.register();
    	System.out.println("routes finished");
    	System.out.println("register security routes");
    	security.register();
    	System.out.println("security routes finished");
    	System.out.println("apply filter");
    	filter.apply();
    	Spark.init();
    	
    	
    }
}
