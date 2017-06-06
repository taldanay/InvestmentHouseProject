package com.SEM.InvestmentHouseSystem;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PortfolioDao extends JpaRepository<Portfolio, Long>{
		
	public Portfolio getPortfolioByInvokerEmail(String invokerEmail);
	
}
