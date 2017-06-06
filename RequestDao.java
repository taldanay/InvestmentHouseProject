package com.SEM.InvestmentHouseSystem;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.SEM.InvestmentHouseSystem.Request.RequestStatus;

public interface RequestDao extends JpaRepository<Request, String>{
	
	public List<Request> getRequestsByInvokerEmail(String invokerEmail);
	public List<Request> getRequestsByStatus(RequestStatus status);
	
}
