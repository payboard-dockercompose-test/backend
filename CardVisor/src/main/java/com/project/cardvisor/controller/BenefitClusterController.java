package com.project.cardvisor.controller;

import java.util.LinkedList;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.cardvisor.service.BenefitClusterService;

import lombok.RequiredArgsConstructor;

@RequestMapping("/benefitCluster")
@RestController
@RequiredArgsConstructor
public class BenefitClusterController {
	
	private final BenefitClusterService bser;
	
	@GetMapping("/mcc")
	public LinkedList<Object> benefitByMCC(){
		return bser.benefitByMCC();
	}
}
