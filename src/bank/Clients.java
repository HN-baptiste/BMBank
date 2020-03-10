package bank;

public class Clients {
	private String name;
	private String firstname;
	static private int numClient = 0;
	
	public Clients(String name, String firstname) {
		this.name = name;
		this.firstname = firstname;
		numClient ++;		
	}
	
	

}
