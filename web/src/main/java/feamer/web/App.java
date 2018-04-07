package feamer.web;

import feamer.web.controller.MainController;
import feamer.web.controller.SecurityContoller;
import feamer.web.controller.WebsocketController;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        System.out.println( "Hello World!" );
    }
    
    WebsocketController websocket = new WebsocketController();
    MainController main = new MainController();
    SecurityContoller security = new SecurityContoller();
    
    public App () {
    	websocket.register();
    	main.register();
    	security.register();
    }
}
