package com.project.cardvisor.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.project.cardvisor.repo.CardListRepository;

@Service
public class CardClusterService {
	
	private final CardListRepository clrepo;
	
	
	public CardClusterService(CardListRepository clrepo) {
		this.clrepo = clrepo;
	}
	
	public List<String> getCardImg(){
		
		return null;
	}
	public List<Map<String,Object>> SelectTop5CardList(){
		List<Map<String,Object>> top5 = clrepo.selectTop5CardList();
		return top5;
	}
	
	
}
