package component;

import java.time.LocalDateTime;

public class Debit extends Flow {

	public Debit(String com, double amount, Integer target, boolean effect, LocalDateTime d) {
		super(com, amount, target, effect, d);
	}

}
