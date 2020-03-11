package component;

import java.time.LocalDateTime;

public class Transfert  extends Flow{
	
	private Integer source; 

	public Transfert(String com, double amount, Integer target, boolean effect, LocalDateTime d, Integer source) {
		super(com, amount, target, effect, d);
		this.source = source;
	}

	public Integer getSource() {
		return source;
	}

	public void setSource(Integer source) {
		this.source = source;
	}
}
