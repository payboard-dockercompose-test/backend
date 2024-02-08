package com.project.cardvisor.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.project.cardvisor.repo.PaymentRepository;

@Service
public class InternationalService {
	

	@Autowired
	PaymentRepository payrep;
	
	//해외 토탈 결제 금액
	public Long getTotalpayment() {
		return payrep.selectTotalOverseasPayment();
	}
	
}
