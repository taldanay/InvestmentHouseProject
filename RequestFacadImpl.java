package com.SEM.InvestmentHouseSystem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.SEM.InvestmentHouseSystem.Request.RequestStatus;
import com.SEM.InvestmentHouseSystem.Request.RequestType;

import stockexchange.client.StockExchangeClient;
import stockexchange.client.StockExchangeTransaction;

@Service
public class RequestFacadImpl implements RequestFacad {

	@Autowired
	private RequestDao requestDao;
	private StockDetailsFacad stockDetailsFacad;
	private AccountFacade accountFacade;
	private PortfolioFacad portfolioFacad;
	
	@Autowired
	public void setAccountFacade(AccountFacade accountFacade) {
		this.accountFacade = accountFacade;
	}
	
	@Autowired
	public void setPortfolioFacad(PortfolioFacad portfolioFacad) {
		this.portfolioFacad = portfolioFacad;
	}
	
	@Autowired
	public void setStockDetailsFacad(StockDetailsFacad stockDetailsFacad) {
		this.stockDetailsFacad = stockDetailsFacad;
	}
	

	@Override
	@Transactional
	public List<Request> saveAll(Request... requests) {
		return requestDao.save(Arrays.asList(requests));
	}

	@Override
	@Transactional(readOnly=true)
	public List<Request> getAll() {
		return requestDao.findAll();
	}

	@Override
	@Transactional(readOnly=true)
	public Request getRequestById(String requestId) {
		return requestDao.findOne(requestId);
	}

	@Override
	@Transactional(readOnly=true)
	public List<Request> getRequestsByInvokerEmail(String invokerEmail) {
		return requestDao.getRequestsByInvokerEmail(invokerEmail);
	}

	@Override
	@Transactional(readOnly=true)
	public List<Request> getRequestsByStatus(RequestStatus status) {
		return requestDao.getRequestsByStatus(status);
	}

	@Override
	public void checkForCompletedRequests(StockExchangeClient sec) {
		new Thread(new checkForCompletedRequestsAtTheStockMarket(sec)).start();
	}
	
	
	private class checkForCompletedRequestsAtTheStockMarket implements Runnable {

		private StockExchangeClient sec;
		
		public checkForCompletedRequestsAtTheStockMarket(StockExchangeClient sec) {
			this.sec = sec;
		}
		
		@Override
		public void run() {
			
			List<Request> requests = getRequestsByStatus(RequestStatus.PENDING);
			for (Request request : requests) {
				List<StockExchangeTransaction> lst = sec.getTransactionsForCommand(request.getRequestId());
				if(!lst.isEmpty()) {
					request.setStatus(RequestStatus.COMPLETED);
					
					Account account = accountFacade.getAccount(request.getInvokerEmail());
					Portfolio portfolio = portfolioFacad.getPortfolioById(account.getPortfolioId());
					ArrayList<StockDetails> stocks = portfolio.getInvestorStocks();
					StockDetails sd = null;
					for (StockDetails stockDetails : stocks) {
						if (stockDetails.getStockId().equals(request.getStockId())){
							sd = stockDetails;
							break;
						}
					}
							
					
					double counter = 0 ;
					for (StockExchangeTransaction stockExchangeTransaction : lst)
						counter += stockExchangeTransaction.getActualPrice();
					
					request.setPurchasePrice(counter);
					saveAll(request);
					
					if(sd != null) {
						sd.setAmount(sd.getAmount() + request.getAmount());
						sd.setPurchasePrice(sd.getPurchasePrice() + request.getPurchasePrice());
						stockDetailsFacad.saveAll(sd);
					}
					else
						stockDetailsFacad.saveAll(new StockDetails(request.getStockId(), request.getPurchasePrice() , request.getAmount(), request.getInvokerEmail()));
					
					if (request.getType() == RequestType.ASK) 
						account.setBalance(account.getBalance() - counter);
					else if(request.getType() == RequestType.BID)
						account.setBalance(account.getBalance() + counter);
				}
			}	
		}
		
	}

	
	
	
	
}
