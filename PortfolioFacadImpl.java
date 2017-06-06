package com.SEM.InvestmentHouseSystem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PortfolioFacadImpl implements PortfolioFacad{

	@Autowired
	private PortfolioDao portfolioDao;
	private RequestFacad requestFacad;
	private StockDetailsFacad stockDetailsFacad;
	
	@Autowired
	public void setRequestFacad(RequestFacad requestFacad) {
		this.requestFacad = requestFacad;
	}
	
	@Autowired
	public void setStockDetailsFacad(StockDetailsFacad stockDetailsFacad) {
		this.stockDetailsFacad = stockDetailsFacad;
	}
	
	
	@Override
	@Transactional(readOnly=true)
	public Portfolio getPortfolioById(Long id) {
		Portfolio portfolio = portfolioDao.getOne(id);		
		loadRequestAndStocksDetailsToPortfolio(portfolio);
		return portfolio;
	}

	@Override
	@Transactional
	public List<Portfolio> saveAll(Portfolio... portfolios) {
		return portfolioDao.save(Arrays.asList(portfolios));
	}

	@Override
	@Transactional(readOnly=true)
	public List<Portfolio> getAll() {
		List<Portfolio> list = portfolioDao.findAll();
		list.forEach(s -> loadRequestAndStocksDetailsToPortfolio(s));
		return list;
	}

	
	private List<StockDetails> loadPortfolioStocksToMap(String invokerEmail) {
		return stockDetailsFacad.getAllStockDetailsByInvokerEmail(invokerEmail);
	}

	
	private List<Request> loadPortfolioRequestsToList(String invokerEmail) {
		return requestFacad.getRequestsByInvokerEmail(invokerEmail);
	}
	
	private void loadRequestAndStocksDetailsToPortfolio(Portfolio portfolio) {
		// load all invoker requests
		ArrayList<Long> requests = new ArrayList<>();
		loadPortfolioRequestsToList(portfolio.getInvokerEmail()).forEach(s -> requests.add(s.getRequestId()));
		portfolio.setRequestsIds(requests);
		// load all invoker stocks
		ArrayList<StockDetails> stocks = new ArrayList<>();
		loadPortfolioStocksToMap(portfolio.getInvokerEmail()).forEach(s -> stocks.add(s));
		portfolio.setInvestorStocks(stocks);
	}

	@Override
	public Portfolio getPortfolioByInvokerEmail(String invokerEmail) {
		return portfolioDao.getPortfolioByInvokerEmail(invokerEmail);
	}


}
