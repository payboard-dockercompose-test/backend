package com.project.cardvisor.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.cardvisor.service.CardReginfoService;
import com.project.cardvisor.service.CustClusterService;

@RestController
@RequestMapping("/main")
public class MainController {
	@Autowired
	 CustClusterService genderRatioService;

	@Autowired
	CardReginfoService cardreginfoservice;
	
	
	
	
	
	   //총 회원 수
    @GetMapping("/totalCustomer")
    public int CustomerTotalCount () {
    	int size = genderRatioService.CustomerTotalCount();
    	System.out.println("size:"+size);
    	return size;
    	}
	//매월 1일에 증가된 회원수
    @GetMapping("/addOneDayCustomer")
    public int addOnedaycustomer () {
    	int size = cardreginfoservice.addOnedaycustomer();
    	System.out.println("size:"+size);
    	return size;
    	}
    
    @GetMapping("/totalAmount")
    public int TotalAmountPayments() {
    	
    	return 0;
    }
}
