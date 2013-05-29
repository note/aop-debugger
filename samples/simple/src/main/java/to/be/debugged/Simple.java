package to.be.debugged;

public class Simple {

	public static void main(String[] argv) {
		while(true) {
		System.out.println("Bazinga456!");
		int intRes = addIntegers(34, 55);
		Person person = new Person("Adam", "Nowak");
		person.greet();
		}
	}

	private static int addIntegers(int a, int b) {
		return a + b;
	}
}
