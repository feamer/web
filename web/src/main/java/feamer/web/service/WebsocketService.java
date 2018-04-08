package feamer.web.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.json.JSONObject;
import org.json.JSONTokener;

@WebSocket
public class WebsocketService {

	private static ConcurrentHashMap<String, CopyOnWriteArrayList<Session>> sessions = new ConcurrentHashMap<String, CopyOnWriteArrayList<Session>>();

	@OnWebSocketConnect
	public void connect(Session session) {
		// default user
		
		String token = session.getUpgradeRequest().getHeader("Authorization");
		String user = SecurityService.getInstance().getUserFromToken(token);
		if (sessions.containsKey(user)) {
			if (sessions.get(user) != null) {
				sessions.get(user).add(session);
			} else {
				CopyOnWriteArrayList<Session> list = sessions.get(user);
				list = new CopyOnWriteArrayList<>();
				list.add(session);
			}
		} else {
			CopyOnWriteArrayList<Session> list = new CopyOnWriteArrayList<>();
			list.add(session);
			sessions.put(user, list);
		}
		System.out.println("added new Websocket connection for user: " + user);
	}

	@OnWebSocketClose
	public void close(Session session, int statusCode, String reason) {
		for (String u : sessions.keySet()) {
			for (Session s : sessions.get(u)) {
				if (s.equals(session)) {
					sessions.get(u).remove(s);
					System.out.println("removed session, caused by" + reason);
				}
			}
		}
	}

	@OnWebSocketMessage
	public void message(Session session, String message) {

	}

	public static void sendNotification(String user, String fileId, String filename, String origin) {
		
		CopyOnWriteArrayList<Session> list = sessions.get(user);
		System.out.println("user: "+user);
		if (list == null) {
			System.out.println("list of sessions: "+ sessions);
			return;
		}
		System.out.println("send ws notification");
		JSONObject meta = new JSONObject();
		meta.put("name", filename);
		meta.put("endpoint", "/rest/file/"+fileId);
		meta.put("timestamp", System.currentTimeMillis());
		for (Session s :list) {
			try {
				System.out.println("origin: "+ origin);
				System.out.println("sessionAddress: "+ s.getRemoteAddress().getHostString());
				if (!origin.equals(s.getRemoteAddress().getHostString())) {
					s.getRemote().sendString(meta.toString());
					System.out.println("send ws notification to "+s.getRemoteAddress());
				} else {
					System.out.println("skip connection, because this is the origin location");
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
