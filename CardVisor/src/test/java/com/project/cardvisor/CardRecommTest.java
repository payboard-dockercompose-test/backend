package com.project.cardvisor;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.project.cardvisor.repo.CardRegRepository;
import com.project.cardvisor.repo.CustomerRepository;
import com.project.cardvisor.vo.CardRegInfoVO;
import com.project.cardvisor.vo.CustomerVO;

import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@Slf4j
public class CardRecommTest {

	@Autowired
	CardRegRepository crep;
	
	@Autowired
	CustomerRepository cusrep;
	
	@Test
	public void recommTest() {
		
	}
}
