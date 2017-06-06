package com.SEM.InvestmentHouseSystem;

import org.springframework.data.jpa.repository.JpaRepository;


public interface AccountDao extends JpaRepository<Account, String>{
	
}