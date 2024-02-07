package com.project.cardvisor.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.project.cardvisor.repo.PaymentRepository;

@Service
public class PaymentsService {

	@Autowired
	PaymentRepository payrepo;
	
	public int TotalAmountPayments() {
		
		return 0;
	}
	
}
