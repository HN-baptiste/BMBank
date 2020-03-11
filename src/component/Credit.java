package component;

import java.time.LocalDateTime;


public class Credit  extends Flow{

	public Credit(String com, double amount, Integer target, boolean effect, LocalDateTime d) {
		super(com, amount, target, effect, d);
	}
}
