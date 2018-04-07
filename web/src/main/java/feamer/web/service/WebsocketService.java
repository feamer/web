package feamer.web.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.json.JSONObject;
import org.json.JSONTokener;

@WebSocket
public class WebsocketService {
	
	private ConcurrentHashMap<String ,ArrayList<Session>> sessions = new ConcurrentHashMap<String, ArrayList<Session>>();
	
	@OnWebSocketConnect
	public void connect (Session session) {
		//default user
		String user = "user";
		if (sessions.containsKey(user)) {
			if (sessions.get(user) != null) {				
				sessions.get(user).add(session);
			} else {
				ArrayList<Session> list = sessions.get(user);
				list = new ArrayList<>();
				list.add(session);
			}
		} else {
			ArrayList<Session> list = new ArrayList<>();
			list.add(session);
			sessions.put(user, list);
		}
		System.out.println("added new Websocket connection for user: "+ user);
	}
	
	@OnWebSocketClose
	public void close(Session session, int statusCode, String reason) {
		for (String u : sessions.keySet()) {
			for (Session s: sessions.get(u)) {
				if (s.equals(session)) {
					sessions.get(u).remove(s);
					System.out.println("removed session, caused by"+reason);
				}
			}
		}
	}
	
	@OnWebSocketMessage
	public void message (Session session, String message) {
		
		String u = "";
		
		for (String user : sessions.keySet()) {
			ArrayList<Session> l = sessions.get(user);
				if (l.contains(session)) {
					u = user;
					break;
			}
		}
		
		ArrayList<Session> list = sessions.get(u);
		for (Session s : list) {
			if (!s.equals(session)) {
				try {
					JSONObject jsonMessage = new JSONObject(new JSONTokener(message));
					//debug
					jsonMessage.append("endpoint", "/get/228374");
					s.getRemote().sendString(message);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
}
