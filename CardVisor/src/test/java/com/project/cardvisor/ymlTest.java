package com.project.cardvisor;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.project.cardvisor.repo.CardRegInfoRepository;
import com.project.cardvisor.vo.CardRegInfoVO;

@SpringBootTest
public class ymlTest {

	@Autowired
	CardRegInfoRepository regrepo;
	
	@Test
	void test() {
		CardRegInfoVO test = regrepo.findById("REG-00e05a73-3f4b-4e92-8b9d-8339108c76c4").orElse(null);
		
		System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>"+test);
	}
	
}
