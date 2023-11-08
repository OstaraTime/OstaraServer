package ostara.data;

public class Dept{
	int id;
	String name;

	public Dept(int id, String name){
		this.id = id;
		this.name = name;
	}

	public String toString(){
		return this.name;
	}

}
