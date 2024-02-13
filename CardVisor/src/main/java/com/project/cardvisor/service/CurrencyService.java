package com.project.cardvisor.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.project.cardvisor.repo.CurrencyRepository;
import com.project.cardvisor.vo.CurrencyVO;

@Service
public class CurrencyService {

	@Autowired
	CurrencyRepository crepo;
	
	
	public List<CurrencyVO> LatestCurrencyData(){
		List<CurrencyVO>  cList = crepo.findByLatestCurrencyData();
		return cList;
	}
}
