package com.project.cardvisor.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.cardvisor.service.CustClusterService;

@RestController
public class CustClusterController {
	
	private final CustClusterService genderRatioService;

    public CustClusterController(CustClusterService genderRatioService) {
        this.genderRatioService = genderRatioService;
    }

    @GetMapping("/genderRatio")
    public Map<String, Long> getGenderRatio() {
        return genderRatioService.getGenderRatio();
    }
}

