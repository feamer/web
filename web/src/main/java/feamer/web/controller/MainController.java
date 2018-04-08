package feamer.web.controller;

import java.util.ArrayList;
import java.util.HashMap;import javax.swing.filechooser.FileNameExtensionFilter;

import feamer.web.dto.FileDTO;
import feamer.web.dto.HistoryDTO;
import feamer.web.service.DataService;
import feamer.web.service.SecurityService;
import feamer.web.service.TemplateService;
import feamer.web.service.WebsocketService;
import spark.Spark;

public class MainController {

	public void register() {

		Spark.post("/rest/upload", (req, res) -> {
			System.out.println("upload");
			byte[] bytes =  req.bodyAsBytes();
			String fileId = DataService.generateID();
			String token = req.headers("Authorization");
			String user = SecurityService.getInstance().getUserFromToken(token);
			String filename = req.headers("Filename");
			
			if (filename == null || user == null) {
				Spark.halt(500, "insufficient data");
			}
			
			DataService.getInstance().addNewFile(fileId, filename, user, bytes);
			DataService.getInstance().addNewHistory(DataService.generateID(), user, fileId, "upload");
			WebsocketService.sendNotification(user, fileId, bytes.length, filename);
			
			return fileId;
		});
		
		Spark.post("/rest/share", (req, res) -> {
			String friendname = req.queryParams("friend");
			byte[] bytes = req.bodyAsBytes();
			String fileId = DataService.generateID();
			String token = req.headers("Authorization");
			String user = SecurityService.getInstance().getUserFromToken(token);
			String filename = req.headers("Filename");
			
			System.out.println("share");
			
			DataService.getInstance().addNewFile(fileId, filename, user, bytes);
			DataService.getInstance().addNewHistory(DataService.generateID(), user, fileId, "upload");
			WebsocketService.sendNotification(friendname, fileId, bytes.length, filename);
			
			return fileId;
		});
		
		
		Spark.get("/rest/file/:id", (req, res) -> {
			String id = req.params(":id");
			FileDTO file = DataService.getInstance().getFileById(id);
			String token = req.headers("Authorization");
			String user = SecurityService.getInstance().getUserFromToken(token);
			
			if (user == null || file == null) {
				Spark.halt(500, "insufficient data");
			}
			
			res.header("Content-Disposition", "attachment; filename=\""+file.getName()+"\"");
			res.type("application/octet-stream");
			
			DataService.getInstance().addNewHistory(DataService.generateID(), user, file.getId(), "download");
			return file.getFile();
		});
		
		Spark.get("/rest/id", (req, res) -> {
			String token = req.headers("Authorization");
			if (token == null) {
				Spark.halt(500, "no token available");
			}
			String username = SecurityService.getInstance().getUserFromToken(token);
			return DataService.getInstance().getUser(username).getId();
		});
		
		Spark.get("/rest/friends", (req, res) -> {
			String token = req.headers("Authorization");
			String user = SecurityService.getInstance().getUserFromToken(token);
			return DataService.getInstance().getfriendNames(user);
		});
		
		Spark.post("/rest/addFriend", (req, res) -> {
			String token = req.headers("Authorization");
			String friendname = req.queryParams("name");
			if (friendname == null) {
				return "";
			}
			String user = SecurityService.getInstance().getUserFromToken(token);
			DataService.getInstance().addFriend(user, friendname);
			return "";
		});
		
		Spark.get("/info", (res, req) -> {
			return DataService.getInstance().allData();
		});

		Spark.get("/", (req, res) -> {
			HashMap<String, Object> model = new HashMap<>();

			return TemplateService.getInstance().render(model, "index");
		});
		
		Spark.get("/login", (req, res) -> {
			return TemplateService.getInstance().render(null, "/web/login");
		});
		
		Spark.get("/connected/history", (req, res) -> {
			
			HashMap<String, Object> model = new HashMap<>();
			String token = req.headers("Authorization");
			if (token == null) {
				token = req.cookie("token");
			}
			System.out.println("post history: "+token);
			String user = SecurityService.getInstance().getUserFromToken(token);
			System.out.println("history for user:" +user);
			model.put("allHistory", DataService.getInstance().getHistory(user));
			return TemplateService.getInstance().render(model, "web/connected/history");
		});
	}

}
