package com.SEM.InvestmentHouseSystem;

import java.util.List;

import com.SEM.InvestmentHouseSystem.Request.RequestStatus;

import stockexchange.client.StockExchangeClient;


public interface RequestFacad {

	public List<Request> saveAll(Request ...requests);
	public List<Request> getAll();
	public Request getRequestById(String requestId);
	public List<Request> getRequestsByInvokerEmail(String invokerEmail);
	public List<Request> getRequestsByStatus(RequestStatus status);
	public void checkForCompletedRequests(StockExchangeClient sec);
	
}
