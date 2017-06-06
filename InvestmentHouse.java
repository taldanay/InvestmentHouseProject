package com.SEM.InvestmentHouseSystem;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Timer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import stockexchange.client.Stock;
import stockexchange.client.StockCommandType;
import stockexchange.client.StockExchangeClient;
import stockexchange.client.StockExchangeClientFactory;
import stockexchange.client.StockExchangeCommand;

@SpringBootApplication
public class InvestmentHouse implements CommandLineRunner{
	
	private StockExchangeClient stockMarket;
	private RequestFacad requestFacad;
	private PortfolioFacad portfolioFacad;
	private AccountFacade accountFacade;
	
	@Autowired
	public void setAccountFacade(AccountFacade accountFacade) {
		this.accountFacade = accountFacade;
	}
	
	@Autowired
	public void setPortfolioFacad(PortfolioFacad portfolioFacad) {
		this.portfolioFacad = portfolioFacad;
	}
	
	@Autowired
	public void setRequestFacad(RequestFacad requestFacad) {
		this.requestFacad = requestFacad;
	}
	
	
	public static void main(String[] args) {
		SpringApplication.run(InvestmentHouse.class, args);
	}

	
	@Override
	public void run(String... arg0) throws Exception {
		
		/* Request push notifications
		String myId = "100Plus";
		stockMarket.startListening(myId, (o, event)->System.err.println(event));
		*/
		
		this.stockMarket = StockExchangeClientFactory.getClient();
		
		// handle all users
		new Thread(new Clients()).start();
		
		Timer timer = new Timer(5000, new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				requestFacad.checkForCompletedRequests(stockMarket);
			}
		});
		timer.start();
	}
	
	private class Clients implements Runnable {

		@Override
		public void run() {
			try(ServerSocket serverSocket = new ServerSocket(1234)) {
				while(true) {
					try {
						final Socket socket = serverSocket.accept();
						new Thread(new Runnable() {
						
							@Override
							public void run() {
								try(ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
									ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream())) {
									
									ClientServerRequest csr = getRequestsFromTheClient(ois);
									
									switch (csr.getType()) {
										
										case LOGIN: {
											sendAnswersToTheClient(login(csr), oos);
											break;
										}
										case GETALLREQUESTSBYINVOKEREMAIL: {
											csr.setAllRequests(requestFacad.getRequestsByInvokerEmail(csr.getEmail()));
											sendAnswersToTheClient(csr, oos);
											break;
										}	
										case GETALLSTOCKS: {
											csr.setAllStocks(getAllStocks());
											sendAnswersToTheClient(csr, oos);
											break;
										}
										case GETPORTFOLIO: {
											csr.setPortfolio(portfolioFacad.getPortfolioById(csr.getAccount().getPortfolioId()));
											sendAnswersToTheClient(csr, oos);
											break;
										}
										case REQUEST: {
											handleRequests(csr.getRequest());
											//csr.setRequest(handleRequests(csr.getRequest()));
											//sendAnswersToTheClient(csr, oos);
											break;
										}
										case SIGNUP: {
											csr.setAccount(accountFacade.addAccount(csr.getAccount()));
											csr.setPortfolio(portfolioFacad.saveAll(new Portfolio(csr.getAccount().getEmail())).get(0));
											csr.getAccount().setPortfolioId(csr.getPortfolio().getPortfolioId());
											sendAnswersToTheClient(csr, oos);
											break;
										}
										
										default:
											break;
									}
									
								} catch (IOException e) {
									e.printStackTrace();
								} finally {
									try {
										socket.close();
									} catch (IOException e) {
										e.printStackTrace();
									}
								}
							}

						}).start();
						
					} catch (Exception e) {
						e.printStackTrace();
					}
					
				}
			} catch (IOException e) {
				e.printStackTrace();
			}	
		}
		
	}
	
	private void handleRequests(Request request) {
		StockExchangeCommand sec = null;
		switch (request.getType()) {
			case BID: {
				sec = new StockExchangeCommand(StockCommandType.BID, request.getInvokerEmail(), request.getStockId(), request.getMinPrice(),
											request.getMaxPrice(), request.getAmount());
				break;
			}
			case ASK: {
				sec = new StockExchangeCommand(StockCommandType.ASK, request.getInvokerEmail(), request.getStockId(), request.getMinPrice(),
						request.getMaxPrice(), request.getAmount());
				break;
			}
			default:
				break;
		}
		
		if (sec != null) {
			request.setRequestId(stockMarket.sendCommand(sec));
			requestFacad.saveAll(request);
			chargeCommision(request);
		}
		//return request;
	}
	
	private void chargeCommision(Request request) {
		Account account = accountFacade.getAccount(request.getInvokerEmail());
		Portfolio portfolio = portfolioFacad.getPortfolioById(account.getPortfolioId());
		account.setBalance(account.getBalance() - (request.getMaxPrice() * request.getAmount()) * portfolio.getCommissionPercentage());
	}
	
	public ArrayList<Stock> getAllStocks() {
		List<String> stocksIds = stockMarket.getStocksId();
		ArrayList<Stock> allStocks = new ArrayList<>();
		stocksIds.forEach(s -> allStocks.add(stockMarket.getQuote(s)));
		return allStocks;
	}
	
	public ClientServerRequest login(ClientServerRequest csr) {
		Account account = accountFacade.getAccount(csr.getEmail());
		if(account.getPassword().equals(csr.getPassword())) {
			csr.setAccount(account);
			csr.setPortfolio(portfolioFacad.getPortfolioByInvokerEmail(csr.getEmail()));
			csr.setAllStocks(getAllStocks());
		}
		else
			csr.setAccount(null);
		
		return csr;
	}
	
	private void sendAnswersToTheClient(ClientServerRequest csr, ObjectOutputStream oos) {
		try {
			oos.writeObject(csr);
			oos.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private ClientServerRequest getRequestsFromTheClient(ObjectInputStream ois) {
		ClientServerRequest csr = null;
		while(csr == null) {
			try {
				csr = (ClientServerRequest) ois.readObject();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return csr;
	}
	
}