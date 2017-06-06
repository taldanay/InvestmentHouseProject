package com.SEM.InvestmentHouseSystem;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

import com.SEM.InvestmentHouseSystem.ClientServerRequest.ClientServerRequestType;

import stockexchange.client.Stock;

public class InvestmentHouseFacadImpl implements InvestmentHouseFacad{

	private Socket socket;
	private ObjectOutputStream oos;
	private ObjectInputStream ois;
	
	public InvestmentHouseFacadImpl() {
		try {
			this.socket = new Socket("localhost", 1234);
			oos = new ObjectOutputStream(socket.getOutputStream());
			ois = new ObjectInputStream(socket.getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public ClientServerRequest login(String email, String password) {
		ClientServerRequest csr = new ClientServerRequest(ClientServerRequestType.LOGIN, email, password);
		sendClientServerRequestToTheInvestmentHouse(csr);
		return getAnswersFromInvestmentHouse();
	}

	@Override
	public Account signup(Account account) {
		ClientServerRequest csr = new ClientServerRequest(ClientServerRequestType.SIGNUP, account);
		sendClientServerRequestToTheInvestmentHouse(csr);
		return getAnswersFromInvestmentHouse().getAccount();
	}

	@Override
	public ArrayList<Stock> getAllStocks() {
		ClientServerRequest csr = new ClientServerRequest(ClientServerRequestType.GETALLSTOCKS);
		sendClientServerRequestToTheInvestmentHouse(csr);
		return new ArrayList<>(getAnswersFromInvestmentHouse().getAllStocks());
	}

	@Override
	public ArrayList<Request> getAccountsRequests(Account account) {
		ClientServerRequest csr = new ClientServerRequest(ClientServerRequestType.GETALLREQUESTSBYINVOKEREMAIL, account.getEmail());
		sendClientServerRequestToTheInvestmentHouse(csr);
		return new ArrayList<>(getAnswersFromInvestmentHouse().getAllRequests());
	}

	@Override
	public void sendRequest(Request request) {
		ClientServerRequest csr = new ClientServerRequest(ClientServerRequestType.REQUEST, request);
		sendClientServerRequestToTheInvestmentHouse(csr);
	}
	
	@Override
	public Portfolio getPortfolioByInvokerId(String portfolioId) {
		ClientServerRequest csr = new ClientServerRequest(ClientServerRequestType.GETPORTFOLIO, portfolioId);
		sendClientServerRequestToTheInvestmentHouse(csr);
		return getAnswersFromInvestmentHouse().getPortfolio();
	}
	
	@Override
	public void logOut() {
		try {
			ois.close();
			oos.close();
			socket.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void sendClientServerRequestToTheInvestmentHouse(ClientServerRequest csr) {
		try {
			oos.writeObject(csr);
			oos.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private ClientServerRequest getAnswersFromInvestmentHouse() {
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