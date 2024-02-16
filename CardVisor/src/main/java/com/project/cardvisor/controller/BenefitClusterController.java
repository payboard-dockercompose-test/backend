package com.project.cardvisor.controller;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
	
	@PostMapping("/BenefitDetail")
	public List<Map<String, Object>> BenefitDetail(@RequestBody Map<String, String> data){
		return bser.benefitDetailByCategory((String)data.get("cateName"));
	}
	
}
