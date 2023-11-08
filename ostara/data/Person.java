package ostara.data;

import ostara.data.Dept;

public class Person{
	int id;
	Dept dept;
	String name;

	public Person(int id, Dept dept, String name){
		this.id = id;
		this.dept = dept;
		this.name = name;
	}

	public Person(int id, int dept, String name){
		this.id = id;
		this.dept = Data.getDept(dept);
		this.name = name;
	}

	public String toString(){
		return this.name;
	}

	public String getName(){
		return this.name;
	}

}
