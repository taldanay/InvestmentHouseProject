package com.SEM.InvestmentHouseSystem;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface StockDetailsDao extends JpaRepository<StockDetails, String>{
	
	public List<StockDetails> getAllStockDetailsByInvokerEmail(String invokerEmail);

}
