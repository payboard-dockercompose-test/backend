package com.project.cardvisor.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.project.cardvisor.repo.CardListRepository;
import com.project.cardvisor.vo.CardListVO;

@Service
public class CardClusterService {
	
	private final CardListRepository clrepo;
	
	
	public CardClusterService(CardListRepository clrepo) {
		this.clrepo = clrepo;
	}
	
	
	public ResponseEntity<List<CardListVO>> getCards() {
		 List<CardListVO> cardList = (List<CardListVO>) clrepo.findAll(); // 카드 정보를 가져오는 로직을 구현해야 합니다.
		    
		    
		 return ResponseEntity.ok(cardList);
		
	
	}
	public List<Map<String,Object>> SelectTop5CardList(){
		List<Map<String,Object>> top5 = clrepo.selectTop5CardList();
		return top5;
	}
	
	
}
