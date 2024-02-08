package com.project.cardvisor.controller;

import java.io.Console;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.cardvisor.repo.PaymentRepository;
import com.project.cardvisor.service.CustClusterService;
import com.project.cardvisor.vo.CustomerVO;
import com.project.cardvisor.vo.PaymentsVO;

@RestController
@RequestMapping("/customer")
public class CustClusterController {
	
	@Autowired
	private CustClusterService cService;

	public CustClusterController(CustClusterService cService) {
        this.cService = cService;
    }

    @GetMapping("/genderRatio")
    public Map<String, Long> getGenderRatio() {
        return cService.getGenderRatio();
    }
    
    @GetMapping("/ageGroup")
    public ResponseEntity<Map<String, Integer>> getAgeGroupCount() {
      Map<String, Integer> ageGroupCount = cService.getAgeGroupCount();
      return new ResponseEntity<>(ageGroupCount, HttpStatus.OK);
    }
    
    @GetMapping("/jobTypes")
    public List<Map<String, Object>> getAllJobTypes() {
        return cService.findAllJobTypes();
    }
    
    @GetMapping("/custSalary")
    public List<Object[]> getAllCustSalary() {
        return cService.findAllCustSalary();
    }
    
    @GetMapping("/amountBySalary")
    public ResponseEntity<List<Object[]>> getAveragePayAmountBySalary() {
        List<Object[]> averagePayAmountBySalary = cService.getAveragePayAmountBySalary();
        return ResponseEntity.ok(averagePayAmountBySalary);
    }
    
}

