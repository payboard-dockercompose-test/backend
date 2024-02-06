package com.project.cardvisor;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.project.cardvisor.repo.CardRegInfoRepository;
import com.project.cardvisor.vo.CardRegInfoVO;

@SpringBootTest
public class DBTest {

	@Autowired
	CardRegInfoRepository crirepo;
	
	@Test
	void f1() {
		CardRegInfoVO vo = crirepo.findById("REG-0000be30-6834-40f0-b78b-79156ab88f5b").orElse(null);
		System.out.println(vo.toString());
	}
	
}
