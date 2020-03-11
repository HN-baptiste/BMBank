package component;

import java.time.LocalDateTime;
import java.util.Date;

public abstract class Flow {
	private String comment;
	private final Integer idFlow;
	private static int countflow = 0;
	private Double amount;
	private Integer targetAccId;
	private boolean effect;
	private LocalDateTime flowDate;
	
	public Flow(String com, double amount, Integer target, boolean effect, LocalDateTime d) {
		this.comment = com;
		this.idFlow = countflow++;
		this.amount = amount;
		this.targetAccId = target;
		this.effect = effect;
		this.flowDate = d;
	}
	/*
	 * Getters et Setters
	 */
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public Integer getIdFlow() {
		return idFlow;
	}
	public Double getAmount() {
		return amount;
	}
	public void setAmount(Double amount) {
		this.amount = amount;
	}
	public Integer getTargetAccId() {
		return targetAccId;
	}
	public void setTargetAccId(Integer targetAccId) {
		this.targetAccId = targetAccId;
	}
	public boolean isEffect() {
		return effect;
	}
	public void setEffect(boolean effect) {
		this.effect = effect;
	}
	public LocalDateTime getFlowDate() {
		return flowDate;
	}
	public void setFlowDate(LocalDateTime flowDate) {
		this.flowDate = flowDate;
	}
	
	

}
