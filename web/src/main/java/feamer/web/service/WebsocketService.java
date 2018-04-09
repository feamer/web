package feamer.web.service;

import java.io.IOException;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.json.JSONObject;

@WebSocket
public class WebsocketService {

	private static ConcurrentHashMap<String, CopyOnWriteArrayList<Session>> sessions = new ConcurrentHashMap<String, CopyOnWriteArrayList<Session>>();

	@OnWebSocketConnect
	public void connect(Session session) {
		String token = session.getUpgradeRequest().getHeader("Authorization");
		String user = SecurityService.getInstance().getUserFromToken(token);
		System.out.println("connect user: "+user);
		if (sessions.containsKey(user)) {
			if (sessions.get(user) != null) {
				sessions.get(user).add(session);
			} else {
				CopyOnWriteArrayList<Session> list;
				list = new CopyOnWriteArrayList<>();
				list.add(session);
				sessions.put(user, list);
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
					System.out.println(getSessionOverview());
				}
			}
		}
	}

	@OnWebSocketMessage
	public void message(Session session, String message) {

	}

	public static void sendNotification(String user, String fileId, long size, String filename) {

		System.out.println("user: " + user);
		CopyOnWriteArrayList<Session> list = new CopyOnWriteArrayList<Session>(sessions.get(user));

		System.out.println("send ws notification");
		JSONObject meta = new JSONObject();
		meta.put("name", filename);
		meta.put("endpoint", "/rest/file/" + fileId);
		meta.put("size", size);
		meta.put("timestamp", System.currentTimeMillis());
		for (Session s : list) {
			try {

				s.getRemote().sendString(meta.toString());
				System.out.println("send ws notification to " + s.getRemoteAddress());

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	private String getSessionOverview () {
		String status = "";
		
		status += "Websocket status: \n\n";
		for (Entry<String, CopyOnWriteArrayList<Session>> entry: sessions.entrySet()) {
			status += entry.getKey() +": ";
			for (Session s : entry.getValue()) {
				status += s.getRemote().getInetSocketAddress() + " ";
			}
			status+="\n";
		}
		
		return status;
	}
}
