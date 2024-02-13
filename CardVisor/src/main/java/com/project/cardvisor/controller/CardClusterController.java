package com.project.cardvisor.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.cardvisor.service.CardClusterService;
import com.project.cardvisor.vo.CardListVO;

@RestController
@RequestMapping("/CardCluster")
public class CardClusterController {
	
	private final CardClusterService ccserv;
	
	public CardClusterController(CardClusterService ccserv) {
		this.ccserv = ccserv;
	}
	
	@GetMapping("/MccByCard")
	String getMccByCard() {
		return null;
	}
	
	@GetMapping("/Cards")
	public ResponseEntity<List<CardListVO>> getCards(){
		return ccserv.getCards();
	}
	
	
	
	

}
