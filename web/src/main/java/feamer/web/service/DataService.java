package feamer.web.service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.naming.spi.DirStateFactory.Result;

public class DataService {
	
	private static DataService service;
	Connection con;
	
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
			System.out.println("insufficient database detected");
		}
	}
	
	
	private void createInitialDB () {
		try {
			System.out.println("create initial database tables");
			Statement s = con.createStatement();
			s.execute("create table users(id varchar(255) not null primary key, username varchar(255), password text);");
			s.execute("create table files(id varchar(255) not null primary key, user varchar(255) foreign key references users(id), file blob);");
			s.execute("create table histoy(id varchar(255) not null primary key, user varchar(255) foreign key references users(id), fileid varchar(255) foreign key references files(id));");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
