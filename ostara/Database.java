package ostara;

import java.sql.*;
import java.io.*;
import java.util.*;

import ostara.data.Person;
import ostara.data.Dept;
import ostara.data.EventType;
//import ostara.data.Token;
import ostara.data.Client;
import ostara.data.User;

public class Database{

	private static String DB_URL;
	private static String DB_USER;
	private static String DB_PASS;

	static void initialize(String URL, String USER, String PASS){
		DB_URL = URL;
		DB_USER = USER;
		DB_PASS = PASS;
	}

	public static Dept getDept(int deptID){
		Dept r = null;
		try(
			Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(String.format("SELECT dept.DEPT_ID, dept.NAME from dept WHERE dept.DEPT_ID = '%d' ", deptID));
		){
			while (rs.next()){
				r = new Dept(rs.getInt("dept.DEPT_ID"), rs.getString("dept.NAME"));
			}
		}catch(SQLException e){
			e.printStackTrace();
		}
		return r;
	}

	public static Person getPerson(int personID){
		Person r = null;
		try(
			Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(String.format("SELECT person.PERSON_ID, person.NAME, person.DEPT_ID from person WHERE person.PERSON_ID = '%d' ", personID));
		){
			while (rs.next()){
				r = new Person(rs.getInt("person.PERSON_ID"), rs.getInt("person.DEPT_ID"), rs.getString("person.NAME"));
			}
		}catch(SQLException e){
			e.printStackTrace();
		}
		return r;
	}

	public static Person getPerson(String tokenValue){
		Person r = null;
		try(
			Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(String.format("SELECT person.PERSON_ID, person.NAME, person.DEPT_ID from person, token WHERE token.VALUE = '%s' AND person.PERSON_ID = token.PERSON_ID ", tokenValue));
		){
			while (rs.next()){
				r = new Person(rs.getInt("person.PERSON_ID"), rs.getInt("person.DEPT_ID"), rs.getString("person.NAME"));
			}
		}catch(SQLException e){
			e.printStackTrace();
		}
		return r;
	}

	public static User getUser(String username){
		User r = null;
		try(
			Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(String.format("SELECT user.USER_ID, user.USERNAME, user.USER_PERSON, user.USER_PASS from user, token WHERE user.USERNAME = '%s' ", username));
		){
			while (rs.next()){
				r = new User(rs.getInt("user.USER_ID"), rs.getString("user.USERNAME"), rs.getString("user.USER_PASS"), rs.getInt("user.USER_PERSON"));
			}
		}catch(SQLException e){
			e.printStackTrace();
		}
		return r;
	}

	public static EventType getEventType(int eventTypeID){
		EventType r = null;
		try(
			Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(String.format("SELECT event_type.EVENT_TYPE_ID, event_type.NAME from event_type WHERE event_type.EVENT_TYPE_ID = '%d' ", eventTypeID));
		){
			while (rs.next()){
				r = new EventType(rs.getInt("event_type.EVENT_TYPE_ID"), rs.getString("event_type.NAME"));
			}
		}catch(SQLException e){
			e.printStackTrace();
		}
		return r;
	}

	public static Client getClient(int clientID){
		Client r = null;
		try(
			Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(String.format("SELECT client.CLIENT_ID, client.NAME, client.LOCATION, client.AUTH from client WHERE client.CLIENT_ID = '%d' ", clientID));
		){
			while (rs.next()){
				r = new Client(rs.getInt("client.CLIENT_ID"), rs.getString("client.NAME"), rs.getString("client.LOCATION"), rs.getString("client.AUTH"));
			}
		}catch(SQLException e){
			e.printStackTrace();
		}
		return r;
	}

	public static List<EventType> getEventTypes(){
		ArrayList<EventType> r = new ArrayList<EventType>();
		try(
			Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(String.format("SELECT event_type.EVENT_TYPE_ID, event_type.NAME from event_type "));
		){
			while (rs.next()){
				r.add( new EventType(rs.getInt("event_type.EVENT_TYPE_ID"), rs.getString("event_type.NAME")) );
			}
		}catch(SQLException e){
			e.printStackTrace();
		}
		return (List<EventType>)r;
	}

	public static void insertEvent(String tokenValue, String eventType, int clientID) throws SQLException{
		Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
		Statement stmt = conn.createStatement();
		stmt.executeUpdate(String.format("INSERT INTO `event` (`PERSON_ID`, `TOKEN_ID`, `EVENT_TYPE`, `CLIENT_ID`) SELECT token.PERSON_ID, token.TOKEN_ID, event_type.EVENT_TYPE_ID, '%d' FROM token, event_type WHERE token.VALUE='%s' AND event_type.NAME='%s'", clientID, tokenValue, eventType));
	}
	public static void insertEvent(String tokenValue, String eventType, String clientID) throws SQLException{
		Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
		Statement stmt = conn.createStatement();
		stmt.executeUpdate(String.format("INSERT INTO `event` (`PERSON_ID`, `TOKEN_ID`, `EVENT_TYPE`, `CLIENT_ID`) SELECT token.PERSON_ID, token.TOKEN_ID, event_type.EVENT_TYPE_ID, '%s' FROM token, event_type WHERE token.VALUE='%s' AND event_type.NAME='%s'", clientID, tokenValue, eventType));
	}

	public static void insertEvent(String tokenValue, int eventType, String clientID) throws SQLException{
		Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
		Statement stmt = conn.createStatement();
		stmt.executeUpdate(String.format("INSERT INTO `event` (`PERSON_ID`, `TOKEN_ID`, `EVENT_TYPE`, `CLIENT_ID`) SELECT token.PERSON_ID, token.TOKEN_ID, '%d', '%s' FROM token WHERE token.VALUE='%s'", eventType, clientID, tokenValue));
	}
/*
	public static void insertEvent(Token token, EventType eventType, Client client) throws SQLException{
		insertEvent(token.id.toString(), eventType.id.toString(), client.id.toString());
	}


	public static void insertEvent(Token token, EventType eventType, Client client) throws SQLException{
		insertEvent(token.id, eventType.id, client.id);
	}
*/

}
