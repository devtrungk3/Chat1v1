import java.util.ArrayList;
import java.util.Scanner;

import com.google.gson.Gson;

public class test {
	
	static String name = "";
	
	public static void main(String[] args) {
		String name = "Trung";
		Integer age = 20;
		String txt = "{\"name\":"+name+",\"age\":"+age+"}";
		Gson gson = new Gson();
		Person person = gson.fromJson(txt, Person.class);
		
		System.out.println("{\"name\":"+person.name+",\"age\":"+person.age+"}");
	}
}

class Person {
	
	String name;
	Integer age;
	
	public Person(String name, Integer age) {
		this.name = name;
		this.age = age;
	}
}
