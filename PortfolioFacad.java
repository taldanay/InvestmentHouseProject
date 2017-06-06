package com.SEM.InvestmentHouseSystem;

import java.util.List;

public interface PortfolioFacad {
	
	public Portfolio getPortfolioById(Long id);
	public List<Portfolio> saveAll(Portfolio ...portfolios);
	public List<Portfolio> getAll();
	public Portfolio getPortfolioByInvokerEmail(String invokerEmail);

}
