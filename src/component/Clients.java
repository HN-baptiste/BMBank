package component;

public class Clients {
	private String name;
	private String firstname;
	private final int  numClient;
	private static int countclient = 0;
	
	public Clients(String name, String firstname) {
		this.name = name;
		this.firstname = firstname;
		this.numClient = countclient++;		
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public int getNumClient() {
		return numClient;
	}

	@Override
	public String toString() {
		return "Clients [name=" + name + ", firstname=" + firstname + ", numClient=" + numClient + "]";
	}





	
	

}
