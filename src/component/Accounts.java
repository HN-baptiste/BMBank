package component;

public abstract class Accounts {
	protected String label;
	protected double balance;
	protected final int accountNumber;
	protected static int countAccounts = 0;
	protected Clients cli;

	public Accounts(String l, Clients c) {
		this.label = l;
		this.cli = c;
		this.accountNumber = countAccounts++;
		this.balance = 0;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public double getBalance() {
		return balance;
	}

	/*
	 * Gère l'update de la balance selon le type de mouvement
	 */
	public void setBalance(Flow flow) {
		if(!flow.isEffect()) {
			if(flow instanceof Debit)
				this.balance -= flow.getAmount();
			else if(flow instanceof Credit)
				this.balance += flow.getAmount();
			else if(flow instanceof Transfert) {
				if(flow.getTargetAccId() == this.getAccountNumber())
					this.balance += flow.getAmount();
				else if(((Transfert) flow).getSource() == this.getAccountNumber()) {
					this.balance -= flow.getAmount();
				}
			}
		}		
	}
	


	@Override
	public String toString() {
		return "Accounts [label=" + label + ", balance=" + balance + ", accountNumber=" + accountNumber + ", cli=" + cli
				+ "]";
	}

	public Integer getAccountNumber() {
		return accountNumber;
	}

	
	public Clients getCli() {
		return cli;
	}

	public void setCli(Clients cli) {
		this.cli = cli;
	}
}
