package com.SEM.InvestmentHouseSystem;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="StockDetails")
public class StockDetails {
	
	private Long stockDetailsId;
	private String stockId;
	private Double purchasePrice;
	private Integer amount;
	private String invokerEmail;
	
	public StockDetails() {
	}
	
	public StockDetails(String stockId, Double purchasePrice, Integer amount, String invokerEmail) {
		setStockId(stockId);
		setAmount(amount);
		setPurchasePrice(purchasePrice);
		setInvokerEmail(invokerEmail);
	}
	
	@Id
	@GeneratedValue
	@Column(name="Stock_Details_Id")
	public Long getStockDetailsId() {
		return stockDetailsId;
	}

	public void setStockDetailsId(Long stockDetailsId) {
		this.stockDetailsId = stockDetailsId;
	}

	@Column(name="Stock_Id")
	public String getStockId() {
		return stockId;
	}

	public void setStockId(String stockId) {
		this.stockId = stockId;
	}

	@Column(name="Purchase_Price")
	public Double getPurchasePrice() {
		return purchasePrice;
	}

	public void setPurchasePrice(Double purchasePrice) {
		this.purchasePrice = purchasePrice;
	}

	@Column(name="Amount")
	public Integer getAmount() {
		return amount;
	}

	public void setAmount(Integer amount) {
		this.amount = amount;
	}

	@Column(name="Invoker_Email")
	public String getInvokerEmail() {
		return invokerEmail;
	}

	public void setInvokerEmail(String invokerEmail) {
		this.invokerEmail = invokerEmail;
	}

	
	
}
