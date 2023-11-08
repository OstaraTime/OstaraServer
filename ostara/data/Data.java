package ostara.data;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.ListIterator;

import ostara.Database;
import ostara.data.Person;
import ostara.data.Dept;
import ostara.data.EventType;
import ostara.data.Client;
import ostara.data.User;
import ostara.exceptions.InvalidLoginException;
import ostara.server.Session;

public class Data{
	private static HashMap<Integer, Person> persons;
	private static HashMap<Integer, Dept> depts;
	private static HashMap<Integer, EventType> eventTypes;
	private static HashMap<Integer, Client> clients;
	private static HashMap<String, User> users;

	public static void initialize(){
		persons = new HashMap<Integer, Person>();
		depts = new HashMap<Integer, Dept>();
		eventTypes = new HashMap<Integer, EventType>();
		clients = new HashMap<Integer, Client>();
		users = new HashMap<String, User>();
	}

	public static Dept getDept(int deptID){
		if(depts.containsKey(deptID)){
			return depts.get(deptID);
		}else{
			Dept dept = Database.getDept(deptID);
			depts.put(deptID, dept);
			return dept;
		}
	}

	public static Person getPerson(int personID){
		if(persons.containsKey(personID)){
			return persons.get(personID);
		}else{
			Person person = Database.getPerson(personID);
			persons.put(personID, person);
			return person;
		}
	}

	public static EventType getEventType(int eventTypeID){
		if(eventTypes.containsKey(eventTypeID)){
			return eventTypes.get(eventTypeID);
		}else{
			EventType eventType = Database.getEventType(eventTypeID);
			eventTypes.put(eventTypeID, eventType);
			return eventType;
		}
	}

	public static List<EventType> getEventTypes(){
		for( EventType i : Database.getEventTypes()){
			eventTypes.put(i.id, i);
		}
		return (List<EventType>)new ArrayList<EventType>(eventTypes.values());
	}

	public static List<String> getEventTypeNames(){
		List<EventType> eventTypes = getEventTypes();
		ArrayList<String> eventTypeNames = new ArrayList<String>();
		ListIterator<EventType> i = eventTypes.listIterator();
		while(i.hasNext()){
			 eventTypeNames.add(i.next().toString());
		}
		return eventTypeNames;
	}

	public static Client getClient(int clientID){
		if(clients.containsKey(clientID)){
			return clients.get(clientID);
		}else{
			Client client = Database.getClient(clientID);
			clients.put(clientID, client);
			return client;
		}
	}

	private static User getUser(String username){
		if(users.containsKey(username)){
			return users.get(username);
		}else{
			User user = Database.getUser(username);
			users.put(username, user);
			return user;
		}
	}

	public static Session authenticate(String username, String password, String salt) throws InvalidLoginException{
		return getUser(username).authenticate(username, password, salt);
	}

}
