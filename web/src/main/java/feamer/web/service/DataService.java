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
			Statement s = con.createStatement();
			ResultSet userData = s.executeQuery("select * from users");
			ResultSet files = s.executeQuery("select * from files");
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	private void createInitialDB () {
		
	}

}
