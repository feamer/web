package feamer.web.service;

import java.sql.Blob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

import feamer.web.dto.FileDTO;
import feamer.web.dto.UserDTO;

public class DataService {
	
	private static DataService service;
	Connection con;
	
	
	String addUser = "insert into users values (?, ?, ?, ?)";
	String addFile = "insert into files values (?, ?, ?, ?, ?)";
	String addHistory = "insert into history values (?, ?, ?, ?)";
	String getFile = "select * from files where id=?";
	String getUser = "select * from users where username=?";
	String getUserById = "select * from users where id=?";
	String deleteold = "delete from files where timestamp<?";
	String updateFriend = "update users set friends=? where username=?";
	
	private DataService (){
		try {
			con = DriverManager.getConnection("jdbc:h2:./db", "sa", "");
		} catch (SQLException e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}
	
	public static DataService getInstance() {
		if (service == null) {
			service = new DataService();
		}
		
		return service;
	}
	
	public void checkInitialState() {
		try {
			System.out.println("check database");
			Statement s = con.createStatement();
			
			ResultSet userData = s.executeQuery("select * from users");
			ResultSet files = s.executeQuery("select * from files");
			ResultSet history = s.executeQuery("select * from history");
			
		} catch (SQLException e) {
			e.printStackTrace();
			createInitialDB();
			addNewUser(generateID(), "tobi", "c219c4dc4b7ff6be7a7090459bc6d06a879a1577");
			
			System.out.println("insufficient database detected");
		}
	}
	
	
	private void createInitialDB () {
		try {
			System.out.println("create initial database tables");
			Statement s = con.createStatement();
			s.execute("create table users(id varchar(255) not null primary key, username varchar(255), password varchar(255), friends array);");
			s.execute("create table files(id varchar(255) not null primary key, filename varchar(255), userid varchar(255), file blob, timestamp bigint);");
			s.execute("create table history(id varchar(255) not null primary key, userid varchar(255), fileid varchar(255), type varchar(255));");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static String generateID () {
		return UUID.randomUUID().toString();
	}
	
	public void addNewUser(String id, String username, String password) {
		try {
			PreparedStatement s = con.prepareStatement(addUser);
			s.setString(1, id);
			s.setString(2, username);
			s.setString(3, password);
			s.setArray(4, null);
			
			s.execute();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		clearFiles();
	}
	
	public void addNewFile (String id, String filename, String userid, byte[] file) {
		
		try {
			PreparedStatement s = con.prepareStatement(addFile);
			s.setString(1, id);
			s.setString(2, filename);
			s.setString(3, userid);
			s.setBytes(4, file);
			s.setLong(5, System.currentTimeMillis());
			
			s.execute();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		clearFiles();
	}
	
	public UserDTO getUser(String username) {
		UserDTO user = new UserDTO();
		
		PreparedStatement s;
		try {
			s = con.prepareStatement(getUser);
			s.setString(1, username);
			ResultSet result =  s.executeQuery();
			
			result.next();
			user.setId(result.getString(1));
			user.setName(result.getString(2));
			user.setPassword(result.getString(3));
			return user;
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public void addNewHistory (String id, String userid, String fileId, String type) {
		try {
			PreparedStatement s = con.prepareStatement(addHistory);
			s.setString(1, id);
			s.setString(2, userid);
			s.setString(3, fileId);
			s.setString(4, type);
			s.execute();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public FileDTO getFileById (String id) {
		FileDTO file = new FileDTO();
		PreparedStatement s;
		try {
			s = con.prepareStatement(getFile);
			s.setString(1, id);
			
			ResultSet result = s.executeQuery();
			boolean isPresent = result.next();
			if (isPresent) {
				return null;
			}
			file.setId(result.getString("id"));
			file.setRelatedUser(result.getString("userid"));
			file.setName(result.getString("filename"));
			Blob blob = result.getBlob("file");
			file.setFile(blob.getBytes(0, (int)blob.length()));
			return file;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public UserDTO getUserById (String id){
		return null;
	}
	
	public String allData () {
		
		String result = "";
		try {
			Statement sFiles = con.createStatement();
			Statement sUsers = con.createStatement();
			Statement sHistory = con.createStatement();
			ResultSet files = sFiles.executeQuery("select * from files");
			ResultSet users = sUsers.executeQuery("select * from users");
			ResultSet history = sHistory.executeQuery("select * from history");
			result += "files:\n";
			while (files.next()) {
				result += ""+files.getString(1)+" ,"+files.getString(2)+", "+files.getString(3)+ ", "+ files.getBlob(4).length()+", "+files.getLong(5)+"\n";
			}
			
			result += "users:\n";
			while (users.next()) {
				result += ""+users.getString(1)+", "+users.getString(2)+", "+users.getString(3)+", "+(Object[])users.getObject(4)+"\n";
			}
			
			result += "history:\n";
			while (history.next()) {
				result += ""+history.getString(1)+", "+history.getString(2)+", "+history.getString(3)+", "+history.getString(4)+"\n";
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return result;
		
	}
	
	public void addFriend (String user, String friend) {
		Object[] friends;
		try {
			Statement query = con.createStatement();
			ResultSet result = query.executeQuery("select firends from users where username="+user);
			result.next();
			friends = (Object[]) result.getObject("friends");
			if (friends == null) {
				friends = new Object[1];
			}

			friends[0] = friend;
			
			PreparedStatement s = con.prepareStatement(updateFriend);
			s.setObject(1, friends);
			s.execute();
			;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void clearFiles() {
		long time = 600000;
		long timeThreshhold = System.currentTimeMillis()-time;
		
		try {
			PreparedStatement s = con.prepareStatement(deleteold);
			s.setLong(1, timeThreshhold);
			s.execute();
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
