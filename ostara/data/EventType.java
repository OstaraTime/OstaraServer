package ostara.data;

public class EventType{
	int id;
	String name;

	public EventType(int id, String name){
		this.id = id;
		this.name = name;
	}

	public String toString(){
		return this.name;
	}

}
