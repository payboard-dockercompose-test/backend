package com.project.cardvisor.controller;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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
	
	@GetMapping("/benefitTreeChart")
	public LinkedList<Object> benefitTreeChartByMCC(){
		return bser.benefitTreeMapByMCC();
	}
	
	@GetMapping("/benefitTopAndBottom")
	public Map<String, Object> benefitTop5ByMCC(){
		return bser.benefitTopAndBottomByMCC();
	}
	
//	@GetMapping("/benefitBottom")
//	public List<LinkedHashMap<String, Object>> benefitBottom5ByMCC(){
//		return bser.benefitTopAndBottomByMCC();
//	}
}
