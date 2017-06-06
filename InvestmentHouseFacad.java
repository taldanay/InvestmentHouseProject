package com.SEM.InvestmentHouseSystem;

import java.io.IOException;
import java.util.ArrayList;

import stockexchange.client.Stock;

public interface InvestmentHouseFacad {
	
	public ClientServerRequest login(String email,String password) throws IOException, ClassNotFoundException;
    public Account signup(Account account);
    public ArrayList<Stock> getAllStocks();
    public ArrayList<Request> getAccountsRequests(Account account);
    public void sendRequest(Request request);
    public Portfolio getPortfolioByInvokerId(String portfolioId);
    public void logOut();

}
