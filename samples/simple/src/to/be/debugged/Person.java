package to.be.debugged;

public class Person {
	String firstName;
	String lastName;

	public Person(String firstName, String lastName) {
		this.firstName = firstName;
		this.lastName = lastName;
	}

	public void greet() {
		System.out.println("Hello " + firstName + " " + lastName);
	}
}
